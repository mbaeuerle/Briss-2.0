/**
 * Copyright 2010 Gerhard Aigner
 * 
 * This file is part of BRISS.
 * 
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss.utils;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PageNumberParser {

	
	private PageNumberParser() {
	};

	/**
	 * Super simple page-number parser. It handles entries like: "1-2;34;3-16"
	 * 
	 * @param input
	 *            String to be parsed.
	 * @return
	 * @throws ParseException
	 */
	public static Set<Integer> parsePageNumber(final String input)
			throws ParseException {

		Pattern p = Pattern.compile("[^0-9-;]");
		Matcher m = p.matcher(input);

		if (m.find())
			throw new ParseException(
					"Allowed characters: \"0-9\" \";\" \"-\" ", 0);

		// now tokenize by ;
		StringTokenizer tokenizer = new StringTokenizer(input, ";");

		Set<Integer> pNS = new HashSet<Integer>();
		while (tokenizer.hasMoreElements()) {
			pNS.addAll(extractPageNumbers(tokenizer.nextToken()));
		}

		return pNS;
	}

	private static Set<Integer> extractPageNumbers(final String input)
			throws ParseException {

		StringTokenizer tokenizer = new StringTokenizer(input, "-");
		Set<Integer> returnSet = new HashSet<Integer>();
		if (tokenizer.countTokens() == 1) {
			// it's only a number, lets parse it
			Integer pageNumber = Integer.parseInt(input);
			returnSet.add(pageNumber);
			return returnSet;
		} else if (tokenizer.countTokens() == 2) {
			int start = Integer.parseInt(tokenizer.nextToken());
			int end = Integer.parseInt(tokenizer.nextToken());
			if (start > end)
				throw new ParseException("End must be bigger than start in \""
						+ input + "\"", 0);
			else {
				for (int i = start; i <= end; i++) {
					returnSet.add(i);
				}
				return returnSet;
			}
		} else
			throw new ParseException("\"" + input
					+ "\" has to many - characters!", 0);
	}
}
