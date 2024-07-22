package com.potato.ecommerce.domain.order.repository.order;

import static com.potato.ecommerce.domain.member.entity.QMemberEntity.memberEntity;
import static com.potato.ecommerce.domain.order.entity.QOrderEntity.orderEntity;
import static com.querydsl.core.types.Projections.fields;

import com.potato.ecommerce.domain.member.entity.QMemberEntity;
import com.potato.ecommerce.domain.order.dto.OrderList;
import com.potato.ecommerce.domain.order.entity.OrderEntity;
import com.potato.ecommerce.domain.order.entity.QOrderEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<OrderList> getOrders(String subject, Long lastOrderId, int size) {
        return jpaQueryFactory.select(fields(OrderList.class,
                orderEntity.id,
                orderEntity.status,
                orderEntity.orderedAt,
                orderEntity.orderNum))
            .from(orderEntity)
            .where(
                ltOrderId(lastOrderId),
                orderEntity.member.email.eq(subject)
            )
            .orderBy(orderEntity.orderedAt.desc())
            .limit(size)
            .fetch();
    }

    private BooleanExpression ltOrderId(final Long lastOrderId) {
        if (lastOrderId == null) {
            return null;
        }

        return orderEntity.id.lt(lastOrderId);
    }
}
