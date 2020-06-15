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
 * A {@link TokenSequenceNormalizer} that merges words
 * explicitly hyphenated with a {@link TokenType#HYPHENATION} token.
 * <p>
 * All {@link TokenType#HYPHENATION}
 * tokens and all immediately subsequent
 * {@link TokenType#WHITESPACE},
 * {@link TokenType#IMPLICIT_LINE_BREAK}
 * and {@link TokenType#EXPLICIT_LINE_BREAK}
 * tokens (until, not including, the first token of any other type)
 * are discarded.
 * </p>
 */
public class ExplicitHyphensNormalizer implements TokenSequenceNormalizer {
	
	@Override
	public ArrayList<Token> normalizeTokenSequence(final ArrayList<Token> tokenSequence) {
		final ArrayList<Token> result = new ArrayList<>(tokenSequence.size());
		
		boolean afterSeparatedWord = false;
		for (final Token token : tokenSequence) {
			switch (token.getType()) {
				case EXPLICIT_LINE_BREAK:
				case IMPLICIT_LINE_BREAK:
				case WHITESPACE:
					if (afterSeparatedWord)
						continue;
					break;
				case HYPHENATION:
					afterSeparatedWord = true;
					continue;
				default:
					afterSeparatedWord = false;
			}
			result.add(token);
		}
		
		return result;
	}
	
}
