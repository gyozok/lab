package dev.gyozok.estore.productsservice.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productlookup")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductLookupEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String productId;
    @Column(unique = true)
    private String title;
}
