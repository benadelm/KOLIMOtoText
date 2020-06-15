/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.tokens;

/**
 * Basic types of output material tokens.
 * <p>
 * Every token type has an associated
 * {@link TokenTypeClass}
 * that provides a further level of coarsening
 * and can be retrieved using
 * {@link #getTokenTypeClass()}.
 * </p>
 * <p>
 * Some token types may be not be treated differently
 * in the current implementation of the converter,
 * but represent distinctions in the input
 * that could be relevant to (some steps of) the conversion.
 * </p>
 */
public enum TokenType {
	
	// ********
	// line/page breaks
	// ********
	
	/**
	 * A line break explicitly expressed by markup
	 * such as {@code <lb/>} in TEI or {@code <br/>} in XHTML.
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#LINEBREAKS}.
	 * </p>
	 */
	EXPLICIT_LINE_BREAK(TokenTypeClass.LINEBREAKS),
	
	/**
	 * A line break in the document text
	 * (such as U+000A LINE FEED).
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#LINEBREAKS}.
	 * </p>
	 */
	IMPLICIT_LINE_BREAK(TokenTypeClass.LINEBREAKS),
	
	/**
	 * A page break such as {@code <pb/>} in TEI.
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#LINEBREAKS}.
	 * </p>
	 */
	PAGE_BREAK(TokenTypeClass.LINEBREAKS),
	
	/**
	 * The boundary between paragraphs
	 * such as {@code <p>...</p>} in TEI or XHTML.
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#LINEBREAKS}.
	 * </p>
	 */
	PARAGRAPH_BOUNDARY(TokenTypeClass.LINEBREAKS),
	
	// ********
	// text and (intra-line) whitespace
	// ********
	
	/**
	 * Syllabification hyphenation explicitly expressed by markup.
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#TEXT}.
	 * </p>
	 * 
	 * @see
	 * #POSSIBLE_HYPHENATION
	 */
	HYPHENATION(TokenTypeClass.TEXT),
	
	/**
	 * Parts of the document text
	 * (usually, hyphens at the end of a line)
	 * that possibly represent syllabification hyphenation.
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#TEXT}.
	 * </p>
	 * 
	 * @see
	 * #HYPHENATION
	 */
	POSSIBLE_HYPHENATION(TokenTypeClass.TEXT),
	
	/**
	 * Whitespace inside text lines,
	 * such as spaces (U+0020 SPACE).
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#WHITESPACE}.
	 * </p>
	 */
	WHITESPACE(TokenTypeClass.WHITESPACE),
	
	/**
	 * Text with no necessity of special treatment during conversion.
	 * <p>
	 * This token type has
	 * {@link TokenTypeClass#TEXT}.
	 * </p>
	 */
	TEXT(TokenTypeClass.TEXT);
	
	private final TokenTypeClass pTokenTypeClass;
	
	private TokenType(final TokenTypeClass tokenTypeClass) {
		pTokenTypeClass = tokenTypeClass;
	}
	
	/**
	 * Returns the
	 * {@link TokenTypeClass}
	 * associated with this token type.
	 * 
	 * @return
	 * the
	 * {@link TokenTypeClass}
	 * associated with this token type;
	 * not {@code null}
	 */
	public TokenTypeClass getTokenTypeClass() {
		return pTokenTypeClass;
	}
}
