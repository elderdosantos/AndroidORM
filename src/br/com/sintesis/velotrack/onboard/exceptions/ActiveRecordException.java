package br.com.sintesis.velotrack.onboard.exceptions;

public class ActiveRecordException extends Exception {

	private static final long serialVersionUID = 1;

	public ActiveRecordException (String message) {
		super(message);
	}
}