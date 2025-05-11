package com.domain.service;

import com.domain.model.Order;
import com.domain.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentOptimizerTest {
    private PaymentOptimizer optimizer;

    @BeforeEach
    public void setUp() {
        optimizer = new PaymentOptimizer();
    }

    @Test
    public void optimizePayments_ExampleScenarioFromRequirements_ProducesExpectedMethodUsage() {
        // Tworzenie zamówień
        Order order1 = new Order("ORDER1", new BigDecimal("100.00"), Collections.singletonList("mZysk"));
        Order order2 = new Order("ORDER2", new BigDecimal("200.00"), Collections.singletonList("BosBankrut"));
        Order order3 = new Order("ORDER3", new BigDecimal("150.00"), Arrays.asList("mZysk", "BosBankrut"));
        Order order4 = new Order("ORDER4", new BigDecimal("50.00"), Collections.emptyList());
        List<Order> orders = Arrays.asList(order1, order2, order3, order4);

        // Tworzenie metod płatności
        PaymentMethod points = new PaymentMethod("PUNKTY", 15, new BigDecimal("100.00"));
        PaymentMethod mZysk = new PaymentMethod("mZysk", 10, new BigDecimal("180.00"));
        PaymentMethod bosBankrut = new PaymentMethod("BosBankrut", 5, new BigDecimal("200.00"));
        List<PaymentMethod> methods = Arrays.asList(points, mZysk, bosBankrut);

        // Optymalizacja płatności
        optimizer.optimizePayments(orders, methods);

        // Sprawdzenie wyników
        assertEquals(new BigDecimal("100.00"), points.getUsed());
        assertEquals(new BigDecimal("165.00"), mZysk.getUsed());
        assertEquals(new BigDecimal("190.00"), bosBankrut.getUsed());
    }
}
