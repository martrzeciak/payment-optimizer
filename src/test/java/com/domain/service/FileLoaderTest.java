package com.domain.service;

import com.domain.model.Order;
import com.domain.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileLoaderTest {
    private FileLoader loader;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loader = new FileLoader();
    }

    @Test
    void loadOrders_ValidOrdersJsonFile_ReturnsCorrectlyParsedOrderList() throws IOException {
        String ordersJson = """
                [
                    {
                        "id": "ORDER1",
                        "value": "100.00",
                        "promotions": ["mZysk"]
                    },
                    {
                        "id": "ORDER2",
                        "value": "200.00",
                        "promotions": ["BosBankrut"]
                    }
                ]
                """;

        Path orderFile = tempDir.resolve("orders.json");
        Files.writeString(orderFile, ordersJson);

        List<Order> orders = loader.loadOrders(orderFile.toString());

        assertEquals(2, orders.size());
        assertEquals("ORDER1", orders.getFirst().getId());
        assertEquals(new BigDecimal("100.00"), orders.getFirst().getValue());
        assertEquals(1, orders.getFirst().getPromotions().size());
        assertEquals("mZysk", orders.getFirst().getPromotions().getFirst());
    }

    @Test
    void loadPaymentMethods_WithSampleData_ReturnsExpectedPaymentMethodObjects() throws IOException {
        String methodsJson = """
                [
                    {
                        "id": "PUNKTY",
                        "discount": "15",
                        "limit": "100.00"
                    },
                    {
                        "id": "mZysk",
                        "discount": "10",
                        "limit": "180.00"
                    }
                ]
                """;

        Path methodsFile = tempDir.resolve("methods.json");
        Files.writeString(methodsFile, methodsJson);

        List<PaymentMethod> methods = loader.loadPaymentMethods(methodsFile.toString());

        assertEquals(2, methods.size());
        assertEquals("PUNKTY", methods.getFirst().getId());
        assertEquals(15, methods.getFirst().getDiscount());
        assertEquals(new BigDecimal("100.00"), methods.getFirst().getLimit());
    }
}
