/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import xmltotext.conversion.NodeAction;
import xmltotext.conversion.NodeConverter;
import xmltotext.tokens.Token;

/**
 * Implements the core process of extracting text from XML files:
 * converting an XML {@link Document} into a {@link Token} sequence.
 */
public class TextExtraction {
	
	/**
	 * Converts an XML {@link Document} into a {@link Token} sequence
	 * using the specified {@link NodeConverter}.
	 * <p>
	 * A call to this method is equivalent to a call to
	 * {@link #extractTokenSequence(Node, NodeConverter)}
	 * with the root element of the document (as returned by
	 * {@link Document#getDocumentElement()}).
	 * </p>
	 * 
	 * @param document
	 * the XML {@link Document} to be converted;
	 * not {@code null}
	 * 
	 * @param nodeConverter
	 * the {@link NodeConverter} to be used for processing nodes;
	 * not {@code null}
	 * 
	 * @return
	 * an {@link ArrayList} of {@link Token} instances;
	 * not {@code null};
	 * unless the {@link NodeConverter} generates
	 * a {@link Token} that is {@code null},
	 * no element of the list will be {@code null}.
	 */
	public static ArrayList<Token> extractTokenSequence(final Document document, final NodeConverter nodeConverter) {
		return extractTokenSequence(document.getDocumentElement(), nodeConverter);
	}
	
	/**
	 * Converts an XML {@link Node} and the tree below it
	 * into a {@link Token} sequence
	 * using the specified {@link NodeConverter}.
	 * <p>
	 * This method calls
	 * {@link NodeConverter#action(Node, Consumer)}
	 * for the nodes of the tree in the order
	 * in which they would appear in their XML representation,
	 * skipping the trees below nodes for which
	 * {@link NodeConverter#action(Node, Consumer)}
	 * returns
	 * {@link NodeAction#SKIP}.
	 * </p>
	 * <p>
	 * For example, when called with a {@link Node}
	 * representing the XML
	 * {@code <a><b><c/><d/></b><e/></a>},
	 * {@link NodeConverter#action(Node, Consumer)}
	 * is first called for {@code a}.
	 * If the call returns
	 * {@link NodeAction#SKIP},
	 * this method returns; otherwise,
	 * {@link NodeConverter#action(Node, Consumer)}
	 * is called for {@code b}.
	 * If it then returns
	 * {@link NodeAction#SKIP},
	 * the tree below {@code b} is skipped and the next call to
	 * {@link NodeConverter#action(Node, Consumer)}
	 * is for {@code e}. Otherwise,
	 * {@link NodeConverter#action(Node, Consumer)}
	 * is called for {@code c} and then for {@code d},
	 * and finally for {@code e}.
	 * </p>
	 * 
	 * @param subtreeRoot
	 * the root of the XML tree to be converted;
	 * if {@code null}, a new empty list will be returned
	 * 
	 * @param nodeConverter
	 * the {@link NodeConverter} to be used for processing nodes;
	 * not {@code null}
	 * 
	 * @return
	 * an {@link ArrayList} of {@link Token} instances;
	 * not {@code null};
	 * unless the {@link NodeConverter} generates
	 * a {@link Token} that is {@code null},
	 * no element of the list will be {@code null}.
	 */
	public static ArrayList<Token> extractTokenSequence(Node subtreeRoot, final NodeConverter nodeConverter) {
		final ArrayList<Token> tokens = new ArrayList<>();
		final Consumer<Token> tokenConsumer = tokens::add;
		
		final ArrayDeque<Stackframe> stack = new ArrayDeque<>();
		while (true) {
			if (subtreeRoot != null) {
				final NodeAction action = nodeConverter.action(subtreeRoot, tokenConsumer);
				final Token postponedToken = action.getPostponedToken();
				switch (action.getType()) {
					case SKIP:
						if (postponedToken != null)
							tokens.add(postponedToken);
						break;
					case RECURSE:
						if (postponedToken != null)
							stack.push(new Stackframe(null, postponedToken));
						Node child = subtreeRoot.getLastChild();
						while (child != null) {
							stack.push(new Stackframe(child, null));
							child = child.getPreviousSibling();
						}
						break;
				}
			}
			
			if (stack.isEmpty())
				break;
			final Stackframe next = stack.pop();
			subtreeRoot = next.node;
			if (next.postponedToken != null)
				tokens.add(next.postponedToken);
		}
		
		return tokens;
	}
	
	private static class Stackframe {
		public final Node node;
		public final Token postponedToken;
		
		public Stackframe(final Node node, final Token postponedToken) {
			this.node = node;
			this.postponedToken = postponedToken;
		}
	}
	
}
