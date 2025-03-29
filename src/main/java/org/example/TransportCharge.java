package org.example;

public class TransportCharge {

    private int transportID;
    private int customerID;
    private String transportMethod;
    private int transportCharge;

    public TransportCharge(int transportID, int customerID, String transportMethod, int transportCharge){
        this.transportID = transportID;
        this.customerID = customerID;
        this.transportMethod = transportMethod;
        this.transportCharge = transportCharge;
    }

    public int getCustomerID(){
        return customerID;
    }
    public int getTransportCharge(){
        return transportCharge;
    }



}
