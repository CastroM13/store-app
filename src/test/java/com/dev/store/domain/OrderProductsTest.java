package com.dev.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dev.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderProductsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderProducts.class);
        OrderProducts orderProducts1 = new OrderProducts();
        orderProducts1.setId(1L);
        OrderProducts orderProducts2 = new OrderProducts();
        orderProducts2.setId(orderProducts1.getId());
        assertThat(orderProducts1).isEqualTo(orderProducts2);
        orderProducts2.setId(2L);
        assertThat(orderProducts1).isNotEqualTo(orderProducts2);
        orderProducts1.setId(null);
        assertThat(orderProducts1).isNotEqualTo(orderProducts2);
    }
}
