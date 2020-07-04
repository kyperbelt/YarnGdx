package com.kyper.yarn;

public class DialogueException extends IllegalStateException{
	private static final long serialVersionUID = 2246870310082702640L;
	
	public DialogueException() {
	}
	
	public DialogueException(String message) {
		super(message);
	}
}
