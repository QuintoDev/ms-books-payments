package com.relatos.catalogue.model;

import java.util.ArrayList;

import com.relatos.catalogue.validation.PartialUpdate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Book {
	private long ISBN;

	@NotNull(message = "Title cannot be null")
	@NotBlank(message = "Title cannot be empty")
	private String title;

	private String author;

	@Positive(message = "Price must be greater than 0", groups = PartialUpdate.class)
	private double price;

	private String cover;
	private String description;
	private String publicationDate;
	private ArrayList<String> genre;
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

	public String getcover() {
		return cover;
	}

	public void setcover(String cover) {
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

	public ArrayList<String> getGenre() {
		return genre;
	}

	public void setGenre(ArrayList<String> genre) {
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
