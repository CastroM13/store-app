package com.dev.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dev.store.IntegrationTest;
import com.dev.store.domain.Cards;
import com.dev.store.repository.CardsRepository;
import com.dev.store.repository.search.CardsSearchRepository;
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
 * Integration tests for the {@link CardsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CardsResourceIT {

    private static final Integer DEFAULT_ID_CARDS = 1;
    private static final Integer UPDATED_ID_CARDS = 2;

    private static final String DEFAULT_IDENTITY_DOCUMENT = "AAAAAAAAAA";
    private static final String UPDATED_IDENTITY_DOCUMENT = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CARD_NUMBER = "BBBBBBBBBB";

    private static final Integer DEFAULT_SECURITY_CODE = 1;
    private static final Integer UPDATED_SECURITY_CODE = 2;

    private static final String DEFAULT_EXPIRATION_DATE = "AAAAAAAAAA";
    private static final String UPDATED_EXPIRATION_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_CARDHOLDER = "AAAAAAAAAA";
    private static final String UPDATED_CARDHOLDER = "BBBBBBBBBB";

    private static final Integer DEFAULT_PARCEL_QUANTITY = 1;
    private static final Integer UPDATED_PARCEL_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/cards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/cards";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CardsRepository cardsRepository;

    /**
     * This repository is mocked in the com.dev.store.repository.search test package.
     *
     * @see com.dev.store.repository.search.CardsSearchRepositoryMockConfiguration
     */
    @Autowired
    private CardsSearchRepository mockCardsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCardsMockMvc;

    private Cards cards;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cards createEntity(EntityManager em) {
        Cards cards = new Cards()
            .idCards(DEFAULT_ID_CARDS)
            .identityDocument(DEFAULT_IDENTITY_DOCUMENT)
            .cardNumber(DEFAULT_CARD_NUMBER)
            .securityCode(DEFAULT_SECURITY_CODE)
            .expirationDate(DEFAULT_EXPIRATION_DATE)
            .cardholder(DEFAULT_CARDHOLDER)
            .parcelQuantity(DEFAULT_PARCEL_QUANTITY);
        return cards;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cards createUpdatedEntity(EntityManager em) {
        Cards cards = new Cards()
            .idCards(UPDATED_ID_CARDS)
            .identityDocument(UPDATED_IDENTITY_DOCUMENT)
            .cardNumber(UPDATED_CARD_NUMBER)
            .securityCode(UPDATED_SECURITY_CODE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .cardholder(UPDATED_CARDHOLDER)
            .parcelQuantity(UPDATED_PARCEL_QUANTITY);
        return cards;
    }

    @BeforeEach
    public void initTest() {
        cards = createEntity(em);
    }

    @Test
    @Transactional
    void createCards() throws Exception {
        int databaseSizeBeforeCreate = cardsRepository.findAll().size();
        // Create the Cards
        restCardsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cards)))
            .andExpect(status().isCreated());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeCreate + 1);
        Cards testCards = cardsList.get(cardsList.size() - 1);
        assertThat(testCards.getIdCards()).isEqualTo(DEFAULT_ID_CARDS);
        assertThat(testCards.getIdentityDocument()).isEqualTo(DEFAULT_IDENTITY_DOCUMENT);
        assertThat(testCards.getCardNumber()).isEqualTo(DEFAULT_CARD_NUMBER);
        assertThat(testCards.getSecurityCode()).isEqualTo(DEFAULT_SECURITY_CODE);
        assertThat(testCards.getExpirationDate()).isEqualTo(DEFAULT_EXPIRATION_DATE);
        assertThat(testCards.getCardholder()).isEqualTo(DEFAULT_CARDHOLDER);
        assertThat(testCards.getParcelQuantity()).isEqualTo(DEFAULT_PARCEL_QUANTITY);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(1)).save(testCards);
    }

    @Test
    @Transactional
    void createCardsWithExistingId() throws Exception {
        // Create the Cards with an existing ID
        cards.setId(1L);

        int databaseSizeBeforeCreate = cardsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCardsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cards)))
            .andExpect(status().isBadRequest());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(0)).save(cards);
    }

    @Test
    @Transactional
    void getAllCards() throws Exception {
        // Initialize the database
        cardsRepository.saveAndFlush(cards);

        // Get all the cardsList
        restCardsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cards.getId().intValue())))
            .andExpect(jsonPath("$.[*].idCards").value(hasItem(DEFAULT_ID_CARDS)))
            .andExpect(jsonPath("$.[*].identityDocument").value(hasItem(DEFAULT_IDENTITY_DOCUMENT)))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].securityCode").value(hasItem(DEFAULT_SECURITY_CODE)))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE)))
            .andExpect(jsonPath("$.[*].cardholder").value(hasItem(DEFAULT_CARDHOLDER)))
            .andExpect(jsonPath("$.[*].parcelQuantity").value(hasItem(DEFAULT_PARCEL_QUANTITY)));
    }

    @Test
    @Transactional
    void getCards() throws Exception {
        // Initialize the database
        cardsRepository.saveAndFlush(cards);

        // Get the cards
        restCardsMockMvc
            .perform(get(ENTITY_API_URL_ID, cards.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cards.getId().intValue()))
            .andExpect(jsonPath("$.idCards").value(DEFAULT_ID_CARDS))
            .andExpect(jsonPath("$.identityDocument").value(DEFAULT_IDENTITY_DOCUMENT))
            .andExpect(jsonPath("$.cardNumber").value(DEFAULT_CARD_NUMBER))
            .andExpect(jsonPath("$.securityCode").value(DEFAULT_SECURITY_CODE))
            .andExpect(jsonPath("$.expirationDate").value(DEFAULT_EXPIRATION_DATE))
            .andExpect(jsonPath("$.cardholder").value(DEFAULT_CARDHOLDER))
            .andExpect(jsonPath("$.parcelQuantity").value(DEFAULT_PARCEL_QUANTITY));
    }

    @Test
    @Transactional
    void getNonExistingCards() throws Exception {
        // Get the cards
        restCardsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCards() throws Exception {
        // Initialize the database
        cardsRepository.saveAndFlush(cards);

        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();

        // Update the cards
        Cards updatedCards = cardsRepository.findById(cards.getId()).get();
        // Disconnect from session so that the updates on updatedCards are not directly saved in db
        em.detach(updatedCards);
        updatedCards
            .idCards(UPDATED_ID_CARDS)
            .identityDocument(UPDATED_IDENTITY_DOCUMENT)
            .cardNumber(UPDATED_CARD_NUMBER)
            .securityCode(UPDATED_SECURITY_CODE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .cardholder(UPDATED_CARDHOLDER)
            .parcelQuantity(UPDATED_PARCEL_QUANTITY);

        restCardsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCards.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCards))
            )
            .andExpect(status().isOk());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);
        Cards testCards = cardsList.get(cardsList.size() - 1);
        assertThat(testCards.getIdCards()).isEqualTo(UPDATED_ID_CARDS);
        assertThat(testCards.getIdentityDocument()).isEqualTo(UPDATED_IDENTITY_DOCUMENT);
        assertThat(testCards.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testCards.getSecurityCode()).isEqualTo(UPDATED_SECURITY_CODE);
        assertThat(testCards.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
        assertThat(testCards.getCardholder()).isEqualTo(UPDATED_CARDHOLDER);
        assertThat(testCards.getParcelQuantity()).isEqualTo(UPDATED_PARCEL_QUANTITY);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository).save(testCards);
    }

    @Test
    @Transactional
    void putNonExistingCards() throws Exception {
        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();
        cards.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cards.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cards))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(0)).save(cards);
    }

    @Test
    @Transactional
    void putWithIdMismatchCards() throws Exception {
        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();
        cards.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cards))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(0)).save(cards);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCards() throws Exception {
        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();
        cards.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cards)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(0)).save(cards);
    }

    @Test
    @Transactional
    void partialUpdateCardsWithPatch() throws Exception {
        // Initialize the database
        cardsRepository.saveAndFlush(cards);

        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();

        // Update the cards using partial update
        Cards partialUpdatedCards = new Cards();
        partialUpdatedCards.setId(cards.getId());

        partialUpdatedCards.cardNumber(UPDATED_CARD_NUMBER).securityCode(UPDATED_SECURITY_CODE).cardholder(UPDATED_CARDHOLDER);

        restCardsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCards.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCards))
            )
            .andExpect(status().isOk());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);
        Cards testCards = cardsList.get(cardsList.size() - 1);
        assertThat(testCards.getIdCards()).isEqualTo(DEFAULT_ID_CARDS);
        assertThat(testCards.getIdentityDocument()).isEqualTo(DEFAULT_IDENTITY_DOCUMENT);
        assertThat(testCards.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testCards.getSecurityCode()).isEqualTo(UPDATED_SECURITY_CODE);
        assertThat(testCards.getExpirationDate()).isEqualTo(DEFAULT_EXPIRATION_DATE);
        assertThat(testCards.getCardholder()).isEqualTo(UPDATED_CARDHOLDER);
        assertThat(testCards.getParcelQuantity()).isEqualTo(DEFAULT_PARCEL_QUANTITY);
    }

    @Test
    @Transactional
    void fullUpdateCardsWithPatch() throws Exception {
        // Initialize the database
        cardsRepository.saveAndFlush(cards);

        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();

        // Update the cards using partial update
        Cards partialUpdatedCards = new Cards();
        partialUpdatedCards.setId(cards.getId());

        partialUpdatedCards
            .idCards(UPDATED_ID_CARDS)
            .identityDocument(UPDATED_IDENTITY_DOCUMENT)
            .cardNumber(UPDATED_CARD_NUMBER)
            .securityCode(UPDATED_SECURITY_CODE)
            .expirationDate(UPDATED_EXPIRATION_DATE)
            .cardholder(UPDATED_CARDHOLDER)
            .parcelQuantity(UPDATED_PARCEL_QUANTITY);

        restCardsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCards.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCards))
            )
            .andExpect(status().isOk());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);
        Cards testCards = cardsList.get(cardsList.size() - 1);
        assertThat(testCards.getIdCards()).isEqualTo(UPDATED_ID_CARDS);
        assertThat(testCards.getIdentityDocument()).isEqualTo(UPDATED_IDENTITY_DOCUMENT);
        assertThat(testCards.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testCards.getSecurityCode()).isEqualTo(UPDATED_SECURITY_CODE);
        assertThat(testCards.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
        assertThat(testCards.getCardholder()).isEqualTo(UPDATED_CARDHOLDER);
        assertThat(testCards.getParcelQuantity()).isEqualTo(UPDATED_PARCEL_QUANTITY);
    }

    @Test
    @Transactional
    void patchNonExistingCards() throws Exception {
        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();
        cards.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cards.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cards))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(0)).save(cards);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCards() throws Exception {
        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();
        cards.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cards))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(0)).save(cards);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCards() throws Exception {
        int databaseSizeBeforeUpdate = cardsRepository.findAll().size();
        cards.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cards)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cards in the database
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(0)).save(cards);
    }

    @Test
    @Transactional
    void deleteCards() throws Exception {
        // Initialize the database
        cardsRepository.saveAndFlush(cards);

        int databaseSizeBeforeDelete = cardsRepository.findAll().size();

        // Delete the cards
        restCardsMockMvc
            .perform(delete(ENTITY_API_URL_ID, cards.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cards> cardsList = cardsRepository.findAll();
        assertThat(cardsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Cards in Elasticsearch
        verify(mockCardsSearchRepository, times(1)).deleteById(cards.getId());
    }

    @Test
    @Transactional
    void searchCards() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        cardsRepository.saveAndFlush(cards);
        when(mockCardsSearchRepository.search(queryStringQuery("id:" + cards.getId()))).thenReturn(Collections.singletonList(cards));

        // Search the cards
        restCardsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + cards.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cards.getId().intValue())))
            .andExpect(jsonPath("$.[*].idCards").value(hasItem(DEFAULT_ID_CARDS)))
            .andExpect(jsonPath("$.[*].identityDocument").value(hasItem(DEFAULT_IDENTITY_DOCUMENT)))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].securityCode").value(hasItem(DEFAULT_SECURITY_CODE)))
            .andExpect(jsonPath("$.[*].expirationDate").value(hasItem(DEFAULT_EXPIRATION_DATE)))
            .andExpect(jsonPath("$.[*].cardholder").value(hasItem(DEFAULT_CARDHOLDER)))
            .andExpect(jsonPath("$.[*].parcelQuantity").value(hasItem(DEFAULT_PARCEL_QUANTITY)));
    }
}
