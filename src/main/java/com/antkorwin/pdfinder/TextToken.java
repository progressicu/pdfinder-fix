package com.antkorwin.pdfinder;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * Text block from PDF file,
 * describes position, source page and content.
 *
 * @author Korovin Anatoliy
 */
@Data
@Builder
@AllArgsConstructor
public class TextToken {

	private String text;
	private TextPosition position;
	private Integer pageNumber;

	public boolean isEmptyToken() {
		return this.equals(EMPTY_TOKEN);
	}

	static TextToken EMPTY_TOKEN = new TextToken(null, null, null);
}