package com.relatos.catalogue.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.relatos.catalogue.validation.PartialUpdate;

import jakarta.validation.Valid;

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
				logger.error("[BookCatalogue - getAllBooks] The book list is empty. Current size: {}.", book.size());
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
			return ResponseEntity.ok(book);
		} catch (IllegalArgumentException e) {
			logger.warn("[BookCatalogue - getBooksByISBN] Client error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			logger.error("[BookCatalogue - getBooksByISBN] Server error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "An unexpected error occurred."));
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
	public ResponseEntity<?> searchBooks(
	        @RequestParam(required = false) String title,
	        @RequestParam(required = false) String author,
	        @RequestParam(required = false) String genre,
	        @RequestParam(required = false) Long isbn,
	        @RequestParam(required = false) Double rate,
	        @RequestParam(required = false) Boolean display) {
	    try {
	        List<Book> books = bookService.searchBooks(title, author, genre, isbn, rate, display);
	        if (books.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Map.of("error", "No books matching the criteria were found."));
	        }
	        return ResponseEntity.ok(books);
	    } catch (Exception e) {
	        logger.error("[BookCatalogue - searchBooks] Unexpected error: {}", e.getMessage());
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
	public ResponseEntity<?> createBook(@RequestBody @Valid Book book) {
		try {
			Book createdBook = bookService.createBook(book);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
		} catch (IllegalArgumentException e) {
			logger.warn("[BookCatalogue - createBook] Client error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			logger.error("[BookCatalogue - createBook] Server error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "An unexpected error occurred."));
		}
	}

	/**
	 * 
	 * @param isbn
	 * @param updateBook
	 * @return
	 */
	@PutMapping("/{isbn}")
	public ResponseEntity<?> updateBook(@PathVariable long isbn, @RequestBody Book updateBook) {
	    try {
	        Book updatedBook = bookService.updateBook(isbn, updateBook);
	        return ResponseEntity.ok(updatedBook);
	    } catch (IllegalArgumentException e) {
	        logger.warn("[BookCatalogue - updateBook] Client error: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
	    } catch (Exception e) {
	        logger.error("[BookCatalogue - updateBook] Server error: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
	    }
	}

	/**
	 * 
	 * @param isbn
	 * @param updateBook
	 * @return
	 */
	@PatchMapping("/{isbn}")
	public ResponseEntity<?> partialUpdateBook(@Valid @PathVariable long isbn,
			@Validated(PartialUpdate.class) @RequestBody Book updateBook) {
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
	public ResponseEntity<?> deleteBookByISBN(@Valid @PathVariable long isbn) {
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
