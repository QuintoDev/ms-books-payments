package com.relatos.catalogue.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "books")
public class Book {

	@Id
	@Column(name = "isbn", nullable = false, unique = true)
	private long ISBN;

	@NotNull(message = "Title cannot be null")
	@NotBlank(message = "Title cannot be empty")
	@Size(max = 255, message = "Title cannot exceed 255 characters")
	@Column(nullable = false)
	private String title;

	@NotNull(message = "Author cannot be null")
	@NotBlank(message = "Author cannot be empty")
	@Size(max = 255, message = "Author cannot exceed 255 characters")
	@Column(nullable = false)
	private String author;

	@Positive(message = "Price must be greater than 0")
	@Column(nullable = false)
	private double price;

	@Pattern(regexp = "^(http|https)://.*$", message = "Cover must be a valid URL")
	private String cover;

	@Size(max = 5000, message = "Description cannot exceed 5000 characters")
	@Column(columnDefinition = "TEXT")
	private String description;

	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Publication date must follow the format yyyy-MM-dd")
	private String publicationDate;

	@ElementCollection
	@CollectionTable(name = "book_genre", joinColumns = @JoinColumn(name = "book_isbn"))
	@Column(name = "genre")
	@NotEmpty(message = "Genres cannot be empty")
	@Size(max = 10, message = "A book cannot have more than 10 genres")
	private List<String> genre = new ArrayList<>();

	@Min(value = 1, message = "Rate must be at least 1")
	@Max(value = 5, message = "Rate cannot exceed 5")
	private double rate;

	private boolean display;

	public long getISBN() {
		return ISBN;
	}

	public void setISBN(long iSBN) {
		ISBN = iSBN;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public List<String> getGenre() {
		return genre;
	}

	public void setGenre(List<String> genre) {
		this.genre = genre;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	@Override
	public String toString() {
		return "Book [ISBN=" + ISBN + ", title=" + title + ", author=" + author + ", price=" + price + ", cover="
				+ cover + ", description=" + description + ", publicationDate=" + publicationDate + ", genre=" + genre
				+ ", rate=" + rate + ", display=" + display + "]";
	}

}
