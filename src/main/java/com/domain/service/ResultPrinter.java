package com.domain.service;

import com.domain.model.PaymentMethod;

import java.util.List;

/**
 * Utility class for printing the summary of payment method usage.
 * <p>
 * Outputs, for each payment method, the total amount used if it is greater than zero.
 * The output format matches the requirements from the business specification:
 * <pre>
 * <payment_method_id> <used_amount>
 * </pre>
 * Each payment method appears on a separate line, and only methods with non-zero usage are shown.
 */
public class ResultPrinter {

    /**
     * Prints the total amount spent from each payment method to standard output.
     * Only methods with a positive used amount are printed.
     *
     * @param paymentMethods List of all payment methods (with usage already calculated)
     */
    public void printResult(List<PaymentMethod> paymentMethods) {
        paymentMethods.forEach(method -> {
            // Only print methods that have been used (used amount > 0)
            if (method.getUsed().compareTo(java.math.BigDecimal.ZERO) > 0) {
                // Output format: <payment_method_id> <used_amount>
                System.out.println(method.getId() + " " + method.getUsed());
            }
        });
    }
}
