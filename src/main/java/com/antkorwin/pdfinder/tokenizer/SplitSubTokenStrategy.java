package com.antkorwin.pdfinder.tokenizer;

import java.util.List;

public interface SplitSubTokenStrategy {
	List<SubToken> split(String text);
}
