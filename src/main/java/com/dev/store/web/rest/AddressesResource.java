package com.dev.store.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.dev.store.domain.Addresses;
import com.dev.store.repository.AddressesRepository;
import com.dev.store.repository.search.AddressesSearchRepository;
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
 * REST controller for managing {@link com.dev.store.domain.Addresses}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AddressesResource {

    private final Logger log = LoggerFactory.getLogger(AddressesResource.class);

    private static final String ENTITY_NAME = "addresses";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressesRepository addressesRepository;

    private final AddressesSearchRepository addressesSearchRepository;

    public AddressesResource(AddressesRepository addressesRepository, AddressesSearchRepository addressesSearchRepository) {
        this.addressesRepository = addressesRepository;
        this.addressesSearchRepository = addressesSearchRepository;
    }

    /**
     * {@code POST  /addresses} : Create a new addresses.
     *
     * @param addresses the addresses to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new addresses, or with status {@code 400 (Bad Request)} if the addresses has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/addresses")
    public ResponseEntity<Addresses> createAddresses(@RequestBody Addresses addresses) throws URISyntaxException {
        log.debug("REST request to save Addresses : {}", addresses);
        if (addresses.getId() != null) {
            throw new BadRequestAlertException("A new addresses cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Addresses result = addressesRepository.save(addresses);
        addressesSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /addresses/:id} : Updates an existing addresses.
     *
     * @param id the id of the addresses to save.
     * @param addresses the addresses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addresses,
     * or with status {@code 400 (Bad Request)} if the addresses is not valid,
     * or with status {@code 500 (Internal Server Error)} if the addresses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<Addresses> updateAddresses(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Addresses addresses
    ) throws URISyntaxException {
        log.debug("REST request to update Addresses : {}, {}", id, addresses);
        if (addresses.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addresses.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Addresses result = addressesRepository.save(addresses);
        addressesSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, addresses.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /addresses/:id} : Partial updates given fields of an existing addresses, field will ignore if it is null
     *
     * @param id the id of the addresses to save.
     * @param addresses the addresses to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addresses,
     * or with status {@code 400 (Bad Request)} if the addresses is not valid,
     * or with status {@code 404 (Not Found)} if the addresses is not found,
     * or with status {@code 500 (Internal Server Error)} if the addresses couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/addresses/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Addresses> partialUpdateAddresses(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Addresses addresses
    ) throws URISyntaxException {
        log.debug("REST request to partial update Addresses partially : {}, {}", id, addresses);
        if (addresses.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addresses.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Addresses> result = addressesRepository
            .findById(addresses.getId())
            .map(
                existingAddresses -> {
                    if (addresses.getIdAdresses() != null) {
                        existingAddresses.setIdAdresses(addresses.getIdAdresses());
                    }
                    if (addresses.getCountry() != null) {
                        existingAddresses.setCountry(addresses.getCountry());
                    }
                    if (addresses.getStreetAddress() != null) {
                        existingAddresses.setStreetAddress(addresses.getStreetAddress());
                    }
                    if (addresses.getPostalCode() != null) {
                        existingAddresses.setPostalCode(addresses.getPostalCode());
                    }
                    if (addresses.getCity() != null) {
                        existingAddresses.setCity(addresses.getCity());
                    }
                    if (addresses.getStateProvince() != null) {
                        existingAddresses.setStateProvince(addresses.getStateProvince());
                    }

                    return existingAddresses;
                }
            )
            .map(addressesRepository::save)
            .map(
                savedAddresses -> {
                    addressesSearchRepository.save(savedAddresses);

                    return savedAddresses;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, addresses.getId().toString())
        );
    }

    /**
     * {@code GET  /addresses} : get all the addresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addresses in body.
     */
    @GetMapping("/addresses")
    public List<Addresses> getAllAddresses() {
        log.debug("REST request to get all Addresses");
        return addressesRepository.findAll();
    }

    /**
     * {@code GET  /addresses/:id} : get the "id" addresses.
     *
     * @param id the id of the addresses to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the addresses, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/addresses/{id}")
    public ResponseEntity<Addresses> getAddresses(@PathVariable Long id) {
        log.debug("REST request to get Addresses : {}", id);
        Optional<Addresses> addresses = addressesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(addresses);
    }

    /**
     * {@code DELETE  /addresses/:id} : delete the "id" addresses.
     *
     * @param id the id of the addresses to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddresses(@PathVariable Long id) {
        log.debug("REST request to delete Addresses : {}", id);
        addressesRepository.deleteById(id);
        addressesSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/addresses?query=:query} : search for the addresses corresponding
     * to the query.
     *
     * @param query the query of the addresses search.
     * @return the result of the search.
     */
    @GetMapping("/_search/addresses")
    public List<Addresses> searchAddresses(@RequestParam String query) {
        log.debug("REST request to search Addresses for query {}", query);
        return StreamSupport
            .stream(addressesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
