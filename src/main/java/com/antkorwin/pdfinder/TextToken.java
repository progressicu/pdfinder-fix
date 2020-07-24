package com.antkorwin.pdfinder;


import com.itextpdf.kernel.font.PdfFont;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Text block from PDF file,
 * describes position, source page and content.
 *
 * @author Korovin Anatoliy
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextToken {

	private String text;
	private TextPosition position;
	private Integer pageNumber;

	private PdfFont font;
	private float fontSize;


	public boolean isEmptyToken() {
		return this.equals(EMPTY_TOKEN);
	}

	static TextToken EMPTY_TOKEN = new TextToken();
}