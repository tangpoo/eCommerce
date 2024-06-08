package com.potato.ecommerce.domain.order;

import com.potato.ecommerce.domain.member.MemberSteps;
import com.potato.ecommerce.domain.member.entity.MemberEntity;
import com.potato.ecommerce.domain.order.controller.dto.request.CreateOrderRequestDTO;
import com.potato.ecommerce.domain.order.dto.OrderProduct;
import com.potato.ecommerce.domain.order.entity.OrderEntity;
import com.potato.ecommerce.domain.payment.dto.PayType;
import com.potato.ecommerce.domain.receiver.ReceiverSteps;
import com.potato.ecommerce.domain.receiver.entity.ReceiverEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class OrderSteps {


    public static OrderEntity createOrder() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        final MemberEntity member = MemberSteps.createMember(passwordEncoder);
        return OrderEntity.builder()
            .member(member)
            .receiver(ReceiverSteps.createReceiverWithMember(member))
            .payType(PayType.CARD)
            .orderNum(UUID.randomUUID().toString())
            .totalAmount(10000L)
            .build();
    }

    public static OrderEntity createOrderWithMemberAndReceiver(final MemberEntity member,
        final ReceiverEntity receiver,
        final PayType type, final Long totalAmount) {
        return OrderEntity.builder()
            .member(member)
            .receiver(receiver)
            .payType(type)
            .orderNum(UUID.randomUUID().toString())
            .totalAmount(totalAmount)
            .build();
    }
}
