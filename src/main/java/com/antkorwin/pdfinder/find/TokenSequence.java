package com.antkorwin.pdfinder.find;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.antkorwin.pdfinder.TextToken;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenSequence {

	private final List<TextToken> sequence;


	public List<TextToken> findAllSubSequence(List<String> subSequence, BiFunction<TextToken, String, Boolean> matchTokenFunction) {

		List<TextToken> result = new ArrayList<>();
		if (sequence.size() < subSequence.size()) {
			return result;
		}

		for (int i = 0; i < sequence.size(); i++) {

			boolean mismatch = false;
			for (int j = 0, k = i; j < subSequence.size(); j++) {

				if (k >= sequence.size()) {
					mismatch = true;
					break;
				}

				TextToken token = sequence.get(k);
				String searchString = subSequence.get(j);
				if (!matchTokenFunction.apply(token, searchString)) {
					mismatch = true;
					break;
				} else {
					k++;
				}
			}

			if (!mismatch) {
				for(int m =i; m<i+subSequence.size(); m++){
					result.add(sequence.get(m));
				}
			}
		}

		return result;
	}
}
