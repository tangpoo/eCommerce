package com.potato.ecommerce.domain.product.repository.search;

import java.io.IOException;

public interface ProductSearchQueryRepository {
    Double searchProductCategoryPriceAverage(Long categoryId) throws IOException;
}
