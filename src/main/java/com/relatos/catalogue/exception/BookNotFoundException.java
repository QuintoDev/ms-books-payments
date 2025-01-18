package com.relatos.catalogue.exception;

@SuppressWarnings("serial")
public class BookNotFoundException extends RuntimeException {
	public BookNotFoundException(String message) {
		super(message);
	}
}