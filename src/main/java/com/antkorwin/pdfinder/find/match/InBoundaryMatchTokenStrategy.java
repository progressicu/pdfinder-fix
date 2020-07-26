package com.antkorwin.pdfinder.find.match;

import com.antkorwin.pdfinder.Boundary;
import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.MatchTokenStrategy;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class InBoundaryMatchTokenStrategy implements MatchTokenStrategy {

	private final Boundary boundary;

	@Override
	public boolean matchToken(TextToken token, String searchString) {
		if (boundary == null) {
			return true;
		}
		return boundary.isInBoundary(token.getPosition());
	}
}
