package com.antkorwin.pdfinder.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
public class WhiteSpaceSplitSubTokenStrategy implements SplitSubTokenStrategy {

	private static final String WHITE_SPACE = "\\s+";

	@Override
	public List<SubToken> split(String text) {

		List<SubToken> tokens = new ArrayList<>();
		if (text == null) {
			return tokens;
		}

		Pattern pattern = Pattern.compile(WHITE_SPACE);
		Matcher matcher = pattern.matcher(text);

		int startIndex = 0;
		while (matcher.find()) {
			if (matcher.start() != 0) {
				String token = text.substring(startIndex, matcher.start());
				tokens.add(SubToken.builder()
				                   .token(token)
				                   .startIndex(startIndex)
				                   .original(text)
				                   .build());
			}
			startIndex = matcher.end();
		}

		if (startIndex < text.length()) {
			String token = text.substring(startIndex);
			tokens.add(SubToken.builder()
			                   .token(token)
			                   .startIndex(startIndex)
			                   .original(text)
			                   .build());
		}

		return tokens;
	}
}
