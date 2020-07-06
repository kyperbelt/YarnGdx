package com.kyper.yarn.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	private Pattern pattern;
	private StringBuilder stringBuilder;

	public Regex(String pattern) {
		this.pattern = Pattern.compile(pattern);
		this.stringBuilder = new StringBuilder();
	}

	public Regex(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return pattern.toString();
	}

	public Matcher match(CharSequence text) {
		return pattern.matcher(text);
	}

	public Matcher match(String text, int begin_index) {
		stringBuilder.setLength(0);
		stringBuilder.append(text, begin_index, text.length());
		return match(stringBuilder);
	}

	public Matcher match(String text, int begin_index, int end_index) {
		stringBuilder.setLength(0);
		stringBuilder.append(text, begin_index, end_index);
		return match(stringBuilder);
	}
}