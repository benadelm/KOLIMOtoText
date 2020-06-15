/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.normalization;

import java.util.ArrayList;

import xmltotext.tokens.Token;
import xmltotext.tokens.TokenType;
import xmltotext.tokens.TokenTypeClass;

/**
 * Contains an implementation of logic to normalize token sequences,
 * which essentially means collapsing line breaks and whitespace.
 * This class also contains a method to convert a sequence of tokens
 * into a {@link String}, dealing with special tokens such as
 * line breaks (including paragraph boundaries) or whitespace.
 */
public class TokenSequenceNormalization {
	
	/**
	 * Normalizes a token sequence.
	 * <p>
	 * The token sequence is normalized by calling the
	 * {@link TokenSequenceNormalizer#normalizeTokenSequence(ArrayList)}
	 * methods of the provided
	 * {@link TokenSequenceNormalizer}
	 * instances in the order in which they are returned by the
	 * {@link Iterable}.
	 * </p>
	 * <p>
	 * Between the calls to the normalizers as well as before the first
	 * and after the last call to a normalizer,
	 * the token sequence is collapsed by
	 * <ol>
	 * <li>
	 * merging all adjacent tokens with a
	 * {@link TokenType}
	 * of the same
	 * {@link TokenTypeClass}
	 * (excluding
	 * {@link TokenTypeClass#TEXT}:
	 * tokens of this type class are not merged);
	 * </li>
	 * <li>
	 * removing tokens with
	 * {@link TokenTypeClass#WHITESPACE}
	 * that are adjacent to tokens with
	 * {@link TokenTypeClass#LINEBREAKS}
	 * (or are at the start or end of the sequence);
	 * </li>
	 * <li>
	 * removing tokens with
	 * {@link TokenTypeClass#LINEBREAKS}
	 * that are at the start or end of the sequence.
	 * </li>
	 * </ol>
	 * </p>
	 * <p>
	 * When merging adjacent tokens with a
	 * {@link TokenType}
	 * of the same
	 * {@link TokenTypeClass},
	 * the
	 * {@link TokenType}
	 * and the text of the merged token are subject to the following
	 * precedence rules:
	 * </p>
	 * <ul>
	 * <li>
	 * For
	 * {@link TokenTypeClass#WHITESPACE}:
	 * <ul>
	 * <li>
	 * If any of the merged tokens has the text {@code "\t"}
	 * (a single U+0009 CHARACTER TABULATION character),
	 * then the merged token has the text {@code "\t"}, too.
	 * </li>
	 * <li>
	 * Otherwise, the text of the first token is used
	 * (for example, if tokens {@code t1}, {@code t2} and {@code t3}
	 * appear in this order in the sequence and are merged,
	 * the text of {@code t1} is used).
	 * </li>
	 * </ul>
	 * </li>
	 * <li>
	 * For
	 * {@link TokenTypeClass#LINEBREAKS}:
	 * <ul>
	 * <li>
	 * If any of the merged tokens is a
	 * {@link TokenType#PARAGRAPH_BOUNDARY},
	 * the resulting token is a
	 * {@link TokenType#PARAGRAPH_BOUNDARY},
	 * too.
	 * </li>
	 * <li>
	 * Otherwise, if any of the merged tokens is an
	 * {@link TokenType#EXPLICIT_LINE_BREAK},
	 * the resulting token is an
	 * {@link TokenType#EXPLICIT_LINE_BREAK},
	 * too.
	 * </li>
	 * <li>
	 * Otherwise, the resulting token is an
	 * {@link TokenType#IMPLICIT_LINE_BREAK}
	 * (even in the case that every merged token is a
	 * {@link TokenType#PAGE_BREAK}).
	 * </li>
	 * <li>
	 * Token texts are ignored.
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param tokenSequence
	 * the token sequence to be normalized;
	 * neither this {@link ArrayList}
	 * nor any element in it may be {@code null}
	 * 
	 * @param normalizers
	 * the normalizers to apply (in order);
	 * neither this {@link Iterable}
	 * nor any element in it may be {@code null}
	 * 
	 * @return
	 * the normalized token sequence;
	 * not {@code null},
	 * and no element in it will be {@code null}
	 */
	public static ArrayList<Token> normalizeTokenSequence(ArrayList<Token> tokenSequence, final Iterable<? extends TokenSequenceNormalizer> normalizers) {
		for (final TokenSequenceNormalizer normalizer : normalizers)
			tokenSequence = normalizer.normalizeTokenSequence(collapseTokenSequence(tokenSequence));
		return collapseTokenSequence(tokenSequence);
	}
	
	private static ArrayList<Token> collapseTokenSequence(final ArrayList<Token> tokenSequence) {
		return removeBoundaryLinebreaks(removeBoundarySpaces(collapseRuns(tokenSequence)));
	}
	
	private static ArrayList<Token> collapseRuns(final ArrayList<Token> tokenSequence) {
		final ArrayList<Token> result = new ArrayList<>(tokenSequence.size());
		
		Run run = null;
		for (final Token token : tokenSequence) {
			final TokenType tokenType = token.getType();
			final TokenTypeClass tokenTypeClass = tokenType.getTokenTypeClass();;
			
			if ((run != null) && (run.runClass != tokenTypeClass)) {
				appendRun(run, result);
				run = null;
			}
			
			if (tokenTypeClass == TokenTypeClass.TEXT) {
				final String text = token.getText();
				if ((text != null) && "".equals(text))
					continue;
				result.add(token);
				continue;
			}
			
			if (run == null)
				run = new Run(tokenType, tokenTypeClass, token.getText(), token.getConversions());
			
			switch (run.runClass) {
				case LINEBREAKS:
					lineBreakPrecedence(run, token, tokenType);
					break;
				case WHITESPACE:
					whitespacePrecedence(run, token, tokenType);
					break;
				default:
			}
		}
		
		if (run != null)
			appendRun(run, result);
		
		return result;
	}
	
