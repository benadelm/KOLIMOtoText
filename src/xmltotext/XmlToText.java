/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import xmltotext.normalization.TokenSequenceNormalization;
import xmltotext.tokens.ConversionTypes;
import xmltotext.tokens.Token;
import xmltotext.util.XmlHelper;

public class XmlToText {
	
	private static final TeiNodeConverter TEI_CONVERTER = new TeiNodeConverter();
	private static final XhtmlNodeConverter XHTML_CONVERTER = new XhtmlNodeConverter();
	
	public static void main(final String[] args) {
		if (args.length != 3) {
			System.err.println("expecting three arguments:");
			System.err.println("input directory");
			System.err.println("output directory");
			System.err.println("conversion type (\"tools\" or \"human\")");
			System.exit(1);
			return;
		}
		
		final int conversionType;
		switch (args[2]) {
			case "tools":
				conversionType = ConversionTypes.TOOLS;
				break;
			case "human":
				conversionType = ConversionTypes.HUMAN;
				break;
			default:
				System.err.print("Unsupported conversion type: ");
				System.err.println(args[2]);
				System.exit(1);
				return;
		}
		
		final FileSystem fs = FileSystems.getDefault();
		final Path inputDir = makePath(fs, args[0]);
		final Path outputDir = makePath(fs, args[1]);
		
		try (final DirectoryStream<Path> files = Files.newDirectoryStream(inputDir)) {
			for (final Path file : files)
				exportText(extractText(file, conversionType), outputDir.resolve(file.getFileName()));
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static Path makePath(final FileSystem fs, final String pathString) {
		return fs.getPath(pathString).toAbsolutePath().normalize();
	}
	
	private static String extractText(final Path xmlFile, final int conversionType) throws IOException {
		final Document document = loadDocument(xmlFile);
		final Element documentElement = document.getDocumentElement();
		
		final ArrayList<Token> tokens;
		switch (documentElement.getNodeName()) {
			case "TEI":
				tokens = TextExtraction.extractTokenSequence(documentElement, TEI_CONVERTER);
				break;
			case "html":
				tokens = TextExtraction.extractTokenSequence(documentElement, XHTML_CONVERTER);
				break;
			default:
				System.err.print("Cannot convert ");
				System.err.println(xmlFile.getFileName().toString());
				System.err.print("No converter for root element \"");
				System.err.print(documentElement.getNodeName());
				System.err.println('"');
				System.exit(2);
				return null;
		}
		
		filter(tokens, conversionType);
		return TokenSequenceNormalization.tokenSequenceToString(Normalization.normalize(tokens));
	}
	
	private static Document loadDocument(final Path xmlFile) throws IOException {
		try {
			return XmlHelper.load(xmlFile);
		} catch (final SAXException e) {
			System.err.print("XML exception processing file ");
			System.err.println(xmlFile.getFileName().toString());
			throw new RuntimeException(e);
		} catch (final IOException e) {
			System.err.print("IO exception processing file ");
			System.err.println(xmlFile.getFileName().toString());
			throw e;
		}
	}
	
	private static void filter(final ArrayList<Token> tokens, final int conversionType) {
		tokens.removeIf(token -> (token.getConversions() & conversionType) == 0);
	}
	
	private static void exportText(final String text, final Path outputFile) throws IOException {
		try (final BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			writer.write(text);
			writer.flush();
		} catch (final IOException e) {
			System.err.print("IO exception writing file ");
			System.err.println(outputFile.getFileName().toString());
			throw e;
		}
	}
	
}
