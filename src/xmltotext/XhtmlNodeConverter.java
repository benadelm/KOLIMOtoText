/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext;

import java.util.HashSet;
import java.util.function.Consumer;

import org.w3c.dom.Node;

import xmltotext.conversion.ActionType;
import xmltotext.conversion.NodeAction;
import xmltotext.conversion.NodeConverter;
import xmltotext.conversion.text.TextProcessor;
import xmltotext.tokens.Token;
import xmltotext.tokens.TokenType;
import xmltotext.util.ConversionUtil;
import xmltotext.util.XmlUtil;

/**
 * A {@link NodeConverter} for XHTML files.
 * <p>
 * This class implements shallow node processing for
 * the XHTML elements present in our data.
 * Some elements (in particular, {@code head}) are skipped,
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
public class XhtmlNodeConverter implements NodeConverter {

	private static final String[] TAGS_TO_SKIP = new String[] {
			"head"
		};
	private static final String[] BLOCK_ELEMENTS = new String[] {
			"div",
			"p",
			"h1",
			"h2",
			"h3",
			"h4",
			"h5",
			"h6",
			"ol",
			"ul",
			"blockquote"
		};
	private static final HashSet<String> TAGS_TO_SKIP_SET;
	private static final HashSet<String> BLOCK_ELEMENTS_SET;
	
	static {
		TAGS_TO_SKIP_SET = toSet(TAGS_TO_SKIP);
		BLOCK_ELEMENTS_SET = toSet(BLOCK_ELEMENTS);
	}
	
	private static HashSet<String> toSet(final String[] strings) {
		final HashSet<String> result = new HashSet<>();
		for (final String str : strings)
			result.add(str);
		return result;
	}
	
	@Override
	public NodeAction action(final Node node, final Consumer<? super Token> tokenConsumer) {
		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE:
				return processElement(node, node.getNodeName(), tokenConsumer);
			case Node.CDATA_SECTION_NODE:
			case Node.TEXT_NODE:
				TextProcessor.processText(node.getNodeValue(), XhtmlNodeConverter::processCodePoint, tokenConsumer);
				return NodeAction.SKIP; // no child nodes anyway
			case Node.COMMENT_NODE:
				return NodeAction.SKIP;
			default:
				// this case never occurred in our data
				return NodeAction.SIMPLY_RECURSE;
		}
	}
	
	private NodeAction processElement(final Node node, final String nodeName, final Consumer<? super Token> tokenConsumer) {
		if (TAGS_TO_SKIP_SET.contains(nodeName))
			return NodeAction.SKIP;
		switch (nodeName) {
			case "br":
			case "tr":
				tokenConsumer.accept(Token.EXPLICIT_LINE_BREAK);
				return NodeAction.SIMPLY_RECURSE;
			case "img":
				ConversionUtil.putSkipNotification("[Bild]", tokenConsumer);
				return NodeAction.SKIP;
			case "a":
				if (XmlUtil.hasAttribute(node, "class", "pageref"))
					return NodeAction.SKIP;
				return NodeAction.SIMPLY_RECURSE;
			case "div":
			case "table":
				if (XmlUtil.hasAttribute(node, "class", "toc"))
					return NodeAction.SKIP;
				tokenConsumer.accept(Token.PARAGRAPH_BOUNDARY);
				return NodeAction.RECURSE_PARAGRAPH;
			case "span":
				if (XmlUtil.hasAttribute(node, "class", "footnote"))
					return ConversionUtil.putFootnote(tokenConsumer);
				return NodeAction.SIMPLY_RECURSE;
			case "td":
				tokenConsumer.accept(new Token(TokenType.WHITESPACE, "\t"));
				return NodeAction.SIMPLY_RECURSE;
			case "hr":
				tokenConsumer.accept(Token.PARAGRAPH_BOUNDARY);
				return NodeAction.SIMPLY_RECURSE;
			case "li":
				tokenConsumer.accept(Token.EXPLICIT_LINE_BREAK);
				return new NodeAction(ActionType.RECURSE, Token.EXPLICIT_LINE_BREAK);
			default:
				if (BLOCK_ELEMENTS_SET.contains(nodeName)) {
					tokenConsumer.accept(Token.PARAGRAPH_BOUNDARY);
					return NodeAction.RECURSE_PARAGRAPH;
				}
				return NodeAction.SIMPLY_RECURSE;
		}
	}
	
	private static void processCodePoint(final String text, final int start, final int end, final int codePoint, final StringBuilder textBuilder, final Consumer<? super Token> tokenConsumer) {
		switch (codePoint) {
			case 0xA:
			case 0xD:
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.WHITESPACE, text.substring(start, end)));
				break;
			case 0x17F: // ſ
				textBuilder.appendCodePoint('s');
				break;
			case 0xA4: // ¤
				textBuilder.appendCodePoint(0xF1);
				break;
			case 0x303: // ̃
				textBuilder.appendCodePoint(0x342);
				break;
			case 0x2CD: // ˍ
			case 0xA6: // ¦
			case 0xBF: // ¿
				break;
			case 0x2D: // -
				if (end == text.length()) {
					TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
					tokenConsumer.accept(new Token(TokenType.POSSIBLE_HYPHENATION, "-"));
					break;
				}
			default:
				processOtherCodePoint(text, start, end, codePoint, textBuilder, tokenConsumer);
				break;
		}
	}
	
	private static void processOtherCodePoint(final String text, final int start, final int end, final int codePoint, final StringBuilder textBuilder, final Consumer<? super Token> tokenConsumer) {
		switch (Character.getType(codePoint)) {
			case Character.SPACE_SEPARATOR:
			case Character.CONTROL:
				TextProcessor.flushTextBuilder(textBuilder, tokenConsumer);
				tokenConsumer.accept(new Token(TokenType.WHITESPACE, text.substring(start, end)));
				break;
			default:
				textBuilder.appendCodePoint(codePoint);
				break;
		}
	}
	
}
