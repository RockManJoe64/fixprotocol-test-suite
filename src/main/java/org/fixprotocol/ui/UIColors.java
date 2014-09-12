package org.fixprotocol.ui;

import java.awt.Color;

/**
 * @author Jose Chavez <chavez.j@eoxlive.com>
 * @since Sep 11, 2014
 */
public final class UIColors {
	
	public static final Color BEIGE = hex2Rgb("#F5F5DC");
	
	/**
	 * 
	 * @param colorStr e.g. "#FFFFFF"
	 * @return 
	 */
	public static Color hex2Rgb(String colorStr) {
		return new Color(
				Integer.valueOf(colorStr.substring(1, 3), 16),
				Integer.valueOf(colorStr.substring(3, 5), 16), 
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}
	
}
