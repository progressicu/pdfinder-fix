package com.antkorwin.pdfinder.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.tokenizer.SubToken;
import lombok.RequiredArgsConstructor;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class PdfSearchSequence implements OnePageResult {

	private final OnePageResult originalData;
	private final List<SubToken> searchTokens;
	private final MatchTokenStrategy matchTokenStrategy;


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

		new TokenSequence(tokenListFromSingleRow).findAllSubSequence(searchSequence, matchTokenStrategy::matchToken)
		                                         .forEach(token -> addTokenInResultMap(token, rowPosition, result));
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
}
