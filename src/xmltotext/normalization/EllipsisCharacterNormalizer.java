/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.normalization;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xmltotext.tokens.Token;

/**
 * A {@link TokenSequenceNormalizer} that substitutes
 * {@code U+2026 HORIZONTAL ELLIPSIS} (&#x2026;)
 * for ASCII replacements of that character, 
 * <p>
 * Currently, this class replaces substrings of
 * at least two stop (.) characters, possibly separated by
 * an arbitrary amount of whitespace
 * (blank, {@code \t}, {@code \n}, {@code \x0B},
 * {@code \f}, or {@code \r}).
 * </p>
 * <p>
 * For example,
 * {@code "And then . . ."}
 * would become
 * <code>&quot;And then &#x2026;&quot;</code>.
 * </p>
 */
public class EllipsisCharacterNormalizer implements TokenSequenceNormalizer {
	
	private static final Pattern ELLIPSIS_PATTERN = Pattern.compile("(" + Pattern.quote(".") + "\\s*){2,}");
	private static final String ELLIPSIS_REPLACEMENT = Matcher.quoteReplacement("\u2026");
	
	@Override
	public ArrayList<Token> normalizeTokenSequence(final ArrayList<Token> tokenSequence) {
		final ArrayList<Token> result = new ArrayList<>(tokenSequence.size());
		
		for (final Token token : tokenSequence) {
			final String text = token.getText();
			if (text != null) {
				final String newText = ELLIPSIS_PATTERN.matcher(text).replaceAll(ELLIPSIS_REPLACEMENT);
				if (!text.equals(newText)) {
					result.add(new Token(token.getType(), newText, token.getConversions()));
					continue;
				}
			}
			result.add(token);
		}
		
		return result;
	}
	
}
