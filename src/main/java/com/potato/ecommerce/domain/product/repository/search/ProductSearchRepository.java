package com.potato.ecommerce.domain.product.repository.search;

import com.potato.ecommerce.domain.product.entity.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {

    List<Product> findAllByName(String name, Pageable pageable);
}
