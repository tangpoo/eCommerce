package com.potato.ecommerce.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.potato.ecommerce.domain.order.dto.HistoryInfo;
import com.potato.ecommerce.domain.order.dto.OrderProduct;
import com.potato.ecommerce.domain.order.entity.HistoryEntity;
import com.potato.ecommerce.domain.order.entity.OrderEntity;
import com.potato.ecommerce.domain.order.repository.history.HistoryJpaRepository;
import com.potato.ecommerce.domain.order.repository.order.OrderJpaRepository;
import com.potato.ecommerce.domain.order.service.HistoryService;
import com.potato.ecommerce.domain.product.ProductSteps;
import com.potato.ecommerce.domain.product.entity.ProductEntity;
import com.potato.ecommerce.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceTests {

    @InjectMocks
    private HistoryService historyService;

    @Mock
    private HistoryJpaRepository historyJpaRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Test
    void History_create() {
        // Arrange
        Long orderId = 1L;
        List<OrderProduct> orderProductsList = new ArrayList<>();
        orderProductsList.add(new OrderProduct(1L, 1));
        orderProductsList.add(new OrderProduct(2L, 1));
        final OrderEntity order = OrderSteps.createOrder();
        final ProductEntity product = Mockito.spy(ProductSteps.createProduct());

        given(orderJpaRepository.findById(orderId)).willReturn(Optional.of(order));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(product.getTotalPrice(anyInt())).willReturn(10000L);

        // Act
        historyService.createHistory(orderId, orderProductsList);

        // Assert
        assertThat(product.getStock()).isEqualTo(8);
        verify(historyJpaRepository, times(1)).saveAll(anyList());
    }

    @Test
    void History_delete() {
        // Arrange
        String orderNum = "orderNum";
        List<HistoryEntity> historyList = new ArrayList<>();
        historyList.add(craeteHistory());

        given(historyJpaRepository.findAllByOrder_OrderNum(orderNum)).willReturn(historyList);

        // Act
        historyService.deleteHistory(orderNum);

        // Assert
        verify(historyJpaRepository, times(1)).deleteAll(historyList);
    }

    @Test
    void History_findOne() {
        // Arrange
        Long orderId = 1L;
        final HistoryEntity historyEntity = craeteHistory();
        final List<HistoryEntity> historyList = new ArrayList<>();
        historyList.add(historyEntity);

        given(historyJpaRepository.findAllByOrderId(orderId)).willReturn(historyList);

        // Act
        final List<HistoryInfo> response = historyService.getHistory(orderId);

        // Assert
        assertThat(response.get(0).getOrderPrice()).isEqualTo(historyEntity.getOrderPrice());
    }

    private static HistoryEntity craeteHistory() {
        return HistoryEntity.builder()
            .order(OrderSteps.createOrder())
            .product(ProductSteps.createProduct())
            .quantity(5)
            .orderPrice(1000L)
            .build();
    }
}
