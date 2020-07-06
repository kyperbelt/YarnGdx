package com.kyper.yarn.compiler;

import java.util.Locale;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

public class ParseException extends RuntimeException {
	private static final long serialVersionUID = -6422941521497633431L;

	public int line_number = 0;

	public ParseException(String message) {
		super(message);
	}

	public ParseException(String message, Exception cause) {
		super(message, cause);
	}

	public static ParseException make(ParserRuleContext context, String message) {
		int line_number = context.getStart().getLine();
		
		int start = context.getStart().getStartIndex();
		int end = context.getStop().getStopIndex();
		String body = context.getStart().getInputStream().getText(new Interval(start, end));
		String theMessage = String.format(Locale.getDefault(),"Error on line %d\n%s\n%s",line_number,body,message);
		
		ParseException e = new ParseException(theMessage);
		
//		ArrayList<String> expected_type_names = new ArrayList<String>();
//		for (TokenType type : expected_types) {
//			expected_type_names.add(type.name());
//		}
//		String possible_values = String.join(",", expected_type_names);
//		String message = String.format("Line %1$s:%2$s: Expected %3$s, but found %4$s", line_number,
//				found_token.column_number, possible_values, found_token.type.name());
//		ParseException e = new ParseException(message);
//		e.line_number = line_number;
		return e;
	}

}