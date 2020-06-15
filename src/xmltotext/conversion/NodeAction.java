/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.conversion;

import xmltotext.tokens.Token;

/**
 * Represents instructions for the generic converter
 * on what to do with an XML node.
 * <p>
 * The instructions consist of the primary action
 * ({@link ActionType})
 * and optionally a
 * {@link Token},
 * called the <i>postponed token</i>,
 * to be appended to the output after processing the XML tree
 * below the node.
 * </p>
 * <p>
 * This class also contains some static constants
 * with typical node actions
 * that need not be instantiated over and over again.
 * </p>
 */
public class NodeAction {
	
	/**
	 * Skip any tree below this node.
	 * Do not append any {@link Token} afterwards.
	 */
	public static final NodeAction SKIP = new NodeAction(ActionType.SKIP, null);
	
	/**
	 * Process the tree below this node.
	 * Do not append any {@link Token} afterwards.
	 */
	public static final NodeAction SIMPLY_RECURSE = new NodeAction(ActionType.RECURSE, null);
	
	/**
	 * Process the tree below this node.
	 * Insert a {@link Token#PARAGRAPH_BOUNDARY} afterwards.
	 */
	public static final NodeAction RECURSE_PARAGRAPH = new NodeAction(ActionType.RECURSE, Token.PARAGRAPH_BOUNDARY);
	
	private final ActionType pType;
	private final Token pPostponedToken;
	
	/**
	 * Initializes a new instance of this class.
	 * 
	 * @param type
	 * the primary action;
	 * not {@code null}
	 * 
	 * @param postponedToken
	 * a {@link Token} to be appended to the output after processing
	 * the XML tree below the node;
	 * or {@code null} if there is no such {@link Token}
	 */
	public NodeAction(final ActionType type, final Token postponedToken) {
		pType = type;
		pPostponedToken = postponedToken;
	}
	
	/**
	 * Returns the primary action.
	 * 
	 * @return
	 * the primary action;
	 * not {@code null}
	 */
	public ActionType getType() {
		return pType;
	}
	
	/**
	 * Returns the {@link Token} to be appended to the output
	 * after processing the XML tree below the node.
	 * 
	 * @return
	 * a {@link Token} to be appended to the output after processing
	 * the XML tree below the node;
	 * or {@code null} if there is none
	 */
	public Token getPostponedToken() {
		return pPostponedToken;
	}
	
}
