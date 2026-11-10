package dev.gyozok.estore.productsservice.queries;

import dev.gyozok.estore.productsservice.core.data.ProductEntity;
import dev.gyozok.estore.productsservice.core.data.ProductsRepository;
import dev.gyozok.estore.productsservice.queries.rest.ProductRestModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductsQueryHandler {

    private final ProductsRepository productsRepository;

    public ProductsQueryHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery query) {
        List<ProductRestModel> productsRest = new ArrayList<>();
        List<ProductEntity> storedProducts = productsRepository.findAll();
        productsRest = storedProducts.stream()
                .map(prod -> new ProductRestModel(
                        prod.getProductId(),
                        prod.getTitle(),
                        prod.getPrice(),
                        prod.getQuantity()
                ))
                .collect(Collectors.toList());
//        for (ProductEntity prod : storedProducts) {
//            ProductRestModel productRestModel = new ProductRestModel(
//                        prod.getProductId(),
//                        prod.getTitle(),
//                        prod.getPrice(),
//                        prod.getQuantity()
//                );
//            productsRest.add(productRestModel);
//        }
        return productsRest;
    }
}
