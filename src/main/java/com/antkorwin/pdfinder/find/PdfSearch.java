package com.antkorwin.pdfinder.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.antkorwin.pdfinder.Boundary;
import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.tokenizer.SplitSubTokenStrategy;
import com.antkorwin.pdfinder.tokenizer.SubToken;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
public class PdfSearch implements OnePageResult {

	private final OnePageResult originalData;
	private final boolean caseSensitive;
	private final Boundary boundary;
	private final SplitSubTokenStrategy splitSubTokenStrategy;
	private final List<SubToken> searchTokens;

	public PdfSearch(OnePageResult originalData,
	                 String searchString,
	                 boolean caseSensitive,
	                 Boundary boundary,
	                 SplitSubTokenStrategy splitSubTokenStrategy) {

		this.originalData = originalData;
		this.caseSensitive = caseSensitive;
		this.boundary = boundary;
		this.splitSubTokenStrategy = splitSubTokenStrategy;
		this.searchTokens = split(searchString);
	}

	@Override
	public Map<Float, List<TextToken>> result() {

		Map<Float, List<TextToken>> result = new ConcurrentHashMap<>();
		originalData.result()
		            .forEach((rowPosition, tokens) -> findTokenSequenceInRow(tokens, rowPosition, result));

		return result;
	}


	private void findTokenSequenceInRow(List<TextToken> tokenListFromSingleRow,
	                                    Float rowPosition,
	                                    Map<Float, List<TextToken>> result) {

		List<String> searchSequence = searchTokens.stream()
		                                          .map(SubToken::getToken)
		                                          .collect(Collectors.toList());

		new TokenSequence(tokenListFromSingleRow).findAllSubSequence(searchSequence, this::matchToken)
		                                         .forEach(token -> addTokenInResultMap(token, rowPosition, result));
	}

	private boolean matchToken(TextToken token, String searchString) {
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

	private List<SubToken> split(String searchString) {
		return splitSubTokenStrategy.split(searchString);
	}
}
