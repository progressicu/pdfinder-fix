package com.antkorwin.pdfinder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextTokenComparatorTest {

	@Test
	void allPointsInSameRow() {
		// Arrange
		//                      Text  X   Y
		TextToken t1 = newToken("T1", 0, 10);
		TextToken t2 = newToken("T2", 5, 10);
		TextToken t3 = newToken("T3", 10, 10);
		List<TextToken> list = Arrays.asList(t3, t1, t2);
		// Act
		List<TextToken> sorted = list.stream()
		                             .sorted(new TextTokenComparator())
		                             .collect(Collectors.toList());
		// Assert
		assertThat(sorted).containsExactly(t1, t2, t3);
	}

	@Test
	void samePoints() {
		// Arrange
		//                      Text  X   Y
		TextToken t1 = newToken("T1", 5, 10);
		TextToken t2 = newToken("T2", 5, 10);
		List<TextToken> list = Arrays.asList(t1, t2);
		// Act
		List<TextToken> sorted = list.stream()
		                             .sorted(new TextTokenComparator())
		                             .collect(Collectors.toList());
		// Assert
		assertThat(sorted).containsExactly(t1, t2);
	}

	@Test
	void differentRows() {
		// Arrange
		//                      Text  X   Y
		TextToken t1 = newToken("T1", 10, 0);
		TextToken t2 = newToken("T2", 10, 5);
		TextToken t3 = newToken("T3", 10, 10);
		List<TextToken> list = Arrays.asList(t3, t1, t2);
		// Act
		List<TextToken> sorted = list.stream()
		                             .sorted(new TextTokenComparator())
		                             .collect(Collectors.toList());
		// Assert
		assertThat(sorted).containsExactly(t3, t2, t1);
	}

	@Test
	void complexTest() {
		// Arrange
		//                      Text  X   Y
		TextToken t1 = newToken("T1", 10, 100);
		TextToken t2 = newToken("T2", 50, 100);
		TextToken t3 = newToken("T3", 7, 50);
		TextToken t4 = newToken("T4", 0, 10);
		TextToken t5 = newToken("T5", 100, 10);
		List<TextToken> list = Arrays.asList(t3, t1, t5, t2, t4);
		// Act
		List<TextToken> sorted = list.stream()
		                             .sorted(new TextTokenComparator())
		                             .collect(Collectors.toList());
		// Assert
		assertThat(sorted).containsExactly(t1, t2, t3, t4, t5);
	}


	private TextToken newToken(String text, float x, float y) {
		return TextToken.builder()
		                .text(text)
		                .position(TextPosition.builder()
		                                      .y(y)
		                                      .x(x)
		                                      .build())
		                .build();
	}
}