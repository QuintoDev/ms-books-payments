package com.relatos.catalogue.utils;

import java.util.Random;

public class ISBNGenerator {
	public static String generateISBN() {
		Random random = new Random();
		StringBuilder isbn = new StringBuilder("978");
		for (int i = 0; i < 9; i++) {
			isbn.append(random.nextInt(10));
		}
		isbn.append(calculateCheckDigit(isbn.toString()));
		return isbn.toString();
	}

	private static int calculateCheckDigit(String isbn) {
		int sum = 0;
		for (int i = 0; i < isbn.length(); i++) {
			int digit = Character.getNumericValue(isbn.charAt(i));
			sum += (i % 2 == 0) ? digit : digit * 3;
		}
		return (10 - (sum % 10)) % 10;
	}

}
