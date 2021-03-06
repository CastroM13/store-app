package com.dev.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dev.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WishlistTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Wishlist.class);
        Wishlist wishlist1 = new Wishlist();
        wishlist1.setId(1L);
        Wishlist wishlist2 = new Wishlist();
        wishlist2.setId(wishlist1.getId());
        assertThat(wishlist1).isEqualTo(wishlist2);
        wishlist2.setId(2L);
        assertThat(wishlist1).isNotEqualTo(wishlist2);
        wishlist1.setId(null);
        assertThat(wishlist1).isNotEqualTo(wishlist2);
    }
}
