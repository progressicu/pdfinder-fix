package com.antkorwin.pdfinder.find.match;

import java.util.Arrays;
import java.util.List;

import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.MatchTokenStrategy;

public class CompositeMatchTokenStrategy implements MatchTokenStrategy {

	private final List<MatchTokenStrategy> strategyList;

	public CompositeMatchTokenStrategy(MatchTokenStrategy... strategies) {
		this.strategyList = Arrays.asList(strategies);
	}


	@Override
	public boolean matchToken(TextToken token, String searchString) {
		for (MatchTokenStrategy strategy : strategyList) {
			if (!strategy.matchToken(token, searchString)) {
				return false;
			}
		}
		return true;
	}
}
