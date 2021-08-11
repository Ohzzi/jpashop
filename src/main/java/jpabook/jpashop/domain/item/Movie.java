package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("M")
@Getter
public class Movie extends Item {

    private String director;
    private String actor;

    @Builder
    public Movie(Long id, String name, int price, int stockQuantity, List<Category> categories, String director, String actor) {
        super(id, name, price, stockQuantity, categories);
        this.director = director;
        this.actor = actor;
    }
}
