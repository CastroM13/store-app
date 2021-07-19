package com.dev.store.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.dev.store.domain.OrderProducts;
import com.dev.store.repository.OrderProductsRepository;
import com.dev.store.repository.search.OrderProductsSearchRepository;
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
 * REST controller for managing {@link com.dev.store.domain.OrderProducts}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OrderProductsResource {

    private final Logger log = LoggerFactory.getLogger(OrderProductsResource.class);

    private static final String ENTITY_NAME = "orderProducts";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderProductsRepository orderProductsRepository;

    private final OrderProductsSearchRepository orderProductsSearchRepository;

    public OrderProductsResource(
        OrderProductsRepository orderProductsRepository,
        OrderProductsSearchRepository orderProductsSearchRepository
    ) {
        this.orderProductsRepository = orderProductsRepository;
        this.orderProductsSearchRepository = orderProductsSearchRepository;
    }

    /**
     * {@code POST  /order-products} : Create a new orderProducts.
     *
     * @param orderProducts the orderProducts to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderProducts, or with status {@code 400 (Bad Request)} if the orderProducts has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-products")
    public ResponseEntity<OrderProducts> createOrderProducts(@RequestBody OrderProducts orderProducts) throws URISyntaxException {
        log.debug("REST request to save OrderProducts : {}", orderProducts);
        if (orderProducts.getId() != null) {
            throw new BadRequestAlertException("A new orderProducts cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderProducts result = orderProductsRepository.save(orderProducts);
        orderProductsSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/order-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-products/:id} : Updates an existing orderProducts.
     *
     * @param id the id of the orderProducts to save.
     * @param orderProducts the orderProducts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderProducts,
     * or with status {@code 400 (Bad Request)} if the orderProducts is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderProducts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-products/{id}")
    public ResponseEntity<OrderProducts> updateOrderProducts(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderProducts orderProducts
    ) throws URISyntaxException {
        log.debug("REST request to update OrderProducts : {}, {}", id, orderProducts);
        if (orderProducts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderProducts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderProductsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderProducts result = orderProductsRepository.save(orderProducts);
        orderProductsSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderProducts.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-products/:id} : Partial updates given fields of an existing orderProducts, field will ignore if it is null
     *
     * @param id the id of the orderProducts to save.
     * @param orderProducts the orderProducts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderProducts,
     * or with status {@code 400 (Bad Request)} if the orderProducts is not valid,
     * or with status {@code 404 (Not Found)} if the orderProducts is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderProducts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-products/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<OrderProducts> partialUpdateOrderProducts(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderProducts orderProducts
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderProducts partially : {}, {}", id, orderProducts);
        if (orderProducts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderProducts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderProductsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderProducts> result = orderProductsRepository
            .findById(orderProducts.getId())
            .map(
                existingOrderProducts -> {
                    if (orderProducts.getIdOrderProducts() != null) {
                        existingOrderProducts.setIdOrderProducts(orderProducts.getIdOrderProducts());
                    }
                    if (orderProducts.getIdOrder() != null) {
                        existingOrderProducts.setIdOrder(orderProducts.getIdOrder());
                    }
                    if (orderProducts.getIdProduct() != null) {
                        existingOrderProducts.setIdProduct(orderProducts.getIdProduct());
                    }

                    return existingOrderProducts;
                }
            )
            .map(orderProductsRepository::save)
            .map(
                savedOrderProducts -> {
                    orderProductsSearchRepository.save(savedOrderProducts);

                    return savedOrderProducts;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderProducts.getId().toString())
        );
    }

    /**
     * {@code GET  /order-products} : get all the orderProducts.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderProducts in body.
     */
    @GetMapping("/order-products")
    public List<OrderProducts> getAllOrderProducts(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all OrderProducts");
        return orderProductsRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /order-products/:id} : get the "id" orderProducts.
     *
     * @param id the id of the orderProducts to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderProducts, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-products/{id}")
    public ResponseEntity<OrderProducts> getOrderProducts(@PathVariable Long id) {
        log.debug("REST request to get OrderProducts : {}", id);
        Optional<OrderProducts> orderProducts = orderProductsRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(orderProducts);
    }

    /**
     * {@code DELETE  /order-products/:id} : delete the "id" orderProducts.
     *
     * @param id the id of the orderProducts to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-products/{id}")
    public ResponseEntity<Void> deleteOrderProducts(@PathVariable Long id) {
        log.debug("REST request to delete OrderProducts : {}", id);
        orderProductsRepository.deleteById(id);
        orderProductsSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/order-products?query=:query} : search for the orderProducts corresponding
     * to the query.
     *
     * @param query the query of the orderProducts search.
     * @return the result of the search.
     */
    @GetMapping("/_search/order-products")
    public List<OrderProducts> searchOrderProducts(@RequestParam String query) {
        log.debug("REST request to search OrderProducts for query {}", query);
        return StreamSupport
            .stream(orderProductsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
