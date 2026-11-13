package dev.gyozok.estore.orderservice.query;

import dev.gyozok.estore.orderservice.core.events.OrderApprovedEvent;
import dev.gyozok.estore.orderservice.core.events.OrderCreatedEvent;
import dev.gyozok.estore.orderservice.core.events.OrderRejectedEvent;
import dev.gyozok.estore.orderservice.query.data.OrderEntity;
import dev.gyozok.estore.orderservice.query.data.OrdersRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("order-group")
public class OrderEventsHandler {

    private final OrdersRepository ordersRepository;

    public OrderEventsHandler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(event, orderEntity);

        try {
            ordersRepository.save(orderEntity);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void on(OrderApprovedEvent event) {
        OrderEntity orderEntity = ordersRepository.findByOrderId(event.getOrderId());
        if (orderEntity == null) {
            //Do something about it
            return;
        }
        orderEntity.setOrderStatus(event.getOrderStatus());

        ordersRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderRejectedEvent orderRejectedEvent) {
        OrderEntity orderEntity = ordersRepository.findByOrderId(orderRejectedEvent.getOrderId());
        orderEntity.setOrderStatus(orderRejectedEvent.getOrderStatus());
        ordersRepository.save(orderEntity);
    }
}
