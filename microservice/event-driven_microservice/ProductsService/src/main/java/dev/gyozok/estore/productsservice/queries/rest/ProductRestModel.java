package dev.gyozok.estore.productsservice.queries.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRestModel {
        private String productId;
        private String title;
        private BigDecimal price;
        private int quantity;
}
