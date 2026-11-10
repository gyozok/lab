package dev.gyozok.estore.productsservice.queries;

import dev.gyozok.estore.productsservice.core.data.ProductEntity;
import dev.gyozok.estore.productsservice.core.data.ProductsRepository;
import dev.gyozok.estore.productsservice.core.events.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductsEventHandler {

    private final ProductsRepository productsRepository;

    public ProductsEventHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
//        idempotent event handling
        if (productsRepository.existsByTitle(event.title())) {
            return;
        }
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        productsRepository.save(productEntity);
    }
}
