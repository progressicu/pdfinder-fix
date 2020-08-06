package com.antkorwin.pdfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

/**
 * The result of searching in pdf file.
 *
 * @author Korovin Anatoliy
 */
public class PdfFindResult {

	@Getter
	private final Map<Integer, List<TextToken>> tokensByPageMap = new ConcurrentHashMap<>();


	PdfFindResult addResultForPage(int pageNumber, List<TextToken> result) {
		tokensByPageMap.put(pageNumber, result);
		return this;
	}

	/**
	 * get first found token, from the beginning of a document
	 */
	public Optional<TextToken> getFirstToken() {

		if (tokensByPageMap == null) {
			throw new PdfFindInternalException("call search(String) before try to retrieve a result.");
		}

		Optional<Integer> firstPageWithToken = tokensByPageMap.keySet()
		                                                      .stream()
		                                                      .sorted()
		                                                      .findFirst();

		if (!firstPageWithToken.isPresent()) {
			return Optional.empty();
		}

		List<TextToken> tokensFromFirstPage = tokensByPageMap.get(firstPageWithToken.get());
		return Optional.of(tokensFromFirstPage.get(0));
	}

	/**
	 * get last found token
	 */
	public Optional<TextToken> getLastToken() {
		if (tokensByPageMap == null) {
			throw new PdfFindInternalException("call search(String) before try to retrieve a result.");
		}

		Optional<Integer> lastPage = tokensByPageMap.keySet()
		                                            .stream()
		                                            .min(Collections.reverseOrder());

		if (!lastPage.isPresent()) {
			return Optional.empty();
		}

		List<TextToken> tokensFromLastPage = tokensByPageMap.get(lastPage.get());
		return Optional.of(tokensFromLastPage.get(0));
	}

	/**
	 * get all found tokens from selected page
	 */
	public List<TextToken> getTokensFromPage(int pageNumber) {
		return tokensByPageMap.get(pageNumber);
	}


	public List<TextToken> getAllTokens() {
		List<TextToken> result = new ArrayList<>();
		tokensByPageMap.values().forEach(result::addAll);
		return result;
	}

	/**
	 * total count of found tokens
	 */
	public long countOfTokens() {
		AtomicLong count = new AtomicLong(0);
		tokensByPageMap.forEach((k, v) -> count.addAndGet(v.size()));
		return count.get();
	}
}
