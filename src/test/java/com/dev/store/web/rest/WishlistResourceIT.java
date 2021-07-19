package com.dev.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dev.store.IntegrationTest;
import com.dev.store.domain.Wishlist;
import com.dev.store.repository.WishlistRepository;
import com.dev.store.repository.search.WishlistSearchRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WishlistResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WishlistResourceIT {

    private static final Integer DEFAULT_ID_WISHLIST = 1;
    private static final Integer UPDATED_ID_WISHLIST = 2;

    private static final Integer DEFAULT_ID_USER = 1;
    private static final Integer UPDATED_ID_USER = 2;

    private static final Integer DEFAULT_ID_PRODUCTS = 1;
    private static final Integer UPDATED_ID_PRODUCTS = 2;

    private static final String ENTITY_API_URL = "/api/wishlists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/wishlists";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WishlistRepository wishlistRepository;

    /**
     * This repository is mocked in the com.dev.store.repository.search test package.
     *
     * @see com.dev.store.repository.search.WishlistSearchRepositoryMockConfiguration
     */
    @Autowired
    private WishlistSearchRepository mockWishlistSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishlistMockMvc;

    private Wishlist wishlist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wishlist createEntity(EntityManager em) {
        Wishlist wishlist = new Wishlist().idWishlist(DEFAULT_ID_WISHLIST).idUser(DEFAULT_ID_USER).idProducts(DEFAULT_ID_PRODUCTS);
        return wishlist;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wishlist createUpdatedEntity(EntityManager em) {
        Wishlist wishlist = new Wishlist().idWishlist(UPDATED_ID_WISHLIST).idUser(UPDATED_ID_USER).idProducts(UPDATED_ID_PRODUCTS);
        return wishlist;
    }

    @BeforeEach
    public void initTest() {
        wishlist = createEntity(em);
    }

    @Test
    @Transactional
    void createWishlist() throws Exception {
        int databaseSizeBeforeCreate = wishlistRepository.findAll().size();
        // Create the Wishlist
        restWishlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wishlist)))
            .andExpect(status().isCreated());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeCreate + 1);
        Wishlist testWishlist = wishlistList.get(wishlistList.size() - 1);
        assertThat(testWishlist.getIdWishlist()).isEqualTo(DEFAULT_ID_WISHLIST);
        assertThat(testWishlist.getIdUser()).isEqualTo(DEFAULT_ID_USER);
        assertThat(testWishlist.getIdProducts()).isEqualTo(DEFAULT_ID_PRODUCTS);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(1)).save(testWishlist);
    }

    @Test
    @Transactional
    void createWishlistWithExistingId() throws Exception {
        // Create the Wishlist with an existing ID
        wishlist.setId(1L);

        int databaseSizeBeforeCreate = wishlistRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishlistMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wishlist)))
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeCreate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    void getAllWishlists() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        // Get all the wishlistList
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishlist.getId().intValue())))
            .andExpect(jsonPath("$.[*].idWishlist").value(hasItem(DEFAULT_ID_WISHLIST)))
            .andExpect(jsonPath("$.[*].idUser").value(hasItem(DEFAULT_ID_USER)))
            .andExpect(jsonPath("$.[*].idProducts").value(hasItem(DEFAULT_ID_PRODUCTS)));
    }

    @Test
    @Transactional
    void getWishlist() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        // Get the wishlist
        restWishlistMockMvc
            .perform(get(ENTITY_API_URL_ID, wishlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishlist.getId().intValue()))
            .andExpect(jsonPath("$.idWishlist").value(DEFAULT_ID_WISHLIST))
            .andExpect(jsonPath("$.idUser").value(DEFAULT_ID_USER))
            .andExpect(jsonPath("$.idProducts").value(DEFAULT_ID_PRODUCTS));
    }

    @Test
    @Transactional
    void getNonExistingWishlist() throws Exception {
        // Get the wishlist
        restWishlistMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWishlist() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();

        // Update the wishlist
        Wishlist updatedWishlist = wishlistRepository.findById(wishlist.getId()).get();
        // Disconnect from session so that the updates on updatedWishlist are not directly saved in db
        em.detach(updatedWishlist);
        updatedWishlist.idWishlist(UPDATED_ID_WISHLIST).idUser(UPDATED_ID_USER).idProducts(UPDATED_ID_PRODUCTS);

        restWishlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWishlist.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWishlist))
            )
            .andExpect(status().isOk());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);
        Wishlist testWishlist = wishlistList.get(wishlistList.size() - 1);
        assertThat(testWishlist.getIdWishlist()).isEqualTo(UPDATED_ID_WISHLIST);
        assertThat(testWishlist.getIdUser()).isEqualTo(UPDATED_ID_USER);
        assertThat(testWishlist.getIdProducts()).isEqualTo(UPDATED_ID_PRODUCTS);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository).save(testWishlist);
    }

    @Test
    @Transactional
    void putNonExistingWishlist() throws Exception {
        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();
        wishlist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishlist.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishlist))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    void putWithIdMismatchWishlist() throws Exception {
        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();
        wishlist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishlist))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWishlist() throws Exception {
        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();
        wishlist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wishlist)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    void partialUpdateWishlistWithPatch() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();

        // Update the wishlist using partial update
        Wishlist partialUpdatedWishlist = new Wishlist();
        partialUpdatedWishlist.setId(wishlist.getId());

        partialUpdatedWishlist.idUser(UPDATED_ID_USER).idProducts(UPDATED_ID_PRODUCTS);

        restWishlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishlist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWishlist))
            )
            .andExpect(status().isOk());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);
        Wishlist testWishlist = wishlistList.get(wishlistList.size() - 1);
        assertThat(testWishlist.getIdWishlist()).isEqualTo(DEFAULT_ID_WISHLIST);
        assertThat(testWishlist.getIdUser()).isEqualTo(UPDATED_ID_USER);
        assertThat(testWishlist.getIdProducts()).isEqualTo(UPDATED_ID_PRODUCTS);
    }

    @Test
    @Transactional
    void fullUpdateWishlistWithPatch() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();

        // Update the wishlist using partial update
        Wishlist partialUpdatedWishlist = new Wishlist();
        partialUpdatedWishlist.setId(wishlist.getId());

        partialUpdatedWishlist.idWishlist(UPDATED_ID_WISHLIST).idUser(UPDATED_ID_USER).idProducts(UPDATED_ID_PRODUCTS);

        restWishlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishlist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWishlist))
            )
            .andExpect(status().isOk());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);
        Wishlist testWishlist = wishlistList.get(wishlistList.size() - 1);
        assertThat(testWishlist.getIdWishlist()).isEqualTo(UPDATED_ID_WISHLIST);
        assertThat(testWishlist.getIdUser()).isEqualTo(UPDATED_ID_USER);
        assertThat(testWishlist.getIdProducts()).isEqualTo(UPDATED_ID_PRODUCTS);
    }

    @Test
    @Transactional
    void patchNonExistingWishlist() throws Exception {
        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();
        wishlist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wishlist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wishlist))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWishlist() throws Exception {
        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();
        wishlist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wishlist))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWishlist() throws Exception {
        int databaseSizeBeforeUpdate = wishlistRepository.findAll().size();
        wishlist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishlistMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(wishlist)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wishlist in the database
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(0)).save(wishlist);
    }

    @Test
    @Transactional
    void deleteWishlist() throws Exception {
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);

        int databaseSizeBeforeDelete = wishlistRepository.findAll().size();

        // Delete the wishlist
        restWishlistMockMvc
            .perform(delete(ENTITY_API_URL_ID, wishlist.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertThat(wishlistList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Wishlist in Elasticsearch
        verify(mockWishlistSearchRepository, times(1)).deleteById(wishlist.getId());
    }

    @Test
    @Transactional
    void searchWishlist() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        wishlistRepository.saveAndFlush(wishlist);
        when(mockWishlistSearchRepository.search(queryStringQuery("id:" + wishlist.getId())))
            .thenReturn(Collections.singletonList(wishlist));

        // Search the wishlist
        restWishlistMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + wishlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishlist.getId().intValue())))
            .andExpect(jsonPath("$.[*].idWishlist").value(hasItem(DEFAULT_ID_WISHLIST)))
            .andExpect(jsonPath("$.[*].idUser").value(hasItem(DEFAULT_ID_USER)))
            .andExpect(jsonPath("$.[*].idProducts").value(hasItem(DEFAULT_ID_PRODUCTS)));
    }
}
