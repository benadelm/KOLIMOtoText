/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Encapsulates the usual Java bureaucracy for loading XML files.
 */
public class XmlHelper {
	
	private static final DocumentBuilder DOCUMENT_BUILDER;
	
	static {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DOCUMENT_BUILDER = dbf.newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Loads an XML file into a DOM {@link Document}.
	 * 
	 * @param xmlFile
	 * (a {@link Path} locating) the XML file;
	 * not {@code null}
	 * 
	 * @return
	 * a {@link Document} representation of the XML file contents;
	 * not {@code null}
	 * 
	 * @throws SAXException
	 * if any parse error occurs
	 * 
	 * @throws IOException
	 * if any IO error occurs
	 */
	public static Document load(final Path xmlFile) throws SAXException, IOException {
		try (final InputStream inputStream = Files.newInputStream(xmlFile, StandardOpenOption.READ)) {
			return DOCUMENT_BUILDER.parse(inputStream);
		}
	}
	
}
