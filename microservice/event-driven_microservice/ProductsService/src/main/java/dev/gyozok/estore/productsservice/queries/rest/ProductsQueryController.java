package dev.gyozok.estore.productsservice.queries.rest;

import dev.gyozok.estore.productsservice.queries.FindProductsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products")
public class ProductsQueryController {

    private final QueryGateway queryGateway;

    public ProductsQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<ProductRestModel> getProduct() {
        FindProductsQuery findProductsQuery = new FindProductsQuery();

//        List<ProductRestModel> products = queryGateway.query(findProductsQuery, ResponseTypes.multipleInstancesOf(ProductRestModel.class))
//                .join();
        CompletableFuture<List<ProductRestModel>> future = queryGateway.query(
                new FindProductsQuery(),
                ResponseTypes.multipleInstancesOf(ProductRestModel.class)
        );

        List<ProductRestModel> products = future.join();
        return products;
    }
}
