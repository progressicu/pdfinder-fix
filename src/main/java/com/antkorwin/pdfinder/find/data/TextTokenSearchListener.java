package com.antkorwin.pdfinder.find.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.antkorwin.pdfinder.TextPosition;
import com.antkorwin.pdfinder.TextToken;
import com.antkorwin.pdfinder.find.SinglePageTokenData;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.itextpdf.kernel.pdf.canvas.parser.EventType.RENDER_TEXT;


/**
 * iText event listener to search tokens in PDF file.
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class TextTokenSearchListener implements IEventListener,
                                                SinglePageTokenData {

	private final int pageNumber;
	private final int threshold;

	@Getter
	private Map<Float, List<TextToken>> textTokenMap = new HashMap<>();

//	public static Map<PdfFont, Map<String, Float>> glyphSizeMap = new ConcurrentHashMap<>();


	@Override
	public Set<EventType> getSupportedEvents() {
		HashSet<EventType> types = new HashSet<>();
		types.add(RENDER_TEXT);
		return types;
	}

	@Override
	public void eventOccurred(IEventData iEventData, EventType eventType) {

		TextRenderInfo info = (TextRenderInfo) iEventData;
		String text = info.getText();
		TextPosition textPosition = TextPosition.fromRenderInfo(info);

//		glyphSizeMap.compute(info.getFont(), (font, oldMap) -> {
//			if (oldMap == null) {
//				ConcurrentHashMap<String, Float> glyphWidth = new ConcurrentHashMap<>();
//				glyphWidth.put(text, textPosition.getWidth());
//				return glyphWidth;
//			} else {
//				oldMap.computeIfAbsent(text, key -> textPosition.getWidth());
//				return oldMap;
//			}
//		});

		textTokenMap.compute(textPosition.getY(), (lineY, tokens) -> {

			TextToken newToken = TextToken.builder()
			                              .text(text)
			                              .position(textPosition)
			                              .pageNumber(pageNumber)
			                              .font(info.getFont())
			                              .fontSize(info.getFontSize())
			                              .fontMatrix(matrix(info))
			                              .build();

			if (tokens == null) {
				tokens = new ArrayList<>();
				return addNewToken(tokens, newToken);
			}

			Optional<TextToken> lastToken = getLastToken(tokens);
			if (lastToken.isPresent() &&
			    lastToken.get().getFont().equals(newToken.getFont()) &&
			    getDistanceBetween(lastToken.get(), newToken) < threshold) {

				TextToken last = lastToken.get();
				last.setText(last.getText() + text);
				float distance = getDistanceBetween(lastToken.get(), newToken);
				last.getPosition().setWidth(last.getPosition().getWidth() +
				                            textPosition.getWidth() +
				                            distance);

				last.getPosition().setHeight(last.getPosition().getHeight() + textPosition.getHeight());
			} else {
				addNewToken(tokens, newToken);
				return tokens;
			}

			return tokens;
		});
	}

	private Matrix matrix(TextRenderInfo textRenderInfo) {
		return textRenderInfo.getTextMatrix()
		                     .multiply(textRenderInfo.getGraphicsState().getCtm());
	}

	private List<TextToken> addNewToken(List<TextToken> tokens, TextToken token) {
		tokens.add(token);
		return tokens;
	}

	// todo check the same font and size ?
	private float getDistanceBetween(TextToken firstToken, TextToken secondToken) {

		if (firstToken.getPosition().getLeft() > secondToken.getPosition().getLeft()) {
			TextToken tmp = firstToken;
			firstToken = secondToken;
			secondToken = tmp;
		}

		return Math.abs(firstToken.getPosition().getRight() - secondToken.getPosition().getLeft());
	}

	private Optional<TextToken> getLastToken(List<TextToken> tokens) {
		if (tokens == null || tokens.size() == 0) {
			return Optional.empty();
		}
		return Optional.of(tokens.get(tokens.size() - 1));
	}


	@Override
	public Map<Float, List<TextToken>> result() {
		return textTokenMap;
	}
}