package com.domain;

import com.domain.model.Order;
import com.domain.model.PaymentMethod;
import com.domain.service.FileLoader;
import com.domain.service.PaymentOptimizer;
import com.domain.service.ResultPrinter;

import java.io.IOException;
import java.util.List;

/**
 * Main entry point for the payment optimization application.
 * <p>
 * Handles command-line arguments, data loading, payment processing,
 * and result output according to the business requirements.
 */
public class Main {

    /**
     * Executes the payment optimization workflow.
     *
     * @param args Command-line arguments:
     *             [0] Absolute path to orders JSON file
     *             [1] Absolute path to payment methods JSON file
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length < 2) {
            System.err.println("Usage: java -jar app.jar <orders.json> <paymentmethods.json>");
            System.exit(1);
        }

        String ordersPath = args[0];
        String paymentMethodsPath = args[1];

        try {
            // Phase 1: Data loading
            FileLoader fileLoader = new FileLoader();
            List<Order> orders = fileLoader.loadOrders(ordersPath);
            List<PaymentMethod> paymentMethods = fileLoader.loadPaymentMethods(paymentMethodsPath);

            // Phase 2: Payment optimization
            PaymentOptimizer optimizer = new PaymentOptimizer();
            optimizer.optimizePayments(orders, paymentMethods);

            // Phase 3: Result output
            ResultPrinter printer = new ResultPrinter();
            printer.printResult(paymentMethods);

        } catch (IOException e) {
            System.err.println("File loading error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Processing error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
