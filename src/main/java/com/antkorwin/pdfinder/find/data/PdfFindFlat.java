package com.antkorwin.pdfinder.find.data;

import java.util.ArrayList;
import java.util.List;

import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.SinglePageTokenData;
import lombok.RequiredArgsConstructor;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class PdfFindFlat {

	private final SinglePageTokenData originalData;


	public List<TextToken> result() {
		List<TextToken> result = new ArrayList<>();
		originalData.result().forEach((k, v) -> result.addAll(v));
		return result;
	}
}
