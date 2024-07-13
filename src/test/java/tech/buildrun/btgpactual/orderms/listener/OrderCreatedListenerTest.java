package tech.buildrun.btgpactual.orderms.listener;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.MessageBuilder;
import tech.buildrun.btgpactual.orderms.factory.OrderCreatedEventFactory;
import tech.buildrun.btgpactual.orderms.service.OrderService;

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
        void shouldCallServiceWithCorrectParameters() {

            // ARRANGE
            var event = OrderCreatedEventFactory.buildWithOneItem();
            var message = MessageBuilder.withPayload(event).build();

            // ACT
            orderCreatedListener.listen(message);

            // ASSERT
            verify(orderService, times(1)).save(eq(message.getPayload()));
        }
    }

}