package dev.gyozok.estore.orderservice.query;

import dev.gyozok.estore.orderservice.core.model.OrderSummary;
import dev.gyozok.estore.orderservice.query.data.OrderEntity;
import dev.gyozok.estore.orderservice.query.data.OrdersRepository;
import dev.gyozok.estore.orderservice.query.subscriptionqueries.FindOrderQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class OrderQueryHandler {

    private final OrdersRepository ordersRepository;

    public OrderQueryHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        OrderEntity orderEntity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());

        return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
    }
}
