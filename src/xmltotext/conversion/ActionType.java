/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.conversion;

/**
 * Possible actions that the generic converter can take
 * upon encountering an XML node.
 *
 */
public enum ActionType {
	
	/**
	 * Skip the node. Continue with the next sibling.
	 */
	SKIP,
	
	/**
	 * Recurse into the tree below this node.
	 * Process the child nodes of this node
	 * before processing its next sibling.
	 */
	RECURSE
}
