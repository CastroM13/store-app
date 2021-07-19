package com.dev.store.repository.search;

import com.dev.store.domain.Users;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Users} entity.
 */
public interface UsersSearchRepository extends ElasticsearchRepository<Users, Long> {}
