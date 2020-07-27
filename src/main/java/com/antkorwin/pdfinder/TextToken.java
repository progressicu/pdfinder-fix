package com.antkorwin.pdfinder;


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
}