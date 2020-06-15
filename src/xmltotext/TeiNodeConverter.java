/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext;

import java.util.HashSet;
import java.util.function.Consumer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xmltotext.conversion.ActionType;
import xmltotext.conversion.NodeAction;
import xmltotext.conversion.NodeConverter;
import xmltotext.conversion.text.TextProcessor;
import xmltotext.tokens.ConversionTypes;
import xmltotext.tokens.Token;
import xmltotext.tokens.TokenType;
import xmltotext.util.ConversionUtil;
import xmltotext.util.XmlUtil;

/**
 * A {@link NodeConverter} for TEI XML files.
 * <p>
 * This class implements shallow node processing for
 * the TEI elements present in our data.
 * Some elements (for example, {@code teiHeader}) are skipped,
 * others lead to the insertion of special tokens such as
 * line breaks or paragraph boundaries.
 * There is also some special processing for certain text
 * characters.
 * Besides converting line break characters into line break tokens,
 * whitespace characters into whitespace tokens and so on,
 * the long s (&#x17F;, {@code U+017F LATIN SMALL LETTER LONG S})
 * is converted into a normal ASCII character {@code s}.
 * There is also special treatment for some other characters.
 * </p>
 */
public class TeiNodeConverter implements NodeConverter {
	
	private static final String[] TAGS_TO_SKIP = new String[] {
			"teiHeader",
			"front",
			"back",
			"date",
			"sic",
			"fw",
			"ptr",
			"milestone",
			"title"
		};
	private static final HashSet<String> TAGS_TO_SKIP_SET;
	
	static {
		TAGS_TO_SKIP_SET = new HashSet<>();
		for (final String tagToIgnore : TAGS_TO_SKIP)
			TAGS_TO_SKIP_SET.add(tagToIgnore);
	}
	
	@Override
	public NodeAction action(final Node node, final Consumer<? super Token> tokenConsumer) {
		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE:
				return processElement(node, node.getNodeName(), tokenConsumer);
			case Node.CDATA_SECTION_NODE:
			case Node.TEXT_NODE:
				TextProcessor.processText(node.getNodeValue(), TeiNodeConverter::processCodePoint, tokenConsumer);
				return NodeAction.SKIP; // no child nodes anyway
			default:
				// this case never occurred in our data
				return NodeAction.SIMPLY_RECURSE;
		}
	}

	private NodeAction processElement(final Node node, final String nodeName, final Consumer<? super Token> tokenConsumer) {
		if (TAGS_TO_SKIP_SET.contains(nodeName))
			return NodeAction.SKIP;
		switch (nodeName) {
			case "space":
				tokenConsumer.accept(new Token(TokenType.WHITESPACE, null));
				return NodeAction.SKIP;
			case "lb":
				tokenConsumer.accept(Token.EXPLICIT_LINE_BREAK);
				return NodeAction.SIMPLY_RECURSE;
			case "pb":
				tokenConsumer.accept(Token.PAGE_BREAK);
				return NodeAction.SIMPLY_RECURSE;
			case "l":
			case "row":
			case "item":
				tokenConsumer.accept(Token.EXPLICIT_LINE_BREAK);
				return new NodeAction(ActionType.RECURSE, Token.EXPLICIT_LINE_BREAK);
			case "div":
				if (XmlUtil.hasAttribute(node, "type", "contents"))
					return NodeAction.SKIP;
			case "p":
			case "list":
			case "dateline":
			case "postscript":
			case "salute":
			case "table":
			case "head":
				tokenConsumer.accept(Token.PARAGRAPH_BOUNDARY);
				return NodeAction.RECURSE_PARAGRAPH;
			case "cell":
				tokenConsumer.accept(new Token(TokenType.WHITESPACE, "\t"));
				return NodeAction.SIMPLY_RECURSE;
			case "note":
				return processNote(node, tokenConsumer);
			case "gap":
				tokenConsumer.accept(new Token(TokenType.TEXT, "[\u2026]", ConversionTypes.HUMAN));
				return NodeAction.SKIP;
			case "figure":
			case "graphic":
				ConversionUtil.putSkipNotification("[Bild]", tokenConsumer);
				return NodeAction.SKIP;
			case "formula":
				ConversionUtil.putSkipNotification("[Formel]", tokenConsumer);
				return NodeAction.SKIP;
			default:
				return NodeAction.SIMPLY_RECURSE;
		}
	}

	private NodeAction processNote(final Node node, final Consumer<? super Token> tokenConsumer) {
		final NamedNodeMap attributes = node.getAttributes();
		if (attributes == null)
			// should not happen for proper Element nodes
			return NodeAction.SIMPLY_RECURSE;
		final Node placeAttribute = attributes.getNamedItem("place");
		if (placeAttribute == null)
			return NodeAction.SIMPLY_RECURSE;
		final String place = placeAttribute.getNodeValue();
		if ("foot".equals(place))
			return ConversionUtil.putFootnote(tokenConsumer);
		// this case never occurred in our data
		return NodeAction.SKIP;
	}
	
	private static void processCodePoint(final String text, final int start, final int end, final int codePoint, final StringBuilder textBuilder, final Consumer<? super Token> tokenConsumer) {
		switch (codePoint) {
			case 0xA:
			case 0xD:
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.IMPLICIT_LINE_BREAK, text.substring(start, end)));
				break;
			case 0x17F: // ſ
				textBuilder.appendCodePoint('s');
				break;
			case 0xAC: // ¬
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.HYPHENATION, "\u00AC"));
				break;
			case 0x2D: // -
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.POSSIBLE_HYPHENATION, "-"));
				break;
			default:
				processOtherCodePoint(text, start, end, codePoint, textBuilder, tokenConsumer);
				break;
		}
	}
	
	private static void processOtherCodePoint(final String text, final int start, final int end, final int codePoint, final StringBuilder textBuilder, final Consumer<? super Token> tokenConsumer) {
		switch (Character.getType(codePoint)) {
			case Character.LINE_SEPARATOR:
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.IMPLICIT_LINE_BREAK, text.substring(start, end)));
				break;
			case Character.PARAGRAPH_SEPARATOR:
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.PARAGRAPH_BOUNDARY, text.substring(start, end)));
				break;
			case Character.SPACE_SEPARATOR:
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.WHITESPACE, text.substring(start, end)));
				break;
			default:
				textBuilder.appendCodePoint(codePoint);
				break;
		}
	}
	
}
