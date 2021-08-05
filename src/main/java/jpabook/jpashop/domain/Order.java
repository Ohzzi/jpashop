package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "orders")
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //==연관관계 편의 메소드==//
    public void addMemberToOrder(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItemToOrder(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.addOrderToOrderItem(this);
    }

    public void addDeliveryToOrder(Delivery delivery) {
        this.delivery = delivery;
        delivery.addOrderToDelivery(this);
    }

    /*
     * 클래스 내부에서만 사용할 메소드이므로 private 선언
     */
    private void changeOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    //==생성 메소드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = Order.builder().orderStatus(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now())
                .build(); // orderStatus 와 orderDate 의 setter 가 없으므로 builder 패턴을 이용해 생성해준다.
        /*
         * 연관관계 편의 메소드를 이용한 매핑
         */
        order.addMemberToOrder(member);
        order.addDeliveryToOrder(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItemToOrder(orderItem);
        }
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료가 된 상품은 취소가 불가능합니다.");
        }
        this.changeOrderStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

}
