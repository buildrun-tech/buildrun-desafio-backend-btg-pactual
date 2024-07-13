package tech.buildrun.btgpactual.orderms.listener;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.MessageBuilder;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderItemEvent;
import tech.buildrun.btgpactual.orderms.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCreatedListenerTest {

    @Mock
    OrderService orderService;

    @InjectMocks
    OrderCreatedListener orderCreatedListener;

    @Nested
    class Listen {

        @Test
        void shouldCallServiceWhenConsumeTheMessage() {

            // ARRANGE
            var itens = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(20.50));
            var event = new OrderCreatedEvent(1L, 2L, List.of(itens));
            var message = MessageBuilder.withPayload(event).build();

            // ACT
            orderCreatedListener.listen(message);

            // ASSERT
            verify(orderService, times(1)).save(eq(message.getPayload()));
        }

    }
}