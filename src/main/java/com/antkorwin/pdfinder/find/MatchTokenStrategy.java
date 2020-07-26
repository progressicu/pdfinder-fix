package com.antkorwin.pdfinder.find;


import com.antkorwin.pdfinder.TextToken;

public interface MatchTokenStrategy {

	boolean matchToken(TextToken token, String searchString);
}
