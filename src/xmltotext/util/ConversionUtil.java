/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.util;

import java.util.function.Consumer;

import xmltotext.conversion.ActionType;
import xmltotext.conversion.NodeAction;
import xmltotext.tokens.ConversionTypes;
import xmltotext.tokens.Token;
import xmltotext.tokens.TokenType;

/**
 * Contains some common operations necessary during conversion.
 */
public class ConversionUtil {
	
	/**
	 * Creates a placeholder for an image
	 * or some other non-textual material.
	 * The placeholder consists of the specified notification text,
	 * surrounded by paragraph breaks.
	 * The notification text token (but not the paragraph boundaries)
	 * is marked with {@link ConversionTypes#HUMAN} so that
	 * it is not included in conversions for text-processing tools.
	 * 
	 * @param notificationText
	 * the placeholder notification text;
	 * not {@code null}
	 * 
	 * @param tokenConsumer
	 * a {@link Consumer} to consume the placeholder tokens;
	 * not {@code null}
	 * 
	 */
	public static void putSkipNotification(final String notificationText, final Consumer<? super Token> tokenConsumer) {
		tokenConsumer.accept(Token.PARAGRAPH_BOUNDARY);
		tokenConsumer.accept(new Token(TokenType.TEXT, notificationText, ConversionTypes.HUMAN));
		tokenConsumer.accept(Token.PARAGRAPH_BOUNDARY);
	}
	
	/**
	 * Creates tokens to surround the text of a footnote.
	 * The tokens are marked with {@link ConversionTypes#HUMAN}
	 * so that they are not included in conversions
	 * for text-processing tools.
	 * 
	 * @param tokenConsumer
	 * a {@link Consumer} to consume the tokens;
	 * not {@code null}
	 * 
	 * @return
	 * a {@link NodeAction} with {@link ActionType#RECURSE};
	 * not {@code null}
	 */
	public static NodeAction putFootnote(final Consumer<? super Token> tokenConsumer) {
		tokenConsumer.accept(Token.HUMAN_ONLY_WHITESPACE);
		tokenConsumer.accept(new Token(TokenType.TEXT, "[Fu\u00DFnote:", ConversionTypes.HUMAN));
		tokenConsumer.accept(Token.HUMAN_ONLY_WHITESPACE);
		return new NodeAction(ActionType.RECURSE, new Token(TokenType.TEXT, "]", ConversionTypes.HUMAN));
	}
	
}
