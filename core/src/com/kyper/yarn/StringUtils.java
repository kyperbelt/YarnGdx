package com.kyper.yarn;

public class StringUtils {
	private static final int START_CAPACITY = 200;
	private static StringBuilder string_builder;

	private static StringBuilder getBuilder() {
		if (string_builder == null)
			string_builder = new StringBuilder(START_CAPACITY);
		return string_builder;
	}

	public static String format(String format_string, Object... params) {
		StringBuilder builder = getBuilder();
		builder.setLength(0);

		builder.append(format_string);

		int found_start = 0;
		int found_end = 0;
		int last_param_used = 1;
		int paramd1 = 0;	
		int offset = 0;

		for (int i = 0; i < format_string.length(); i++) {
			char c = format_string.charAt(i);
			
			if (c == '%') {
				if(i > 0) {
					char p = format_string.charAt(i-1);
					if(p == '\\')
						continue;
				}
				paramd1 = last_param_used;
				found_start = i;
				i++;
				while (i < format_string.length() && c != 's' && c != 'd') {
					
					c = format_string.charAt(i);
					if (isDigit(c)) {
						paramd1 = getNumericValue(c);
						if (isDigit(format_string.charAt(i + 1))) {
							i++;
							paramd1 *= 10;
							c = format_string.charAt(i);
							int paramd2 = getNumericValue(c);
							paramd1 += paramd2;
						}
						if (paramd1 > params.length)
							throw new IllegalArgumentException(
									"Illegal Format String [\"" + format_string + "\"] not enough params");

					}
					i++;
				}
				found_end = i;
				if (paramd1 == last_param_used) {
					last_param_used++;
				}
				String paramused = params[paramd1-1].toString();
				builder.replace(found_start+offset, found_end+offset, paramused);
				offset += paramused.length()-(found_end-found_start);
			}
		}

		return builder.toString();
	}
	
	public static boolean isDigit(char c) {
		switch(c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return true;
		}
		return false;
	}
	
	public static int getNumericValue(char c) {
		switch(c) {
		case '0': return 0;
		case '1': return 1;
		case '2': return 2;
		case '3': return 3;
		case '4': return 4;
		case '5': return 5;
		case '6': return 6;
		case '7': return 7;
		case '8': return 8;
		case '9': return 9;
		}
		return -1;
	}

}
