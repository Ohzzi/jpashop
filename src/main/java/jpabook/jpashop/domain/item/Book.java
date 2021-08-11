package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("B")
@Getter
public class Book extends Item{

    private String author;
    private String isbn;

    @Builder
    public Book(Long id, String name, int price, int stockQuantity, List<Category> categories, String author, String isbn) {
        super(id, name, price, stockQuantity, categories);
        this.author = author;
        this.isbn = isbn;
    }
}
