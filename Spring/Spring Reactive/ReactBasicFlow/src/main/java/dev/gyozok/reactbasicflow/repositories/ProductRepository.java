package dev.gyozok.reactbasicflow.repositories;

import dev.gyozok.reactbasicflow.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//We didn;t use any stereotype annotation, because it is not a concrete class, it is an abstraction.
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
}
