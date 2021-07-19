package com.dev.store.repository.search;

import com.dev.store.domain.Products;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Products} entity.
 */
public interface ProductsSearchRepository extends ElasticsearchRepository<Products, Long> {}
