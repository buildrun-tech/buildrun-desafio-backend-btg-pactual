package tech.buildrun.btgpactual.orderms.factory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import tech.buildrun.btgpactual.orderms.controller.dto.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponseFactory {


    public static Page<OrderResponse> buildWithOneItem() {
        var orderResponse =  new OrderResponse(1L, 2L, BigDecimal.valueOf(20.50));

        return new PageImpl<>(List.of(orderResponse));
    }
}
