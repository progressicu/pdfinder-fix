package com.antkorwin.pdfinder;

import java.io.File;
import java.util.List;

import com.antkorwin.pdfinder.find.FlatSearch;
import com.antkorwin.pdfinder.find.PdfSearch;
import com.antkorwin.pdfinder.find.PdfSplitResult;
import com.antkorwin.pdfinder.tokenizer.SplitSubTokenStrategy;
import com.antkorwin.pdfinder.tokenizer.WhiteSpaceSplitSubTokenStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Entry point into the API to search text position in pdf files
 *
 * @author Korovin Anatoliy
 */
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
	private boolean caseSensitive = true;

	/**
	 * the minimal distance between two text blocks,
	 * to join in single text token
	 */
	public PdfFind threshold(int threshold) {
		this.threshold = threshold;
		return this;
	}

	/**
	 * set the boundary to search text only in this area
	 */
	public PdfFind boundary(Boundary boundary) {
		this.boundary = boundary;
		return this;
	}

	/**
	 * set to false if you need to find text without case sensitive,
	 * (true by default)
	 */
	public PdfFind caseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		return this;
	}

	/**
	 * search all text tokens matched to searchString
	 */
	public PdfFindResult search(String searchString) {

		try (PdfReader reader = new PdfReader(file);
		     PdfDocument pdfDoc = new PdfDocument(reader)) {

			return searchInDocument(pdfDoc, searchString);
		} catch (Exception e) {
			log.error("Error while search text in PDF file", e);
			throw new PdfFindInternalException(e);
		}
	}

	private PdfFindResult searchInDocument(PdfDocument pdfDoc, String searchString) {
		PdfFindResult result = new PdfFindResult();
		for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
			PdfPage page = pdfDoc.getPage(pageNum);
			List<TextToken> tokensFromPage = getTokensFromPage(page, pageNum, searchString);
			if (tokensFromPage.size() > 0) {
				result.addResultForPage(pageNum, tokensFromPage);
			}
		}
		return result;
	}

	private List<TextToken> getTokensFromPage(PdfPage page, int pageNumber, String searchString) {

		TextTokenSearchListener listener = new TextTokenSearchListener(pageNumber,
		                                                               threshold);
		new PdfCanvasProcessor(listener).processPageContent(page);
		PdfSplitResult split = new PdfSplitResult(listener.getExtractResult(), new WhiteSpaceSplitSubTokenStrategy());
		SplitSubTokenStrategy splitStrategy = new WhiteSpaceSplitSubTokenStrategy();
		PdfSearch search = new PdfSearch(split, searchString, caseSensitive, boundary, splitStrategy);
		return new FlatSearch(search).result();
	}
}
