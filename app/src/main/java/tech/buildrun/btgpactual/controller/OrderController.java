package tech.buildrun.btgpactual.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.btgpactual.controller.dto.ApiResponse;
import tech.buildrun.btgpactual.controller.dto.OrderInfoResponse;
import tech.buildrun.btgpactual.controller.dto.PaginationResponse;
import tech.buildrun.btgpactual.entity.OrderEntity;
import tech.buildrun.btgpactual.service.OrderService;

import java.util.Map;

@RestController
@RequestMapping
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<OrderInfoResponse>> orders(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        var orders = orderService.findAll(PageRequest.of(page, pageSize));

        return ResponseEntity.ok(
                new ApiResponse<>(
                        null,
                        OrderInfoResponse.fromOrders(orders.getContent()),
                        PaginationResponse.fromPage(orders)
                )
        );
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderInfoResponse>> customerOrders(@PathVariable("customerId") Long customerId,
                                                                        @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        var totalInOrders = orderService.sumTotalInOrdersByCustomer(customerId);
        var orders = orderService.findAllByCustomer(customerId, PageRequest.of(page, pageSize));

        return ResponseEntity.ok(
                new ApiResponse<>(
                        Map.of("totalInOrders", totalInOrders),
                        OrderInfoResponse.fromOrders(orders.getContent()),
                        PaginationResponse.fromPage(orders))
        );
    }
}
