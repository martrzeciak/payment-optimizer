package com.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a customer order in the system.
 * <p>
 * Contains order details including unique identifier, monetary value,
 * and applicable payment promotions.
 */
public class Order {
    private String id;
    private BigDecimal value;
    private List<String> promotions;

    /**
     * Default constructor initializes promotions list as empty ArrayList.
     */
    public Order() {
        promotions = new ArrayList<>();
    }

    /**
     * Parameterized constructor for order creation.
     *
     * @param id         Unique identifier for the order
     * @param value      Total value of the order (must have exactly 2 decimal places)
     * @param promotions List of applicable promotion IDs (null-safe)
     */
    public Order(String id, BigDecimal value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions != null ? promotions : new ArrayList<>();
    }

    // Accessor methods with JavaDoc comments

    /**
     * @return The unique identifier of the order
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the order identifier.
     *
     * @param id New unique identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The monetary value of the order with 2 decimal precision
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the order value.
     *
     * @param value New monetary value (must match system currency precision)
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the list of applicable promotion IDs.
     * <p>
     * The returned list is modifiable - changes affect the order's promotions.
     *
     * @return List of promotion IDs (never null)
     */
    public List<String> getPromotions() {
        return promotions;
    }

    /**
     * Replaces the current promotions with a new list.
     *
     * @param promotions New list of promotion IDs (null resets to empty list)
     */
    public void setPromotions(List<String> promotions) {
        this.promotions = promotions != null ? promotions : new ArrayList<>();
    }

    // Object identity methods

    /**
     * Returns a string representation of the order.
     * Format: "Order{id='...', value=..., promotions=[...]}"
     */
    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", promotions=" + promotions +
                '}';
    }

    /**
     * Compares orders for equality based on:
     * - Same ID
     * - Numerically equivalent value (ignoring scale)
     * - Same promotions in same order
     *
     * @param o Object to compare
     * @return true if orders are functionally equivalent
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id) &&
                (value == null ? order.value == null : value.compareTo(order.value) == 0) &&
                Objects.equals(promotions, order.promotions);
    }

    /**
     * Generates hash code based on:
     * - ID
     * - Value (normalized to strip trailing zeros)
     * - Promotions list
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                value != null ? value.stripTrailingZeros() : null,
                promotions
        );
    }
}
