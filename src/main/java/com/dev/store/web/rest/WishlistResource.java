package com.dev.store.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.dev.store.domain.Wishlist;
import com.dev.store.repository.WishlistRepository;
import com.dev.store.repository.search.WishlistSearchRepository;
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
 * REST controller for managing {@link com.dev.store.domain.Wishlist}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WishlistResource {

    private final Logger log = LoggerFactory.getLogger(WishlistResource.class);

    private static final String ENTITY_NAME = "wishlist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WishlistRepository wishlistRepository;

    private final WishlistSearchRepository wishlistSearchRepository;

    public WishlistResource(WishlistRepository wishlistRepository, WishlistSearchRepository wishlistSearchRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistSearchRepository = wishlistSearchRepository;
    }

    /**
     * {@code POST  /wishlists} : Create a new wishlist.
     *
     * @param wishlist the wishlist to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishlist, or with status {@code 400 (Bad Request)} if the wishlist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wishlists")
    public ResponseEntity<Wishlist> createWishlist(@RequestBody Wishlist wishlist) throws URISyntaxException {
        log.debug("REST request to save Wishlist : {}", wishlist);
        if (wishlist.getId() != null) {
            throw new BadRequestAlertException("A new wishlist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Wishlist result = wishlistRepository.save(wishlist);
        wishlistSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/wishlists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wishlists/:id} : Updates an existing wishlist.
     *
     * @param id the id of the wishlist to save.
     * @param wishlist the wishlist to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishlist,
     * or with status {@code 400 (Bad Request)} if the wishlist is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wishlist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wishlists/{id}")
    public ResponseEntity<Wishlist> updateWishlist(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Wishlist wishlist
    ) throws URISyntaxException {
        log.debug("REST request to update Wishlist : {}, {}", id, wishlist);
        if (wishlist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishlist.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishlistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Wishlist result = wishlistRepository.save(wishlist);
        wishlistSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishlist.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /wishlists/:id} : Partial updates given fields of an existing wishlist, field will ignore if it is null
     *
     * @param id the id of the wishlist to save.
     * @param wishlist the wishlist to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishlist,
     * or with status {@code 400 (Bad Request)} if the wishlist is not valid,
     * or with status {@code 404 (Not Found)} if the wishlist is not found,
     * or with status {@code 500 (Internal Server Error)} if the wishlist couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/wishlists/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Wishlist> partialUpdateWishlist(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Wishlist wishlist
    ) throws URISyntaxException {
        log.debug("REST request to partial update Wishlist partially : {}, {}", id, wishlist);
        if (wishlist.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishlist.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishlistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Wishlist> result = wishlistRepository
            .findById(wishlist.getId())
            .map(
                existingWishlist -> {
                    if (wishlist.getIdWishlist() != null) {
                        existingWishlist.setIdWishlist(wishlist.getIdWishlist());
                    }
                    if (wishlist.getIdUser() != null) {
                        existingWishlist.setIdUser(wishlist.getIdUser());
                    }
                    if (wishlist.getIdProducts() != null) {
                        existingWishlist.setIdProducts(wishlist.getIdProducts());
                    }

                    return existingWishlist;
                }
            )
            .map(wishlistRepository::save)
            .map(
                savedWishlist -> {
                    wishlistSearchRepository.save(savedWishlist);

                    return savedWishlist;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishlist.getId().toString())
        );
    }

    /**
     * {@code GET  /wishlists} : get all the wishlists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wishlists in body.
     */
    @GetMapping("/wishlists")
    public List<Wishlist> getAllWishlists() {
        log.debug("REST request to get all Wishlists");
        return wishlistRepository.findAll();
    }

    /**
     * {@code GET  /wishlists/:id} : get the "id" wishlist.
     *
     * @param id the id of the wishlist to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishlist, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wishlists/{id}")
    public ResponseEntity<Wishlist> getWishlist(@PathVariable Long id) {
        log.debug("REST request to get Wishlist : {}", id);
        Optional<Wishlist> wishlist = wishlistRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wishlist);
    }

    /**
     * {@code DELETE  /wishlists/:id} : delete the "id" wishlist.
     *
     * @param id the id of the wishlist to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wishlists/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        log.debug("REST request to delete Wishlist : {}", id);
        wishlistRepository.deleteById(id);
        wishlistSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/wishlists?query=:query} : search for the wishlist corresponding
     * to the query.
     *
     * @param query the query of the wishlist search.
     * @return the result of the search.
     */
    @GetMapping("/_search/wishlists")
    public List<Wishlist> searchWishlists(@RequestParam String query) {
        log.debug("REST request to search Wishlists for query {}", query);
        return StreamSupport
            .stream(wishlistSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
