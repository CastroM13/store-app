package com.dev.store.repository;

import com.dev.store.domain.OrderProducts;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the OrderProducts entity.
 */
@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    @Query(
        value = "select distinct orderProducts from OrderProducts orderProducts left join fetch orderProducts.orders left join fetch orderProducts.products",
        countQuery = "select count(distinct orderProducts) from OrderProducts orderProducts"
    )
    Page<OrderProducts> findAllWithEagerRelationships(Pageable pageable);

    @Query(
        "select distinct orderProducts from OrderProducts orderProducts left join fetch orderProducts.orders left join fetch orderProducts.products"
    )
    List<OrderProducts> findAllWithEagerRelationships();

    @Query(
        "select orderProducts from OrderProducts orderProducts left join fetch orderProducts.orders left join fetch orderProducts.products where orderProducts.id =:id"
    )
    Optional<OrderProducts> findOneWithEagerRelationships(@Param("id") Long id);
}
