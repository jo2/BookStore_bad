package de.adesso.bookstore.services;

import de.adesso.bookstore.entities.Book;
import de.adesso.bookstore.entities.Receipt;
import de.adesso.bookstore.entities.ReceiptPosition;
import de.adesso.bookstore.repositories.BookRepository;
import de.adesso.bookstore.repositories.ReceiptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookstoreServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private BookstoreService bookstoreService;

    @Test
    void testAddBook() {
        Book beforeSave = new Book(null, "Title_1", "Author_1", 10.0, 2010, 10);
        Book afterSave = new Book(1L, "Title_1", "Author_1", 10.0, 2010, 10);
        when(bookRepository.save(beforeSave)).thenReturn(afterSave);

        Book result = bookstoreService.addBook(beforeSave);

        assertThat(result).isEqualTo(afterSave);
    }

    @Test
    public void testUpdateBook() {
        Book beforeSave = new Book(null, "Title_1", "Author_1", 10.0, 2010, 10);
        Book update = new Book(1L, "Title_1", "Author_1", 15.0, 2010, 20);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(beforeSave));
        when(bookRepository.save(beforeSave)).thenReturn(update);

        Book result = bookstoreService.updateBook(1L, update);

        assertThat(result).isEqualTo(update);
    }

    @Test
    public void testUpdateBookNotFound() {
        Book update = new Book(1L, "Title_1", "Author_1", 15.0, 2010, 20);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Book result = bookstoreService.updateBook(1L, update);
        assertThat(result).isNull();
    }

    @Test
    public void testBuyBooks() {
        Receipt receipt = new Receipt(1L,
                List.of(
                        new ReceiptPosition(1L, 1L, "Title_1", "Author_1", 10.0, 10, 0.0, 1, 10.0),
                        new ReceiptPosition(2L, 2L, "Title_2", "Author_1", 10.0, 10, 0.0, 2, 20.0),
                        new ReceiptPosition(3L, 3L, "Title_3", "Author_1", 20.0, 10, 0.0, 1, 20.0)
                ),
                50.0,
                LocalDateTime.of(2021, 8, 20, 12, 0)
        );

        when(bookRepository.findAllById(List.of(1L, 2L, 3L))).thenReturn(List.of(
                new Book(1L, "Title_1", "Author_1", 10.0, 10, 10),
                new Book(2L, "Title_2", "Author_1", 10.0, 10, 10),
                new Book(3L, "Title_3", "Author_1", 20.0, 10, 10)
        ));

        bookstoreService.buyBooks(receipt);

        verify(receiptRepository).save(receipt);
        verify(bookRepository).saveAll(List.of(
                new Book(1L, "Title_1", "Author_1", 10.0, 10, 9),
                new Book(2L, "Title_2", "Author_1", 10.0, 10, 8),
                new Book(3L, "Title_3", "Author_1", 20.0, 10, 9)
        ));
        verify(paymentService).pay(50.0);
    }

    @Test
    public void testGetAllReceipts() {
        List<Receipt> receipts = List.of(
                new Receipt(1L,
                        List.of(
                                new ReceiptPosition(1L, 1L, "Title_1", "Author_1", 10.0, 10, 0.0, 1, 10.0),
                                new ReceiptPosition(2L, 2L, "Title_2", "Author_1", 10.0, 10, 0.0, 2, 20.0),
                                new ReceiptPosition(3L, 3L, "Title_3", "Author_1", 20.0, 10, 0.0, 1, 20.0)
                        ),
                        50.0,
                        LocalDateTime.of(2021, 8, 20, 12, 0)
                ),
                new Receipt(2L,
                        List.of(
                                new ReceiptPosition(4L, 4L, "Title_1", "Author_2", 10.0, 10, 0.0, 1, 10.0),
                                new ReceiptPosition(5L, 5L, "Title_2", "Author_2", 10.0, 10, 0.0, 2, 20.0),
                                new ReceiptPosition(6L, 6L, "Title_3", "Author_2", 20.0, 10, 0.0, 1, 20.0)
                        ),
                        50.0,
                        LocalDateTime.of(2021, 8, 20, 12, 0)
                )
        );

        when(receiptRepository.findAll()).thenReturn(receipts);

        assertThat(bookstoreService.getAllReceipts()).isEqualTo(receipts);
    }

    @Test
    public void testGenerateReceipt() {
        when(bookRepository.findAllById(List.of(1L, 2L, 3L))).thenReturn(List.of(
                new Book(1L, "Title_1", "Author_1", 10.0, 10, 10),
                new Book(2L, "Title_2", "Author_1", 10.0, 10, 10),
                new Book(3L, "Title_3", "Author_1", 20.0, 10, 10)
        ));

        Receipt result = bookstoreService.generateReceipt(List.of(1L, 2L, 3L));

        assertThat(result).isEqualTo(new Receipt(null,
                List.of(
                        new ReceiptPosition(null, 1L, "Title_1", "Author_1", 10.0, 10, 0.0, 1, 10.0),
                        new ReceiptPosition(null, 2L, "Title_2", "Author_1", 10.0, 10, 0.0, 1, 10.0),
                        new ReceiptPosition(null, 3L, "Title_3", "Author_1", 20.0, 10, 0.0, 1, 20.0)
                ),
                40.0,
                null
        ));
    }

    @Test
    public void testFindById() {
        Book book = new Book(1L, "Title_1", "Author_1", 10.0, 10, 10);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookstoreService.findById(1L);
        assertThat(result).isEqualTo(book);
    }

    @Test
    public void testFindByIdNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Book result = bookstoreService.findById(1L);
        assertThat(result).isNull();
    }

    @Test
    public void testRemoveBook() {
        bookstoreService.removeBook(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    public void testGetAllBooks() {
        List<Book> books = List.of(
                new Book(1L, "Title_1", "Author_1", 10.0, 10, 10),
                new Book(2L, "Title_2", "Author_1", 10.0, 10, 10),
                new Book(3L, "Title_3", "Author_1", 20.0, 10, 10)
        );

        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookstoreService.getAllBooks();
        assertThat(result).isEqualTo(books);
    }
}
