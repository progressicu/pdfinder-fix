package com.antkorwin.pdfinder;

/**
 * @author Korovin Anatoliy
 */
public class PdfFindInternalException extends RuntimeException {

	public PdfFindInternalException(String message) {
		super(message);
	}

	public PdfFindInternalException(Throwable cause) {
		super(cause);
	}
}
