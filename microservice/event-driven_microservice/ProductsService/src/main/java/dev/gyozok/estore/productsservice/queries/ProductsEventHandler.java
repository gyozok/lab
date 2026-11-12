package dev.gyozok.estore.productsservice.queries;

import dev.gyozok.estore.core.events.ProductReservedEvent;
import dev.gyozok.estore.productsservice.core.data.ProductEntity;
import dev.gyozok.estore.productsservice.core.data.ProductsRepository;
import dev.gyozok.estore.productsservice.core.events.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
@Slf4j
public class ProductsEventHandler {

    private final ProductsRepository productsRepository;

    public ProductsEventHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @ExceptionHandler(resultType= Exception.class)
    public void handle(Exception exception) throws Exception{
        throw exception;
    }

    @ExceptionHandler(resultType= IllegalStateException.class)
    public void handle(IllegalArgumentException exception) {
        //log error message
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productsRepository.save(productEntity);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {
        log.info("ProductReservedEvent is called for orderId: " + productReservedEvent.getOrderId()
                + " productId: " + productReservedEvent.getProductId());

        ProductEntity productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());
        int quantity = productEntity.getQuantity();
        productEntity.setQuantity(quantity - productReservedEvent.getQuantity());
        productsRepository.save(productEntity);


    }
}
