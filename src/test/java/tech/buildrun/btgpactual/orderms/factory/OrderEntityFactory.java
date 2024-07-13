package tech.buildrun.btgpactual.orderms.factory;

import tech.buildrun.btgpactual.orderms.entity.OrderEntity;
import tech.buildrun.btgpactual.orderms.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public class OrderEntityFactory {

    public static OrderEntity buildWithOneItem() {
        var items = new OrderItem("notebook", 1, BigDecimal.valueOf(20.50));

        var entity = new OrderEntity();
        entity.setOrderId(1L);
        entity.setCustomerId(2L);
        entity.setTotal(BigDecimal.valueOf(20.50));
        entity.setItems(List.of(items));

        return entity;
    }
}
