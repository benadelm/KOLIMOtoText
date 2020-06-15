/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.normalization;

import java.util.ArrayList;

import xmltotext.tokens.Token;
import xmltotext.tokens.TokenType;

/**
 * A {@link TokenSequenceNormalizer} that implements some heuristics
 * to deal with hyphens at the end of lines.
 * <p>
 * In our data, line breaks after hyphens are usually not supposed to
 * be whitespace; instead, they represent line breaks
 * that were present in the original document for layout reasons
 * (typesetting), like:
 * </p>
 * <p>
 * <tt>that this is a far-<br/>fetched assumption</tt>
 * </p>
 * <p>
 * This class therefore discards
 * {@link TokenType#EXPLICIT_LINE_BREAK}
 * and
 * {@link TokenType#IMPLICIT_LINE_BREAK}
 * tokens that follow
 * {@link TokenType#POSSIBLE_HYPHENATION}
 * tokens.
 * </p>
 * <p>
 * While most of such hyphens are part of the spelling
 * (like in the example above: <i>far-fetched</i>),
 * there are some TEI documents in our data where they also appear
 * when there is a hyphenated word. For example, in the DTA version
 * of <i lang="de">Adam Mensch</i> by Hermann Conradi:
 * </p>
 * <p lang="de">
 * <tt>auf dem Ti&#x17F;che und den näch&#x17F;ten Stühlen
 * herum-&lt;lb/&gt;<br/>lagen, bückte &#x17F;ich nach einem Journal,
 * das ihm&lt;lb/&gt;</tt>
 * </p>
 * <p>
 * The constructor
 * {@link #ImplicitHyphensNormalizer(boolean)}
 * takes an argument to specify whether to deal with this or not.
 * To determine whether a
 * {@link TokenType#POSSIBLE_HYPHENATION}
 * token at the end of a line should be part of the output,
 * this class implements the following heuristic
 * (designed for our German data):
 * </p>
 * <ul>
 * <li>
 * If the first word in the next line starts with a capital letter,
 * the hyphen is preserved (<i lang="de">Cigaretten-Parf&uuml;m</i>,
 * <i lang="de">Bibel-Capitel</i>).
 * </li>
 * <li>
 * If the first (full) word in the next line is
 * &#x201C;<span lang="de">und</span>&#x201D;
 * or
 * &#x201C;<span lang="de">oder</span>&#x201D;,
 * the hyphen is preserved and whitespace is inserted after it
 * (<i lang="de">Wein- und Spielnacht</i>,
 * <i lang="de">gleich- oder mehrwerthigen</i>).
 * </li>
 * <li>
 * Otherwise, the hyphen is removed.
 * </li>
 * </ul>
 * <p>
 * Actually, the
 * <span lang="de">und</span>-<span lang="de">oder</span>-heuristic
 * is applied anyway.
 * </p>
 */
public class ImplicitHyphensNormalizer implements TokenSequenceNormalizer {
	
	private final boolean pNoHyphens;
	
	/**
	 * Initializes a new instance of this class.
	 * 
	 * @param noHyphens
	 * {@code true} to turn off the hyphenation heuristic
	 * and include every
	 * {@link TokenType#POSSIBLE_HYPHENATION}
	 * token in the output;
	 * {@code false} otherwise
	 */
	public ImplicitHyphensNormalizer(final boolean noHyphens) {
		pNoHyphens = noHyphens;
	}
	
	@Override
	public ArrayList<Token> normalizeTokenSequence(final ArrayList<Token> tokenSequence) {
		final ArrayList<Token> result = new ArrayList<>(tokenSequence.size());
		
		boolean afterLineBreak = false;
		Token pendingMinus = null;
		for (final Token token : tokenSequence) {
			switch (token.getType()) {
				case EXPLICIT_LINE_BREAK:
				case IMPLICIT_LINE_BREAK:
					afterLineBreak = true;
					if (pendingMinus != null)
						continue;
					break;
				case POSSIBLE_HYPHENATION:
					pendingMinus = token;
					afterLineBreak = false;
					continue;
				case TEXT:
					final String text = token.getText();
					if (afterLineBreak && (pendingMinus != null)) {
						// Trennzeichenheuristik
						if (startsWithUndOrOder(text)) {
							result.add(pendingMinus);
							result.add(new Token(TokenType.WHITESPACE, null));
						} else if (pNoHyphens || startsWithUppercase(text)) {
							result.add(pendingMinus);
						}
						pendingMinus = null;
						break;
					}
				default:
					if (pendingMinus != null) {
						result.add(pendingMinus);
						pendingMinus = null;
					}
					afterLineBreak = false;
					break;
			}
			result.add(token);
		}
		
		return result;
	}

	private static boolean startsWithUndOrOder(final String text) {
		return startsWithWord(text, "und") || startsWithWord(text, "oder");
	}

	private static boolean startsWithWord(final String text, final String word) {
		if (text.startsWith(word)) {
			if (hasFurtherCharacters(text, word.length()))
				return false;
			return true;
		}
		return false;
	}

	private static boolean hasFurtherCharacters(final String text, final int l) {
		return (text.length() > l) && Character.isLetter(text.codePointAt(l));
	}
	
	private static boolean startsWithUppercase(final String text) {
		final int firstCodePoint = text.codePointAt(0);
		if (Character.isUpperCase(firstCodePoint)) {
			if (Character.isUpperCase(text.codePointAt(Character.charCount(firstCodePoint))))
				return false;
			return true;
		}
		return false;
	}
	
}
