package com.potato.ecommerce.domain.product.repository;

import com.potato.ecommerce.domain.product.entity.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticSearchRepository extends ElasticsearchRepository<Product, Long> {

    List<Product> findAllByName(String name, Pageable pageable);
}
