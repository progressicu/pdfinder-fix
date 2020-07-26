package com.antkorwin.pdfinder.find;

import com.antkorwin.pdfinder.Boundary;
import com.antkorwin.pdfinder.TextToken;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class InBoundaryMatchTokenStrategy implements MatchTokenStrategy {

	private final boolean caseSensitive;
	private final Boundary boundary;


	@Override
	public boolean matchToken(TextToken token, String searchString) {
		if (boundary != null) {
			return match(token.getText(), searchString) &&
			       boundary.isInBoundary(token.getPosition());
		}
		return match(token.getText(), searchString);
	}

	private boolean match(String first, String second) {
		return this.caseSensitive
		       ? first.contains(second)
		       : first.toLowerCase().contains(second.toLowerCase());
	}
}
