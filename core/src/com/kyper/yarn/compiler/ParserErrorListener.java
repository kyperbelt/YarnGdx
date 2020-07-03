package com.kyper.yarn.compiler;

import java.util.Locale;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public final class ParserErrorListener extends BaseErrorListener{
	
	private static final ParserErrorListener instance = new ParserErrorListener();
	public static ParserErrorListener getInstance() {
		return instance;
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		
		
		 StringBuilder builder = new StringBuilder();

         // the human readable message
         int[] format = new int[] { line, charPositionInLine + 1 };
         builder.append(String.format(Locale.getDefault(), "Error on line %d at position %d:\n", format));
         // the actual error message
         builder.append(System.lineSeparator());
         builder.append(msg);
//         #if DEBUG
//         builder.AppendLine($"Debug: Offending symbol type: {recognizer.Vocabulary.GetSymbolicName(offendingSymbol.Type)}");
//         #endif

         Token offender = (Token)offendingSymbol;
         
         if (offender.getTokenSource() != null) {
             // the line with the error on it
             String input = offender.getTokenSource().getInputStream().toString();
             String[] lines = input.split("\n");
             String errorLine = lines[line - 1];
             builder.append(System.lineSeparator());
             builder.append(errorLine);

             // adding indicator symbols pointing out where the error is on the line
             int start = offender.getStartIndex();
             int stop = offender.getStopIndex();
             if (start >= 0 && stop >= 0)
             {
                 // the end point of the error in "line space"
                 int end = (stop - start) + charPositionInLine + 1;
                 for (int i = 0; i < end; i++)
                 {
                     // move over until we are at the point we need to be
                     if (i >= charPositionInLine && i < end)
                     {
                         builder.append("^");
                     }
                     else
                     {
                         builder.append(" ");
                     }
                 }
             }
         }

         
         throw new ParseException(builder.toString());
		
	}

}
