package com.antkorwin.pdfinder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.antkorwin.pdfinder.find.PdfExtractResult;
import com.antkorwin.pdfinder.tokenizer.SubToken;
import com.antkorwin.pdfinder.tokenizer.WhiteSpaceSplitSubTokenStrategy;
import com.itextpdf.kernel.font.PdfFont;
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
public class TextTokenSearchListener implements IEventListener {

	private final int pageNumber;
	private final int threshold;

	@Getter
	private Map<Float, List<TextToken>> textTokenMap = new HashMap<>();

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

		textTokenMap.compute(textPosition.getY(), (lineY, tokens) -> {

			TextToken newToken = TextToken.builder()
			                              .text(text)
			                              .position(textPosition)
			                              .pageNumber(pageNumber)
			                              .font(info.getFont())
			                              .fontSize(info.getFontSize())
			                              .build();

			if (tokens == null) {
				tokens = new ArrayList<>();
				return addNewToken(tokens, newToken);
			}

			Optional<TextToken> lastToken = getLastToken(tokens);
			if (lastToken.isPresent()) {
				TextToken last = lastToken.get();
				// todo check the same font and size ?
				if (getDistanceBetween(last, newToken) < threshold) {
					last.setText(last.getText() + text);
					last.getPosition().setWidth(last.getPosition().getWidth() + textPosition.getWidth());
					last.getPosition().setHeight(last.getPosition().getHeight() + textPosition.getHeight());
				}
			} else {
				addNewToken(tokens, newToken);
				return tokens;
			}

			return tokens;
		});
	}

	public PdfExtractResult getExtractResult(){
		return new PdfExtractResult(()->this.textTokenMap);
	}

	private List<TextToken> addNewToken(List<TextToken> tokens, TextToken token) {
		tokens.add(token);
		return tokens;
	}

	private float getDistanceBetween(TextToken firstToken, TextToken secondToken) {

		if (firstToken.isEmptyToken() || secondToken.isEmptyToken()) {
			// it makes no sense to estimate the distance
			// if we have an empty token on the left or on the right.
			return 0;
		}

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
}