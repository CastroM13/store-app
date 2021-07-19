package com.dev.store.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.dev.store.domain.Cards;
import com.dev.store.repository.CardsRepository;
import com.dev.store.repository.search.CardsSearchRepository;
import com.dev.store.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dev.store.domain.Cards}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CardsResource {

    private final Logger log = LoggerFactory.getLogger(CardsResource.class);

    private static final String ENTITY_NAME = "cards";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CardsRepository cardsRepository;

    private final CardsSearchRepository cardsSearchRepository;

    public CardsResource(CardsRepository cardsRepository, CardsSearchRepository cardsSearchRepository) {
        this.cardsRepository = cardsRepository;
        this.cardsSearchRepository = cardsSearchRepository;
    }

    /**
     * {@code POST  /cards} : Create a new cards.
     *
     * @param cards the cards to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cards, or with status {@code 400 (Bad Request)} if the cards has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cards")
    public ResponseEntity<Cards> createCards(@RequestBody Cards cards) throws URISyntaxException {
        log.debug("REST request to save Cards : {}", cards);
        if (cards.getId() != null) {
            throw new BadRequestAlertException("A new cards cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Cards result = cardsRepository.save(cards);
        cardsSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/cards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cards/:id} : Updates an existing cards.
     *
     * @param id the id of the cards to save.
     * @param cards the cards to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cards,
     * or with status {@code 400 (Bad Request)} if the cards is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cards couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cards/{id}")
    public ResponseEntity<Cards> updateCards(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cards cards)
        throws URISyntaxException {
        log.debug("REST request to update Cards : {}, {}", id, cards);
        if (cards.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cards.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Cards result = cardsRepository.save(cards);
        cardsSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cards.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cards/:id} : Partial updates given fields of an existing cards, field will ignore if it is null
     *
     * @param id the id of the cards to save.
     * @param cards the cards to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cards,
     * or with status {@code 400 (Bad Request)} if the cards is not valid,
     * or with status {@code 404 (Not Found)} if the cards is not found,
     * or with status {@code 500 (Internal Server Error)} if the cards couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cards/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Cards> partialUpdateCards(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cards cards)
        throws URISyntaxException {
        log.debug("REST request to partial update Cards partially : {}, {}", id, cards);
        if (cards.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cards.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Cards> result = cardsRepository
            .findById(cards.getId())
            .map(
                existingCards -> {
                    if (cards.getIdCards() != null) {
                        existingCards.setIdCards(cards.getIdCards());
                    }
                    if (cards.getIdentityDocument() != null) {
                        existingCards.setIdentityDocument(cards.getIdentityDocument());
                    }
                    if (cards.getCardNumber() != null) {
                        existingCards.setCardNumber(cards.getCardNumber());
                    }
                    if (cards.getSecurityCode() != null) {
                        existingCards.setSecurityCode(cards.getSecurityCode());
                    }
                    if (cards.getExpirationDate() != null) {
                        existingCards.setExpirationDate(cards.getExpirationDate());
                    }
                    if (cards.getCardholder() != null) {
                        existingCards.setCardholder(cards.getCardholder());
                    }
                    if (cards.getParcelQuantity() != null) {
                        existingCards.setParcelQuantity(cards.getParcelQuantity());
                    }

                    return existingCards;
                }
            )
            .map(cardsRepository::save)
            .map(
                savedCards -> {
                    cardsSearchRepository.save(savedCards);

                    return savedCards;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cards.getId().toString())
        );
    }

    /**
     * {@code GET  /cards} : get all the cards.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cards in body.
     */
    @GetMapping("/cards")
    public List<Cards> getAllCards() {
        log.debug("REST request to get all Cards");
        return cardsRepository.findAll();
    }

    /**
     * {@code GET  /cards/:id} : get the "id" cards.
     *
     * @param id the id of the cards to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cards, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cards/{id}")
    public ResponseEntity<Cards> getCards(@PathVariable Long id) {
        log.debug("REST request to get Cards : {}", id);
        Optional<Cards> cards = cardsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cards);
    }

    /**
     * {@code DELETE  /cards/:id} : delete the "id" cards.
     *
     * @param id the id of the cards to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCards(@PathVariable Long id) {
        log.debug("REST request to delete Cards : {}", id);
        cardsRepository.deleteById(id);
        cardsSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/cards?query=:query} : search for the cards corresponding
     * to the query.
     *
     * @param query the query of the cards search.
     * @return the result of the search.
     */
    @GetMapping("/_search/cards")
    public List<Cards> searchCards(@RequestParam String query) {
        log.debug("REST request to search Cards for query {}", query);
        return StreamSupport
            .stream(cardsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
