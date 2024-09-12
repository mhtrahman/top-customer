package com.version1.topcustomer.services;


import com.version1.topcustomer.models.Customer;
import com.version1.topcustomer.models.Invoice;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

	private static final String CUSTOMER_SERVICE_URL = "http://localhost:9090";
	private static final String INVOICE_SERVICE_URL = "http://localhost:9092";

	private final HttpClient httpClient = HttpClient.newHttpClient();

	public String getTopSpendingCustomer() {
		List<Customer> customers = getCustomers();
		List<Invoice> invoices = getInvoices();

		Map<Integer, Double> customerSpending = calculateTotalSpending(customers, invoices);

		var topCustomers = findTopSpendingCustomers(customers, customerSpending);

		if (topCustomers.size() > 1) {
			StringBuilder result = new StringBuilder("The customers who spent the most are:\n");
			for (Customer customer : topCustomers) {
				result.append(customer.getName())
					  .append(" ")
					  .append(customer.getSurname());
			}
			result.append(" with a total of ")
				  .append(customerSpending.get(topCustomers.getFirst().getId()))
				  .append(" spent.\n");
			return result.toString();
		} else {
			var topCustomer = topCustomers.getFirst();
			return "The customer who spent the most is " + topCustomer.getName() + " " + topCustomer.getSurname() +
					" with a total of " + customerSpending.get(topCustomer.getId()) + " spent.";
		}
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
			throw new RuntimeException(e);
		}
	}

	private Map<Integer, Double> calculateTotalSpending(final List<Customer> customers, final List<Invoice> invoices) {
		Map<Integer, Double> customerSpending = new HashMap<>();

		for (Customer customer : customers) {
			customerSpending.put(customer.getId(), 0.0);
		}

		for (Invoice invoice : invoices) {
			double currentSpending = customerSpending.get(invoice.getCustomerId());
			customerSpending.put(invoice.getCustomerId(), currentSpending + invoice.getAmount());
		}

		return customerSpending;
	}

	private List<Customer> findTopSpendingCustomers(List<Customer> customers, Map<Integer, Double> customerSpending) {
		double maxSpending = 0;
		var topCustomers = new ArrayList<Customer>();

		for (Customer customer : customers) {
			double totalSpent = customerSpending.get(customer.getId());
			if (totalSpent > maxSpending) {
				maxSpending = totalSpent;
				topCustomers.add(customer);
			}
		}

		return topCustomers;
	}
}
