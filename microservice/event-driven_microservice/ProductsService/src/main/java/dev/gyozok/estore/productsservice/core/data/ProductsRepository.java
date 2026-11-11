package dev.gyozok.estore.productsservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductsRepository extends JpaRepository<ProductEntity, String> {

    ProductEntity findByProductId(String productId);

    ProductEntity findByProductIdOrTitle(String productId, String title);

    boolean existsByTitle(String title);
}
