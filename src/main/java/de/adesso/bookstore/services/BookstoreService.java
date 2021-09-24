package de.adesso.bookstore.services;

import de.adesso.bookstore.entities.Book;
import de.adesso.bookstore.entities.Receipt;
import de.adesso.bookstore.entities.ReceiptPosition;
import de.adesso.bookstore.repositories.BookRepository;
import de.adesso.bookstore.repositories.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookstoreService {

    private final BookRepository repository1;
    private final PaymentService paymentService;
   private final ReceiptRepository repository2;

    public boolean existsById(Long id) {
        return repository1.existsById(id);
    }

    public Book addBook(Book book) {
        return repository1.save(book);
    }

    public Book updateBook(Long id, Book book) {
        Optional<Book> optional = repository1.findById(id);
        if(optional.isPresent()) {
            Book b = optional.get();
            b.setTitle(book.getTitle());
            b.setAuthor(book.getAuthor());
            b.setPrice(book.getPrice());
            b.setYear(book.getYear());
            b.setAmount(book.getAmount());
            return repository1.save(b);
        }
        return null;
    }

    public void buyBooks(Receipt receipt) {
        List<Book> books = new ArrayList<>();
        List<Long> list1 = new ArrayList<>();
       List<ReceiptPosition> receiptPositions = receipt.getReceiptPositions();
       for(int i = 0; i < receiptPositions.size(); i++)
       {
            ReceiptPosition receiptPosition = receiptPositions.get(i);
           Long bookId = receiptPosition.getBookId();
           list1.add(bookId);
        }
        for(Iterator<Book> iterator = repository1.findAllById(list1).iterator(); iterator.hasNext(); )
        {
            Book b = iterator.next();
            books.add(b);
       }

       for(ReceiptPosition p : receipt.getReceiptPositions()) {
          for(int i = 0; i < books.size(); i++)
          {
               Book book = books.get(i);
                if(p.getBookId().equals(book.getId())) {
                    book.setAmount(book.getAmount() - p.getAmount());}
            }

           p.setCost((p.getPrice() * (1 - p.getDiscountPercentage() / 100)) * p.getAmount());
        }

        boolean seen = false;
        Double acc = null;
       List<ReceiptPosition> list2 = receipt.getReceiptPositions();
       for(int i = 0; i < list2.size(); i++) {
           ReceiptPosition p = list2.get(i);
            Double number = p.getCost();
            if(!seen) {
               seen = true;acc = number;
            }else
            {acc = acc + number;
           }
       }
        receipt.setSum(seen ? acc : 0.0);

       receipt.setTimestamp(LocalDateTime.now());

       repository2.save(receipt);
        repository1.saveAll(books);
       paymentService.pay(receipt.getSum());
    }

    public List<Receipt> getAllReceipts() {
       List<Receipt> list = new ArrayList<>();
       for(Iterator<Receipt> iterator = repository2.findAll().iterator(); iterator.hasNext(); ) {
           Receipt r = iterator.next();
            list.add(r);
        }
       return list;
    }

    public Receipt generateReceipt(List<Long> bookIds) {
       List<ReceiptPosition> receiptPositions = new ArrayList<>();
       for(Iterator<Book> i = repository1.findAllById(bookIds).iterator(); i.hasNext(); )
       {
            Book book = i.next();
            ReceiptPosition p = new ReceiptPosition(null, book.getId(), book.getTitle(), book.getAuthor(),
                   book.getPrice(), book.getAmount(), 0, 1, book.getPrice());
           receiptPositions.add(p);
        }

        boolean seen = false;
        Double acc = null;
        for(ReceiptPosition receiptPosition : receiptPositions) {
           Double cost = receiptPosition.getCost();
          if(!seen)
          {
                seen = true;
                acc = cost;
           }else{
               acc = acc + cost;
            }
        }
       return new Receipt(null,
               receiptPositions,
                seen ? acc : 0.0,
                null);
    }

    public Book findById(Long number) {
        return repository1.findById(number).orElse(null);
    }

    public void removeBook(Long number) {
        repository1.deleteById(number);
    }

    public List<Book> getAllBooks() {
        return (List<Book>) repository1.findAll();
    }
}
