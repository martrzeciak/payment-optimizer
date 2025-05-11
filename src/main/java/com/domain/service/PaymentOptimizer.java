package com.domain.service;

import com.domain.model.Order;
import com.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Optimizes payment distribution across orders to maximize discounts while
 * adhering to business rules and payment method constraints.
 * <p>
 * Implements a three-phase approach based on requirements from the 2025-Promocje-dla-metod-platnosci-v2.pdf:
 * 1. Promotional card assignments (Rules 1-2)
 * 2. Full points payments (Rule 4)
 * 3. Mixed payments and fallback (Rule 3)
 */
public class PaymentOptimizer {
    // Constants for business rules from PDF
    private static final String POINTS_ID = "PUNKTY";
    private static final BigDecimal TEN_PERCENT = new BigDecimal("0.10");
    private static final BigDecimal NINETY_PERCENT = new BigDecimal("0.90");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * Main optimization entry point
     * @param orders List of orders to process
     * @param paymentMethods Available payment methods with limits/discounts
     */
    public void optimizePayments(List<Order> orders, List<PaymentMethod> paymentMethods) {
        validateInput(orders, paymentMethods);
        Map<String, PaymentMethod> methodsById = createMethodMap(paymentMethods);
        List<Order> unassignedOrders = new ArrayList<>(orders);

        processPromotionalPhase(unassignedOrders, methodsById);
        processPointsPhase(unassignedOrders, methodsById.get(POINTS_ID));
        processRemainingOrders(unassignedOrders, methodsById);
    }

    /**
     * Validates input parameters
     * @throws IllegalArgumentException if null inputs provided
     */
    private void validateInput(List<Order> orders, List<PaymentMethod> paymentMethods) {
        if (orders == null || paymentMethods == null) {
            throw new IllegalArgumentException("Input collections cannot be null");
        }
    }

    /**
     * Creates lookup map for payment methods
     * @return Map of methods keyed by their IDs
     */
    private Map<String, PaymentMethod> createMethodMap(List<PaymentMethod> paymentMethods) {
        return paymentMethods.stream()
                .collect(Collectors.toMap(PaymentMethod::getId, m -> m));
    }

    // Region: Promotional Phase (Rules 1-2 from PDF)
    /**
     * Processes Phase 1: Assigns promotional cards to maximize discounts
     * Uses greedy algorithm - largest discount on largest order first
     */
    private void processPromotionalPhase(List<Order> orders, Map<String, PaymentMethod> methods) {
        Set<String> usedPromos = new HashSet<>();
        while (true) {
            PromoAssignment best = findBestPromoAssignment(orders, methods, usedPromos);
            if (best == null) break;

            usedPromos.add(best.method().getId());
            orders.remove(best.order());
            applyPromoPayment(best);
        }
    }

    /**
     * Finds best eligible promotional assignment
     * @return Best order/method pair or null if none available
     */
    private PromoAssignment findBestPromoAssignment(List<Order> orders,
                                                    Map<String, PaymentMethod> methods,
                                                    Set<String> usedPromos) {
        PromoAssignment best = null;
        BigDecimal bestDiscount = BigDecimal.ZERO;

        for (Order order : orders) {
            for (String promo : order.getPromotions()) {
                if (usedPromos.contains(promo)) continue;

                PaymentMethod method = methods.get(promo);
                if (!canAfford(method, order.getValue())) continue;

                BigDecimal discount = calculateDiscountAmount(order.getValue(), method);
                if (discount.compareTo(bestDiscount) > 0) {
                    bestDiscount = discount;
                    best = new PromoAssignment(order, method);
                }
            }
        }
        return best;
    }

    /**
     * Applies payment using promotional method
     */
    private void applyPromoPayment(PromoAssignment assignment) {
        BigDecimal toPay = orderAfterDiscount(
                assignment.order().getValue(),
                assignment.method().getDiscount()
        );
        assignment.method().use(toPay);
    }

    // Region: Points Phase (Rule 4 from PDF)
    /**
     * Processes Phase 2: Full points payments
     */
    private void processPointsPhase(List<Order> orders, PaymentMethod points) {
        if (points == null) return;
        orders.removeIf(order -> tryFullPointsPayment(order, points));
    }

    /**
     * Attempts full points payment for an order
     * @return true if payment succeeded
     */
    private boolean tryFullPointsPayment(Order order, PaymentMethod points) {
        if (!canAfford(points, order.getValue())) return false;

        BigDecimal toPay = orderAfterDiscount(order.getValue(), points.getDiscount());
        points.use(toPay);
        return true;
    }

