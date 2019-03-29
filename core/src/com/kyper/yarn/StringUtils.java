package com.kyper.yarn;

public class StringUtils {
  private static final int           START_CAPACITY = 200;
  private static       StringBuilder stringBuilder;

  private static StringBuilder getBuilder(){
    if (stringBuilder == null) stringBuilder = new StringBuilder(START_CAPACITY);
    return stringBuilder;
  }

  public static String format(String formatString, Object... params){
    StringBuilder builder = getBuilder();
    builder.setLength(0);

    builder.append(formatString);

    int foundStart    = 0;
    int foundEnd      = 0;
    int lastParamUsed = 1;
    int paramd1       = 0;
    int offset        = 0;

    for (int i = 0; i < formatString.length(); i++) {
      char c = formatString.charAt(i);

      if (c == '%') {
        if (i > 0) {
          char p = formatString.charAt(i - 1);
          if (p == '\\') continue;
        }
        paramd1 = lastParamUsed;
        foundStart = i;
        i++;
        while (i < formatString.length() && c != 's' && c != 'd') {

          c = formatString.charAt(i);
          if (isDigit(c)) {
            paramd1 = getNumericValue(c);
            if (isDigit(formatString.charAt(i + 1))) {
              i++;
              paramd1 *= 10;
              c = formatString.charAt(i);
              int paramd2 = getNumericValue(c);
              paramd1 += paramd2;
            }
            if (paramd1 > params.length)
              throw new IllegalArgumentException("Illegal Format String [\"" + formatString + "\"] not enough params");

          }
          i++;
        }
        foundEnd = i;
        if (paramd1 == lastParamUsed) {
          lastParamUsed++;
        }
        String paramused = params[paramd1 - 1].toString();
        builder.replace(foundStart + offset, foundEnd + offset, paramused);
        offset += paramused.length() - (foundEnd - foundStart);
      }
    }

    return builder.toString();
  }

  public static boolean isDigit(char c){
    switch (c) {
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

  public static int getNumericValue(char c){
    switch (c) {
      case '0':
        return 0;
      case '1':
        return 1;
      case '2':
        return 2;
      case '3':
        return 3;
      case '4':
        return 4;
      case '5':
        return 5;
      case '6':
        return 6;
      case '7':
        return 7;
      case '8':
        return 8;
      case '9':
        return 9;
    }
    return -1;
  }

}
