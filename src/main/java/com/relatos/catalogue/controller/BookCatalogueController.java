package com.relatos.catalogue.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.relatos.catalogue.model.Book;
import com.relatos.catalogue.service.BookService;

@RestController
@RequestMapping("/books")
public class BookCatalogueController {

	private final BookService bookService;
	private final Logger logger = LoggerFactory.getLogger(BookService.class);

	@Autowired
	public BookCatalogueController(BookService bookService) {
		this.bookService = bookService;
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseEntity<?> getBooks() {
		try {
			List<Book> book = bookService.getAllBooks();
			if (book.isEmpty()) {
				logger.warn("[BookCatalogue - getAllBooks] The book list is empty. Current size: {}.", book.size());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "The book list is empty."));
			} else {
				logger.info("[BookCatalogue - getAllBooks] Successfully retrieved the list of books. Total books: {}.",
						book.size());
				logger.debug("[BookCatalogue - getBooksByISBN] Book details: {}.", book.toString());
				return ResponseEntity.ok(book);
			}
		} catch (Exception e) {
			logger.error("[BookCatalogue - getAllBooks] An error occurred while retrieving the list of books: {}",
					e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "An unexpected error occurred."));
		}
	}

	/**
	 * 
	 * @param isbn
	 * @return
	 */
	@GetMapping("/{isbn}")
	public ResponseEntity<?> getBooksByISBN(@PathVariable long isbn) {
		try {
			Book book = bookService.getBookByISBN(isbn);
			logger.info("[BookCatalogue - getBooksByISBN] Successfully found a book with ISBN: {}.", isbn);
			logger.debug("[BookCatalogue - getBooksByISBN] Book details: {}.", book.toString());
			return ResponseEntity.ok(book);
		} catch (Exception e) {
			logger.error("[BookCatalogue - getBooksByISBN] No book found with the provided ISBN: {}.", isbn);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
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
	@GetMapping("/search")
	public ResponseEntity<?> searchBooks(@RequestParam(required = false) String title,
			@RequestParam(required = false) String author, @RequestParam(required = false) String publicationDate,
			@RequestParam(required = false) String genre, @RequestParam(required = false) Long isbn,
			@RequestParam(required = false) Double rate, @RequestParam(required = false) Boolean display) {
		try {
			List<Book> books = bookService.searchBooks(title, author, publicationDate, genre, isbn, rate, display);
			if (books.isEmpty()) {
				logger.error(
						"[BookCatalogue - searchBooks] No books match the given criteria. Title: {}, Author: {}, Genre: {}, PublicationDate: {}, ISBN: {}, Rate: {}, Display: {}. ",
						title, author, genre, publicationDate, isbn, rate, display);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("error", "No books matching the criteria were found."));
			}
			logger.info("[BookCatalogue - searchBooks] Books matching the search criteria were found. Count: {}.",
					books.size());
			logger.debug("[BookCatalogue - searchBooks] Matching books: {}.", books.toString());
			return ResponseEntity.ok(books);
		} catch (Exception e) {
			logger.error(
					"[BookCatalogue - searchBooks] An unexpected error occurred while searching for books. Error: {}",
					e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "An unexpected error occurred."));
		}
	}

	/**
	 * 
	 * @param createBook
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> createBook(@RequestBody Book createBook) {
		try {
			Book book = bookService.createBook(createBook);
			logger.info("[BookCatalogue - createBook] Successfully created a book titled '{}'.", createBook.getTitle());
			return ResponseEntity.status(HttpStatus.CREATED).body(book);
		} catch (Exception e) {
			logger.error("[BookCatalogue - createBook] Failed to create the book titled '{}'. Error: {}",
					createBook.getTitle(), e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "An error occurred while creating the book."));
		}
	}

	/**
	 * 
	 * @param isbn
	 * @param updateBook
	 * @return
	 */
	@PutMapping("/{isbn}")
	public ResponseEntity<?> updateFullBook(@PathVariable long isbn, @RequestBody Book updateBook) {
		try {
			Book book = bookService.updateBook(isbn, updateBook);
			logger.info("[BookCatalogue - updateFullBook] Successfully updated the book titled '{}'.",
					updateBook.getTitle());
			logger.debug("[BookCatalogue - updateFullBook] Updated book details: {}.", updateBook.toString());
			return ResponseEntity.ok(book);
		} catch (Exception e) {
			logger.error("[BookCatalogue - updateFullBook] Failed to update the book with ISBN {}.", isbn);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * 
	 * @param isbn
	 * @param updateBook
	 * @return
	 */
	@PatchMapping("/{isbn}")
	public ResponseEntity<?> partialUpdateBook(@PathVariable long isbn, @RequestBody Book updateBook) {
		try {
			Book book = bookService.partialUpdateBook(isbn, updateBook);
			logger.info(
					"[BookCatalogue - partialUpdateBook] Successfully performed a partial update on the book titled '{}'.",
					book.getTitle());
			logger.debug("[BookCatalogue - partialUpdateBook] Partially updated book details: {}.", book.toString());
			return ResponseEntity.ok(book);
		} catch (Exception e) {
			logger.error(
					"[BookCatalogue - partialUpdateBook] Error: Failed to partially update the book with ISBN: {}.",
					isbn);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * 
	 * @param isbn
	 * @return
	 */
	@DeleteMapping("/{isbn}")
	public ResponseEntity<?> deleteBookByISBN(@PathVariable long isbn) {
		try {
			boolean isDeleted = bookService.deleteBookByISBN(isbn);
			if (isDeleted) {
				logger.info("[BookCatalogue - deleteBookByISBN] Successfully deleted the book with ISBN: {}.", isbn);
				return ResponseEntity.noContent().build();
			} else {
				logger.error("[BookCatalogue - deleteBookByISBN] Book not found with ISBN: {}.", isbn);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("error", "The book with the provided ISBN was not found."));
			}
		} catch (Exception e) {
			logger.error(
					"[BookCatalogue - deleteBookByISBN] Unexpected error while trying to delete the book with ISBN: {}. Error: {}",
					isbn, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "An unexpected error occurred."));
		}
	}

}
