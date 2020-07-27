package com.antkorwin.pdfinder.tokenizer;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class SearchPhraseSplitSubTokenStrategyTest {

	@Test
	void multiTokenMultiSplit() {
		SearchPhraseSplitSubTokenStrategy splitStrategy = new SearchPhraseSplitSubTokenStrategy();
		List<SubToken> split = splitStrategy.split("А.Ю. Коваль");
		// Assert
		assertThat(split).extracting(SubToken::getToken)
		                 .containsExactly("А", ".", "Ю", ".", "Коваль");
	}

	@Test
	void specialSymbolInTheMiddleOfString() {
		SearchPhraseSplitSubTokenStrategy splitStrategy = new SearchPhraseSplitSubTokenStrategy();
		List<SubToken> split = splitStrategy.split("А.Ю.");
		// Assert
		assertThat(split).extracting(SubToken::getToken)
		                 .containsExactly("А", ".", "Ю", ".");
	}

	@Test
	void startFromSpecialSymbol() {
		SearchPhraseSplitSubTokenStrategy splitStrategy = new SearchPhraseSplitSubTokenStrategy();
		List<SubToken> split = splitStrategy.split(".Ю.");
		// Assert
		assertThat(split).extracting(SubToken::getToken)
		                 .containsExactly(".", "Ю", ".");
	}

	@Test
	void testIndexAfterSplit() {

	}
}