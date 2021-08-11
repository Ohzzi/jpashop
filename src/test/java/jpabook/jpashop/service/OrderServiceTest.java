package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("JPA", 10000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOne(orderId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDER).describedAs("상품 주문시 상태는 ORDER");
        assertThat(order.getOrderItems().size()).isEqualTo(1).describedAs("주문한 상품 종류 수가 정확해야 한다.");
        assertThat(order.getTotalPrice()).isEqualTo(10000 * orderCount).describedAs("주문 가격은 가격 * 수량이다.");
        assertThat(book.getStockQuantity()).isEqualTo(10 - orderCount).describedAs("주문 수량만큼 재고가 감소해야 한다.");
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        assertThrows(NotEnoughStockException.class, () -> {
            //given
            Member member = createMember();
            Book book = createBook("JPA", 10000, 10);
            int orderCount = 11;

            //when
            orderService.order(member.getId(), book.getId(), orderCount);

            //then
            Assertions.fail("예외가 발생해야 한다.");
        });
    }
    
    @Test
    public void 상품취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("JPA", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        
        //when
        orderService.cancel(orderId);

        //then
        Order order = orderRepository.findOne(orderId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL).describedAs("주문 취소시 상태는 CANCEL이다.");
        assertThat(book.getStockQuantity()).isEqualTo(10).describedAs("주문이 취소된 상품은 재고가 그만큼 증가해야 한다.");
    }

    private Member createMember() {
        Member member = Member.builder()
                .name("회원")
                .address(new Address("서울", "중구", "123-123"))
                .build();
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = Book.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
        em.persist(book);
        return book;
    }

}