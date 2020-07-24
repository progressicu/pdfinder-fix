package com.antkorwin.pdfinder.find;

import java.util.List;
import java.util.Map;

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
public class PdfExtractResult implements OnePageResult {

	private final OnePageResult pdfExtractResult;

	@Override
	public Map<Float, List<TextToken>> result() {
		return pdfExtractResult.result();
	}
}