	private static void lineBreakPrecedence(final Run run, final Token token, final TokenType tokenType) {
		run.runType = lineBreakPrecedence(run.runType, tokenType);
	}
	
	private static TokenType lineBreakPrecedence(final TokenType runType, final TokenType tokenType) {
		switch (runType) {
			case PAGE_BREAK:
				if (tokenType == TokenType.PAGE_BREAK)
					return TokenType.IMPLICIT_LINE_BREAK;
				return tokenType;
			case IMPLICIT_LINE_BREAK:
				if (tokenType == TokenType.EXPLICIT_LINE_BREAK)
					return TokenType.EXPLICIT_LINE_BREAK;
			case EXPLICIT_LINE_BREAK:
				if (tokenType == TokenType.PARAGRAPH_BOUNDARY)
					return tokenType;
				return runType;
			case PARAGRAPH_BOUNDARY:
				return TokenType.PARAGRAPH_BOUNDARY;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	private static void whitespacePrecedence(final Run run, final Token token, final TokenType tokenType) {
		if ("\t".equals(token.getText()))
			run.runText = "\t";
	}
	
	private static void appendRun(final Run run, final ArrayList<Token> target) {
		target.add(new Token(run.runType, run.runText, run.runConversionTypes));
	}
	
	private static ArrayList<Token> removeBoundarySpaces(final ArrayList<Token> tokenSequence) {
		return removeBoundaryItems(tokenSequence, TokenTypeClass.LINEBREAKS, TokenTypeClass.WHITESPACE);
	}
	
	private static ArrayList<Token> removeBoundaryLinebreaks(final ArrayList<Token> tokenSequence) {
		return removeBoundaryItems(tokenSequence, null, TokenTypeClass.LINEBREAKS);
	}
	
	private static ArrayList<Token> removeBoundaryItems(final ArrayList<Token> tokenSequence, final TokenTypeClass boundaryClass, final TokenTypeClass classToRemove) {
		final int n = tokenSequence.size();
		final ArrayList<Token> result = new ArrayList<>(n);
		
		int start = -1;
		int i_next;
		for (int i = 0; i < n; i = i_next) {
			i_next = i + 1;
			final Token token = tokenSequence.get(i);
			final TokenTypeClass tokenTypeClass = token.getType().getTokenTypeClass();
			if (tokenTypeClass == boundaryClass) {
				start = -1;
				result.add(token);
			} else if (tokenTypeClass != classToRemove) {
				if (start >= 0)
					result.addAll(tokenSequence.subList(start, i));
				result.add(token);
				start = i_next;
			}
		}
		
		return result;
	}
	
	/**
	 * Converts a token sequence into a {@link String}.
	 * <p>
	 * The result is a concatenation of texts
	 * for every token, in order. If a token has text (that is, if
	 * {@link Token#getText()}
	 * does not return {@code null}), that text is used.
	 * Otherwise, a default text is used depending on the
	 * {@link TokenType}:
	 * </p>
	 * <ul>
	 * <li>
	 * {@code " "} (a single U+0020 SPACE character) for
	 * {@link TokenType#WHITESPACE}
	 * </li>
	 * <li>
	 * {@code "\n"} (a single U+000A LINE FEED character) for
	 * {@link TokenType#EXPLICIT_LINE_BREAK}
	 * or
	 * {@link TokenType#IMPLICIT_LINE_BREAK}
	 * </li>
	 * <li>
	 * {@code "\n\n"} (two U+000A LINE FEED characters) for
	 * {@link TokenType#PARAGRAPH_BOUNDARY}
	 * </li>
	 * </ul>
	 * <p>
	 * If a token of another {@link TokenType} is encountered whose
	 * {@link Token#getText()}
	 * method returns {@code null}, an
	 * {@link IllegalArgumentException}
	 * is thrown.
	 * </p>
	 * 
	 * @param tokenSequence
	 * the token sequence to be converted into a {@link String};
	 * neither this {@link ArrayList} nor any of its elements
	 * may be {@code null}
	 * 
	 * @return
	 * a {@link String} constructed according to the description above;
	 * not {@code null}
	 */
	public static String tokenSequenceToString(final ArrayList<Token> tokenSequence) {
		final StringBuilder sb = new StringBuilder();
		
		for (final Token token : tokenSequence) {
			final String text = token.getText();
			if (text == null)
				specialTokenToString(token, token.getType(), sb);
			else
				sb.append(text);
		}
		
		return sb.toString();
	}
	
	private static void specialTokenToString(final Token token, final TokenType tokenType, final StringBuilder sb) {
		switch (tokenType) {
			case PARAGRAPH_BOUNDARY:
				sb.appendCodePoint('\n');
				// fall-through
			case EXPLICIT_LINE_BREAK:
			case IMPLICIT_LINE_BREAK:
				sb.appendCodePoint('\n');
				break;
			case WHITESPACE:
				sb.appendCodePoint(' ');
				break;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	private static class Run {
		public TokenType runType;
		public TokenTypeClass runClass;
		public String runText;
		public int runConversionTypes;
		
		public Run(final TokenType runType, final TokenTypeClass runClass, final String runText, final int runConversionTypes) {
			this.runType = runType;
			this.runClass = runClass;
			this.runText = runText;
			this.runConversionTypes = runConversionTypes;
		}
	}
}
