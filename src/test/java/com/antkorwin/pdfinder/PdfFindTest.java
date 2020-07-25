package com.antkorwin.pdfinder;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PdfFindTest {

	@Test
	void multiplePages() {
		File file = loadFile("junit5_manual.pdf");

		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .search("condition");

		TextToken firstToken = result.getFirstToken().get();
		assertThat(firstToken.getText()).contains("container or test based on certain conditions programmatically");
		assertThat(firstToken.getPageNumber()).isEqualTo(23);

		TextToken lastToken = result.getLastToken().get();
		assertThat(lastToken.getText()).contains("PreconditionViolationException");
		assertThat(lastToken.getPageNumber()).isEqualTo(142);
	}

	@Test
	void boundary() {
		File file = loadFile("junit5_manual.pdf");

		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .boundary(new Boundary(50, 750, 200, 50))
		                                        .search("Assert");

		assertThat(result.countOfTokens()).isEqualTo(1);
		assertThat(result.getFirstToken().get().getText()).contains("Assert statistics for test events");
		assertThat(result.getFirstToken().get().getPosition().getX()).isBetween(50f, 200f);
		assertThat(result.getFirstToken().get().getPosition().getY()).isBetween(750f, 800f);
	}

	@Test
	void notFound() {
		File file = loadFile("junit5_manual.pdf");

		PdfFindResult result = new PdfFind(file).search("Q1w2e3r4t5");

		assertThat(result.countOfTokens()).isEqualTo(0);
		assertThat(result.getFirstToken()).isEmpty();
		assertThat(result.getLastToken()).isEmpty();
	}

	@Test
	void caseSensitive() {
		// Arrange
		File file = loadFile("junit5_manual.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .search("Junit");
		// Assert
		assertThat(result.countOfTokens()).isEqualTo(0);
	}

	@Test
	void caseInsensitive() {
		// Arrange
		File file = loadFile("junit5_manual.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("Junit");
		// Assert
		assertThat(result.countOfTokens()).isEqualTo(810);
	}


	@Test
	void trimSpaceInTokens() {
		// Arrange
		File file = loadFile("space2.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("Конец");
		// Assert
		System.out.println(result.getTokensByPageMap());
	}

	@Test
	void trimTabsInTokens() {
		// Arrange
		File file = loadFile("space-test.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("space");
		// Assert
		System.out.println(result.getTokensByPageMap());
	}

	@Test
	void trimTabbbb() {
		// Arrange
		File file = loadFile("bad.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("Ю. Ковалык");
		// Assert
		System.out.println(result.countOfTokens());
	}

	@Test
	void whiteSpaceTokenizerTest() {
		// Arrange
		File file = loadFile("spacing.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("space");
		// Assert
		System.out.println(result.countOfTokens());
	}

	@Test
	void whiteSpaceTokenizerSequenceTest() {
		// Arrange
		File file = loadFile("tokenizer_with_spacing.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("space");
		// Assert
		assertThat(result.getTokensFromPage(1)).hasSize(4)
		                                       .extracting(t -> t.getPosition().getY())
		                                       .containsExactly(550.2544f, 655.3426f, 760.4307f, 760.4307f);
	}

	@Test
	void extractingTest() {
		// Arrange
		File file = loadFile("tokenizer_with_spacing.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("space before");
		// Assert
		assertThat(result.getTokensFromPage(1)).hasSize(2)
		                                       .extracting(t -> t.getPosition().getY())
		                                       .containsOnly(760.4307f);
	}

	private File loadFile(String s) {
		return new File(getClass().getClassLoader()
		                          .getResource(s)
		                          .getFile());
	}
}