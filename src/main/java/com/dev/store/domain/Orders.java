package com.dev.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Orders.
 */
@Entity
@Table(name = "orders")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_order")
    private Integer idOrder;

    @Column(name = "id_order_products")
    private Integer idOrderProducts;

    @Column(name = "total")
    private Float total;

    @ManyToOne
    @JsonIgnoreProperties(value = { "addresses", "cards", "orders", "wishlists" }, allowSetters = true)
    private Users idOrders;

    @ManyToMany(mappedBy = "orders")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "products" }, allowSetters = true)
    private Set<OrderProducts> idOrderProducts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Orders id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getIdOrder() {
        return this.idOrder;
    }

    public Orders idOrder(Integer idOrder) {
        this.idOrder = idOrder;
        return this;
    }

    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    public Integer getIdOrderProducts() {
        return this.idOrderProducts;
    }

    public Orders idOrderProducts(Integer idOrderProducts) {
        this.idOrderProducts = idOrderProducts;
        return this;
    }

    public void setIdOrderProducts(Integer idOrderProducts) {
        this.idOrderProducts = idOrderProducts;
    }

    public Float getTotal() {
        return this.total;
    }

    public Orders total(Float total) {
        this.total = total;
        return this;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Users getIdOrders() {
        return this.idOrders;
    }

    public Orders idOrders(Users users) {
        this.setIdOrders(users);
        return this;
    }

    public void setIdOrders(Users users) {
        this.idOrders = users;
    }

    public Set<OrderProducts> getIdOrderProducts() {
        return this.idOrderProducts;
    }

    public Orders idOrderProducts(Set<OrderProducts> orderProducts) {
        this.setIdOrderProducts(orderProducts);
        return this;
    }

    public Orders addIdOrderProducts(OrderProducts orderProducts) {
        this.idOrderProducts.add(orderProducts);
        orderProducts.getOrders().add(this);
        return this;
    }

    public Orders removeIdOrderProducts(OrderProducts orderProducts) {
        this.idOrderProducts.remove(orderProducts);
        orderProducts.getOrders().remove(this);
        return this;
    }

    public void setIdOrderProducts(Set<OrderProducts> orderProducts) {
        if (this.idOrderProducts != null) {
            this.idOrderProducts.forEach(i -> i.removeOrders(this));
        }
        if (orderProducts != null) {
            orderProducts.forEach(i -> i.addOrders(this));
        }
        this.idOrderProducts = orderProducts;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Orders)) {
            return false;
        }
        return id != null && id.equals(((Orders) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Orders{" +
            "id=" + getId() +
            ", idOrder=" + getIdOrder() +
            ", idOrderProducts=" + getIdOrderProducts() +
            ", total=" + getTotal() +
            "}";
    }
}
