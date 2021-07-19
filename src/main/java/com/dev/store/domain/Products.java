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
 * A Products.
 */
@Entity
@Table(name = "products")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "products")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_products")
    private Integer idProducts;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private Float value;

    @Column(name = "image")
    private String image;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "products")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "orders", "products" }, allowSetters = true)
    private Set<OrderProducts> idProducts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Products id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getIdProducts() {
        return this.idProducts;
    }

    public Products idProducts(Integer idProducts) {
        this.idProducts = idProducts;
        return this;
    }

    public void setIdProducts(Integer idProducts) {
        this.idProducts = idProducts;
    }

    public String getName() {
        return this.name;
    }

    public Products name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getValue() {
        return this.value;
    }

    public Products value(Float value) {
        this.value = value;
        return this;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getImage() {
        return this.image;
    }

    public Products image(String image) {
        this.image = image;
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return this.description;
    }

    public Products description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<OrderProducts> getIdProducts() {
        return this.idProducts;
    }

    public Products idProducts(Set<OrderProducts> orderProducts) {
        this.setIdProducts(orderProducts);
        return this;
    }

    public Products addIdProducts(OrderProducts orderProducts) {
        this.idProducts.add(orderProducts);
        orderProducts.getProducts().add(this);
        return this;
    }

    public Products removeIdProducts(OrderProducts orderProducts) {
        this.idProducts.remove(orderProducts);
        orderProducts.getProducts().remove(this);
        return this;
    }

    public void setIdProducts(Set<OrderProducts> orderProducts) {
        if (this.idProducts != null) {
            this.idProducts.forEach(i -> i.removeProducts(this));
        }
        if (orderProducts != null) {
            orderProducts.forEach(i -> i.addProducts(this));
        }
        this.idProducts = orderProducts;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", idProducts=" + getIdProducts() +
            ", name='" + getName() + "'" +
            ", value=" + getValue() +
            ", image='" + getImage() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
