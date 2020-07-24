package com.antkorwin.pdfinder.find;

import java.util.ArrayList;
import java.util.List;

import com.antkorwin.pdfinder.TextToken;
import lombok.RequiredArgsConstructor;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class FlatSearch {

	private final OnePageResult originalData;

	public List<TextToken> result() {

		List<TextToken> result = new ArrayList<>();

		originalData.result().forEach((k, v) -> {
			result.addAll(v);
		});

		return result;
	}
}
