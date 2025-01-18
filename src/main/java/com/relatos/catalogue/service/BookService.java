package com.relatos.catalogue.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.relatos.catalogue.exception.BookNotFoundException;
import com.relatos.catalogue.exception.DuplicateBookException;
import com.relatos.catalogue.model.Book;
import com.relatos.catalogue.utils.ISBNGenerator;

@Service
public class BookService {

	private final List<Book> books = new ArrayList<Book>();
	private final Logger logger = LoggerFactory.getLogger(BookService.class);

	/**
	 * 
	 * @return
	 */
	public List<Book> getAllBooks() {
		return books;
	}

	/**
	 * 
	 * @param isbn
	 * @return
	 */
	public Book getBookByISBN(long isbn) {
		return books.stream().filter(book -> book.getISBN() == isbn).findFirst()
				.orElseThrow(() -> new BookNotFoundException("Book not found with ISBN " + isbn));
	}

	/**
	 * 
	 * @param title
	 * @param author
	 * @param publicationDate
	 * @param genre
	 * @param isbn
	 * @param rate
	 * @param display
	 * @return
	 */
	public List<Book> searchBooks(String title, String author, String publicationDate, String genre, Long isbn,
			Double rate, Boolean display) {
		return books.stream()
				.filter(book -> (title == null || book.getTitle().equalsIgnoreCase(title))
						&& (author == null || book.getAuthor().equalsIgnoreCase(author))
						&& (publicationDate == null || book.getPublicationDate().equals(publicationDate))
						&& (genre == null || book.getGenre().contains(genre))
						&& (isbn == null || book.getISBN() == isbn) && (rate == null || book.getRate() == rate)
						&& (display == null || book.isDisplay() == display))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @param book
	 * @return
	 */
	public Book createBook(Book book) {
		if (books.stream().anyMatch(b -> b.getTitle().equalsIgnoreCase(book.getTitle()))) {
			throw new DuplicateBookException("A book with the same title already exists.");
		}
		book.setISBN(Long.parseLong(ISBNGenerator.generateISBN()));
		books.add(book);
		logger.info("The book {} was created successfully.", book.toString());
		return book;
	}

	/**
	 * 
	 * @param isbn
	 * @param updateBook
	 * @return
	 */
	public Book updateBook(long isbn, Book updateBook) {
		Book book = books.stream().filter(b -> b.getISBN() == isbn).findFirst()
				.orElseThrow(() -> new BookNotFoundException("Book not found with ISBN " + isbn));
		book.setTitle(updateBook.getTitle());
		book.setAuthor(updateBook.getAuthor());
		book.setPrice(updateBook.getPrice());
		book.setcover(updateBook.getcover());
		book.setDescription(updateBook.getDescription());
		book.setPublicationDate(updateBook.getPublicationDate());
		book.setGenre(updateBook.getGenre());
		book.setRate(updateBook.getRate());
		book.setDisplay(updateBook.isDisplay());
		return book;
	}

	/**
	 * 
	 * @param isbn
	 * @param updateBook
	 * @return
	 */
	public Book partialUpdateBook(long isbn, Book updateBook) {
		Book book = books.stream().filter(b -> b.getISBN() == isbn).findFirst()
				.orElseThrow(() -> new BookNotFoundException("Book not found with ISBN " + isbn));
		System.out.println(book);
		//book.setPrice(updateBook.getPrice());
		return book;
	}

	/**
	 * 
	 * @param isbn
	 * @return
	 */
	public boolean deleteBookByISBN(long isbn) {
		boolean removed = books.removeIf(book -> book.getISBN() == isbn);
		if (removed) {
			logger.info("The book with ISBN {} was removed.", isbn);
		} else {
			logger.error("No books with ISBN {} were found to delete.", isbn);
		}
		return removed;
	}

}
