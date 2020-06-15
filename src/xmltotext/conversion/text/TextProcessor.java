/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.conversion.text;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.function.Consumer;

import xmltotext.tokens.Token;
import xmltotext.tokens.TokenType;

/**
 * Contains an implementation of
 * generic code-point-wise text processing.
 */
public class TextProcessor {
	
	/**
	 * Iterates over the Unicode code points in the given string,
	 * calling a {@link CodePointProcessor} for each code point.
	 * <p>
	 * This method also creates a {@link StringBuilder} which
	 * the {@link CodePointProcessor} can manipulate while
	 * processing the code points. If there are any characters
	 * left in the builder after processing the last code point,
	 * this method creates a
	 * {@link Token}
	 * with
	 * {@link TokenType#TEXT}
	 * and the contents of the {@link StringBuilder} as text,
	 * normalized to {@link Form#NFC NFC}.
	 * </p>
	 * 
	 * @param text
	 * the text to be processed;
	 * not {@code null}
	 * 
	 * @param codePointProcessor
	 * a {@link CodePointProcessor}
	 * to call for each code point of the text;
	 * not {@code null}
	 * 
	 * @param tokenConsumer
	 * a {@link Consumer} to be passed to the
	 * {@link CodePointProcessor}
	 * and to consume the text node possibly generated in the end;
	 * not {@code null}
	 */
	public static void processText(final String text, final CodePointProcessor codePointProcessor, final Consumer<? super Token> tokenConsumer) {
		final int n = text.length();
		final StringBuilder textBuilder = new StringBuilder();
		int start = 0;
		while (start < n) {
			final int codePoint = text.codePointAt(start);
			final int end = start + Character.charCount(codePoint);
			codePointProcessor.processCodePoint(text, start, end, codePoint, textBuilder, tokenConsumer);
			start = end;
		}
		if (textBuilder.length() > 0)
			addTextToken(textBuilder, tokenConsumer);
	}
	
	/**
	 * Creates a
	 * {@link Token}
	 * with
	 * {@link TokenType#TEXT}
	 * and the contents of a {@link StringBuilder} as text,
	 * normalized to {@link Form#NFC NFC},
	 * and clears the {@link StringBuilder}.
	 * 
	 * @param textBuilder
	 * the {@link StringBuilder} whose contents to use as token text;
	 * will be cleared by this method;
	 * not {@code null}
	 * 
	 * @param tokenConsumer
	 * a {@link Consumer} to consume the newly created text node
	 */
	public static void flushTextBuilder(final StringBuilder textBuilder, final Consumer<? super Token> tokenConsumer) {
		addTextToken(textBuilder, tokenConsumer);
		textBuilder.setLength(0);
	}

	private static void addTextToken(final StringBuilder textBuilder, final Consumer<? super Token> tokenConsumer) {
		tokenConsumer.accept(new Token(TokenType.TEXT, Normalizer.normalize(textBuilder, Form.NFC)));
	}
	
}
