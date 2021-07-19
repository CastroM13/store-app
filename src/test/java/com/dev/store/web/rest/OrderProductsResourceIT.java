package com.dev.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dev.store.IntegrationTest;
import com.dev.store.domain.OrderProducts;
import com.dev.store.repository.OrderProductsRepository;
import com.dev.store.repository.search.OrderProductsSearchRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrderProductsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderProductsResourceIT {

    private static final Integer DEFAULT_ID_ORDER_PRODUCTS = 1;
    private static final Integer UPDATED_ID_ORDER_PRODUCTS = 2;

    private static final Integer DEFAULT_ID_ORDER = 1;
    private static final Integer UPDATED_ID_ORDER = 2;

    private static final Integer DEFAULT_ID_PRODUCT = 1;
    private static final Integer UPDATED_ID_PRODUCT = 2;

    private static final String ENTITY_API_URL = "/api/order-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/order-products";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderProductsRepository orderProductsRepository;

    @Mock
    private OrderProductsRepository orderProductsRepositoryMock;

    /**
     * This repository is mocked in the com.dev.store.repository.search test package.
     *
     * @see com.dev.store.repository.search.OrderProductsSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrderProductsSearchRepository mockOrderProductsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderProductsMockMvc;

    private OrderProducts orderProducts;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProducts createEntity(EntityManager em) {
        OrderProducts orderProducts = new OrderProducts()
            .idOrderProducts(DEFAULT_ID_ORDER_PRODUCTS)
            .idOrder(DEFAULT_ID_ORDER)
            .idProduct(DEFAULT_ID_PRODUCT);
        return orderProducts;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProducts createUpdatedEntity(EntityManager em) {
        OrderProducts orderProducts = new OrderProducts()
            .idOrderProducts(UPDATED_ID_ORDER_PRODUCTS)
            .idOrder(UPDATED_ID_ORDER)
            .idProduct(UPDATED_ID_PRODUCT);
        return orderProducts;
    }

    @BeforeEach
    public void initTest() {
        orderProducts = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderProducts() throws Exception {
        int databaseSizeBeforeCreate = orderProductsRepository.findAll().size();
        // Create the OrderProducts
        restOrderProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderProducts)))
            .andExpect(status().isCreated());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeCreate + 1);
        OrderProducts testOrderProducts = orderProductsList.get(orderProductsList.size() - 1);
        assertThat(testOrderProducts.getIdOrderProducts()).isEqualTo(DEFAULT_ID_ORDER_PRODUCTS);
        assertThat(testOrderProducts.getIdOrder()).isEqualTo(DEFAULT_ID_ORDER);
        assertThat(testOrderProducts.getIdProduct()).isEqualTo(DEFAULT_ID_PRODUCT);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(1)).save(testOrderProducts);
    }

    @Test
    @Transactional
    void createOrderProductsWithExistingId() throws Exception {
        // Create the OrderProducts with an existing ID
        orderProducts.setId(1L);

        int databaseSizeBeforeCreate = orderProductsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderProducts)))
            .andExpect(status().isBadRequest());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeCreate);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(0)).save(orderProducts);
    }

    @Test
    @Transactional
    void getAllOrderProducts() throws Exception {
        // Initialize the database
        orderProductsRepository.saveAndFlush(orderProducts);

        // Get all the orderProductsList
        restOrderProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderProducts.getId().intValue())))
            .andExpect(jsonPath("$.[*].idOrderProducts").value(hasItem(DEFAULT_ID_ORDER_PRODUCTS)))
            .andExpect(jsonPath("$.[*].idOrder").value(hasItem(DEFAULT_ID_ORDER)))
            .andExpect(jsonPath("$.[*].idProduct").value(hasItem(DEFAULT_ID_PRODUCT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderProductsWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderProductsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderProductsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderProductsRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderProductsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderProductsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderProductsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderProductsRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getOrderProducts() throws Exception {
        // Initialize the database
        orderProductsRepository.saveAndFlush(orderProducts);

        // Get the orderProducts
        restOrderProductsMockMvc
            .perform(get(ENTITY_API_URL_ID, orderProducts.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderProducts.getId().intValue()))
            .andExpect(jsonPath("$.idOrderProducts").value(DEFAULT_ID_ORDER_PRODUCTS))
            .andExpect(jsonPath("$.idOrder").value(DEFAULT_ID_ORDER))
            .andExpect(jsonPath("$.idProduct").value(DEFAULT_ID_PRODUCT));
    }

    @Test
    @Transactional
    void getNonExistingOrderProducts() throws Exception {
        // Get the orderProducts
        restOrderProductsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrderProducts() throws Exception {
        // Initialize the database
        orderProductsRepository.saveAndFlush(orderProducts);

        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();

        // Update the orderProducts
        OrderProducts updatedOrderProducts = orderProductsRepository.findById(orderProducts.getId()).get();
        // Disconnect from session so that the updates on updatedOrderProducts are not directly saved in db
        em.detach(updatedOrderProducts);
        updatedOrderProducts.idOrderProducts(UPDATED_ID_ORDER_PRODUCTS).idOrder(UPDATED_ID_ORDER).idProduct(UPDATED_ID_PRODUCT);

        restOrderProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderProducts.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrderProducts))
            )
            .andExpect(status().isOk());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);
        OrderProducts testOrderProducts = orderProductsList.get(orderProductsList.size() - 1);
        assertThat(testOrderProducts.getIdOrderProducts()).isEqualTo(UPDATED_ID_ORDER_PRODUCTS);
        assertThat(testOrderProducts.getIdOrder()).isEqualTo(UPDATED_ID_ORDER);
        assertThat(testOrderProducts.getIdProduct()).isEqualTo(UPDATED_ID_PRODUCT);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository).save(testOrderProducts);
    }

    @Test
    @Transactional
    void putNonExistingOrderProducts() throws Exception {
        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();
        orderProducts.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderProducts.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderProducts))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(0)).save(orderProducts);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderProducts() throws Exception {
        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();
        orderProducts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderProducts))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(0)).save(orderProducts);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderProducts() throws Exception {
        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();
        orderProducts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderProducts)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(0)).save(orderProducts);
    }

    @Test
    @Transactional
    void partialUpdateOrderProductsWithPatch() throws Exception {
        // Initialize the database
        orderProductsRepository.saveAndFlush(orderProducts);

        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();

        // Update the orderProducts using partial update
        OrderProducts partialUpdatedOrderProducts = new OrderProducts();
        partialUpdatedOrderProducts.setId(orderProducts.getId());

        restOrderProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderProducts))
            )
            .andExpect(status().isOk());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);
        OrderProducts testOrderProducts = orderProductsList.get(orderProductsList.size() - 1);
        assertThat(testOrderProducts.getIdOrderProducts()).isEqualTo(DEFAULT_ID_ORDER_PRODUCTS);
        assertThat(testOrderProducts.getIdOrder()).isEqualTo(DEFAULT_ID_ORDER);
        assertThat(testOrderProducts.getIdProduct()).isEqualTo(DEFAULT_ID_PRODUCT);
    }

    @Test
    @Transactional
    void fullUpdateOrderProductsWithPatch() throws Exception {
        // Initialize the database
        orderProductsRepository.saveAndFlush(orderProducts);

        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();

        // Update the orderProducts using partial update
        OrderProducts partialUpdatedOrderProducts = new OrderProducts();
        partialUpdatedOrderProducts.setId(orderProducts.getId());

        partialUpdatedOrderProducts.idOrderProducts(UPDATED_ID_ORDER_PRODUCTS).idOrder(UPDATED_ID_ORDER).idProduct(UPDATED_ID_PRODUCT);

        restOrderProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderProducts))
            )
            .andExpect(status().isOk());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);
        OrderProducts testOrderProducts = orderProductsList.get(orderProductsList.size() - 1);
        assertThat(testOrderProducts.getIdOrderProducts()).isEqualTo(UPDATED_ID_ORDER_PRODUCTS);
        assertThat(testOrderProducts.getIdOrder()).isEqualTo(UPDATED_ID_ORDER);
        assertThat(testOrderProducts.getIdProduct()).isEqualTo(UPDATED_ID_PRODUCT);
    }

    @Test
    @Transactional
    void patchNonExistingOrderProducts() throws Exception {
        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();
        orderProducts.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderProducts))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(0)).save(orderProducts);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderProducts() throws Exception {
        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();
        orderProducts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderProducts))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(0)).save(orderProducts);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderProducts() throws Exception {
        int databaseSizeBeforeUpdate = orderProductsRepository.findAll().size();
        orderProducts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderProducts))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderProducts in the database
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(0)).save(orderProducts);
    }

    @Test
    @Transactional
    void deleteOrderProducts() throws Exception {
        // Initialize the database
        orderProductsRepository.saveAndFlush(orderProducts);

        int databaseSizeBeforeDelete = orderProductsRepository.findAll().size();

        // Delete the orderProducts
        restOrderProductsMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderProducts.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderProducts> orderProductsList = orderProductsRepository.findAll();
        assertThat(orderProductsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OrderProducts in Elasticsearch
        verify(mockOrderProductsSearchRepository, times(1)).deleteById(orderProducts.getId());
    }

    @Test
    @Transactional
    void searchOrderProducts() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        orderProductsRepository.saveAndFlush(orderProducts);
        when(mockOrderProductsSearchRepository.search(queryStringQuery("id:" + orderProducts.getId())))
            .thenReturn(Collections.singletonList(orderProducts));

        // Search the orderProducts
        restOrderProductsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orderProducts.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderProducts.getId().intValue())))
            .andExpect(jsonPath("$.[*].idOrderProducts").value(hasItem(DEFAULT_ID_ORDER_PRODUCTS)))
            .andExpect(jsonPath("$.[*].idOrder").value(hasItem(DEFAULT_ID_ORDER)))
            .andExpect(jsonPath("$.[*].idProduct").value(hasItem(DEFAULT_ID_PRODUCT)));
    }
}
