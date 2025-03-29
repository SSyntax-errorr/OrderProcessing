package org.example;

public class Payment {
    private String paymentMethod;  // cash, cheque, or credit card
    private String cardToken;      // Placeholder for card token (never store real card number)
    private boolean chequeReturned;

    // Constructor for payment method
    public Payment(String method, String token) {
        this.paymentMethod = method;
        this.cardToken = token;
        this.chequeReturned = false; // Default for cheque payment
    }

    // Simulate cheque return
    public void setChequeReturned(boolean status) {
        this.chequeReturned = status;
    }

    // Placeholder methods to simulate real-world payment handling
    public void processPayment() {
        if (paymentMethod.equals("credit card") && !chequeReturned) {
            // Call third-party payment gateway API (not implemented here)
            System.out.println("Processing credit card payment...");
        } else {
            System.out.println("Processing payment via: " + paymentMethod);
        }
    }
}

