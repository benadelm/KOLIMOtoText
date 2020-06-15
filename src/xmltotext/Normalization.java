/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext;

import java.util.ArrayList;

import xmltotext.normalization.EllipsisCharacterNormalizer;
import xmltotext.normalization.ExplicitHyphensNormalizer;
import xmltotext.normalization.ImplicitHyphensNormalizer;
import xmltotext.normalization.TokenSequenceNormalization;
import xmltotext.normalization.TokenSequenceNormalizer;
import xmltotext.tokens.Token;
import xmltotext.tokens.TokenType;

/**
 * Implements the normalization logic.
 * <p>
 * This class is mainly a wrapper around
 * {@link TokenSequenceNormalization}
 * with a certain set of
 * {@link TokenSequenceNormalizer}
 * implementations and a decision when to apply which:
 * </p>
 * <ul>
 * <li>
 * If the token sequence to normalize contains any
 * {@link TokenType#HYPHENATION}
 * token, then
 * <ol>
 * <li>
 * an
 * {@link ExplicitHyphensNormalizer},
 * </li>
 * <li>
 * an
 * {@link ImplicitHyphensNormalizer}
 * with hyphenation heuristic turned <b>off</b>
 * </li>
 * <li>
 * and an
 * {@link EllipsisCharacterNormalizer}
 * </li>
 * </ol>
 * are applied (in this order);
 * </li>
 * <li>
 * otherwise,
 * <ol>
 * <li>
 * an
 * {@link ImplicitHyphensNormalizer}
 * with hyphenation heuristic turned <b>on</b>
 * </li>
 * <li>
 * and an
 * {@link EllipsisCharacterNormalizer}
 * </li>
 * </ol>
 * are applied (in this order).
 * </li>
 * </ul>
 */
public class Normalization {
	
	private static final ArrayList<TokenSequenceNormalizer> NORMALIZERS_IF_EXPLICIT_HYPHENS;
	private static final ArrayList<TokenSequenceNormalizer> NORMALIZERS_IF_NO_EXPLICIT_HYPHENS;
	
	static {
		NORMALIZERS_IF_EXPLICIT_HYPHENS = new ArrayList<>();
		NORMALIZERS_IF_NO_EXPLICIT_HYPHENS = new ArrayList<>();
		
		final EllipsisCharacterNormalizer ellipsisCharacterNormalizer = new EllipsisCharacterNormalizer();
		
		NORMALIZERS_IF_EXPLICIT_HYPHENS.add(new ExplicitHyphensNormalizer());
		NORMALIZERS_IF_EXPLICIT_HYPHENS.add(new ImplicitHyphensNormalizer(true));
		NORMALIZERS_IF_EXPLICIT_HYPHENS.add(ellipsisCharacterNormalizer);
		
		NORMALIZERS_IF_NO_EXPLICIT_HYPHENS.add(new ImplicitHyphensNormalizer(false));
		NORMALIZERS_IF_NO_EXPLICIT_HYPHENS.add(ellipsisCharacterNormalizer);
	}
	
	/**
	 * Normalizes a token sequence
	 * according to the normalization logic
	 * implemented by this class.
	 * 
	 * @param tokenSequence
	 * the token sequence to be normalized;
	 * neither this {@link ArrayList}
	 * nor any element in it may be {@code null}
	 * 
	 * @return
	 * the normalized token sequence;
	 * not {@code null}, and no element in it will be {@code null}
	 */
	public static ArrayList<Token> normalize(final ArrayList<Token> tokenSequence) {
		final ArrayList<TokenSequenceNormalizer> normalizers;
		if (containsExplicitHyphens(tokenSequence))
			normalizers = NORMALIZERS_IF_EXPLICIT_HYPHENS;
		else
			normalizers = NORMALIZERS_IF_NO_EXPLICIT_HYPHENS;
		
		return TokenSequenceNormalization.normalizeTokenSequence(tokenSequence, normalizers);
	}
	
	private static boolean containsExplicitHyphens(final ArrayList<Token> tokens) {
		return tokens.stream().map(Token::getType).filter(t -> t == TokenType.HYPHENATION).findAny().isPresent();
	}
	
}
