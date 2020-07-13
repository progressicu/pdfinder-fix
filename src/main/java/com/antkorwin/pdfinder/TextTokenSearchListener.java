package com.antkorwin.pdfinder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

			TextToken newToken = new TextToken(text, textPosition, pageNumber);

			if (tokens == null) {
				tokens = new ArrayList<>();
				return addNewToken(tokens, newToken);
			}

			TextToken lastToken = getLastToken(tokens);
			if (getDistanceBetween(lastToken, newToken) < threshold) {
				lastToken.setText(lastToken.getText() + text);
				lastToken.getPosition().setWidth(lastToken.getPosition().getWidth() + textPosition.getWidth());
				lastToken.getPosition().setHeight(lastToken.getPosition().getHeight() + textPosition.getHeight());
			} else {
				addNewToken(tokens, newToken);
			}

			return tokens;
		});
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


	private TextToken getLastToken(List<TextToken> tokens) {
		if (tokens == null || tokens.size() == 0) {
			return TextToken.EMPTY_TOKEN;
		}
		return tokens.get(tokens.size() - 1);
	}

	/**
	 * find tokens by search string
	 */
	public List<TextToken> findTokens(String text) {
		List<TextToken> result = new ArrayList<>();

		SortedSet<Float> keys = new TreeSet<>(textTokenMap.keySet());
		for (Float key : keys) {
			List<TextToken> tokens = textTokenMap.get(key);
			tokens.stream()
			      .filter(t -> t.getText().contains(text))
			      .forEach(result::add);
		}
		return result;
	}

	/**
	 * find tokens by search string in boundary
	 */
	public List<TextToken> findTokensInBoundary(String text, Boundary boundary) {

		List<TextToken> result = new ArrayList<>();
		SortedSet<Float> keys = new TreeSet<>(textTokenMap.keySet());
		for (Float key : keys) {
			List<TextToken> tokens = textTokenMap.get(key);
			tokens.stream()
			      .filter(t -> t.getText().contains(text))
			      .filter(t -> boundary.isInBoundary(t.getPosition()))
			      .forEach(result::add);
		}
		return result;
	}
}