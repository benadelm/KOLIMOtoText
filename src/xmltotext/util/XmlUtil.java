/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Contains some common operations in connection with XML and DOM.
 */
public class XmlUtil {
	
	/**
	 * Checks whether an XML node has an attribute
	 * with the specified name and value.
	 * <p>
	 * This method returns {@code true} if and only if the XML node
	 * has an attribute with the specified name
	 * (as found by {@link NamedNodeMap#getNamedItem(String)})
	 * and the value of that attribute
	 * {@link String#equals(Object) equals}
	 * the specified value.
	 * </p>
	 * <p>
	 * For example, if {@code node} is the {@link Node} representation
	 * of the XML element
	 * {@code <foo bar="baz" />}
	 * then
	 * {@code hasAttribute(node, "bar", "baz")}
	 * will return {@code true}, but
	 * {@code hasAttribute(node, "foo", "bar")}
	 * or
	 * {@code hasAttribute(node, "bar", "foo")}
	 * will return {@code false}.
	 * </p>
	 * <p>
	 * This method will also return {@code false}
	 * when called with an XML node that does not have attributes
	 * (that is, where calls to
	 * {@link Node#getAttributes()}
	 * return {@code null}).
	 * </p>
	 * 
	 * @param node
	 * the {@link Node} to be checked for the attribute;
	 * not {@code null}
	 * 
	 * @param attributeName
	 * the name of the attribute to check for;
	 * not {@code null}
	 * 
	 * @param attributeValue
	 * the expected value of the attribute;
	 * not {@code null}
	 * 
	 * @return
	 * {@code true} if the XML node has an attribute
	 * with the specified name
	 * and the value of that attribute is the specified value;
	 * otherwise {@code false}
	 */
	public static boolean hasAttribute(final Node node, final String attributeName, final String attributeValue) {
		final NamedNodeMap attributes = node.getAttributes();
		if (attributes == null)
			return false;
		final Node attribute = attributes.getNamedItem(attributeName);
		return (attribute != null) && attributeValue.equals(attribute.getNodeValue());
	}
	
}
