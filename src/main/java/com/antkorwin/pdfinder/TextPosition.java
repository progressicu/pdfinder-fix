package com.antkorwin.pdfinder;

import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created on 10/07/2020
 * <p>
 * Describe a position of text token in the PDF file.
 *
 * @author Korovin Anatoliy
 */
@Data
@Builder
@AllArgsConstructor
public class TextPosition {

	private float x;
	private float y;
	private float width;
	private float height;


	public static TextPosition fromRenderInfo(TextRenderInfo textRenderInfo) {
		float xStart = textRenderInfo.getBaseline().getStartPoint().get(Vector.I1);
		float xEnd = textRenderInfo.getBaseline().getEndPoint().get(Vector.I1);
		float yStart = textRenderInfo.getBaseline().getStartPoint().get(Vector.I2);
		float yEnd = textRenderInfo.getBaseline().getEndPoint().get(Vector.I2);
		return new TextPosition(xStart,
		                        yStart,
		                        Math.abs(xEnd - xStart),
		                        Math.abs(yEnd - yStart));
	}

	public float getLeft() {
		return x;
	}

	public float getRight() {
		return x + width;
	}

	public float getTop() {
		return y + height;
	}

	public float getBottom() {
		return y;
	}
}
