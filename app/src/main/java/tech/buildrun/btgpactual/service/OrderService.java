package tech.buildrun.btgpactual.service;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import tech.buildrun.btgpactual.entity.OrderEntity;
import tech.buildrun.btgpactual.entity.OrderItem;
import tech.buildrun.btgpactual.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository,
                        MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEvent event) {

        var entity = new OrderEntity();

        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(new ArrayList<>());

        entity.setItems(event.itens()
                .stream()
                .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                .toList());

        var total = entity.getItems()
                .stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        entity.setTotal(total);

        orderRepository.save(entity);
    }

    public Page<OrderEntity> findAll(PageRequest pageRequest) {
        return orderRepository.findAll(pageRequest);
    }

    public Page<OrderEntity> findAllByCustomer(Long customerId, PageRequest pageRequest) {
        return orderRepository.findAllByCustomerId(customerId, pageRequest);
    }

    public BigDecimal sumTotalInOrdersByCustomer(Long customerId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "tb_orders", Document.class);
        Document result = results.getUniqueMappedResult();

        if (result != null && result.get("total") != null) {
            return new BigDecimal(result.get("total").toString());
        } else {
            return BigDecimal.ZERO;
        }
    }
}
