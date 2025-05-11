package com.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a payment method in the system.
 * <p>
 * A payment method can be a loyalty points account or a traditional payment method
 * (such as a bank card). Each method has a unique ID, a percentage discount,
 * a spending limit, and tracks how much has been used.
 */
public class PaymentMethod {
    /** Unique identifier for the payment method (e.g., "PUNKTY" for points, or card name) */
    private String id;

    /** Discount percentage offered by this payment method (e.g., 10 for 10%) */
    private int discount;

    /** Maximum amount available to spend with this payment method */
    private BigDecimal limit;

    /** Amount already used from this method (initialized to zero) */
    private BigDecimal used = BigDecimal.ZERO;

    /**
     * Default constructor required for deserialization and frameworks.
     */
    public PaymentMethod() {
    }

    /**
     * Constructs a payment method with the given properties.
     * @param id unique identifier for the payment method
     * @param discount discount percentage (integer, e.g., 10 for 10%)
     * @param limit maximum available amount for this method
     */
    public PaymentMethod(String id, int discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    /** @return the unique identifier for this payment method */
    public String getId() {
        return id;
    }

    /** @param id sets the unique identifier for this payment method */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the discount percentage for this method */
    public int getDiscount() {
        return discount;
    }

    /** @param discount sets the discount percentage for this method */
    public void setDiscount(int discount) {
        this.discount = discount;
    }

    /** @return the maximum available limit for this method */
    public BigDecimal getLimit() {
        return limit;
    }

    /** @param limit sets the maximum available limit for this method */
    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    /** @return the amount already used from this method */
    public BigDecimal getUsed() {
        return used;
    }

    /** @param used sets the amount already used from this method */
    public void setUsed(BigDecimal used) {
        this.used = used;
    }

    /**
     * Calculates how much is still available to spend with this payment method.
     * @return available limit (limit minus used)
     */
    public BigDecimal getAvailableLimit() {
        return limit.subtract(used);
    }

    /**
     * Uses a given amount from this payment method. Increases the 'used' field.
     * Throws an exception if the amount exceeds the available limit.
     * @param amount the amount to use
     * @throws IllegalArgumentException if not enough funds are available
     */
    public void use(BigDecimal amount) {
        if (amount.compareTo(getAvailableLimit()) > 0) {
            throw new IllegalArgumentException("Insufficient funds for payment method: " + id);
        }
        used = used.add(amount);
    }

    /**
     * Returns a string representation of the payment method.
     */
    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id='" + id + '\'' +
                ", discount=" + discount +
                ", limit=" + limit +
                ", used=" + used +
                '}';
    }

    /**
     * Checks equality based on id, discount, limit, and used amount.
     * For BigDecimal fields, compares values numerically (ignores scale).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentMethod that)) return false;
        return discount == that.discount &&
                Objects.equals(id, that.id) &&
                (limit == null ? that.limit == null : limit.compareTo(that.limit) == 0) &&
                (used == null ? that.used == null : used.compareTo(that.used) == 0);
    }

    /**
     * Hash code based on id, discount, normalized limit, and used amount.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, discount,
                limit != null ? limit.stripTrailingZeros() : null,
                used != null ? used.stripTrailingZeros() : null);
    }
}
