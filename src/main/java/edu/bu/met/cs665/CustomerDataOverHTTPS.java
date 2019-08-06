package edu.bu.met.cs665;

import java.io.IOException;

public interface CustomerDataOverHTTPS {

    void printCustomer(CustomerID id);
    void getCustomer_withHTTPSConnect(CustomerID id) throws IOException;
}
