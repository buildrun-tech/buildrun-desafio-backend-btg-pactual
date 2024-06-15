package tech.buildrun.btgpactual.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import tech.buildrun.btgpactual.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.service.OrderService;

import static tech.buildrun.btgpactual.config.RabbitConfig.ORDER_CREATED_QUEUE;

@Component
public class OrderCreatedListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void listen(Message<OrderCreatedEvent> message) {
        logger.info("Message consumed: {}", message);

        var payload = message.getPayload();

        orderService.save(payload);
    }
}
