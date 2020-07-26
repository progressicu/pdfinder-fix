package com.antkorwin.pdfinder.tokenizer;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class WhiteSpaceSplitSubTokenStrategyTest {

	private SplitSubTokenStrategy splitStrategy = new WhiteSpaceSplitSubTokenStrategy();

	@Test
	void testPositionOfTokens() {
		// Arrange
		String text = "\tfoo\t\tbar\tfoobar 11  FFF";
		// Act
		List<SubToken> tokens = splitStrategy.split(text);
		// Assert
		assertThat(tokens).isNotNull()
		                  .hasSize(5)
		                  .contains(new SubToken("foo", 1, text),
		                            new SubToken("bar", 6, text),
		                            new SubToken("foobar", 10, text),
		                            new SubToken("11", 17, text),
		                            new SubToken("FFF", 21, text));
	}

	@Test
	void emptyString() {
		List<SubToken> split = splitStrategy.split("");
		// Assert
		assertThat(split).isNotNull()
		                 .isEmpty();
	}

	@Test
	void nullSource() {
		List<SubToken> split = splitStrategy.split(null);
		// Assert
		assertThat(split).isNotNull()
		                 .isEmpty();
	}

	@ParameterizedTest
	@MethodSource("whiteSpaceParams")
	void testSplit(String text) {
		// Act
		List<SubToken> tokens = splitStrategy.split(text);
		// Assert
		assertThat(tokens).isNotNull()
		                  .hasSize(2)
		                  .extracting(SubToken::getToken)
		                  .contains("foo", "bar");
	}

	static Stream<String> whiteSpaceParams() {
		return Stream.of("foo bar",
		                 "foo  bar",
		                 " foo bar",
		                 "   foo bar",
		                 "\tfoo\tbar",
		                 "\t\tfoo   bar",
		                 " \t \t    \tfoo     \t    bar",
		                 "foo     \t    bar",
		                 "foo     bar",
		                 "foo\t\t\t\tbar",
		                 "foo bar    ",
		                 "foo bar\t",
		                 "foo bar\t\t",
		                 "foo bar\t\t   \t",
		                 "foo bar ");
	}
}