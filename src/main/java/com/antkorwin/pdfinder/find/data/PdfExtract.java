package com.antkorwin.pdfinder.find.data;

import java.util.List;
import java.util.Map;

import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.SinglePageTokenData;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import lombok.RequiredArgsConstructor;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class PdfExtract implements SinglePageTokenData {

	private final PdfPage page;
	private final int pageNumber;
	private final int threshold;

	@Override
	public Map<Float, List<TextToken>> result() {
		TextTokenSearchListener listener = new TextTokenSearchListener(pageNumber, threshold);
		new PdfCanvasProcessor(listener).processPageContent(page);
		return listener.result();
	}
}
