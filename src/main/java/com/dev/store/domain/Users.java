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
 * A Users.
 */
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "id_adresses")
    private Integer idAdresses;

    @Column(name = "id_cards")
    private Integer idCards;

    @Column(name = "id_orders")
    private Integer idOrders;

    @Column(name = "id_wishlist")
    private Integer idWishlist;

    @OneToMany(mappedBy = "idAdresses")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "idAdresses" }, allowSetters = true)
    private Set<Addresses> addresses = new HashSet<>();

    @OneToMany(mappedBy = "idCards")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "idCards" }, allowSetters = true)
    private Set<Cards> cards = new HashSet<>();

    @OneToMany(mappedBy = "idOrders")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "idOrders", "idOrderProducts" }, allowSetters = true)
    private Set<Orders> orders = new HashSet<>();

    @OneToMany(mappedBy = "idWishlist")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "idWishlist" }, allowSetters = true)
    private Set<Wishlist> wishlists = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getIdUser() {
        return this.idUser;
    }

    public Users idUser(Integer idUser) {
        this.idUser = idUser;
        return this;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return this.name;
    }

    public Users name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public Users email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public Users password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIdAdresses() {
        return this.idAdresses;
    }

    public Users idAdresses(Integer idAdresses) {
        this.idAdresses = idAdresses;
        return this;
    }

    public void setIdAdresses(Integer idAdresses) {
        this.idAdresses = idAdresses;
    }

    public Integer getIdCards() {
        return this.idCards;
    }

    public Users idCards(Integer idCards) {
        this.idCards = idCards;
        return this;
    }

    public void setIdCards(Integer idCards) {
        this.idCards = idCards;
    }

    public Integer getIdOrders() {
        return this.idOrders;
    }

    public Users idOrders(Integer idOrders) {
        this.idOrders = idOrders;
        return this;
    }

    public void setIdOrders(Integer idOrders) {
        this.idOrders = idOrders;
    }

    public Integer getIdWishlist() {
        return this.idWishlist;
    }

    public Users idWishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
        return this;
    }

    public void setIdWishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
    }

    public Set<Addresses> getAddresses() {
        return this.addresses;
    }

    public Users addresses(Set<Addresses> addresses) {
        this.setAddresses(addresses);
        return this;
    }

    public Users addAddresses(Addresses addresses) {
        this.addresses.add(addresses);
        addresses.setIdAdresses(this);
        return this;
    }

    public Users removeAddresses(Addresses addresses) {
        this.addresses.remove(addresses);
        addresses.setIdAdresses(null);
        return this;
    }

    public void setAddresses(Set<Addresses> addresses) {
        if (this.addresses != null) {
            this.addresses.forEach(i -> i.setIdAdresses(null));
        }
        if (addresses != null) {
            addresses.forEach(i -> i.setIdAdresses(this));
        }
        this.addresses = addresses;
    }

    public Set<Cards> getCards() {
        return this.cards;
    }

    public Users cards(Set<Cards> cards) {
        this.setCards(cards);
        return this;
    }

    public Users addCards(Cards cards) {
        this.cards.add(cards);
        cards.setIdCards(this);
        return this;
    }

    public Users removeCards(Cards cards) {
        this.cards.remove(cards);
        cards.setIdCards(null);
        return this;
    }

    public void setCards(Set<Cards> cards) {
        if (this.cards != null) {
            this.cards.forEach(i -> i.setIdCards(null));
        }
        if (cards != null) {
            cards.forEach(i -> i.setIdCards(this));
        }
        this.cards = cards;
    }

    public Set<Orders> getOrders() {
        return this.orders;
    }

    public Users orders(Set<Orders> orders) {
        this.setOrders(orders);
        return this;
    }

    public Users addOrders(Orders orders) {
        this.orders.add(orders);
        orders.setIdOrders(this);
        return this;
    }

    public Users removeOrders(Orders orders) {
        this.orders.remove(orders);
        orders.setIdOrders(null);
        return this;
    }

    public void setOrders(Set<Orders> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setIdOrders(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setIdOrders(this));
        }
        this.orders = orders;
    }

    public Set<Wishlist> getWishlists() {
        return this.wishlists;
    }

    public Users wishlists(Set<Wishlist> wishlists) {
        this.setWishlists(wishlists);
        return this;
    }

    public Users addWishlist(Wishlist wishlist) {
        this.wishlists.add(wishlist);
        wishlist.setIdWishlist(this);
        return this;
    }

    public Users removeWishlist(Wishlist wishlist) {
        this.wishlists.remove(wishlist);
        wishlist.setIdWishlist(null);
        return this;
    }

    public void setWishlists(Set<Wishlist> wishlists) {
        if (this.wishlists != null) {
            this.wishlists.forEach(i -> i.setIdWishlist(null));
        }
        if (wishlists != null) {
            wishlists.forEach(i -> i.setIdWishlist(this));
        }
        this.wishlists = wishlists;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Users)) {
            return false;
        }
        return id != null && id.equals(((Users) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Users{" +
            "id=" + getId() +
            ", idUser=" + getIdUser() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", idAdresses=" + getIdAdresses() +
            ", idCards=" + getIdCards() +
            ", idOrders=" + getIdOrders() +
            ", idWishlist=" + getIdWishlist() +
            "}";
    }
}
