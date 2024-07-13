package tech.buildrun.btgpactual.orderms.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import tech.buildrun.btgpactual.orderms.entity.OrderEntity;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderItemEvent;
import tech.buildrun.btgpactual.orderms.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MongoTemplate mongoTemplate;

    @InjectMocks
    OrderService orderService;

    @Captor
    ArgumentCaptor<OrderEntity> orderCaptor;

    @Nested
    class Save {

        @Test
        void shouldCallRepositorySave() {

            // ARRANGE
            var itens = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(20.50));
            var event = new OrderCreatedEvent(1L, 2L, List.of(itens));

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository, times(1)).save(any());
        }

        @Test
        void shouldMapEventToEntityCorrectly() {

            // ARRANGE
            var itens = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(20.50));
            var event = new OrderCreatedEvent(1L, 2L, List.of(itens));

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository).save(orderCaptor.capture());
            assertEquals(event.codigoPedido(), orderCaptor.getValue().getOrderId());
            assertEquals(event.codigoCliente(), orderCaptor.getValue().getCustomerId());
            assertNotNull(orderCaptor.getValue().getTotal());
            assertEquals(1, orderCaptor.getValue().getItems().size());
            assertEquals(itens.produto(), orderCaptor.getValue().getItems().get(0).getProduct());
            assertEquals(itens.quantidade(), orderCaptor.getValue().getItems().get(0).getQuantity());
            assertEquals(itens.preco(), orderCaptor.getValue().getItems().get(0).getPrice());
        }

        @Test
        void shouldCalculateOrderTotalCorrectly() {

            // ARRANGE
            var item1 = new OrderItemEvent("notebook", 2, BigDecimal.valueOf(20.50));
            var item2 = new OrderItemEvent("mouse", 1, BigDecimal.valueOf(35.50));
            var event = new OrderCreatedEvent(1L, 2L, List.of(item1, item2));
            var totalItem1 = item1.preco().multiply(BigDecimal.valueOf(item1.quantidade()));
            var totalItem2 = item2.preco().multiply(BigDecimal.valueOf(item2.quantidade()));

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository).save(orderCaptor.capture());
            assertNotNull(orderCaptor.getValue().getTotal());
            assertEquals(totalItem1.add(totalItem2), orderCaptor.getValue().getTotal());
        }
    }
}