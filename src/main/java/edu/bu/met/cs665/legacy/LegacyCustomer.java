package edu.bu.met.cs665.legacy;

import edu.bu.met.cs665.CustomerData;
import edu.bu.met.cs665.CustomerID;
import edu.bu.met.cs665.rest.RESTCustomer;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LegacyCustomer implements CustomerData {
    private static Logger logger = Logger.getLogger(LegacyCustomer.class);
    private Set<CustomerID> customerIDSet;
    // We add a RESTCustomer component to utilize the rest customer class for now
    RESTCustomer restCustomer;

    public LegacyCustomer() {
        this.customerIDSet = new HashSet<>();
        restCustomer = new RESTCustomer();
    }

    public Set<CustomerID> getCustomerIDSet() {
        return customerIDSet;
    }

    public void setCustomerIDSet(Set<CustomerID> customerIDSet) {
        this.customerIDSet = customerIDSet;
    }

    public void addCustomer(CustomerID customerID) {
        customerIDSet.add(customerID);
    }

    public boolean removeCustomer(CustomerID customerID) {
        if (customerIDSet.contains(customerID)) {
            customerIDSet.remove(customerID);
            return true;
        }
        return false;
    }

    /**
     *
     * @param id
     * Prints the customer ID if it's in the database
     */
    @Override
    public void printCustomer(CustomerID id) {
        // If this customer exists in the database, print it
        // First try to set the global customer id
        getCustomer_withUSBConnect(id);
        // Now check if there is a customer, if it is not null, then we can print
        if (customerIDSet.contains(id)) {
            System.out.printf("Name: \n" +
                    "ID: \n" +
                    "Address: \n", id.getName(), id.getId(), id.getAddress());
        }
        logger.warn("There is no customer with that ID");
    }

    /**
     *
     * @param customerID is the customer id
     * This is a legacy method used to read from USB
     */
    @Override
    public void getCustomer_withUSBConnect(CustomerID customerID) {
        if (customerID != null) {
            readAndAddFromUSB();
            logger.info("Successfully read from USB.");
            if (customerIDSet.contains(customerID)) {
                logger.info("Found the customer! ");
            }
            return;
        }
        logger.warn("Customer ID is null because the customer was not found or an error occurred.");
    }

    /**
     * Reads a USB database file and initializes the customer set.
     */
    private void readAndAddFromUSB() {
        // Reading a database file
        try {
            // Read a database file from USB
            BufferedReader reader = new BufferedReader(new FileReader("CUSTOMER_DATABASE.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                // Example txt file
                // ID Name Address
                // So we read each line and split and, an try to match the id
                String[] customerSplit = line.split(" ");
                // Make a new customer object and add to the set
                CustomerID customerID = new CustomerID(customerSplit[1], Integer.parseInt(customerSplit[0]), customerSplit[2]);
                addCustomer(customerID);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // If set is 0, we had an error of some sort
        if (customerIDSet.size() == 0) {
            logger.warn("There was a problem reading the database file.");
        }
    }

    public void printCustomerUsingREST(CustomerID id) throws IOException {
        // Connect to the rest end pointer using the ID
        restCustomer.getCustomer_withHTTPSConnect(id);
        // If the customer set in rest customer isn't 0, and it contains the customer, print
        if (restCustomer.getSetSize() > 0 && restCustomer.containsCustomer(id)) {
            restCustomer.printCustomer(id);
            logger.info("Successfully used the REST service to print the customer ID");
            return;
        }
        logger.warn("Unable to use rest service.");
    }
}
