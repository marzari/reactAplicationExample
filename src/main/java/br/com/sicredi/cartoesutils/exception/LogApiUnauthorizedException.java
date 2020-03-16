package br.com.sicredi.cartoesutils.exception;

public class LogApiUnauthorizedException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public LogApiUnauthorizedException(final String message) {
		super(message);
	}
}
