package com.dev.store.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CardsSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CardsSearchRepositoryMockConfiguration {

    @MockBean
    private CardsSearchRepository mockCardsSearchRepository;
}
