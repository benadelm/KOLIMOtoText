/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.conversion.text;

import java.util.function.Consumer;

import xmltotext.tokens.Token;

/**
 * Can process Unicode code points, one at a time.
 * <p>
 * Typically implementations of this interface provide
 * special processing of certain code points
 * (such as line breaks or other special characters)
 * depending on the format being converted.
 * </p>
 */
@FunctionalInterface
public interface CodePointProcessor {
	
	/**
	 * Processes a single Unicode code point in a text {@link String}.
	 * <p>
	 * The code point may be appended to a {@link StringBuilder},
	 * result in the creation of a {@link Token}
	 * that is consumed by a {@link Consumer},
	 * or it may be ignored.
	 * </p>
	 * 
	 * @param text
	 * the text {@link String} in which the code point occurs,
	 * not {@code null}
	 * 
	 * @param start
	 * the index of the code point in the text
	 * 
	 * @param end
	 * the index of the next code point in the text
	 * (or the {@link String#length() length} of the text
	 * if the current code point is its last code point)
	 * 
	 * @param codePoint
	 * the Unicode code point (as returned by methods such as
	 * {@link String#codePointAt(int)})
	 * 
	 * @param textBuilder
	 * a {@link StringBuilder} that may be modified
	 * depending on the code point
	 * (for example, the code point may be appended
	 * to it using
	 * {@link StringBuilder#appendCodePoint(int)});
	 * not {@code null}
	 * 
	 * @param tokenConsumer
	 * a {@link Consumer} that can consume tokens generated
	 * depending on the code point;
	 * not {@code null}
	 */
	void processCodePoint(String text, int start, int end, int codePoint, StringBuilder textBuilder, Consumer<? super Token> tokenConsumer);
	
}
