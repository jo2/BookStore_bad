package de.adesso.bookstore.controller;

import de.adesso.bookstore.entities.Book;
import de.adesso.bookstore.entities.Receipt;
import de.adesso.bookstore.services.BookstoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
public class ViewController {

   private final BookstoreService service;

    @Autowired
   public ViewController(BookstoreService service) {
      this.service = service;
   }

   @GetMapping({"/", "/index", "/books"})
   public String getIndex(Model m) {
        m.addAttribute("books", service.getAllBooks());
      return "index";
    }

    @GetMapping("/books/create")
    public String getCreateBook(Model m) {
       m.addAttribute("book", new Book());
       return "createBook";
    }

    @GetMapping("/books/{id}/info")
   public String getBookInfo(@PathVariable(name = "id") Long id, Model m) {
        if(!service.existsById(id))
        {
            return "redirect:/";
       }

        m.addAttribute("book", service.findById(id));
       return "bookInfo";
    }

    @GetMapping("/books/{id}/edit")
    public String getBookEdit(@PathVariable(name = "id") Long id, Model m) {
       if(!service.existsById(id))
       {
            return "redirect:/";
        }

       m.addAttribute("book", service.findById(id));
        return "updateBook";
    }

    @GetMapping("/shopping-cart")
   public String getShoppingCart(@RequestParam List<Long> bookIds, Model m) {
        m.addAttribute("receipt", service.generateReceipt(bookIds));
        return "shoppingCart";
    }

    @GetMapping("/accounting")
   public String getAccounting(Model m) {
        m.addAttribute("receipts", service.getAllReceipts());
       return "accounting";
    }

    @PostMapping("/books/create")
    public String createBook(@Valid Book book, BindingResult bindingResult) {
       if(bindingResult.hasErrors())
       {
           return "createBook";
        }
       try
       {
           service.addBook(book);
        } catch (DataIntegrityViolationException ex) {
           System.out.println("create book with non unique author and title");
           bindingResult.addError(new ObjectError("title", "Combination of title and author must be unique."));
            return "createBook";
        }
        return "redirect:/books";
   }

    @PostMapping("/books/update/{id}")
    public String editBook(@PathVariable(name = "id") Long id, @Valid Book book, BindingResult r) {
        if(r.hasErrors()) {
           return "updateBook";
       }
        if(!service.existsById(id))
        {
           r.addError(new ObjectError("id", "No book with this id exists."));
            return "updateBook";
        }
        try
        {
           service.updateBook(id, book);
        } catch (DataIntegrityViolationException ex) {
           System.out.println("edit book with non unique author and title");
           r.addError(new ObjectError("title", "Combination of title and author must be unique."));
            return "updateBook";
        }
        return "redirect:/books";
   }

    @PostMapping("/books/buy")
    public String buyBook(@Valid Receipt receipt) {
        System.out.println(receipt);
       service.buyBooks(receipt);
       return "redirect:/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable(name = "id") Long id) {
       if(!service.existsById(id))
       {
           return "redirect:/";
        }

        service.removeBook(id);
        return "redirect:/books";
    }
}
