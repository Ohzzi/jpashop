package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("A")
@Getter
public class Album extends Item {

    private String artist;
    private String etc;

    @Builder
    public Album(Long id, String name, int price, int stockQuantity, List<Category> categories, String artist, String etc) {
        super(id, name, price, stockQuantity, categories);
        this.artist = artist;
        this.etc = etc;
    }
}
