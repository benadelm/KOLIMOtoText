/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.tokens;

/**
 * An output material token.
 * A token consists of its basic type
 * ({@link TokenType}),
 * the conversion types whose output it is supposed to appear in
 * (see {@link ConversionTypes}),
 * and optionally a text {@link String}.
 * <p>
 * This class also contains some static constants
 * with typical tokens
 * that need not be instantiated over and over again.
 * </p>
 */
public class Token {
	
	/**
	 * An explicit line break
	 * such as {@code <lb/>} in TEI or {@code <br/>} in XHTML.
	 * <p>
	 * This token has {@link TokenType#EXPLICIT_LINE_BREAK},
	 * {@link ConversionTypes#ALL}
	 * and no text.
	 * </p>
	 */
	public static final Token EXPLICIT_LINE_BREAK = new Token(TokenType.EXPLICIT_LINE_BREAK, null);
	
	/**
	 * An explicit page break such as {@code <pb/>} in TEI.
	 * <p>
	 * This token has {@link TokenType#PAGE_BREAK},
	 * {@link ConversionTypes#ALL}
	 * and no text.
	 * </p>
	 */
	public static final Token PAGE_BREAK = new Token(TokenType.PAGE_BREAK, null);
	
	/**
	 * A token to mark the boundaries of paragraphs
	 * such as {@code <p>...</p>} in TEI or XHTML.
	 * <p>
	 * This token has {@link TokenType#PARAGRAPH_BOUNDARY},
	 * {@link ConversionTypes#ALL}
	 * and no text.
	 * </p>
	 */
	public static final Token PARAGRAPH_BOUNDARY = new Token(TokenType.PARAGRAPH_BOUNDARY, null);
	
	/**
	 * Whitespace to be included only in conversions
	 * to a textual representation for use by humans.
	 * <p>
	 * This token has {@link TokenType#WHITESPACE},
	 * {@link ConversionTypes#HUMAN}
	 * and no text.
	 * </p>
	 */
	public static final Token HUMAN_ONLY_WHITESPACE = new Token(TokenType.WHITESPACE, null, ConversionTypes.HUMAN);
	
	private final TokenType pType;
	private final String pText;
	private final int pConversions;
	
	/**
	 * Initializes a new instance of this class.
	 * This constructor has the same effect as
	 * {@link #Token(TokenType, String, int)}
	 * with {@link ConversionTypes#ALL}.
	 * 
	 * @param type
	 * the {@link TokenType} of the token;
	 * not {@code null}
	 * 
	 * @param text
	 * the text {@link String} of the token
	 * or {@code null} to create a token without text
	 * 
	 */
	public Token(final TokenType type, final String text) {
		this(type, text, ConversionTypes.ALL);
	}
	
	/**
	 * Initializes a new instance of this class.
	 * 
	 * @param type
	 * the {@link TokenType} of the token;
	 * not {@code null}
	 * 
	 * @param text
	 * the text {@link String} of the token
	 * or {@code null} to create a token without text
	 * 
	 * @param conversions
	 * the conversion types whose output
	 * the token is supposed to appear in
	 */
	public Token(final TokenType type, final String text, final int conversions) {
		pType = type;
		pText = text;
		pConversions = conversions;
	}
	
	/**
	 * Returns the basic type of this token.
	 * 
	 * @return
	 * the {@link TokenType} of this token;
	 * not {@code null}
	 */
	public TokenType getType() {
		return pType;
	}
	
	/**
	 * Returns the text of this token.
	 * 
	 * @return
	 * the text {@link String} of this token
	 * or {@code null} if there is none
	 */
	public String getText() {
		return pText;
	}
	
	/**
	 * Returns the conversion types
	 * whose output the token is supposed to appear in.
	 * 
	 * @return
	 * the conversion types whose output
	 * the token is supposed to appear in
	 * 
	 * @see
	 * ConversionTypes
	 */
	public int getConversions() {
		return pConversions;
	}
	
}
