package com.antkorwin.pdfinder.tokenizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created on 24/07/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SubToken {

	private String token;
	private int startIndex;

	private String original;
}
