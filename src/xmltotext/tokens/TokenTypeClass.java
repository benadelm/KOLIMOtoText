/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.tokens;

/**
 * Classes of token types, a further level of coarsening.
 */
public enum TokenTypeClass {
	
	/**
	 * The token type is some kind of line break.
	 */
	LINEBREAKS,
	
	/**
	 * The token type is some kind of whitespace
	 * (excluding line breaks).
	 */
	WHITESPACE,
	
	/**
	 * The token type is some kind of text
	 * (possibly with need for special treatment).
	 */
	TEXT
	
}
