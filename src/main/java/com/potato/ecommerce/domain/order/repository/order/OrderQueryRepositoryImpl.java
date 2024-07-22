package com.potato.ecommerce.domain.order.repository.order;

import static com.potato.ecommerce.domain.order.entity.QOrderEntity.orderEntity;
import static com.querydsl.core.types.Projections.fields;

import com.potato.ecommerce.domain.order.dto.OrderList;
import com.potato.ecommerce.domain.order.entity.QOrderEntity;
import com.potato.ecommerce.global.util.RestPage;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<OrderList> getOrders(String subject, Long lastOrderId, int size) {
        QOrderEntity order = orderEntity;

        return jpaQueryFactory.select(fields(OrderList.class,
                    order.id,
                    order.status,
                    order.orderedAt,
                    order.orderNum))
                .from(order)
                .where(
                    order.member.email.eq(subject),
                    ltOrderId(lastOrderId)
                )
                .orderBy(order.orderedAt.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression ltOrderId(final Long lastOrderId) {
        if(lastOrderId == null) {
            return null;
        }

        return orderEntity.id.lt(lastOrderId);
    }
}
