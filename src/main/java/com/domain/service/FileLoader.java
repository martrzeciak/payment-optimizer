package com.domain.service;

import com.domain.model.Order;
import com.domain.model.PaymentMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Handles loading of order and payment method data from JSON files.
 * <p>
 * Provides type-safe deserialization of JSON data into Java objects using Jackson.
 * Configures the JSON parser to ignore unrecognized properties for forward compatibility.
 */
public class FileLoader {
    /**
     * Thread-safe ObjectMapper instance configured for the application's needs.
     * <p>
     * Configuration:
     * - Fails on unknown properties: false (ignores unrecognized JSON fields)
     */
    private static final ObjectMapper objectMapper = createObjectMapper();

    /**
     * Creates and configures the ObjectMapper instance.
     * @return Configured ObjectMapper with desired serialization/deserialization settings
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Ignore unrecognized JSON properties to maintain forward compatibility
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Loads orders from a JSON file.
     * @param filePath Absolute path to the orders JSON file
     * @return List of deserialized Order objects
     * @throws IOException If file cannot be read or contains invalid JSON structure
     */
    public List<Order> loadOrders(String filePath) throws IOException {
        return readListFromFile(filePath, new TypeReference<>() {});
    }

    /**
     * Loads payment methods from a JSON file.
     * @param filePath Absolute path to the payment methods JSON file
     * @return List of deserialized PaymentMethod objects
     * @throws IOException If file cannot be read or contains invalid JSON structure
     */
    public List<PaymentMethod> loadPaymentMethods(String filePath) throws IOException {
        return readListFromFile(filePath, new TypeReference<>() {});
    }

    /**
     * Generic JSON deserialization method for lists of objects.
     * @param <T> Type of objects to deserialize
     * @param filePath Absolute path to the JSON file
     * @param typeRef Type reference for the target collection type
     * @return List of deserialized objects
     * @throws IOException If file cannot be read or contains invalid JSON structure
     */
    private <T> List<T> readListFromFile(String filePath, TypeReference<List<T>> typeRef) throws IOException {
        return objectMapper.readValue(new File(filePath), typeRef);
    }
}
