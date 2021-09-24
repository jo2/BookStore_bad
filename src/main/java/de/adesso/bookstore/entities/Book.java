package de.adesso.bookstore.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title", "author"}))
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    @NotNull
   @Size(min = 2, max = 30, message = "Title must be at least 2 and at most 30 characters long.")
    private String title;

    @NotNull
   @Size(min = 2, max = 20, message = "Author must be at least 2 and at most 20 characters long.")
    private String author;

    @Min(value = 1, message = "Price must be at least 1.")
    private double price;

    @Min(value = 1000, message = "Year must be after 1000 AC.")
    @Max(value = 2050, message = "Year can't be after 2050 AC.")
    private int year;

    @Min(value = 0, message = "Amount must be at least 0.")
    private int amount;
}
