package com.potato.ecommerce.domain.product.repository.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.AvgAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.sun.jdi.DoubleValue;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductSearchQueryRepositoryImpl implements ProductSearchQueryRepository {

    private final ElasticsearchClient elasticsearchClient;

    public Double searchProductCategoryPriceAverage(Long categoryId) throws IOException {

        SearchRequest searchRequest = new SearchRequest.Builder()
            .index("products")
            .query(q -> q.term(t -> t.field("productCategory").value(categoryId)))
            .aggregations("avg_price", a -> a.avg(ag -> ag.field("price")))
            .build();

        SearchResponse<Void> searchResponse = elasticsearchClient.search(searchRequest, Void.class);

        AvgAggregate avgPrice = searchResponse.aggregations()
            .get("avg_price")
            .avg();

        return avgPrice.value();
    }
}
