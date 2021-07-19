package com.dev.store.repository.search;

import com.dev.store.domain.Cards;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Cards} entity.
 */
public interface CardsSearchRepository extends ElasticsearchRepository<Cards, Long> {}
