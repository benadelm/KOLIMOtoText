/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.normalization;

import java.util.ArrayList;

import xmltotext.tokens.Token;

/**
 * Can normalize token sequences
 * (with respect to some normalization criterion).
 */
public interface TokenSequenceNormalizer {
	
	/**
	 * Normalizes a token sequence.
	 * 
	 * @param tokenSequence
	 * the token sequence to be normalized;
	 * neither this {@link ArrayList}
	 * nor any element in it may be {@code null}
	 * 
	 * @return
	 * the normalized token sequence;
	 * not {@code null},
	 * and no element in it will be {@code null}
	 */
	ArrayList<Token> normalizeTokenSequence(ArrayList<Token> tokenSequence);
	
}
