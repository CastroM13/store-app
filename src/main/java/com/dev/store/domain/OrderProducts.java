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
 * A OrderProducts.
 */
@Entity
@Table(name = "order_products")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orderproducts")
public class OrderProducts implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_order_products")
    private Integer idOrderProducts;

    @Column(name = "id_order")
    private Integer idOrder;

    @Column(name = "id_product")
    private Integer idProduct;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_order_products__orders",
        joinColumns = @JoinColumn(name = "order_products_id"),
        inverseJoinColumns = @JoinColumn(name = "orders_id")
    )
    @JsonIgnoreProperties(value = { "idOrders", "idOrderProducts" }, allowSetters = true)
    private Set<Orders> orders = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_order_products__products",
        joinColumns = @JoinColumn(name = "order_products_id"),
        inverseJoinColumns = @JoinColumn(name = "products_id")
    )
    @JsonIgnoreProperties(value = { "idProducts" }, allowSetters = true)
    private Set<Products> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderProducts id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getIdOrderProducts() {
        return this.idOrderProducts;
    }

    public OrderProducts idOrderProducts(Integer idOrderProducts) {
        this.idOrderProducts = idOrderProducts;
        return this;
    }

    public void setIdOrderProducts(Integer idOrderProducts) {
        this.idOrderProducts = idOrderProducts;
    }

    public Integer getIdOrder() {
        return this.idOrder;
    }

    public OrderProducts idOrder(Integer idOrder) {
        this.idOrder = idOrder;
        return this;
    }

    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    public Integer getIdProduct() {
        return this.idProduct;
    }

    public OrderProducts idProduct(Integer idProduct) {
        this.idProduct = idProduct;
        return this;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
    }

    public Set<Orders> getOrders() {
        return this.orders;
    }

    public OrderProducts orders(Set<Orders> orders) {
        this.setOrders(orders);
        return this;
    }

    public OrderProducts addOrders(Orders orders) {
        this.orders.add(orders);
        orders.getIdOrderProducts().add(this);
        return this;
    }

    public OrderProducts removeOrders(Orders orders) {
        this.orders.remove(orders);
        orders.getIdOrderProducts().remove(this);
        return this;
    }

    public void setOrders(Set<Orders> orders) {
        this.orders = orders;
    }

    public Set<Products> getProducts() {
        return this.products;
    }

    public OrderProducts products(Set<Products> products) {
        this.setProducts(products);
        return this;
    }

    public OrderProducts addProducts(Products products) {
        this.products.add(products);
        products.getIdProducts().add(this);
        return this;
    }

    public OrderProducts removeProducts(Products products) {
        this.products.remove(products);
        products.getIdProducts().remove(this);
        return this;
    }

    public void setProducts(Set<Products> products) {
        this.products = products;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderProducts)) {
            return false;
        }
        return id != null && id.equals(((OrderProducts) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderProducts{" +
            "id=" + getId() +
            ", idOrderProducts=" + getIdOrderProducts() +
            ", idOrder=" + getIdOrder() +
            ", idProduct=" + getIdProduct() +
            "}";
    }
}
