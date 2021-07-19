package com.dev.store.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.dev.store.domain.Users;
import com.dev.store.repository.UsersRepository;
import com.dev.store.repository.search.UsersSearchRepository;
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
 * REST controller for managing {@link com.dev.store.domain.Users}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UsersResource {

    private final Logger log = LoggerFactory.getLogger(UsersResource.class);

    private static final String ENTITY_NAME = "users";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UsersRepository usersRepository;

    private final UsersSearchRepository usersSearchRepository;

    public UsersResource(UsersRepository usersRepository, UsersSearchRepository usersSearchRepository) {
        this.usersRepository = usersRepository;
        this.usersSearchRepository = usersSearchRepository;
    }

    /**
     * {@code POST  /users} : Create a new users.
     *
     * @param users the users to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new users, or with status {@code 400 (Bad Request)} if the users has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/users")
    public ResponseEntity<Users> createUsers(@RequestBody Users users) throws URISyntaxException {
        log.debug("REST request to save Users : {}", users);
        if (users.getId() != null) {
            throw new BadRequestAlertException("A new users cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Users result = usersRepository.save(users);
        usersSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /users/:id} : Updates an existing users.
     *
     * @param id the id of the users to save.
     * @param users the users to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated users,
     * or with status {@code 400 (Bad Request)} if the users is not valid,
     * or with status {@code 500 (Internal Server Error)} if the users couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Users> updateUsers(@PathVariable(value = "id", required = false) final Long id, @RequestBody Users users)
        throws URISyntaxException {
        log.debug("REST request to update Users : {}, {}", id, users);
        if (users.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, users.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!usersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Users result = usersRepository.save(users);
        usersSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, users.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /users/:id} : Partial updates given fields of an existing users, field will ignore if it is null
     *
     * @param id the id of the users to save.
     * @param users the users to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated users,
     * or with status {@code 400 (Bad Request)} if the users is not valid,
     * or with status {@code 404 (Not Found)} if the users is not found,
     * or with status {@code 500 (Internal Server Error)} if the users couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/users/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Users> partialUpdateUsers(@PathVariable(value = "id", required = false) final Long id, @RequestBody Users users)
        throws URISyntaxException {
        log.debug("REST request to partial update Users partially : {}, {}", id, users);
        if (users.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, users.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!usersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Users> result = usersRepository
            .findById(users.getId())
            .map(
                existingUsers -> {
                    if (users.getIdUser() != null) {
                        existingUsers.setIdUser(users.getIdUser());
                    }
                    if (users.getName() != null) {
                        existingUsers.setName(users.getName());
                    }
                    if (users.getEmail() != null) {
                        existingUsers.setEmail(users.getEmail());
                    }
                    if (users.getPassword() != null) {
                        existingUsers.setPassword(users.getPassword());
                    }
                    if (users.getIdAdresses() != null) {
                        existingUsers.setIdAdresses(users.getIdAdresses());
                    }
                    if (users.getIdCards() != null) {
                        existingUsers.setIdCards(users.getIdCards());
                    }
                    if (users.getIdOrders() != null) {
                        existingUsers.setIdOrders(users.getIdOrders());
                    }
                    if (users.getIdWishlist() != null) {
                        existingUsers.setIdWishlist(users.getIdWishlist());
                    }

                    return existingUsers;
                }
            )
            .map(usersRepository::save)
            .map(
                savedUsers -> {
                    usersSearchRepository.save(savedUsers);

                    return savedUsers;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, users.getId().toString())
        );
    }

    /**
     * {@code GET  /users} : get all the users.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of users in body.
     */
    @GetMapping("/users")
    public List<Users> getAllUsers() {
        log.debug("REST request to get all Users");
        return usersRepository.findAll();
    }

    /**
     * {@code GET  /users/:id} : get the "id" users.
     *
     * @param id the id of the users to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the users, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Users> getUsers(@PathVariable Long id) {
        log.debug("REST request to get Users : {}", id);
        Optional<Users> users = usersRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(users);
    }

    /**
     * {@code DELETE  /users/:id} : delete the "id" users.
     *
     * @param id the id of the users to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUsers(@PathVariable Long id) {
        log.debug("REST request to delete Users : {}", id);
        usersRepository.deleteById(id);
        usersSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/users?query=:query} : search for the users corresponding
     * to the query.
     *
     * @param query the query of the users search.
     * @return the result of the search.
     */
    @GetMapping("/_search/users")
    public List<Users> searchUsers(@RequestParam String query) {
        log.debug("REST request to search Users for query {}", query);
        return StreamSupport
            .stream(usersSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
