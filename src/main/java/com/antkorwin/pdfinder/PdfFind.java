package com.antkorwin.pdfinder;

import java.io.File;
import java.util.List;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Slf4j
@RequiredArgsConstructor
public class PdfFind {

	/**
	 * default distance between two tokens when these tokens process as single token
	 */
	private static final int DEFAULT_THRESHOLD = 10;

	private final File file;
	private int threshold = DEFAULT_THRESHOLD;
	private Boundary boundary;

	public PdfFind threshold(int threshold) {
		this.threshold = threshold;
		return this;
	}

	public PdfFind boundary(Boundary boundary) {
		this.boundary = boundary;
		return this;
	}

	public PdfFindResult search(String searchString) {

		PdfFindResult result = new PdfFindResult();
		try (PdfReader reader = new PdfReader(file);
		     PdfDocument pdfDoc = new PdfDocument(reader)) {

			searchInDocument(pdfDoc, searchString, result);
		} catch (Exception e) {
			log.error("Error while search text in PDF file", e);
		}
		return result;
	}

	private void searchInDocument(PdfDocument pdfDoc, String searchString, PdfFindResult result) {
		for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
			PdfPage page = pdfDoc.getPage(pageNum);
			List<TextToken> tokensFromPage = getTokensFromPage(page, pageNum, searchString);
			if (tokensFromPage.size() > 0) {
				result.addResultForPage(pageNum, tokensFromPage);
			}
		}
	}

	private List<TextToken> getTokensFromPage(PdfPage page, int pageNumber, String searchString) {
		TextTokenSearchListener listener = new TextTokenSearchListener(pageNumber, threshold);
		new PdfCanvasProcessor(listener).processPageContent(page);
		if (boundary != null) {
			return listener.findTokensInBoundary(searchString, boundary);
		} else {
			return listener.findTokens(searchString);
		}
	}
}
