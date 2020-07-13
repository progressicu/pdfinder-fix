package com.antkorwin.pdfinder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.antkorwin.throwable.functions.ThrowableSupplier;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

/**
 * API to get plain text from a PDF file.
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class PdfText {

	private final byte[] content;

	/**
	 * load pdf from file
	 */
	public static PdfText fromFile(File file) {
		try {
			byte[] content = Files.readAllBytes(file.toPath());
			return new PdfText(content);
		} catch (IOException e) {
			throw new PdfFindInternalException(e);
		}
	}

	/**
	 * load pdf from InputStream
	 */
	public static PdfText fromInputStream(ThrowableSupplier<InputStream> inputStreamThrowableSupplier) {
		try (InputStream inputStream = inputStreamThrowableSupplier.get()) {
			byte[] content = IOUtils.toByteArray(inputStream);
			return new PdfText(content);
		} catch (IOException e) {
			throw new PdfFindInternalException(e);
		}
	}

	/**
	 * extract all text from the pdf file
	 */
	public String extract() {

		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
		     PdfReader reader = new PdfReader(inputStream);
		     PdfDocument pdfDoc = new PdfDocument(reader)) {

			LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
			PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
			return parsePageByPage(pdfDoc, strategy, parser);

		} catch (Exception e) {
			throw new PdfFindInternalException(e);
		}
	}

	private String parsePageByPage(PdfDocument pdfDoc, LocationTextExtractionStrategy strategy, PdfCanvasProcessor parser) {

		StringBuilder resultText = new StringBuilder();
		for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
			parser.processPageContent(pdfDoc.getPage(pageNum));
			resultText.append(strategy.getResultantText());
		}
		return resultText.toString();
	}
}
