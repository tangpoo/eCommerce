package com.potato.ecommerce.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.potato.ecommerce.domain.member.MemberSteps;
import com.potato.ecommerce.domain.member.entity.MemberEntity;
import com.potato.ecommerce.domain.member.repository.MemberJpaRepository;
import com.potato.ecommerce.domain.order.dto.OrderInfo;
import com.potato.ecommerce.domain.order.dto.OrderInfoWithHistory;
import com.potato.ecommerce.domain.order.dto.OrderList;
import com.potato.ecommerce.domain.order.dto.OrderProduct;
import com.potato.ecommerce.domain.order.entity.OrderEntity;
import com.potato.ecommerce.domain.order.entity.OrderStatus;
import com.potato.ecommerce.domain.order.repository.order.OrderJpaRepository;
import com.potato.ecommerce.domain.order.repository.order.OrderQueryRepository;
import com.potato.ecommerce.domain.order.service.HistoryService;
import com.potato.ecommerce.domain.order.service.OrderService;
import com.potato.ecommerce.domain.payment.dto.PayType;
import com.potato.ecommerce.domain.receiver.ReceiverSteps;
import com.potato.ecommerce.domain.receiver.entity.ReceiverEntity;
import com.potato.ecommerce.domain.receiver.repository.ReceiverJpaRepository;
import com.potato.ecommerce.global.util.RestPage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private MemberJpaRepository memberJpaRepository;

    @Mock
    private ReceiverJpaRepository receiverJpaRepository;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OrderQueryRepository orderQueryRepository;

    @Mock
    private HistoryService historyService;

    @Mock
    private RedisTemplate<String, RestPage<OrderList>> redisTemplate;

    @Mock
    private ValueOperations<String, RestPage<OrderList>> valueOperations;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void Order_create() {
        // Arrange
        final Long memberId = 1L;
        final Long receiverId = 1L;
        final PayType type = PayType.CARD;
        final Long totalAmount = 10000L;
        final OrderProduct orderProduct1 = new OrderProduct();
        final OrderProduct orderProduct2 = new OrderProduct();
        List<OrderProduct> orderProductsList = new ArrayList<>();
        orderProductsList.add(orderProduct1);
        orderProductsList.add(orderProduct2);

        final MemberEntity member = MemberSteps.createMember(passwordEncoder);
        final ReceiverEntity receiver = ReceiverSteps.createReceiverWithMember(member);
        final OrderEntity order = Mockito.spy(
            OrderSteps.createOrderWithMemberAndReceiver(member, receiver, type, totalAmount));

        given(memberJpaRepository.findById(memberId)).willReturn(
            Optional.of(member));
        given(receiverJpaRepository.findById(receiverId)).willReturn(
            Optional.of(receiver));
        given(orderJpaRepository.save(any(OrderEntity.class))).willReturn(order);
        given(order.getId()).willReturn(1L);

        // Act
        final OrderInfo response = orderService.createOrder(memberId, receiverId, totalAmount, type,
            orderProductsList);

        // Assert
        assertThat(response.getOrderNum()).isNotNull();
        assertThat(response.getMember().getUsername()).isEqualTo(member.getUserName());
        assertThat(response.getReceiver().getName()).isEqualTo(receiver.getName());
    }

    @Test
    void Order_complete() {
        // Arrange
        String orderNum = "orderNum";

        final OrderEntity order = Mockito.spy(OrderSteps.createOrder());
        given(orderJpaRepository.findByOrderNum(orderNum)).willReturn(Optional.of(order));
        given(orderJpaRepository.saveAndFlush(order)).willReturn(order);
        given(order.complete()).willReturn(order);

        // Act
        final OrderInfo response = orderService.completeOrder(orderNum);

        // Assert
        verify(order, times(1)).complete();
        assertThat(response.getOrderNum()).isEqualTo(order.getOrderNum());
    }


    @Test
    void Order_cancel() {
        // Arrange
        String orderNum = "orderNum";

        final OrderEntity order = Mockito.spy(OrderSteps.createOrder());
        given(orderJpaRepository.findByOrderNum(orderNum)).willReturn(Optional.of(order));
        given(orderJpaRepository.saveAndFlush(order)).willReturn(order);
        given(order.cancel()).willReturn(order);

        // Act
        final OrderInfo response = orderService.cancelOrder(orderNum);

        // Assert
        verify(order, times(1)).cancel();
        assertThat(response.getOrderNum()).isEqualTo(order.getOrderNum());
    }

    @Test
    void Order_find_one() {
        // Arrange
        Long orderId = 1L;

        final OrderEntity order = Mockito.spy(OrderSteps.createOrder());
        given(orderJpaRepository.findById(orderId)).willReturn(Optional.of(order));

        // Act
        final OrderInfoWithHistory response = orderService.getOrder(orderId);

        // Assert
        assertThat(response.getMember().getUsername()).isEqualTo(order.getMember().getUserName());
        assertThat(response.getOrderNum()).isEqualTo(order.getOrderNum());
    }

    @Nested
    class Order_find_all {

        private final String subject = "test@email.com";
        private final int page = 0;
        private final int size = 10;


        @Test
        void redis_cache_hit() {
            // Arrange
            final OrderEntity order1 = OrderSteps.createOrder();
            final OrderEntity order2 = OrderSteps.createOrder();
            final OrderList orderList1 = OrderList.builder().status(OrderStatus.COMPLETE)
                .orderNum(order1.getOrderNum()).build();
            final OrderList orderList2 = OrderList.builder().status(OrderStatus.COMPLETE)
                .orderNum(order2.getOrderNum()).build();
            final List<OrderList> orderLists = new ArrayList<>();
            orderLists.add(orderList1);
            orderLists.add(orderList2);

            final PageRequest pageable = PageRequest.of(page, size);
            int count = 2;
            final RestPage<OrderList> restPage = new RestPage<>(
                new PageImpl<>(orderLists, pageable, count));

            System.out.println("test " + restPage.get().findFirst().get().getOrderNum());

            orderLists.add(orderList1);
            orderLists.add(orderList2);

            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(redisTemplate.opsForValue().get(subject)).willReturn(restPage);

            // Act
            final RestPage<OrderList> response = orderService.getOrders(subject, page, size);

            // Assert
            assertThat(response.get().findFirst().get()).isEqualTo(orderList1);
            verify(orderQueryRepository, times(0)).getOrders(anyString(), anyInt(), anyInt());
        }

        @Test
        void redis_cache_miss() {
            // Arrange
            final OrderEntity order1 = OrderSteps.createOrder();
            final OrderList orderList1 = OrderList.builder().status(OrderStatus.COMPLETE)
                .orderNum(order1.getOrderNum()).build();
            final OrderEntity order2 = OrderSteps.createOrder();
            final OrderList orderList2 = OrderList.builder().status(OrderStatus.COMPLETE)
                .orderNum(order2.getOrderNum()).build();
            List<OrderList> orderLists = new ArrayList<>();
            orderLists.add(orderList1);
            orderLists.add(orderList2);

            final PageRequest pageable = PageRequest.of(page, size);
            int count = 2;
            final RestPage<OrderList> restPage = new RestPage<>(
                new PageImpl<>(orderLists, pageable, count));

            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(redisTemplate.opsForValue().get(subject)).willReturn(null);
            given(orderQueryRepository.getOrders(subject, page, size)).willReturn(restPage);

            // Act
            final RestPage<OrderList> response = orderService.getOrders(subject, page, size);

            // Assert
            assertThat(response.get().findFirst().get()).isEqualTo(orderList1);
            verify(orderQueryRepository, times(1)).getOrders(subject, page, size);
            verify(redisTemplate.opsForValue(), times(1)).set(subject, restPage, 3,
                TimeUnit.MINUTES);
        }
    }

}
