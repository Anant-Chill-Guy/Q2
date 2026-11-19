package com.example.library.controller;

import com.example.library.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final List<Book> books = new ArrayList<>();

    @PostMapping
    public synchronized ResponseEntity<?> addBook(@RequestBody Book book) {
        if (!valid(book, true)) {
            return ResponseEntity.badRequest().body("Invalid book details.");
        }
        if (find(book.getId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book ID already exists.");
        }
        books.add(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping
    public synchronized ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(new ArrayList<>(books));
    }

    @GetMapping("/{id}")
    public synchronized ResponseEntity<Book> getBook(@PathVariable Integer id) {
        Book book = find(id);
        return book == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    public synchronized ResponseEntity<?> updateBook(@PathVariable Integer id, @RequestBody Book details) {
        Book book = find(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        if (!valid(details, false)) {
            return ResponseEntity.badRequest().body("Invalid book details.");
        }
        book.setTitle(details.getTitle());
        book.setAuthor(details.getAuthor());
        book.setCategory(details.getCategory());
        book.setAvailableCopies(details.getAvailableCopies());
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    public synchronized ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        Book book = find(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        books.remove(book);
        return ResponseEntity.noContent().build();
    }

    private Book find(Integer id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        return null;
    }

    private boolean valid(Book book, boolean requireId) {
        return book != null
                && (!requireId || (book.getId() != null && book.getId() > 0))
                && book.getTitle() != null && !book.getTitle().isBlank()
                && book.getAuthor() != null && !book.getAuthor().isBlank()
                && book.getCategory() != null && !book.getCategory().isBlank()
                && book.getAvailableCopies() != null && book.getAvailableCopies() >= 0;
    }
}