    // Region: Remaining Orders (Rule 3 from PDF)
    /**
     * Processes Phase 3: Mixed payments and fallback
     */
    private void processRemainingOrders(List<Order> orders, Map<String, PaymentMethod> methods) {
        PaymentMethod points = methods.get(POINTS_ID);
        orders.forEach(order -> handleOrderPayment(order, points, methods));
    }

    /**
     * Handles payment for individual order in Phase 3
     */
    private void handleOrderPayment(Order order, PaymentMethod points, Map<String, PaymentMethod> methods) {
        if (tryMixedPayment(order, points, methods)) return;
        processFallbackPayment(order, methods);
    }

    /**
     * Attempts mixed payment (≥10% points + card)
     * @return true if mixed payment succeeded
     */
    private boolean tryMixedPayment(Order order, PaymentMethod points, Map<String, PaymentMethod> methods) {
        if (points == null || points.getAvailableLimit().compareTo(BigDecimal.ZERO) <= 0) return false;

        BigDecimal minPoints = calculateMinPointsRequirement(order.getValue());
        BigDecimal pointsToUse = calculatePointsToUse(points, order.getValue(), minPoints);

        if (pointsToUse.compareTo(minPoints) >= 0) {
            applyMixedPayment(order, points, pointsToUse, methods);
            return true;
        }
        return false;
    }

    /**
     * Calculates minimum points required for 10% discount (Rule 3)
     */
    private BigDecimal calculateMinPointsRequirement(BigDecimal orderValue) {
        return orderValue.multiply(TEN_PERCENT)
                .setScale(2, RoundingMode.CEILING);
    }

    /**
     * Calculates actual points to use (constrained by availability)
     */
    private BigDecimal calculatePointsToUse(PaymentMethod points, BigDecimal orderValue, BigDecimal minPoints) {
        return points.getAvailableLimit().min(orderValue).max(minPoints);
    }

    /**
     * Applies mixed payment with 10% discount
     */
    private void applyMixedPayment(Order order, PaymentMethod points,
                                   BigDecimal pointsToUse, Map<String, PaymentMethod> methods) {
        BigDecimal discountedTotal = order.getValue().multiply(NINETY_PERCENT)
                .setScale(2, RoundingMode.HALF_UP);

        points.use(pointsToUse);
        BigDecimal remaining = discountedTotal.subtract(pointsToUse);

        findBestPaymentMethod(remaining, methods)
                .ifPresent(method -> method.use(remaining));
    }

    /**
     * Fallback payment with any available card (no discount)
     */
    private void processFallbackPayment(Order order, Map<String, PaymentMethod> methods) {
        findBestPaymentMethod(order.getValue(), methods)
                .ifPresent(method -> method.use(order.getValue()));
    }

    // Region: Helper Methods
    /**
     * Finds best payment method for remaining amount
     * Prefers methods with highest discount (PDF: minimize card usage)
     */
    private Optional<PaymentMethod> findBestPaymentMethod(BigDecimal amount, Map<String, PaymentMethod> methods) {
        return methods.values().stream()
                .filter(m -> !POINTS_ID.equals(m.getId()))
                .filter(m -> canAfford(m, amount))
                .max(Comparator.comparingInt(PaymentMethod::getDiscount));
    }

    /**
     * Checks if method can cover the amount
     */
    private boolean canAfford(PaymentMethod method, BigDecimal amount) {
        return method != null &&
                method.getAvailableLimit().compareTo(amount) >= 0;
    }

    /**
     * Calculates order value after applying discount
     * Uses HALF_UP rounding for financial calculations
     */
    private BigDecimal orderAfterDiscount(BigDecimal value, int discountPct) {
        return value.multiply(
                        BigDecimal.ONE.subtract(
                                BigDecimal.valueOf(discountPct).divide(HUNDRED, 2, RoundingMode.HALF_UP)
                        )
                )
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates discount amount for comparison purposes
     */
    private BigDecimal calculateDiscountAmount(BigDecimal orderValue, PaymentMethod method) {
        return orderValue.multiply(
                BigDecimal.valueOf(method.getDiscount()).divide(HUNDRED, 2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Record for tracking promotional assignments
     */
    private record PromoAssignment(Order order, PaymentMethod method) {}
}
