package com.relatos.catalogue.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.relatos.catalogue.model.Book;
import com.relatos.catalogue.repository.BookRepository;
import com.relatos.catalogue.utils.ISBNGenerator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Validator validator;
    private final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    public BookService(BookRepository bookRepository, Validator validator) {
        this.bookRepository = bookRepository;
        this.validator = validator;
    }

    public List<Book> getAllBooks() {
        logger.debug("[BookCatalogue - getAllBooks] Fetching all books.");
        List<Book> books = bookRepository.findAll();
        logger.debug("[BookCatalogue - getAllBooks] Found {} books.", books.size());
        return books;
    }

    public Book getBookByISBN(long isbn) {
        logger.debug("[BookCatalogue - getBookByISBN] Searching for book with ISBN: {}", isbn);
        try {
            Book book = bookRepository.findById(isbn)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN " + isbn));
            logger.info("[BookCatalogue - getBookByISBN] Successfully found book: {}", book);
            return book;
        } catch (IllegalArgumentException e) {
            logger.warn("[BookCatalogue - getBookByISBN] Book not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[BookCatalogue - getBookByISBN] Unexpected error: {}", e.getMessage());
            throw new RuntimeException("An unexpected error occurred while retrieving the book.");
        }
    }

    public List<Book> searchBooks(String title, String author, String genre, Long isbn, Double rate, Boolean display) {
        logger.debug("[BookCatalogue - searchBooks] Searching with filters: title={}, author={}, genre={}, isbn={}, rate={}, display={}",
                title, author, genre, isbn, rate, display);
        List<Book> books = bookRepository.findAll();

        List<Book> filteredBooks = books.stream()
                .filter(book -> title == null || book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(book -> author == null || book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(book -> genre == null || book.getGenre().stream().anyMatch(g -> g.equalsIgnoreCase(genre)))
                .filter(book -> isbn == null || book.getISBN() == isbn)
                .filter(book -> rate == null || book.getRate() == rate)
                .filter(book -> display == null || book.isDisplay() == display)
                .collect(Collectors.toList());

        logger.debug("[BookCatalogue - searchBooks] Found {} books matching the criteria.", filteredBooks.size());
        return filteredBooks;
    }

    public Book createBook(Book book) {
        logger.debug("[BookCatalogue - createBook] Creating book: {}", book);
        if (bookRepository.findByTitleContainingIgnoreCase(book.getTitle()).stream()
                .anyMatch(existingBook -> existingBook.getTitle().equalsIgnoreCase(book.getTitle()))) {
            logger.warn("[BookCatalogue - createBook] Attempt to create a book with duplicate title: '{}'", book.getTitle());
            throw new IllegalArgumentException("A book with the same title already exists.");
        }

        try {
            book.setISBN(Long.parseLong(ISBNGenerator.generateISBN()));
            Book savedBook = bookRepository.save(book);
            logger.info("[BookCatalogue - createBook] Successfully created book: '{}'", savedBook.getTitle());
            return savedBook;
        } catch (Exception e) {
            logger.error("[BookCatalogue - createBook] Failed to create book: {}. Error: {}", book, e.getMessage());
            throw new RuntimeException("An unexpected error occurred while creating the book.");
        }
    }

    public Book updateBook(long isbn, Book updateBook) {
        logger.debug("[BookCatalogue - updateBook] Starting update for book with ISBN: {}", isbn);
        try {
            Set<ConstraintViolation<Book>> violations = validator.validate(updateBook);
            if (!violations.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder();
                violations.forEach(violation -> errorMessages.append(violation.getPropertyPath())
                        .append(": ").append(violation.getMessage()).append("; "));
                logger.warn("[Validation Error] {}", errorMessages);
                throw new IllegalArgumentException("Validation failed: " + errorMessages);
            }

            Book existingBook = bookRepository.findById(isbn)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN " + isbn));
            logger.debug("[BookCatalogue - updateBook] Found book: {}", existingBook);

            existingBook.setTitle(updateBook.getTitle());
            existingBook.setAuthor(updateBook.getAuthor());
            existingBook.setPrice(updateBook.getPrice());
            existingBook.setCover(updateBook.getCover());
            existingBook.setDescription(updateBook.getDescription());
            existingBook.setPublicationDate(updateBook.getPublicationDate());
            existingBook.setGenre(updateBook.getGenre());
            existingBook.setRate(updateBook.getRate());
            existingBook.setDisplay(updateBook.isDisplay());
            logger.debug("[BookCatalogue - updateBook] Updated book details: {}", existingBook);

            Book updatedBook = bookRepository.save(existingBook);
            logger.info("[BookCatalogue - updateBook] Successfully updated book with ISBN: {}", isbn);

            return updatedBook;

        } catch (IllegalArgumentException e) {
            logger.warn("[BookCatalogue - updateBook] Validation or client error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("[BookCatalogue - updateBook] Unexpected error while updating book with ISBN: {}. Error: {}", isbn, e.getMessage());
            throw new RuntimeException("An unexpected error occurred while updating the book.");
        }
    }

    public Book partialUpdateBook(long isbn, Book updateBook) {
        logger.debug("[BookCatalogue - partialUpdateBook] Starting partial update for book with ISBN: {}", isbn);
        Book existingBook = bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN " + isbn));

        if (updateBook.getPrice() > 0) {
            existingBook.setPrice(updateBook.getPrice());
            logger.debug("[BookCatalogue - partialUpdateBook] Updated price to: {}", updateBook.getPrice());
        } else if (updateBook.getPrice() <= 0) {
            logger.warn("[BookCatalogue - partialUpdateBook] Invalid price provided: {}", updateBook.getPrice());
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        Book savedBook = bookRepository.save(existingBook);
        logger.info("[BookCatalogue - partialUpdateBook] Successfully partially updated book with ISBN: {}", isbn);
        return savedBook;
    }

    public boolean deleteBookByISBN(long isbn) {
        logger.debug("[BookCatalogue - deleteBookByISBN] Starting deletion for book with ISBN: {}", isbn);

        if (!bookRepository.existsById(isbn)) {
            logger.warn("[BookCatalogue - deleteBookByISBN] No books with ISBN {} were found to delete.", isbn);
            return false;
        }

        bookRepository.deleteById(isbn);
        logger.info("[BookCatalogue - deleteBookByISBN] Successfully deleted book with ISBN: {}", isbn);
        return true;
    }
}