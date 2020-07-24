package com.antkorwin.pdfinder.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.antkorwin.pdfinder.Boundary;
import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.tokenizer.SplitSubTokenStrategy;
import lombok.RequiredArgsConstructor;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class PdfSearch implements OnePageResult {

	private final OnePageResult originalData;
	private final String searchString;
	private final boolean caseSensitive;
	private final Boundary boundary;
	private final SplitSubTokenStrategy splitSubTokenStrategy;


	@Override
	public Map<Float, List<TextToken>> result() {

		Map<Float, List<TextToken>> result = new ConcurrentHashMap<>();
		originalData.result()
		            .forEach((rowPosition, tokens) -> findTokenInRow(tokens, rowPosition, result));

		return result;
	}

	private void findTokenInRow(List<TextToken> tokenListFromSingleRow,
	                            Float rowPosition,
	                            Map<Float, List<TextToken>> result) {

		tokenListFromSingleRow.forEach(token -> {

			if (matchToken(token)) {
				addTokenInResultMap(token, rowPosition, result);
			}
		});
	}

	private boolean matchToken(TextToken token) {
		if (boundary != null) {
			return match(token.getText(), searchString) &&
			       boundary.isInBoundary(token.getPosition());
		}
		return match(token.getText(), searchString);
	}


	private void addTokenInResultMap(TextToken token,
	                                 Float rowPosition,
	                                 Map<Float, List<TextToken>> result) {

		result.compute(rowPosition, (key, oldTokens) -> {
			if (oldTokens == null) {
				ArrayList<TextToken> tokenList = new ArrayList<>();
				tokenList.add(token);
				return tokenList;
			} else {
				oldTokens.add(token);
				return oldTokens;
			}
		});
	}

	private boolean match(String first, String second) {
		return this.caseSensitive
		       ? first.contains(second)
		       : first.toLowerCase().contains(second.toLowerCase());
	}
}
