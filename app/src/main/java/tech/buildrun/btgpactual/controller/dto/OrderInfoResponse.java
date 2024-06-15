package tech.buildrun.btgpactual.controller.dto;

import tech.buildrun.btgpactual.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.List;

public record OrderInfoResponse(Long orderId, Long customerId, BigDecimal total) {

    public static List<OrderInfoResponse> fromOrders(List<OrderEntity> orders) {
        return orders.stream()
                .map(e -> new OrderInfoResponse(e.getOrderId(), e.getCustomerId(), e.getTotal()))
                .toList();
    }
}
