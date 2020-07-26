package com.antkorwin.pdfinder.find;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.data.TokenSequence;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenSequenceTest {

	@Test
	void find() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("BBB").build();
		TextToken t3 = TextToken.builder().text("CCC").build();
		TextToken t4 = TextToken.builder().text("DDD").build();
		TextToken t5 = TextToken.builder().text("EEE").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("BBB", "CCC"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).isNotNull()
		                  .hasSize(2)
		                  .extracting(TextToken::getText)
		                  .contains("BBB", "CCC");
	}

	@Test
	void findMultiple() {
		TextToken t1 = TextToken.builder().text("AAA").pageNumber(1).build();
		TextToken t2 = TextToken.builder().text("BBB").pageNumber(1).build();
		TextToken t3 = TextToken.builder().text("CCC").pageNumber(1).build();
		TextToken t4 = TextToken.builder().text("DDD").pageNumber(2).build();
		TextToken t5 = TextToken.builder().text("BBB").pageNumber(2).build();
		TextToken t6 = TextToken.builder().text("CCC").pageNumber(2).build();
		TextToken t7 = TextToken.builder().text("FFF").pageNumber(2).build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5, t6, t7));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("BBB", "CCC"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).isNotNull()
		                  .hasSize(4)
		                  .extracting(TextToken::getText,
		                              TextToken::getPageNumber)
		                  .contains(Tuple.tuple("BBB", 1), Tuple.tuple("CCC", 1),
		                            Tuple.tuple("BBB", 2), Tuple.tuple("CCC", 2));
	}

	@Test
	void notFound() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("BBB").build();
		TextToken t3 = TextToken.builder().text("DDD").build();
		TextToken t4 = TextToken.builder().text("CCC").build();
		TextToken t5 = TextToken.builder().text("EEE").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("BBB", "CCC"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(0);
	}

	@Test
	void notFoundButEndingWithFirstToken() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("BBB").build();
		TextToken t3 = TextToken.builder().text("DDD").build();
		TextToken t4 = TextToken.builder().text("CCC").build();
		TextToken t5 = TextToken.builder().text("BBB").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("BBB", "CCC"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(0);
	}

	@Test
	void findStringWithSameTokens() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("DDD").build();
		TextToken t3 = TextToken.builder().text("CCC").build();
		TextToken t4 = TextToken.builder().text("BBB").build();
		TextToken t5 = TextToken.builder().text("BBB").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("BBB", "BBB"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(2)
		                  .extracting(TextToken::getText)
		                  .contains("BBB", "BBB");
	}

	@Test
	void findTokenInTheBeginOfSequence() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("DDD").build();
		TextToken t3 = TextToken.builder().text("CCC").build();
		TextToken t4 = TextToken.builder().text("BBB").build();
		TextToken t5 = TextToken.builder().text("BBB").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("AAA", "DDD"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(2)
		                  .extracting(TextToken::getText)
		                  .contains("AAA", "DDD");
	}

	@Test
	void notFoundMultipleSameTokens() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("BBB").build();
		TextToken t3 = TextToken.builder().text("DDD").build();
		TextToken t4 = TextToken.builder().text("CCC").build();
		TextToken t5 = TextToken.builder().text("BBB").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("BBB", "BBB"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(0);
	}

	@Test
	void findEmptyString() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("BBB").build();
		TextToken t3 = TextToken.builder().text("DDD").build();
		TextToken t4 = TextToken.builder().text("CCC").build();
		TextToken t5 = TextToken.builder().text("BBB").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList(""),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(0);
	}

	@Test
	void findSingleToken() {
		TextToken t1 = TextToken.builder().text("AAA").build();
		TextToken t2 = TextToken.builder().text("BBB").build();
		TextToken t3 = TextToken.builder().text("DDD").build();
		TextToken t4 = TextToken.builder().text("CCC").build();
		TextToken t5 = TextToken.builder().text("BBB").build();

		TokenSequence tokenSequence = new TokenSequence(Arrays.asList(t1, t2, t3, t4, t5));

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("CCC"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(1)
		                  .extracting(TextToken::getText)
		                  .contains("CCC");
	}

	@Test
	void findInEmptySequence() {
		TokenSequence tokenSequence = new TokenSequence(Collections.emptyList());

		List<TextToken> result =
				tokenSequence.findAllSubSequence(Arrays.asList("CCC"),
				                                 (token, searchString) -> token.getText().equals(searchString));

		assertThat(result).hasSize(0);
	}
}