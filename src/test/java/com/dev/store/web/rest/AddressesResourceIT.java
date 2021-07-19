package com.dev.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dev.store.IntegrationTest;
import com.dev.store.domain.Addresses;
import com.dev.store.repository.AddressesRepository;
import com.dev.store.repository.search.AddressesSearchRepository;
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
 * Integration tests for the {@link AddressesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AddressesResourceIT {

    private static final Integer DEFAULT_ID_ADRESSES = 1;
    private static final Integer UPDATED_ID_ADRESSES = 2;

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_STREET_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_STREET_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE_PROVINCE = "AAAAAAAAAA";
    private static final String UPDATED_STATE_PROVINCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/addresses";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AddressesRepository addressesRepository;

    /**
     * This repository is mocked in the com.dev.store.repository.search test package.
     *
     * @see com.dev.store.repository.search.AddressesSearchRepositoryMockConfiguration
     */
    @Autowired
    private AddressesSearchRepository mockAddressesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAddressesMockMvc;

    private Addresses addresses;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Addresses createEntity(EntityManager em) {
        Addresses addresses = new Addresses()
            .idAdresses(DEFAULT_ID_ADRESSES)
            .country(DEFAULT_COUNTRY)
            .streetAddress(DEFAULT_STREET_ADDRESS)
            .postalCode(DEFAULT_POSTAL_CODE)
            .city(DEFAULT_CITY)
            .stateProvince(DEFAULT_STATE_PROVINCE);
        return addresses;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Addresses createUpdatedEntity(EntityManager em) {
        Addresses addresses = new Addresses()
            .idAdresses(UPDATED_ID_ADRESSES)
            .country(UPDATED_COUNTRY)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE);
        return addresses;
    }

    @BeforeEach
    public void initTest() {
        addresses = createEntity(em);
    }

    @Test
    @Transactional
    void createAddresses() throws Exception {
        int databaseSizeBeforeCreate = addressesRepository.findAll().size();
        // Create the Addresses
        restAddressesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isCreated());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeCreate + 1);
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getIdAdresses()).isEqualTo(DEFAULT_ID_ADRESSES);
        assertThat(testAddresses.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testAddresses.getStreetAddress()).isEqualTo(DEFAULT_STREET_ADDRESS);
        assertThat(testAddresses.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testAddresses.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testAddresses.getStateProvince()).isEqualTo(DEFAULT_STATE_PROVINCE);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(1)).save(testAddresses);
    }

    @Test
    @Transactional
    void createAddressesWithExistingId() throws Exception {
        // Create the Addresses with an existing ID
        addresses.setId(1L);

        int databaseSizeBeforeCreate = addressesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAddressesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeCreate);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(0)).save(addresses);
    }

    @Test
    @Transactional
    void getAllAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get all the addressesList
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addresses.getId().intValue())))
            .andExpect(jsonPath("$.[*].idAdresses").value(hasItem(DEFAULT_ID_ADRESSES)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)));
    }

    @Test
    @Transactional
    void getAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        // Get the addresses
        restAddressesMockMvc
            .perform(get(ENTITY_API_URL_ID, addresses.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(addresses.getId().intValue()))
            .andExpect(jsonPath("$.idAdresses").value(DEFAULT_ID_ADRESSES))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.streetAddress").value(DEFAULT_STREET_ADDRESS))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.stateProvince").value(DEFAULT_STATE_PROVINCE));
    }

    @Test
    @Transactional
    void getNonExistingAddresses() throws Exception {
        // Get the addresses
        restAddressesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();

        // Update the addresses
        Addresses updatedAddresses = addressesRepository.findById(addresses.getId()).get();
        // Disconnect from session so that the updates on updatedAddresses are not directly saved in db
        em.detach(updatedAddresses);
        updatedAddresses
            .idAdresses(UPDATED_ID_ADRESSES)
            .country(UPDATED_COUNTRY)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE);

        restAddressesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAddresses.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAddresses))
            )
            .andExpect(status().isOk());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getIdAdresses()).isEqualTo(UPDATED_ID_ADRESSES);
        assertThat(testAddresses.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAddresses.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testAddresses.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAddresses.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddresses.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository).save(testAddresses);
    }

    @Test
    @Transactional
    void putNonExistingAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        addresses.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addresses.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(0)).save(addresses);
    }

    @Test
    @Transactional
    void putWithIdMismatchAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(0)).save(addresses);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addresses)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(0)).save(addresses);
    }

    @Test
    @Transactional
    void partialUpdateAddressesWithPatch() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();

        // Update the addresses using partial update
        Addresses partialUpdatedAddresses = new Addresses();
        partialUpdatedAddresses.setId(addresses.getId());

        partialUpdatedAddresses.idAdresses(UPDATED_ID_ADRESSES).streetAddress(UPDATED_STREET_ADDRESS).stateProvince(UPDATED_STATE_PROVINCE);

        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddresses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddresses))
            )
            .andExpect(status().isOk());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getIdAdresses()).isEqualTo(UPDATED_ID_ADRESSES);
        assertThat(testAddresses.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testAddresses.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testAddresses.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testAddresses.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testAddresses.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);
    }

    @Test
    @Transactional
    void fullUpdateAddressesWithPatch() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();

        // Update the addresses using partial update
        Addresses partialUpdatedAddresses = new Addresses();
        partialUpdatedAddresses.setId(addresses.getId());

        partialUpdatedAddresses
            .idAdresses(UPDATED_ID_ADRESSES)
            .country(UPDATED_COUNTRY)
            .streetAddress(UPDATED_STREET_ADDRESS)
            .postalCode(UPDATED_POSTAL_CODE)
            .city(UPDATED_CITY)
            .stateProvince(UPDATED_STATE_PROVINCE);

        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddresses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddresses))
            )
            .andExpect(status().isOk());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);
        Addresses testAddresses = addressesList.get(addressesList.size() - 1);
        assertThat(testAddresses.getIdAdresses()).isEqualTo(UPDATED_ID_ADRESSES);
        assertThat(testAddresses.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAddresses.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testAddresses.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAddresses.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddresses.getStateProvince()).isEqualTo(UPDATED_STATE_PROVINCE);
    }

    @Test
    @Transactional
    void patchNonExistingAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        addresses.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, addresses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(0)).save(addresses);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(0)).save(addresses);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAddresses() throws Exception {
        int databaseSizeBeforeUpdate = addressesRepository.findAll().size();
        addresses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(addresses))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Addresses in the database
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(0)).save(addresses);
    }

    @Test
    @Transactional
    void deleteAddresses() throws Exception {
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);

        int databaseSizeBeforeDelete = addressesRepository.findAll().size();

        // Delete the addresses
        restAddressesMockMvc
            .perform(delete(ENTITY_API_URL_ID, addresses.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Addresses> addressesList = addressesRepository.findAll();
        assertThat(addressesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Addresses in Elasticsearch
        verify(mockAddressesSearchRepository, times(1)).deleteById(addresses.getId());
    }

    @Test
    @Transactional
    void searchAddresses() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        addressesRepository.saveAndFlush(addresses);
        when(mockAddressesSearchRepository.search(queryStringQuery("id:" + addresses.getId())))
            .thenReturn(Collections.singletonList(addresses));

        // Search the addresses
        restAddressesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + addresses.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addresses.getId().intValue())))
            .andExpect(jsonPath("$.[*].idAdresses").value(hasItem(DEFAULT_ID_ADRESSES)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].stateProvince").value(hasItem(DEFAULT_STATE_PROVINCE)));
    }
}
