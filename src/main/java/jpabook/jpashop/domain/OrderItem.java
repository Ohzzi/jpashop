package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
@Getter
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    private int orderPrice; // 주문 당시 가격
    private int count; // 주문 수량

    //==연관관계 메소드 : 네이밍에 set 대신 add 사용하여 각 엔티티에 추가해준다는 의도==//
    public void addOrderToOrderItem(Order order) {
        this.order = order;
    }

    //==생성 메소드==/
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        item.removeStock(count);
        return OrderItem.builder()
                .item(item)
                .orderPrice(orderPrice)
                .count(count)
                .build();
    }

    //==비즈니스 로직==/
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==/
    /**
     * 주문 상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return orderPrice * count;
    }
}
