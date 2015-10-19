package cz.vutbr.wislab.symphony.sip.client;

public class NotInitializedException extends Exception {
	private static final long serialVersionUID = 8276497401862424419L;

	public NotInitializedException(String message) {
	        super(message);
	    }

	    public NotInitializedException(String message, Throwable throwable) {
	        super(message, throwable);
	    }
}
