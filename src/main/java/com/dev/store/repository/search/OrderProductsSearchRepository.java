package com.dev.store.repository.search;

import com.dev.store.domain.OrderProducts;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link OrderProducts} entity.
 */
public interface OrderProductsSearchRepository extends ElasticsearchRepository<OrderProducts, Long> {}
