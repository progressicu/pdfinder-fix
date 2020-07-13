package com.antkorwin.pdfinder;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PdfFindTest {

	@Test
	void multiplePages() {
		File file = new File(getClass().getClassLoader()
		                               .getResource("junit5_manual.pdf")
		                               .getFile());

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
		File file = new File(getClass().getClassLoader()
		                               .getResource("junit5_manual.pdf")
		                               .getFile());

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
		File file = new File(getClass().getClassLoader()
		                               .getResource("junit5_manual.pdf")
		                               .getFile());

		PdfFindResult result = new PdfFind(file).search("Q1w2e3r4t5");

		assertThat(result.countOfTokens()).isEqualTo(0);
		assertThat(result.getFirstToken()).isEmpty();
		assertThat(result.getLastToken()).isEmpty();
	}
}