package com.relatos.catalogue.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
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
	@io.swagger.v3.oas.annotations.Operation(
		    summary = "Obtener todos los libros",
		    description = "Devuelve una lista de todos los libros disponibles en el catálogo.",
		    responses = {
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "200",
		            description = "Lista de libros obtenida con éxito",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "500",
		            description = "Error inesperado en el servidor",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema()
		            )
		        )
		    }
		)
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
	@io.swagger.v3.oas.annotations.Operation(
		    summary = "Obtener un libro por ISBN",
		    description = "Devuelve los detalles de un libro específico identificado por su ISBN.",
		    parameters = {
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "isbn",
		            description = "ISBN del libro a buscar",
		            required = true,
		            example = "9781234567890"
		        )
		    },
		    responses = {
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "200",
		            description = "Libro encontrado con éxito",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "404",
		            description = "Libro no encontrado",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "500",
		            description = "Error inesperado en el servidor",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        )
		    }
		)
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
	@io.swagger.v3.oas.annotations.Operation(
		    summary = "Buscar libros por múltiples filtros",
		    description = "Permite buscar libros combinando varios criterios como título, autor, género, ISBN, calificación y visibilidad.",
		    parameters = {
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "title",
		            description = "Título del libro para filtrar",
		            required = false,
		            example = "1984"
		        ),
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "author",
		            description = "Autor del libro para filtrar",
		            required = false,
		            example = "George Orwell"
		        ),
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "genre",
		            description = "Género del libro para filtrar",
		            required = false,
		            example = "Science Fiction"
		        ),
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "isbn",
		            description = "ISBN del libro para filtrar",
		            required = false,
		            example = "9781234567890"
		        ),
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "rate",
		            description = "Calificación del libro para filtrar",
		            required = false,
		            example = "4.8"
		        ),
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "display",
		            description = "Visibilidad del libro (true o false)",
		            required = false,
		            example = "true"
		        )
		    },
		    responses = {
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "200",
		            description = "Libros encontrados con éxito",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "404",
		            description = "No se encontraron libros con los filtros especificados",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "500",
		            description = "Error inesperado en el servidor",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        )
		    }
		)
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
	@io.swagger.v3.oas.annotations.Operation(
		    summary = "Crear un nuevo libro",
		    description = "Agrega un nuevo libro al catálogo.",
		    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
		        description = "Detalles del libro a crear",
		        required = true,
		        content = @io.swagger.v3.oas.annotations.media.Content(
		            mediaType = "application/json",
		            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		        )
		    ),
		    responses = {
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "201",
		            description = "Libro creado con éxito",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "400",
		            description = "Datos inválidos proporcionados",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "500",
		            description = "Error inesperado en el servidor",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        )
		    }
		)
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
	@io.swagger.v3.oas.annotations.Operation(
		    summary = "Actualizar un libro completo",
		    description = "Actualiza todos los detalles de un libro existente.",
		    parameters = {
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "isbn",
		            description = "ISBN del libro a actualizar",
		            required = true,
		            example = "9781234567890"
		        )
		    },
		    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
		        description = "Detalles actualizados del libro",
		        required = true,
		        content = @io.swagger.v3.oas.annotations.media.Content(
		            mediaType = "application/json",
		            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		        )
		    ),
		    responses = {
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "200",
		            description = "Libro actualizado con éxito",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "404",
		            description = "Libro no encontrado",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "500",
		            description = "Error inesperado en el servidor",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        )
		    }
		)
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
	@io.swagger.v3.oas.annotations.Operation(
		    summary = "Actualizar parcialmente un libro",
		    description = "Actualiza solo los campos especificados de un libro existente.",
		    parameters = {
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "isbn",
		            description = "ISBN del libro a actualizar parcialmente",
		            required = true,
		            example = "9781234567890"
		        )
		    },
		    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
		        description = "Detalles parciales del libro a actualizar",
		        required = true,
		        content = @io.swagger.v3.oas.annotations.media.Content(
		            mediaType = "application/json",
		            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		        )
		    ),
		    responses = {
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "200",
		            description = "Libro actualizado parcialmente con éxito",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Book.class)
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "404",
		            description = "Libro no encontrado",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "400",
		            description = "Datos inválidos proporcionados",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "500",
		            description = "Error inesperado en el servidor",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        )
		    }
		)
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
	@io.swagger.v3.oas.annotations.Operation(
		    summary = "Eliminar un libro",
		    description = "Elimina un libro del catálogo según su ISBN.",
		    parameters = {
		        @io.swagger.v3.oas.annotations.Parameter(
		            name = "isbn",
		            description = "ISBN del libro a eliminar",
		            required = true,
		            example = "9781234567890"
		        )
		    },
		    responses = {
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "204",
		            description = "Libro eliminado con éxito"
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "404",
		            description = "Libro no encontrado",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        ),
		        @io.swagger.v3.oas.annotations.responses.ApiResponse(
		            responseCode = "500",
		            description = "Error inesperado en el servidor",
		            content = @io.swagger.v3.oas.annotations.media.Content(
		                mediaType = "application/json",
		                schema = @io.swagger.v3.oas.annotations.media.Schema
		            )
		        )
		    }
		)
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
