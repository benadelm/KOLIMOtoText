/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import xmltotext.util.XmlHelper;

public class TeiSplitter {
	
	// diese Klasse ggf. als Schnittstelle
	// mit Implementationen für verschiedene Split-Anforderungen
	
	public static ArrayList<TeiSplit> split(final Path teiFile) throws IOException, SAXException {
		return split(XmlHelper.load(teiFile));
	}
	
	public static ArrayList<TeiSplit> split(final Document teiDocument) {
		final ArrayList<TeiSplit> result = new ArrayList<>();
		
		final Node tei = teiDocument.getDocumentElement();
		Node teiChild = tei.getFirstChild();
		while (teiChild != null) {
			if ((teiChild.getNodeType() == Node.ELEMENT_NODE) && "text".equals(teiChild.getNodeName()))
				splitText(teiChild, result);
			teiChild = teiChild.getNextSibling();
		}
		
		return result;
	}
	
	private static void splitText(final Node textNode, final ArrayList<TeiSplit> result) {
		Node textChild = textNode.getFirstChild();
		while (textChild != null) {
			if ((textChild.getNodeType() == Node.ELEMENT_NODE) && "body".equals(textChild.getNodeName()))
				splitBody(textChild, result);
			textChild = textChild.getNextSibling();
		}
	}
	
	private static void splitBody(final Node bodyNode, final ArrayList<TeiSplit> result) {
		Node bodyChild = bodyNode.getFirstChild();
		while (bodyChild != null) {
			if ((bodyChild.getNodeType() == Node.ELEMENT_NODE) && "div".equals(bodyChild.getNodeName()))
				result.add(new TeiSplit(findHeading(bodyChild), bodyChild));
			bodyChild = bodyChild.getNextSibling();
		}
	}
	
	private static String findHeading(final Node divNode) {
		Node divChild = divNode.getFirstChild();
		while (divChild != null) {
			if ((divChild.getNodeType() == Node.ELEMENT_NODE) && "head".equals(divChild.getNodeName()))
				return divChild.getTextContent().trim();
			divChild = divChild.getNextSibling();
		}
		return null;
	}
	
}
