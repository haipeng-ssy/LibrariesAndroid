package com.haipeng.libraryforandroid.CharacterEncode;

import java.util.ArrayList;

import com.haipeng.libraryforandroid.CharacterEncode.HanziToPinyin.Token;


public class HanziToPinyinUtil {
	/**
	 * 传入中文转拼音
	 * @param input
	 * @return
	 */
	public static String getPinYin(String input) {
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					if (sb.length() > 0) {
						sb.append(' ');
					}
					sb.append(token.target);
					sb.append(' ');
					sb.append(token.source);
				} else {
					if (sb.length() > 0) {
						sb.append(' ');
					}
					sb.append(token.source);
				}
			}
		}
		return sb.toString().toLowerCase();
	}
}
