package tech.buildrun.btgpactual.orderms.controller.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tech.buildrun.btgpactual.orderms.entity.OrderEntity;
import tech.buildrun.btgpactual.orderms.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderResponseTest {

    @Nested
    class FromEntity {

        @Test
        void shouldMapCorrectly() {

            // ARRANGE
            var items = new OrderItem("notebook", 1, BigDecimal.valueOf(20.50));

            var input = new OrderEntity();
            input.setOrderId(1L);
            input.setCustomerId(2L);
            input.setTotal(BigDecimal.valueOf(20.50));
            input.setItems(List.of(items));

            // ACT
            var output = OrderResponse.fromEntity(input);

            // ASSERT
            assertEquals(input.getOrderId(), output.orderId());
            assertEquals(input.getCustomerId(), output.customerId());
            assertEquals(input.getTotal(), output.total());
        }
    }
}