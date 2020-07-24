package com.antkorwin.pdfinder.find;

import java.util.List;
import java.util.Map;

import com.antkorwin.pdfinder.TextToken;

public interface OnePageResult {

	Map<Float, List<TextToken>> result();
}
