package de.adesso.bookstore.controller;

import de.adesso.bookstore.entities.Book;
import de.adesso.bookstore.entities.Receipt;
import de.adesso.bookstore.entities.ReceiptPosition;
import de.adesso.bookstore.repositories.BookRepository;
import de.adesso.bookstore.repositories.ReceiptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dummy")
@SpringBootTest(classes = de.adesso.bookstore.TestApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ViewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

   @BeforeEach
    void setUp() {
        bookRepository.saveAll(List.of(
                new Book(1L, "Book_1", "Author_1", 5.0, 2018, 10),
                new Book(2L, "Book_2", "Author_1", 10.0, 2017, 10),
                new Book(3L, "Book_3", "Author_1", 15.0, 2018, 10),
                new Book(4L, "Book_4", "Author_1", 20.0, 2019, 10)
        ));
        
        receiptRepository.save(new Receipt(null,
                List.of(
                        new ReceiptPosition(null, 1L, "Book_1", "Author_1", 5.0, 10, 0.0, 3, 15.0),
                        new ReceiptPosition(null, 2L, "Book_2", "Author_1", 10.0, 10, 0.0, 2, 20.0),
                        new ReceiptPosition(null, 3L, "Book_3", "Author_1", 15.0, 10, 0.0, 1, 15.0)
                ),
                50.0,
                LocalDateTime.of(2021, 8, 20, 12, 0)
        ));
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        receiptRepository.deleteAll();
    }
    
    @Test
    void getIndex() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3>BookStore</h3>")))
                .andExpect(content().string(containsString("Book_1")))
                .andExpect(content().string(containsString("Book_2")))
                .andExpect(content().string(containsString("Book_3")))
                .andExpect(content().string(containsString("Book_4")))
                .andExpect(content().string(containsString("Author_1")))
                .andExpect(content().string(containsString("5.00 €")))
                .andExpect(content().string(containsString("10.00 €")))
                .andExpect(content().string(containsString("15.00 €")))
                .andExpect(content().string(containsString("20.00 €")));
    }

    @Test
    void getCreateBook() throws Exception {
        this.mockMvc.perform(get("/books/create")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3>Create new Book</h3>")));
    }

    @Test
    void getBookInfo() throws Exception {
        this.mockMvc.perform(get("/books/1/info")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3>Book: Book_1</h3>")))
                .andExpect(content().string(containsString("Author: Author_1")))
                .andExpect(content().string(containsString("Year: 2018")))
                .andExpect(content().string(containsString("Price: 5.00 €")));
    }

    @Test
    void getBookInfoIdNotFound() throws Exception {
        this.mockMvc.perform(get("/books/6/info")).andExpect(status().is(302));
    }

    @Test
    void getBookEdit() throws Exception {
        this.mockMvc.perform(get("/books/1/edit")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3>Edit Book_1</h3>")))
                .andExpect(content().string(containsString("<input id='title' type='text' placeholder='Title' class='form-control input-md' required='' name=\"title\" value=\"Book_1\">")))
                .andExpect(content().string(containsString("<input id='author' type='text' placeholder='Author' class='form-control input-md' required='' name=\"author\" value=\"Author_1\">")))
                .andExpect(content().string(containsString("<input id='year' type='text' placeholder='Year' class='form-control input-md' required='' name=\"year\" value=\"2018\">")))
                .andExpect(content().string(containsString("<input id='price' type='text' placeholder='Price' class='form-control input-md' required='' name=\"price\" value=\"5.0\">")))
                .andExpect(content().string(containsString("<input id='amount' type='number' placeholder='Amount' class='form-control input-md' required='' name=\"amount\" value=\"10\">")));
    }

    @Test
    void getBookEditIdNotFound() throws Exception {
        this.mockMvc.perform(get("/books/6/edit")).andExpect(status().is(302));
    }

    @Test
    void getShoppingCart() throws Exception {
        this.mockMvc.perform(get("/shopping-cart?bookIds=1,2")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3>Shopping cart</h3>")))
                .andExpect(content().string(containsString("Book_1 by Author_1")))
                .andExpect(content().string(containsString("5.00 €")))
                .andExpect(content().string(containsString("Book_2 by Author_1")))
                .andExpect(content().string(containsString("10.00 €")));
    }

    @Test
    void getAccounting() throws Exception {
        this.mockMvc.perform(get("/accounting")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<h3>Accounting</h3>")))
                .andExpect(content().string(containsString("Book_1 by Author_1")))
                .andExpect(content().string(containsString("5.00 €")))
                .andExpect(content().string(containsString("Book_2 by Author_1")))
                .andExpect(content().string(containsString("10.00 €")))
                .andExpect(content().string(containsString("Book_3 by Author_1")))
                .andExpect(content().string(containsString("15.00 €")));
    }

    @Test
    void createBook() throws Exception {
        this.mockMvc.perform(post("/books/create")
                        .contentType("application/x-www-form-urlencoded")
                        .content("title=Book_5&author=Author_1&year=2020&price=25.0&amount=12"))
                .andExpect(status().is(302));

        assertThat(bookRepository.count()).isEqualTo(5L);
        assertThat(bookRepository.findById(5L)).isPresent();
        assertThat(bookRepository.findById(5L).orElseThrow())
                .isEqualTo(new Book(5L, "Book_5", "Author_1", 25.0, 2020, 12));
    }

    @Test
    void createBookBindingResultErrors() throws Exception {
        this.mockMvc.perform(post("/books/create")
                        .contentType("application/x-www-form-urlencoded")
                        .content("title=Book_5&author=Author_1&year=202&price=25.0&amount=12"))
                .andExpect(status().isOk());

        assertThat(bookRepository.findById(5L)).isEmpty();
        assertThat(bookRepository.count()).isEqualTo(4L);
    }

    @Test
    void createBookAuthorTitleNotUnique() throws Exception {
        this.mockMvc.perform(post("/books/create")
                        .contentType("application/x-www-form-urlencoded")
                        .content("title=Book_4&author=Author_1&year=2020&price=25.0&amount=12"))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(bookRepository.findById(5L)).isEmpty();
        assertThat(bookRepository.count()).isEqualTo(4L);
    }

    @Test
    void editBook() throws Exception {
        this.mockMvc.perform(post("/books/update/4")
                        .contentType("application/x-www-form-urlencoded")
                        .content("id=4&title=Book_4&author=Author_1&year=2020&price=25.0&amount=15"))
                .andExpect(status().is(302));

        assertThat(bookRepository.findById(4L)).isPresent();
        assertThat(bookRepository.findById(4L).get())
                .isEqualTo( new Book(4L, "Book_4", "Author_1", 25.0, 2020, 15));
    }

    @Test
    void editBookBindingResultErrors() throws Exception {
        this.mockMvc.perform(post("/books/update/4")
                        .contentType("application/x-www-form-urlencoded")
                        .content("id=4&title=Book_4&author=Author_1&year=202&price=20.0&amount=10"))
                .andExpect(status().isOk());

        assertThat(bookRepository.findById(4L)).isPresent();
        assertThat(bookRepository.findById(4L).get())
                .isEqualTo( new Book(4L, "Book_4", "Author_1", 20.0, 2019, 10));
    }

    @Test
    void editBookNoBookWithId() throws Exception {
        this.mockMvc.perform(post("/books/update/5")
                        .contentType("application/x-www-form-urlencoded")
                        .content("id=4&title=Book_3&author=Author_1&year=2020&price=20.0&amount=10"))
                .andExpect(status().isOk());

        assertThat(bookRepository.findById(4L)).isPresent();
        assertThat(bookRepository.findById(4L).get())
                .isEqualTo(new Book(4L, "Book_4", "Author_1", 20.0, 2019, 10));
    }

    @Test
    void editBookAuthorTitleNotUnique() throws Exception {
        this.mockMvc.perform(post("/books/update/4")
                        .contentType("application/x-www-form-urlencoded")
                        .content("id=4&title=Book_3&author=Author_1&year=2020&price=20.0&amount=10"))
                .andExpect(status().isOk());

        System.out.println(bookRepository.findAll());
        assertThat(bookRepository.findById(4L)).isPresent();
        assertThat(bookRepository.findById(4L).get())
                .isEqualTo( new Book(4L, "Book_4", "Author_1", 20.0, 2019, 10));
    }

    @Test
    void buyBookBindingResultErrors() throws Exception {
        this.mockMvc.perform(post("/books/buy")
                        .contentType("application/x-www-form-urlencoded")
                        .content("receiptPositions%5B0%5D.bookId=1&" +
                                        "receiptPositions%5B0%5D.title=Book_1&" +
                                        "receiptPositions%5B0%5D.author=Author_1&" +
                                        "receiptPositions%5B0%5D.price=5.0&" +
                                        "receiptPositions%5B0%5D.cost=15.0&" +
                                        "receiptPositions%5B0%5D.amount=3&" +
                                        "receiptPositions%5B0%5D.discountPercentage=30.0&" +
                                        "receiptPositions%5B1%5D.bookId=2&" +
                                        "receiptPositions%5B1%5D.title=Book_2&" +
                                        "receiptPositions%5B1%5D.author=Author_1&" +
                                        "receiptPositions%5B1%5D.price=10.0&" +
                                        "receiptPositions%5B1%5D.cost=20.0&" +
                                        "receiptPositions%5B1%5D.amount=2&" +
                                        "receiptPositions%5B1%5D.discountPercentage=0.0&" +
                                        "receiptPositions%5B2%5D.bookId=3&" +
                                        "receiptPositions%5B2%5D.title=Book_3&" +
                                        "receiptPositions%5B2%5D.author=Author_1&" +
                                        "receiptPositions%5B2%5D.price=15.0&" +
                                        "receiptPositions%5B2%5D.cost=15.0&" +
                                        "receiptPositions%5B2%5D.amount=1&" +
                                        "receiptPositions%5B2%5D.discountPercentage=0.0&"
                                ))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertThat(receiptRepository.count()).isEqualTo(1L);
        assertThat(bookRepository.findAllById(List.of(1L, 2L, 3L))).isEqualTo(List.of(
                new Book(1L, "Book_1", "Author_1", 5.0, 2018, 10),
                new Book(2L, "Book_2", "Author_1", 10.0, 2017, 10),
                new Book(3L, "Book_3", "Author_1", 15.0, 2018, 10)
        ));
        System.out.println("test");
    }

    @Test
    void buyBook() throws Exception {
        this.mockMvc.perform(post("/books/buy")
                        .contentType("application/x-www-form-urlencoded")
                        .content("receiptPositions%5B0%5D.bookId=1&" +
                                "receiptPositions%5B0%5D.title=Book_1&" +
                                "receiptPositions%5B0%5D.author=Author_1&" +
                                "receiptPositions%5B0%5D.price=5.0&" +
                                "receiptPositions%5B0%5D.cost=15.0&" +
                                "receiptPositions%5B0%5D.amount=3&" +
                                "receiptPositions%5B0%5D.discountPercentage=0.0&" +
                                "receiptPositions%5B1%5D.bookId=2&" +
                                "receiptPositions%5B1%5D.title=Book_2&" +
                                "receiptPositions%5B1%5D.author=Author_1&" +
                                "receiptPositions%5B1%5D.price=10.0&" +
                                "receiptPositions%5B1%5D.cost=20.0&" +
                                "receiptPositions%5B1%5D.amount=2&" +
                                "receiptPositions%5B1%5D.discountPercentage=0.0&" +
                                "receiptPositions%5B2%5D.bookId=3&" +
                                "receiptPositions%5B2%5D.title=Book_3&" +
                                "receiptPositions%5B2%5D.author=Author_1&" +
                                "receiptPositions%5B2%5D.price=15.0&" +
                                "receiptPositions%5B2%5D.cost=15.0&" +
                                "receiptPositions%5B2%5D.amount=1&" +
                                "receiptPositions%5B2%5D.discountPercentage=0.0&"
                        ))
                .andExpect(status().is(302));

        assertThat(receiptRepository.count()).isEqualTo(2L);
        assertThat(bookRepository.findAllById(List.of(1L, 2L, 3L))).isEqualTo(List.of(
                new Book(1L, "Book_1", "Author_1", 5.0, 2018, 7),
                new Book(2L, "Book_2", "Author_1", 10.0, 2017, 8),
                new Book(3L, "Book_3", "Author_1", 15.0, 2018, 9)
        ));
    }

    @Test
    void deleteBook() throws Exception {
        this.mockMvc.perform(post("/books/delete/4"))
                .andExpect(status().is(302));

        assertThat(bookRepository.count()).isEqualTo(3L);
        assertThat(bookRepository.findById(4L)).isEmpty();
    }

    @Test
    void deleteBookNotFound() throws Exception {
        this.mockMvc.perform(post("/books/delete/5"))
                .andExpect(status().is(302));

        assertThat(bookRepository.count()).isEqualTo(4L);
        assertThat(bookRepository.findById(4L)).isPresent();
        assertThat(bookRepository.findById(4L).get())
                .isEqualTo(new Book(4L, "Book_4", "Author_1", 20.0, 2019, 10));
    }
}
