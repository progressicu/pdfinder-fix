package com.antkorwin.pdfinder;

import java.util.Comparator;

/**
 * Created on 06/08/2020
 * <p>
 * TODO: replace on the JavaDoc
 *
 * @author Korovin Anatoliy
 */
public class TextTokenComparator implements Comparator<TextToken> {

	@Override
	public int compare(TextToken t1, TextToken t2) {
		TextPosition p1 = t1.getPosition();
		TextPosition p2 = t2.getPosition();
		if (p1.equals(p2)) {
			return 0;
		}
		int compareY = Float.compare(p1.getTop(), p2.getTop());
		if (compareY == 0) {
			return Float.compare(p1.getLeft(), p2.getLeft());
		}
		return -compareY;
	}
}
