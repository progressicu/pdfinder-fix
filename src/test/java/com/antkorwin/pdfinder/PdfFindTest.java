package com.antkorwin.pdfinder;

import java.io.File;
import java.io.IOException;

import com.antkorwin.ioutils.TempFile;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PdfFindTest {

	@Test
	void multiplePages() {
		File file = loadFile("junit5_manual.pdf");

		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .search("condition");

		TextToken firstToken = result.getFirstToken().get();
		assertThat(firstToken.getText()).contains("conditions");
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

		assertThat(result.countOfTokens()).isEqualTo(4);

		result.getAllTokens().forEach(token -> {
			assertThat(token.getPosition().getX()).isBetween(50f, 200f);
			assertThat(token.getPosition().getY()).isBetween(750f, 800f);
		});

		assertThat(result.getAllTokens()).extracting(TextToken::getText)
		                                 .containsExactly("Assertions",
		                                                  "Asserting",
		                                                  "Assert",
		                                                  "Assertions");
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
		assertThat(result.countOfTokens()).isEqualTo(915);
	}


	@Test
	void trimSpaceInTokens() throws IOException {
		// Arrange
		File file = loadFile("space2.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("Конец");
		// Assert
		System.out.println(result.getTokensByPageMap());

		TextPosition position = result.getFirstToken().get().getPosition();

		File tempFile = TempFile.create();
		PdfWriter writer = new PdfWriter(tempFile);
		PdfReader reader = new PdfReader(file);
		PdfDocument document = new PdfDocument(reader, writer);

		PdfPage page = document.getPage(1);
		PdfCanvas canvas = new PdfCanvas(page);
		canvas.setStrokeColor(DeviceCmyk.BLACK)
		      .setLineWidth(1)
		      .moveTo(position.getLeft(), position.getTop() + 14)
		      .lineTo(position.getRight(), position.getTop() + 14)
		      .lineTo(position.getRight(), position.getBottom())
		      .lineTo(position.getLeft(), position.getBottom())
		      .closePathStroke();

		document.close();

		System.out.println(tempFile.getName());
	}

	@Test
	void splitSearchPhraseByMultipleStrategies() throws IOException {
		// Arrange
		File file = loadFile("test.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(40)
		                                        .caseSensitive(false)
		                                        .search("Т.Т. Тест");
		// Assert
		TextToken firstToken = result.getAllTokens().get(0);
		assertThat(firstToken).extracting(t -> t.getText(),
		                               t -> t.getPosition().getLeft(),
		                               t -> t.getPosition().getTop())
		                   .contains("Т.Т.", 414.17f, 420.84f);

		TextToken secondToken = result.getAllTokens().get(1);
		assertThat(secondToken).extracting(t -> t.getText(),
		                               t -> t.getPosition().getLeft(),
		                               t -> t.getPosition().getTop())
		                   .contains("Тест", 435.962f, 420.84f);
	}


	@Test
	void trimTabsInTokens() throws IOException {
		// Arrange
		File file = loadFile("space-test.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("space");
		// Assert
		System.out.println(result.getTokensByPageMap());

		File tempFile = TempFile.create();
		PdfWriter writer = new PdfWriter(tempFile);
		PdfReader reader = new PdfReader(file);
		PdfDocument document = new PdfDocument(reader, writer);
		PdfPage page = document.getPage(1);
		PdfCanvas canvas = new PdfCanvas(page);

		for (TextToken token : result.getAllTokens()) {
			TextPosition position = token.getPosition();
			canvas.setStrokeColor(DeviceCmyk.BLACK)
			      .setLineWidth(1)
			      .moveTo(position.getLeft(), position.getTop() + 14)
			      .lineTo(position.getRight(), position.getTop() + 14)
			      .lineTo(position.getRight(), position.getBottom())
			      .lineTo(position.getLeft(), position.getBottom())
			      .closePathStroke();
		}

		document.close();

		System.out.println(tempFile.getName());
	}

	@Test
	void trimTabsInTokens__RE() throws IOException {
		// Arrange
		File file = loadFile("rus_eng.pdf");
		// Act
		PdfFindResult result = new PdfFind(file).threshold(10)
		                                        .caseSensitive(false)
		                                        .search("строки");
		// Assert
		System.out.println(result.getTokensByPageMap());

		File tempFile = TempFile.create();
		PdfWriter writer = new PdfWriter(tempFile);
		PdfReader reader = new PdfReader(file);
		PdfDocument document = new PdfDocument(reader, writer);
		PdfPage page = document.getPage(1);
		PdfCanvas canvas = new PdfCanvas(page);

		for (TextToken token : result.getAllTokens()) {
			TextPosition position = token.getPosition();
			canvas.setStrokeColor(DeviceCmyk.BLACK)
			      .setLineWidth(1)
			      .moveTo(position.getLeft(), position.getTop() + 14)
			      .lineTo(position.getRight(), position.getTop() + 14)
			      .lineTo(position.getRight(), position.getBottom())
			      .lineTo(position.getLeft(), position.getBottom())
			      .closePathStroke();
		}

		document.close();

		System.out.println(tempFile.getName());
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
		                                       .containsExactly(760.4307f, 760.4307f, 655.3426f, 550.2544f);
	}


	@Nested
	class ExtractingTest {

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

		@Test
		void extractTokenInTheMiddle() {
			// Arrange
			File file = loadFile("tokenizer_with_spacing.pdf");
			// Act
			PdfFindResult result = new PdfFind(file).threshold(10)
			                                        .caseSensitive(false)
			                                        .search("and tabs");
			// Assert
			assertThat(result.getTokensFromPage(1)).hasSize(2)
			                                       .extracting(t -> t.getPosition().getY())
			                                       .containsOnly(655.3426f);
		}

		@Test
		void extractTokenInTheEnd() {
			// Arrange
			File file = loadFile("tokenizer_with_spacing.pdf");
			// Act
			PdfFindResult result = new PdfFind(file).threshold(10)
			                                        .caseSensitive(false)
			                                        .search("after space");
			// Assert
			assertThat(result.getTokensFromPage(1)).hasSize(2)
			                                       .extracting(t -> t.getPosition().getY())
			                                       .containsOnly(550.2544f);
		}
	}

	private File loadFile(String s) {
		return new File(getClass().getClassLoader()
		                          .getResource(s)
		                          .getFile());
	}
}