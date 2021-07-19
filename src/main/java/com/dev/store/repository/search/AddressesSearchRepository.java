package com.dev.store.repository.search;

import com.dev.store.domain.Addresses;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Addresses} entity.
 */
public interface AddressesSearchRepository extends ElasticsearchRepository<Addresses, Long> {}
