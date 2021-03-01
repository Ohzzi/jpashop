package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
@Table(name = "orders")
@Getter
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private final List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;

    private LocalDate localDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //==연관관계 편의 메소드==//
    public void mapMemberToOrder(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItemToOrder(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.mapOrderToOrderItem(this);
    }

    public void mapDeliveryToOrder(Delivery delivery) {
        this.delivery = delivery;
        delivery.mapOrderToDelivery(this);
    }

}
