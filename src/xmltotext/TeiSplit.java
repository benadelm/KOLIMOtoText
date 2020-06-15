/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext;

import org.w3c.dom.Node;

public class TeiSplit {
	
	private final String pHeading;
	private final Node pSubtreeRoot;
	
	public TeiSplit(final String heading, final Node subtreeRoot) {
		pHeading = heading;
		pSubtreeRoot = subtreeRoot;
	}
	
	public String getHeading() {
		return pHeading;
	}
	
	public Node getSubtreeRoot() {
		return pSubtreeRoot;
	}
	
}
