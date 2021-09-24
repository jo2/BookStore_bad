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
@Table
public class ReceiptPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long bookId;

    @NotNull
    @Size(min = 2, max = 30, message = "Title must be at least 2 and at most 30 characters long.")
    private String title;

    @NotNull
    @Size(min = 2, max = 20, message = "Author must be at least 2 and at most 20 characters long.")
    private String author;

    @Min(value = 1, message = "Price must be at least 1.")
    private double price;

    private double currentStorageVolume;

    @NotNull
    @Min(value = 0, message = "Discount percentage must be at least 0.")
    @Max(value = 20, message = "Discount percentage must be at most 20.")
    private double discountPercentage;

    @NotNull
    @Min(value = 0, message = "Amount must be at least 0.")
    @Max(value = 5, message = "Amount must be at most 5.")
    private int amount;

    @NotNull
    private double cost;
}
