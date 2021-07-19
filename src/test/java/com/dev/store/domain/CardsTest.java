package com.dev.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dev.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CardsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cards.class);
        Cards cards1 = new Cards();
        cards1.setId(1L);
        Cards cards2 = new Cards();
        cards2.setId(cards1.getId());
        assertThat(cards1).isEqualTo(cards2);
        cards2.setId(2L);
        assertThat(cards1).isNotEqualTo(cards2);
        cards1.setId(null);
        assertThat(cards1).isNotEqualTo(cards2);
    }
}
