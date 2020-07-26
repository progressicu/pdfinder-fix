package com.antkorwin.pdfinder;

import java.io.File;
import java.io.InputStream;

import com.antkorwin.throwable.functions.ThrowableSupplier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PdfTextTest {

	@Test
	void extractFromFile() {
		File file = new File(getClass().getClassLoader()
		                               .getResource("simple.pdf")
		                               .getFile());

		String text = PdfText.fromFile(file).extract();
		assertThat(text).containsSubsequence("Aaaaa", "aaaaaaaa",
		                                     "Bbbbb", "bbbbbb",
		                                     "Ccccc", "cccccccc",
		                                     "Dddd",
		                                     "Eeeee",
		                                     "Ffffff",
		                                     "Ggggg",
		                                     "Hhhh",
		                                     "000000",
		                                     "123");
	}

	@Test
	void extractFromStream() {

		ThrowableSupplier<InputStream> streamSupplier =
				() -> getClass().getClassLoader()
				                .getResource("simple.pdf")
				                .openStream();

		String text = PdfText.fromInputStream(streamSupplier).extract();
		assertThat(text).containsSubsequence("Aaaaa", "aaaaaaaa",
		                                     "Bbbbb", "bbbbbb",
		                                     "Ccccc", "cccccccc",
		                                     "Dddd",
		                                     "Eeeee",
		                                     "Ffffff",
		                                     "Ggggg",
		                                     "Hhhh",
		                                     "000000",
		                                     "123");
	}
}