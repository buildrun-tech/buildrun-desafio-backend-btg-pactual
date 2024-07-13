package tech.buildrun.btgpactual.orderms.factory;

import tech.buildrun.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderItemEvent;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreatedEventFactory {

    public static OrderCreatedEvent buildWithOneItem() {

        var itens = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(20.50));
        var event = new OrderCreatedEvent(1L, 2L, List.of(itens));

        return event;
    }

    public static OrderCreatedEvent buildWithTwoItens() {

        var item1 = new OrderItemEvent("notebook", 1, BigDecimal.valueOf(20.50));
        var item2 = new OrderItemEvent("mouse", 1, BigDecimal.valueOf(35.25));

        var event = new OrderCreatedEvent(1L, 2L, List.of(item1, item2));

        return event;
    }
}
