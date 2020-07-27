package com.antkorwin.pdfinder;

import java.io.File;
import java.util.List;

import com.antkorwin.pdfinder.find.data.PdfFindFlat;
import com.antkorwin.pdfinder.find.match.CaseSensitiveMatchTokenStrategy;
import com.antkorwin.pdfinder.find.match.CompositeMatchTokenStrategy;
import com.antkorwin.pdfinder.find.match.InBoundaryMatchTokenStrategy;
import com.antkorwin.pdfinder.find.MatchTokenStrategy;
import com.antkorwin.pdfinder.find.data.PdfExtract;
import com.antkorwin.pdfinder.find.data.PdfFindSequence;
import com.antkorwin.pdfinder.find.data.PdfSplit;
import com.antkorwin.pdfinder.tokenizer.SearchPhraseSplitSubTokenStrategy;
import com.antkorwin.pdfinder.tokenizer.SplitSubTokenStrategy;
import com.antkorwin.pdfinder.tokenizer.SubToken;
import com.antkorwin.pdfinder.tokenizer.WhiteSpaceSplitSubTokenStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
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
	private SplitSubTokenStrategy splitStrategy = new WhiteSpaceSplitSubTokenStrategy();

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
	 * Set the strategy of splitting tokens in PDF file,
	 * by default using white-space split strategy: {@link WhiteSpaceSplitSubTokenStrategy}
	 * which split words in a file by space or tab symbols
	 */
	public PdfFind splitStrategy(SplitSubTokenStrategy splitStrategy) {
		this.splitStrategy = splitStrategy;
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
			List<TextToken> tokensFromPage = searchOnPage(page, pageNum, searchString);
			if (tokensFromPage.size() > 0) {
				result.addResultForPage(pageNum, tokensFromPage);
			}
		}
		return result;
	}

	private List<TextToken> searchOnPage(PdfPage page, int pageNumber, String searchString) {

		PdfExtract extractResult = new PdfExtract(page, pageNumber, threshold);
		PdfSplit splitResult = new PdfSplit(extractResult, splitStrategy);

//		List<SubToken> searchTokens = splitStrategy.split(searchString);
		List<SubToken> searchTokens = new SearchPhraseSplitSubTokenStrategy().split(searchString);

		MatchTokenStrategy matchTokenStrategy = new CompositeMatchTokenStrategy(new CaseSensitiveMatchTokenStrategy(caseSensitive),
		                                                                        new InBoundaryMatchTokenStrategy(boundary));

		PdfFindSequence searchResult = new PdfFindSequence(splitResult, searchTokens, matchTokenStrategy);

		return new PdfFindFlat(searchResult).result();
	}
}
