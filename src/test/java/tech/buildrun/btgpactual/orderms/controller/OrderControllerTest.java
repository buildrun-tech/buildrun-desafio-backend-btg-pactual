package tech.buildrun.btgpactual.orderms.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatusCode;
import tech.buildrun.btgpactual.orderms.controller.dto.OrderResponse;
import tech.buildrun.btgpactual.orderms.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    OrderService orderService;

    @InjectMocks
    OrderController orderController;

    @Captor
    ArgumentCaptor<Long> orderIdCaptor;

    @Nested
    class ListOrders {

        @Test
        void shouldReturnOk() {
            // ARRANGE
            var page = new PageImpl<>(new ArrayList<>());
            doReturn(page).when(orderService).findAllByCustomerId(anyLong(), any());
            doReturn(BigDecimal.valueOf(200.)).when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            // ACT
            var response = orderController.listOrders(1L, 0, 1);

            // ASSERT
            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        }

        @Test
        void shouldPassParametersCorrectly() {
            // ARRANGE
            long orderId = 1L;
            var page = new PageImpl<>(new ArrayList<>());
            doReturn(page).when(orderService).findAllByCustomerId(orderIdCaptor.capture(), any());
            doReturn(BigDecimal.valueOf(200.)).when(orderService).findTotalOnOrdersByCustomerId(orderIdCaptor.capture());

            // ACT
            var response = orderController.listOrders(orderId, 0, 1);

            // ASSERT
            assertEquals(2, orderIdCaptor.getAllValues().size());
            assertEquals(orderId, orderIdCaptor.getAllValues().get(0));
            assertEquals(orderId, orderIdCaptor.getAllValues().get(1));
        }

        @Test
        void shouldReturnResponseBodyCorrectly() {
            // ARRANGE
            long orderId = 1L;
            BigDecimal totalOrders = BigDecimal.valueOf(200.);
            var page = new PageImpl<>(List.of(new OrderResponse(1L, 1L, totalOrders)));

            doReturn(page).when(orderService).findAllByCustomerId(orderIdCaptor.capture(), any());
            doReturn(totalOrders).when(orderService).findTotalOnOrdersByCustomerId(orderIdCaptor.capture());

            // ACT
            var response = orderController.listOrders(orderId, 0, 1);

            // ASSERT
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().pagination());
            assertNotNull(response.getBody().summary());
            assertNotNull(response.getBody().data());

            assertEquals(totalOrders, response.getBody().summary().get("totalOnOrders"));

            assertEquals(page.getNumber(), response.getBody().pagination().page());
            assertEquals(page.getSize(), response.getBody().pagination().pageSize());
            assertEquals(page.getTotalPages(), response.getBody().pagination().totalPages());
            assertEquals(page.getTotalElements(), response.getBody().pagination().totalElements());

            assertEquals(page.getContent(), response.getBody().data());
        }
    }

}