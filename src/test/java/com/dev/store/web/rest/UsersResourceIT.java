package com.dev.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dev.store.IntegrationTest;
import com.dev.store.domain.Users;
import com.dev.store.repository.UsersRepository;
import com.dev.store.repository.search.UsersSearchRepository;
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
 * Integration tests for the {@link UsersResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UsersResourceIT {

    private static final Integer DEFAULT_ID_USER = 1;
    private static final Integer UPDATED_ID_USER = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Integer DEFAULT_ID_ADRESSES = 1;
    private static final Integer UPDATED_ID_ADRESSES = 2;

    private static final Integer DEFAULT_ID_CARDS = 1;
    private static final Integer UPDATED_ID_CARDS = 2;

    private static final Integer DEFAULT_ID_ORDERS = 1;
    private static final Integer UPDATED_ID_ORDERS = 2;

    private static final Integer DEFAULT_ID_WISHLIST = 1;
    private static final Integer UPDATED_ID_WISHLIST = 2;

    private static final String ENTITY_API_URL = "/api/users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/users";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UsersRepository usersRepository;

    /**
     * This repository is mocked in the com.dev.store.repository.search test package.
     *
     * @see com.dev.store.repository.search.UsersSearchRepositoryMockConfiguration
     */
    @Autowired
    private UsersSearchRepository mockUsersSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUsersMockMvc;

    private Users users;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createEntity(EntityManager em) {
        Users users = new Users()
            .idUser(DEFAULT_ID_USER)
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD)
            .idAdresses(DEFAULT_ID_ADRESSES)
            .idCards(DEFAULT_ID_CARDS)
            .idOrders(DEFAULT_ID_ORDERS)
            .idWishlist(DEFAULT_ID_WISHLIST);
        return users;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createUpdatedEntity(EntityManager em) {
        Users users = new Users()
            .idUser(UPDATED_ID_USER)
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .idAdresses(UPDATED_ID_ADRESSES)
            .idCards(UPDATED_ID_CARDS)
            .idOrders(UPDATED_ID_ORDERS)
            .idWishlist(UPDATED_ID_WISHLIST);
        return users;
    }

    @BeforeEach
    public void initTest() {
        users = createEntity(em);
    }

    @Test
    @Transactional
    void createUsers() throws Exception {
        int databaseSizeBeforeCreate = usersRepository.findAll().size();
        // Create the Users
        restUsersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(users)))
            .andExpect(status().isCreated());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeCreate + 1);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getIdUser()).isEqualTo(DEFAULT_ID_USER);
        assertThat(testUsers.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUsers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUsers.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUsers.getIdAdresses()).isEqualTo(DEFAULT_ID_ADRESSES);
        assertThat(testUsers.getIdCards()).isEqualTo(DEFAULT_ID_CARDS);
        assertThat(testUsers.getIdOrders()).isEqualTo(DEFAULT_ID_ORDERS);
        assertThat(testUsers.getIdWishlist()).isEqualTo(DEFAULT_ID_WISHLIST);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(1)).save(testUsers);
    }

    @Test
    @Transactional
    void createUsersWithExistingId() throws Exception {
        // Create the Users with an existing ID
        users.setId(1L);

        int databaseSizeBeforeCreate = usersRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUsersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(users)))
            .andExpect(status().isBadRequest());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeCreate);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(0)).save(users);
    }

    @Test
    @Transactional
    void getAllUsers() throws Exception {
        // Initialize the database
        usersRepository.saveAndFlush(users);

        // Get all the usersList
        restUsersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(users.getId().intValue())))
            .andExpect(jsonPath("$.[*].idUser").value(hasItem(DEFAULT_ID_USER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].idAdresses").value(hasItem(DEFAULT_ID_ADRESSES)))
            .andExpect(jsonPath("$.[*].idCards").value(hasItem(DEFAULT_ID_CARDS)))
            .andExpect(jsonPath("$.[*].idOrders").value(hasItem(DEFAULT_ID_ORDERS)))
            .andExpect(jsonPath("$.[*].idWishlist").value(hasItem(DEFAULT_ID_WISHLIST)));
    }

    @Test
    @Transactional
    void getUsers() throws Exception {
        // Initialize the database
        usersRepository.saveAndFlush(users);

        // Get the users
        restUsersMockMvc
            .perform(get(ENTITY_API_URL_ID, users.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(users.getId().intValue()))
            .andExpect(jsonPath("$.idUser").value(DEFAULT_ID_USER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.idAdresses").value(DEFAULT_ID_ADRESSES))
            .andExpect(jsonPath("$.idCards").value(DEFAULT_ID_CARDS))
            .andExpect(jsonPath("$.idOrders").value(DEFAULT_ID_ORDERS))
            .andExpect(jsonPath("$.idWishlist").value(DEFAULT_ID_WISHLIST));
    }

    @Test
    @Transactional
    void getNonExistingUsers() throws Exception {
        // Get the users
        restUsersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUsers() throws Exception {
        // Initialize the database
        usersRepository.saveAndFlush(users);

        int databaseSizeBeforeUpdate = usersRepository.findAll().size();

        // Update the users
        Users updatedUsers = usersRepository.findById(users.getId()).get();
        // Disconnect from session so that the updates on updatedUsers are not directly saved in db
        em.detach(updatedUsers);
        updatedUsers
            .idUser(UPDATED_ID_USER)
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .idAdresses(UPDATED_ID_ADRESSES)
            .idCards(UPDATED_ID_CARDS)
            .idOrders(UPDATED_ID_ORDERS)
            .idWishlist(UPDATED_ID_WISHLIST);

        restUsersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUsers.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUsers))
            )
            .andExpect(status().isOk());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getIdUser()).isEqualTo(UPDATED_ID_USER);
        assertThat(testUsers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUsers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUsers.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUsers.getIdAdresses()).isEqualTo(UPDATED_ID_ADRESSES);
        assertThat(testUsers.getIdCards()).isEqualTo(UPDATED_ID_CARDS);
        assertThat(testUsers.getIdOrders()).isEqualTo(UPDATED_ID_ORDERS);
        assertThat(testUsers.getIdWishlist()).isEqualTo(UPDATED_ID_WISHLIST);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository).save(testUsers);
    }

    @Test
    @Transactional
    void putNonExistingUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().size();
        users.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUsersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, users.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(users))
            )
            .andExpect(status().isBadRequest());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(0)).save(users);
    }

    @Test
    @Transactional
    void putWithIdMismatchUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(users))
            )
            .andExpect(status().isBadRequest());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(0)).save(users);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(users)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(0)).save(users);
    }

    @Test
    @Transactional
    void partialUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        usersRepository.saveAndFlush(users);

        int databaseSizeBeforeUpdate = usersRepository.findAll().size();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        partialUpdatedUsers
            .name(UPDATED_NAME)
            .password(UPDATED_PASSWORD)
            .idCards(UPDATED_ID_CARDS)
            .idOrders(UPDATED_ID_ORDERS)
            .idWishlist(UPDATED_ID_WISHLIST);

        restUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUsers))
            )
            .andExpect(status().isOk());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getIdUser()).isEqualTo(DEFAULT_ID_USER);
        assertThat(testUsers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUsers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUsers.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUsers.getIdAdresses()).isEqualTo(DEFAULT_ID_ADRESSES);
        assertThat(testUsers.getIdCards()).isEqualTo(UPDATED_ID_CARDS);
        assertThat(testUsers.getIdOrders()).isEqualTo(UPDATED_ID_ORDERS);
        assertThat(testUsers.getIdWishlist()).isEqualTo(UPDATED_ID_WISHLIST);
    }

    @Test
    @Transactional
    void fullUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        usersRepository.saveAndFlush(users);

        int databaseSizeBeforeUpdate = usersRepository.findAll().size();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        partialUpdatedUsers
            .idUser(UPDATED_ID_USER)
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .idAdresses(UPDATED_ID_ADRESSES)
            .idCards(UPDATED_ID_CARDS)
            .idOrders(UPDATED_ID_ORDERS)
            .idWishlist(UPDATED_ID_WISHLIST);

        restUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUsers))
            )
            .andExpect(status().isOk());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getIdUser()).isEqualTo(UPDATED_ID_USER);
        assertThat(testUsers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUsers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUsers.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUsers.getIdAdresses()).isEqualTo(UPDATED_ID_ADRESSES);
        assertThat(testUsers.getIdCards()).isEqualTo(UPDATED_ID_CARDS);
        assertThat(testUsers.getIdOrders()).isEqualTo(UPDATED_ID_ORDERS);
        assertThat(testUsers.getIdWishlist()).isEqualTo(UPDATED_ID_WISHLIST);
    }

    @Test
    @Transactional
    void patchNonExistingUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().size();
        users.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, users.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(users))
            )
            .andExpect(status().isBadRequest());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(0)).save(users);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(users))
            )
            .andExpect(status().isBadRequest());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(0)).save(users);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUsersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(users)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(0)).save(users);
    }

    @Test
    @Transactional
    void deleteUsers() throws Exception {
        // Initialize the database
        usersRepository.saveAndFlush(users);

        int databaseSizeBeforeDelete = usersRepository.findAll().size();

        // Delete the users
        restUsersMockMvc
            .perform(delete(ENTITY_API_URL_ID, users.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Users> usersList = usersRepository.findAll();
        assertThat(usersList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Users in Elasticsearch
        verify(mockUsersSearchRepository, times(1)).deleteById(users.getId());
    }

    @Test
    @Transactional
    void searchUsers() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        usersRepository.saveAndFlush(users);
        when(mockUsersSearchRepository.search(queryStringQuery("id:" + users.getId()))).thenReturn(Collections.singletonList(users));

        // Search the users
        restUsersMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + users.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(users.getId().intValue())))
            .andExpect(jsonPath("$.[*].idUser").value(hasItem(DEFAULT_ID_USER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].idAdresses").value(hasItem(DEFAULT_ID_ADRESSES)))
            .andExpect(jsonPath("$.[*].idCards").value(hasItem(DEFAULT_ID_CARDS)))
            .andExpect(jsonPath("$.[*].idOrders").value(hasItem(DEFAULT_ID_ORDERS)))
            .andExpect(jsonPath("$.[*].idWishlist").value(hasItem(DEFAULT_ID_WISHLIST)));
    }
}
