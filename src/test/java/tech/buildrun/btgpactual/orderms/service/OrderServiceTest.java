package tech.buildrun.btgpactual.orderms.service;

import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import tech.buildrun.btgpactual.orderms.entity.OrderEntity;
import tech.buildrun.btgpactual.orderms.factory.OrderEntityFactory;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderItemEvent;
import tech.buildrun.btgpactual.orderms.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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

    @Captor
    ArgumentCaptor<Aggregation> aggregationCaptor;

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

    @Nested
    class FindAllByCustomerId {


        @Test
        void shouldCallRepositoryCorrectly() {

            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(0, 10);
            var page = new PageImpl<>(new ArrayList<>());
            doReturn(page).when(orderRepository).findAllByCustomerId(anyLong(), any(PageRequest.class));

            // ACT
            orderService.findAllByCustomerId(customerId, pageRequest);

            // ASSERT
            verify(orderRepository, times(1)).findAllByCustomerId(eq(customerId), eq(pageRequest));
        }

        @Test
        void shouldMapToResponseCorrectly() {

            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(0, 10);
            var entity = OrderEntityFactory.buildWithOneItem();
            var page = new PageImpl<>(List.of(entity));
            doReturn(page).when(orderRepository).findAllByCustomerId(anyLong(), any(PageRequest.class));

            // ACT
            var orders = orderService.findAllByCustomerId(customerId, pageRequest);

            // ASSERT
            assertEquals(page.getContent().size(), orders.getContent().size());
            assertEquals(page.getTotalPages(), orders.getTotalPages());
            assertEquals(page.getTotalElements(), orders.getTotalElements());
            assertEquals(entity.getCustomerId(), orders.getContent().getFirst().customerId());
            assertEquals(entity.getOrderId(), orders.getContent().getFirst().orderId());
            assertEquals(entity.getTotal(), orders.getContent().getFirst().total());
        }
    }

    @Nested
    class FindTotalOnOrdersByCustomerId {

        @Test
        void shouldCallMongoTemplateCorrectly() {
            // ARRANGE
            var customerId = 1L;
            var result = mock(AggregationResults.class);
            doReturn(new Document("total", 1)).when(result).getUniqueMappedResult();
            doReturn(result).when(mongoTemplate).aggregate(any(Aggregation.class), anyString(), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), anyString(), eq(Document.class));
        }

        @Test
        void shouldUseCorrectAggregation() {
            // ARRANGE
            var customerId = 1L;
            var result = mock(AggregationResults.class);
            doReturn(new Document("total", 1)).when(result).getUniqueMappedResult();
            doReturn(result).when(mongoTemplate).aggregate(aggregationCaptor.capture(), anyString(), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            var aggr = aggregationCaptor.getValue();
            var expected = newAggregation(
                    match(Criteria.where("customerId").is(customerId)),
                    group().sum("total").as("total")
            );

            assertNotNull(aggr);
            assertEquals(expected.toString(), aggr.toString());
        }

        @Test
        void shouldUseCorrectTableName() {
            // ARRANGE
            var customerId = 1L;
            var result = mock(AggregationResults.class);
            doReturn(new Document("total", 1)).when(result).getUniqueMappedResult();
            doReturn(result).when(mongoTemplate).aggregate(any(Aggregation.class), anyString(), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("tb_orders"), eq(Document.class));
        }
    }
}