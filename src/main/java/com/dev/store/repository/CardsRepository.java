package com.dev.store.repository;

import com.dev.store.domain.Cards;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Cards entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CardsRepository extends JpaRepository<Cards, Long> {}
