package com.version1.topcustomer.services;


import exceptions.ApiException;
import com.version1.topcustomer.models.Customer;
import com.version1.topcustomer.models.Invoice;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerService {

    private static final String CUSTOMER_SERVICE_URL = "http://localhost:9090";
    private static final String INVOICE_SERVICE_URL = "http://localhost:9092";

    private final HttpClient httpClient;

    public String getTopSpendingCustomer() {
        List<Customer> customers = getCustomers();
        List<Invoice> invoices = getInvoices();

        Map<Integer, Double> customerSpending = calculateTotalSpending(customers, invoices);

        var topCustomers = findTopSpendingCustomers(customers, customerSpending);

            StringBuilder result = new StringBuilder("The customer(s) who spent the most ");
            result.append(topCustomers.size()>1?"are ":"is ");
            for (Customer customer : topCustomers) {
                if(topCustomers.size()>1) {
                    if (topCustomers.indexOf(customer) == topCustomers.size() - 1) {
                        result.append(" and ");
                    } else if (topCustomers.indexOf(customer) != 0) {
                        result.append(", ");
                    }
                }
                result.append(customer.getName())
                        .append(" ")
                        .append(customer.getSurname())
                ;
            }
            result.append(" with a total of ")
                    .append(customerSpending.get(topCustomers.getFirst().getId()))
                    .append(" spent.");
            return result.toString();
    }

    private List<Customer> getCustomers() {
        String response = sendGetRequest(CUSTOMER_SERVICE_URL);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray customersArray = jsonResponse.getJSONArray("customers");

        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < customersArray.length(); i++) {
            JSONObject customerObj = customersArray.getJSONObject(i);
            Customer customer = new Customer(customerObj.getInt("ID"), customerObj.getString("name"),
                    customerObj.getString("surname"));
            customers.add(customer);
        }
        return customers;
    }

    private List<Invoice> getInvoices() {
        String response = sendGetRequest(INVOICE_SERVICE_URL);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray invoicesArray = jsonResponse.getJSONArray("invoices");

        List<Invoice> invoices = new ArrayList<>();
        for (int i = 0; i < invoicesArray.length(); i++) {
            JSONObject invoiceObj = invoicesArray.getJSONObject(i);
            Invoice invoice = new Invoice(invoiceObj.getInt("ID"), invoiceObj.getInt("customerId"),
                    invoiceObj.getInt("amount"));
            invoices.add(invoice);
        }
        return invoices;
    }

    private String sendGetRequest(final String urlStr) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ApiException("Failed to send GET request to " + urlStr);
        }
    }

    private Map<Integer, Double> calculateTotalSpending(final List<Customer> customers, final List<Invoice> invoices) {
        Map<Integer, Double> customerSpending = new HashMap<>();

        for (Customer customer : customers) {
            customerSpending.put(customer.getId(), 0.0);
        }

        for (Invoice invoice : invoices) {
            customerSpending.compute(invoice.getCustomerId(), (k, currentSpending) -> currentSpending + invoice.getAmount());
        }

        return customerSpending;
    }

    private List<Customer> findTopSpendingCustomers(List<Customer> customers, Map<Integer, Double> customerSpending) {
        double maxSpending = 0;
        var topCustomers = new ArrayList<Customer>();

        for (Customer customer : customers) {
            double totalSpent = customerSpending.get(customer.getId());
            if (totalSpent >= maxSpending) {
                maxSpending = totalSpent;
                topCustomers.add(customer);
            }
        }

        return topCustomers;
    }
}
