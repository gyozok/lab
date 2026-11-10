package dev.gyozok.estore.productsservice.queries;

import dev.gyozok.estore.productsservice.core.data.ProductEntity;
import dev.gyozok.estore.productsservice.core.data.ProductsRepository;
import dev.gyozok.estore.productsservice.core.events.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
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

        if (true) throw new Exception("Forcing exception in the Event Handler class");
    }
}
