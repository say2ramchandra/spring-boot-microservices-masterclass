package com.masterclass.config;

import com.masterclass.model.Author;
import com.masterclass.model.Book;
import com.masterclass.model.Category;
import com.masterclass.repository.AuthorRepository;
import com.masterclass.repository.BookRepository;
import com.masterclass.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Data Initializer
 * Seeds the database with sample data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing sample data...");

        // Create Categories
        Category fiction = new Category("Fiction", "Fictional literature");
        Category nonFiction = new Category("Non-Fiction", "Factual literature");
        Category sciFi = new Category("Science Fiction", "Science fiction and fantasy");
        Category mystery = new Category("Mystery", "Mystery and thriller books");
        Category tech = new Category("Technology", "Technical books");

        categoryRepository.saveAll(Arrays.asList(fiction, nonFiction, sciFi, mystery, tech));
        log.info("Created {} categories", categoryRepository.count());

        // Create Authors
        Author author1 = new Author(
            "J.K. Rowling",
            "United Kingdom",
            LocalDate.of(1965, 7, 31)
        );
        author1.setBio("British author, best known for the Harry Potter series");

        Author author2 = new Author(
            "George Orwell",
            "United Kingdom",
            LocalDate.of(1903, 6, 25)
        );
        author2.setBio("English novelist and essayist, journalist and critic");

        Author author3 = new Author(
            "Isaac Asimov",
            "United States",
            LocalDate.of(1920, 1, 2)
        );
        author3.setBio("American writer and professor of biochemistry");

        authorRepository.saveAll(Arrays.asList(author1, author2, author3));
        log.info("Created {} authors", authorRepository.count());

        // Create Books
        Book book1 = new Book(
            "Harry Potter and the Philosopher's Stone",
            "978-0-439-70818-8",
            new BigDecimal("25.99"),
            1997
        );
        book1.setDescription("First book in the Harry Potter series");
        book1.setStock(100);
        book1.setAuthor(author1);
        book1.addCategory(fiction);
        book1.addCategory(sciFi);

        Book book2 = new Book(
            "Harry Potter and the Chamber of Secrets",
            "978-0-439-06486-6",
            new BigDecimal("27.99"),
            1998
        );
        book2.setDescription("Second book in the Harry Potter series");
        book2.setStock(85);
        book2.setAuthor(author1);
        book2.addCategory(fiction);
        book2.addCategory(sciFi);

        Book book3 = new Book(
            "1984",
            "978-0-452-28423-4",
            new BigDecimal("15.99"),
            1949
        );
        book3.setDescription("Dystopian social science fiction novel");
        book3.setStock(150);
        book3.setAuthor(author2);
        book3.addCategory(fiction);
        book3.addCategory(sciFi);

        Book book4 = new Book(
            "Animal Farm",
            "978-0-452-28424-1",
            new BigDecimal("12.99"),
            1945
        );
        book4.setDescription("Allegorical novella reflecting events leading up to Russian Revolution");
        book4.setStock(120);
        book4.setAuthor(author2);
        book4.addCategory(fiction);

        Book book5 = new Book(
            "Foundation",
            "978-0-553-29335-7",
            new BigDecimal("18.99"),
            1951
        );
        book5.setDescription("First book in the Foundation series");
        book5.setStock(90);
        book5.setAuthor(author3);
        book5.addCategory(sciFi);

        Book book6 = new Book(
            "I, Robot",
            "978-0-553-38256-3",
            new BigDecimal("14.99"),
            1950
        );
        book6.setDescription("Collection of nine science fiction short stories");
        book6.setStock(75);
        book6.setAuthor(author3);
        book6.addCategory(sciFi);
        book6.addCategory(tech);

        bookRepository.saveAll(Arrays.asList(book1, book2, book3, book4, book5, book6));
        log.info("Created {} books", bookRepository.count());

        // Display summary
        log.info("\n" +
            "========================================\n" +
            "📚 Sample Data Initialized Successfully!\n" +
            "========================================\n" +
            "Authors: {}\n" +
            "Books: {}\n" +
            "Categories: {}\n" +
            "========================================",
            authorRepository.count(),
            bookRepository.count(),
            categoryRepository.count()
        );

        // Display some relationships
        log.info("\n=== Book Relationships ===");
        bookRepository.findAll().forEach(book -> 
            log.info("'{}' by {} in categories: {}",
                book.getTitle(),
                book.getAuthor().getName(),
                book.getCategories().stream()
                    .map(Category::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None")
            )
        );
    }
}
