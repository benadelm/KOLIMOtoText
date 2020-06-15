/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package xmltotext.tokens;

/**
 * Types of conversion for which output material is intended.
 * The values of the static constants in this class are intended
 * to be used as bit flags.
 */
public class ConversionTypes {
	
	/**
	 * Conversion to a textual representation for use by humans.
	 * Replace images and other non-textual material with placeholders.
	 * This constant is also used to mark material to appear in outputs
	 * of such conversions.
	 */
	public static final int HUMAN = 0b01;
	
	/**
	 * Conversion to a textual representation for use by
	 * text-processing tools.
	 * Do not include images or other non-textual material
	 * in the output.
	 * This constant is also used to mark material to appear in outputs
	 * of such conversions.
	 */
	public static final int TOOLS = 0b10;
	
	/**
	 * This constant is used to mark material to appear in the output
	 * of any type.
	 */
	public static final int ALL = HUMAN | TOOLS;
	
}
