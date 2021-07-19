package com.dev.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Cards.
 */
@Entity
@Table(name = "cards")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cards")
public class Cards implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cards")
    private Integer idCards;

    @Column(name = "identity_document")
    private String identityDocument;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "security_code")
    private Integer securityCode;

    @Column(name = "expiration_date")
    private String expirationDate;

    @Column(name = "cardholder")
    private String cardholder;

    @Column(name = "parcel_quantity")
    private Integer parcelQuantity;

    @ManyToOne
    @JsonIgnoreProperties(value = { "addresses", "cards", "orders", "wishlists" }, allowSetters = true)
    private Users idCards;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cards id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getIdCards() {
        return this.idCards;
    }

    public Cards idCards(Integer idCards) {
        this.idCards = idCards;
        return this;
    }

    public void setIdCards(Integer idCards) {
        this.idCards = idCards;
    }

    public String getIdentityDocument() {
        return this.identityDocument;
    }

    public Cards identityDocument(String identityDocument) {
        this.identityDocument = identityDocument;
        return this;
    }

    public void setIdentityDocument(String identityDocument) {
        this.identityDocument = identityDocument;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public Cards cardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getSecurityCode() {
        return this.securityCode;
    }

    public Cards securityCode(Integer securityCode) {
        this.securityCode = securityCode;
        return this;
    }

    public void setSecurityCode(Integer securityCode) {
        this.securityCode = securityCode;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public Cards expirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardholder() {
        return this.cardholder;
    }

    public Cards cardholder(String cardholder) {
        this.cardholder = cardholder;
        return this;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public Integer getParcelQuantity() {
        return this.parcelQuantity;
    }

    public Cards parcelQuantity(Integer parcelQuantity) {
        this.parcelQuantity = parcelQuantity;
        return this;
    }

    public void setParcelQuantity(Integer parcelQuantity) {
        this.parcelQuantity = parcelQuantity;
    }

    public Users getIdCards() {
        return this.idCards;
    }

    public Cards idCards(Users users) {
        this.setIdCards(users);
        return this;
    }

    public void setIdCards(Users users) {
        this.idCards = users;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cards)) {
            return false;
        }
        return id != null && id.equals(((Cards) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cards{" +
            "id=" + getId() +
            ", idCards=" + getIdCards() +
            ", identityDocument='" + getIdentityDocument() + "'" +
            ", cardNumber='" + getCardNumber() + "'" +
            ", securityCode=" + getSecurityCode() +
            ", expirationDate='" + getExpirationDate() + "'" +
            ", cardholder='" + getCardholder() + "'" +
            ", parcelQuantity=" + getParcelQuantity() +
            "}";
    }
}
