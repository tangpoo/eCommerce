package com.potato.ecommerce.domain.order.repository.order;

import com.potato.ecommerce.domain.order.dto.OrderList;
import java.util.List;

public interface OrderQueryRepository {

    List<OrderList> getOrders(String subject, Long lastOrderId, int size);
}
