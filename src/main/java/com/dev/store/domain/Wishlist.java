package com.dev.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Wishlist.
 */
@Entity
@Table(name = "wishlist")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "wishlist")
public class Wishlist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_wishlist")
    private Integer idWishlist;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "id_products")
    private Integer idProducts;

    @ManyToOne
    @JsonIgnoreProperties(value = { "addresses", "cards", "orders", "wishlists" }, allowSetters = true)
    private Users idWishlist;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wishlist id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getIdWishlist() {
        return this.idWishlist;
    }

    public Wishlist idWishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
        return this;
    }

    public void setIdWishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
    }

    public Integer getIdUser() {
        return this.idUser;
    }

    public Wishlist idUser(Integer idUser) {
        this.idUser = idUser;
        return this;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdProducts() {
        return this.idProducts;
    }

    public Wishlist idProducts(Integer idProducts) {
        this.idProducts = idProducts;
        return this;
    }

    public void setIdProducts(Integer idProducts) {
        this.idProducts = idProducts;
    }

    public Users getIdWishlist() {
        return this.idWishlist;
    }

    public Wishlist idWishlist(Users users) {
        this.setIdWishlist(users);
        return this;
    }

    public void setIdWishlist(Users users) {
        this.idWishlist = users;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wishlist)) {
            return false;
        }
        return id != null && id.equals(((Wishlist) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Wishlist{" +
            "id=" + getId() +
            ", idWishlist=" + getIdWishlist() +
            ", idUser=" + getIdUser() +
            ", idProducts=" + getIdProducts() +
            "}";
    }
}
