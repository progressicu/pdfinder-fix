package com.antkorwin.pdfinder.find.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.antkorwin.pdfinder.TextPosition;
import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.SinglePageTokenData;
import com.antkorwin.pdfinder.tokenizer.SplitSubTokenStrategy;
import com.antkorwin.pdfinder.tokenizer.SubToken;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import lombok.RequiredArgsConstructor;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class PdfSplit implements SinglePageTokenData {

	private final SinglePageTokenData pdfExtractResult;
	private final SplitSubTokenStrategy splitTokenStrategy;

	@Override
	public Map<Float, List<TextToken>> result() {

		Map<Float, List<TextToken>> map = new ConcurrentHashMap<>();
		Map<Float, List<TextToken>> extract = pdfExtractResult.result();

		extract.forEach((k, v) -> {
			List<TextToken> result = new ArrayList<>();
			for (TextToken token : v) {
				result.addAll(splitTokens(token));
			}

			if (!result.isEmpty()) {
				map.put(k, result);
			}
		});

		return map;
	}


	List<TextToken> splitTokens(TextToken originalToken) {

		List<TextToken> result = new ArrayList<>();

		PdfFont font = originalToken.getFont();
		float fontSize = originalToken.getFontSize();
		TextPosition startPosition = originalToken.getPosition();
		String text = originalToken.getText();

		List<SubToken> subTokens = splitTokenStrategy.split(text);
		subTokens.forEach(t -> {

			float width = font.getWidth(t.getToken(), fontSize);
			width = getUserWidth(width, originalToken.getFontMatrix());

			float offset = font.getWidth(text.substring(0, t.getStartIndex()), fontSize);
			offset = getUserWidth(offset, originalToken.getFontMatrix());

			TextPosition position = TextPosition.builder()
			                                    .x(startPosition.getX() + offset)
			                                    .y(startPosition.getY())
			                                    .width(width)
			                                    .height(startPosition.getHeight())
			                                    .build();

			TextToken textToken = TextToken.builder()
			                               .position(position)
			                               .text(t.getToken())
			                               .pageNumber(originalToken.getPageNumber())
			                               .font(originalToken.getFont())
			                               .fontSize(originalToken.getFontSize())
			                               .build();

			result.add(textToken);
		});

		return result;
	}

	private float getUserWidth(float width, Matrix fontMatrix) {
		LineSegment textSpace = new LineSegment(new Vector(0, 0, 1), new Vector(width, 0, 1));
		LineSegment userSpace = textSpace.transformBy(fontMatrix);
		return userSpace.getLength();
	}
}
