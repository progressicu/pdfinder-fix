package com.antkorwin.pdfinder.find.match;

import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.MatchTokenStrategy;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CaseSensitiveMatchTokenStrategy implements MatchTokenStrategy {

	private final boolean caseSensitive;

	@Override
	public boolean matchToken(TextToken token, String searchString) {
		if (token.getText() == null || searchString == null) {
			return false;
		}
		return match(token.getText(), searchString);
	}

	private boolean match(String first, String second) {
		return this.caseSensitive
		       ? first.contains(second)
		       : first.toLowerCase().contains(second.toLowerCase());
	}
}
