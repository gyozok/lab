package dev.gyozok.estore.productsservice.commands;

import dev.gyozok.estore.productsservice.core.data.ProductLookupEntity;
import dev.gyozok.estore.productsservice.core.data.ProductLookupRepository;
import dev.gyozok.estore.productsservice.core.events.ProductCreatedEvent;
import jakarta.transaction.Transactional;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventHandler {

    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on (ProductCreatedEvent event) {
        ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.productId(), event.title());

        productLookupRepository.save(productLookupEntity);
    }

    @ResetHandler
    @Transactional
    public void reset() {
        productLookupRepository.deleteAll();
    }
}
