/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.conversion;

import java.util.function.Consumer;

import org.w3c.dom.Node;

import xmltotext.tokens.Token;

/**
 * Can convert single XML nodes to
 * {@link Token}
 * instances for the conversion to plain text.
 */
public interface NodeConverter {
	
	/**
	 * Processes an XML node, generating tokens for the conversion
	 * to plain text, and returns a
	 * {@link NodeAction} specifying how to proceed with this node.
	 * <p>
	 * The processing is shallow, the tree below the node is not
	 * processed. However, depending on the return value of this
	 * method, it may be called again for the nodes in that tree
	 * (see {@link NodeAction}).
	 * </p>
	 * <p>
	 * This method can generate an arbitrary number of tokens
	 * and calls {@link Consumer#accept(Object)} in order
	 * for each {@link Token} to be appended to the output.
	 * </p>
	 * 
	 * @param node
	 * the {@link Node} to be processed;
	 * not {@code null}
	 * 
	 * @param tokenConsumer
	 * a {@link Consumer} to consume tokens
	 * generated for this node;
	 * not {@code null}
	 * 
	 * @return
	 * a {@link NodeAction} specifying how to proceed with this node;
	 * not {@code null}
	 * 
	 * @see NodeAction
	 */
	NodeAction action(Node node, Consumer<? super Token> tokenConsumer);
	
}
