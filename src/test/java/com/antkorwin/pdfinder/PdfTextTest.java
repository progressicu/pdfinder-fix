package com.antkorwin.pdfinder;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PdfTextTest {

	@Test
	void extractText() {
		File file = new File(getClass().getClassLoader()
		                               .getResource("simple.pdf")
		                               .getFile());

		String text = PdfText.fromFile(file).extract();
		assertThat(text).contains("Ffffff");
	}
}