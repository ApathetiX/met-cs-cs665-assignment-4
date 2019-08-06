package edu.bu.met.cs665.rest;

import edu.bu.met.cs665.CustomerDataOverHTTPS;
import edu.bu.met.cs665.CustomerID;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class RESTCustomer implements CustomerDataOverHTTPS {
    private static Logger logger = Logger.getLogger(RESTCustomer.class);
    private static Set<CustomerID> customerIDSet;

    public RESTCustomer() {
        customerIDSet = new HashSet<>();
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

    public boolean containsCustomer(CustomerID id) {
        if (customerIDSet.contains(id)) {
            return true;
        }
        return false;
    }

    public int getSetSize() {
        return this.getCustomerIDSet().size();
    }

    @Override
    public void printCustomer(CustomerID id) {
        if (customerIDSet.contains(id)) {
            System.out.printf("Name: \n" +
                    "ID: \n" +
                    "Address: \n", id.getName(), id.getId(), id.getAddress());
        }
        logger.warn("There is no customer with that ID");
    }

    /**
     * Uses JSON data to init the customer set (DB)
     * @param id
     * @throws IOException
     */
    @Override
    public void getCustomer_withHTTPSConnect(CustomerID id) throws IOException {
        connectAndAddWithREST(id);
    }

    /**
     *
     * @param id is the customer id
     * Connects to an API end point and fills the customer set using provided JSON data
     * @throws IOException
     */
    private void connectAndAddWithREST(CustomerID id) throws IOException {
        // We will use the given the ID to build the URL to the rest service
        // Example URL: www.metcs.com/name/id to return a JSON object
        StringBuilder sb = new StringBuilder("www.metcs.com/");
        sb.append(id.getName()).append("/").append(id.getId());
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            Scanner sc = new Scanner(url.openStream());
            String line;
            while ((line = sc.nextLine()) != null) {
                // Example JSON file
                // ID Name Address
                // So we read each line and split and, an try to match the id
                String[] customerSplit = line.split(" ");
                // Make a new customer object and add to the set
                CustomerID customerID = new CustomerID(customerSplit[1], Integer.parseInt(customerSplit[0]), customerSplit[2]);
                addCustomer(customerID);
            }
            sc.close();
        }
    }
}
