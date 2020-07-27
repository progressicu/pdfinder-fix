package com.antkorwin.pdfinder.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
public class SearchPhraseSplitSubTokenStrategy implements SplitSubTokenStrategy {

	private static final String SPECIAL_SYMBOLS = ".,!:;`~/?\\|'\"@#№$%^&*()-+=[]<>";

	@Override
	public List<SubToken> split(String text) {

		List<SubToken> result = new ArrayList<>();
		WhiteSpaceSplitSubTokenStrategy whiteSpaceSplitStrategy = new WhiteSpaceSplitSubTokenStrategy();

		for (SubToken subToken : whiteSpaceSplitStrategy.split(text)) {

			String token = subToken.getToken();
			int prevEndIndex = 0;
			for (int i = 0; i < token.length(); i++) {
				String currentChar = token.substring(i, i + 1);

				// если входит в спец-символы то сплитим еще на сабтокены:
				if (SPECIAL_SYMBOLS.contains(currentChar)) {

					// добавляем токен
					if (i > prevEndIndex) {
						String newTokenValue = token.substring(prevEndIndex, i);
						SubToken newToken = new SubToken(newTokenValue,
						                                 subToken.getStartIndex() + i - 1,
						                                 subToken.getOriginal());
						result.add(newToken);
					}

					// добавляем разделитель:
					SubToken specialToken = new SubToken(currentChar,
					                                     subToken.getStartIndex() + i,
					                                     subToken.getOriginal());
					result.add(specialToken);

					// сдвигаем
					prevEndIndex = i + 1;
				}
			}

			// добавляем крайний токен (или единственный если небыло сплитов):
			if (prevEndIndex < token.length()) {
				String newTokenValue = token.substring(prevEndIndex, token.length());
				SubToken newToken = new SubToken(newTokenValue,
				                                 subToken.getStartIndex() + prevEndIndex,
				                                 subToken.getOriginal());
				result.add(newToken);
			}
		}


		return result;
	}
}
