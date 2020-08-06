package com.antkorwin.pdfinder;


import java.util.Objects;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Matrix;
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
public class TextToken {

	private String text;
	private TextPosition position;
	private Integer pageNumber;

	private PdfFont font;
	private float fontSize;
	private Matrix fontMatrix;

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		TextToken token = (TextToken) o;
		return Float.compare(token.fontSize, fontSize) == 0 &&
		       Objects.equals(text, token.text) &&
		       Objects.equals(position, token.position) &&
		       Objects.equals(pageNumber, token.pageNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, position, pageNumber, fontSize);
	}
}