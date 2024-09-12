package com.version1.topcustomer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	private static final String CUSTOMER_SERVICE_URL = "http://localhost:9090";
	private static final String INVOICE_SERVICE_URL = "http://localhost:9092";

	@Spy
	private HttpClient httpClient;

	private CustomerService customerService;

	@BeforeEach
	void setUp() throws Exception {
		customerService = new CustomerService();

		HttpRequest invoiceRequest = HttpRequest.newBuilder()
												.uri(URI.create(INVOICE_SERVICE_URL))
												.GET()
												.build();

		HttpRequest customerRequest = HttpRequest.newBuilder()
												 .uri(URI.create(CUSTOMER_SERVICE_URL))
												 .GET()
												 .build();
		var invoiceResultJson = """             
				{
					"invoices": [
					{ "ID": 0, "customerId": 0, "amount": 12 },
					{ "ID": 1, "customerId": 1, "amount": 20 }
					]
				}
				""";

		var customerResultJson = """
				{
					"customers": [
						{ "ID": 0, "name": "Alice", "surname": "Klark" }, { "ID": 1, "name": "Bob", "surname": "Smith" }
					]
				}
				""";

		HttpResponse<String> invoiceResponse = mock(HttpResponse.class);
		HttpResponse<String> customerResponse = mock(HttpResponse.class);

		when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.thenReturn(customerResponse)
				.thenReturn(invoiceResponse);

		when(invoiceResponse.body()).thenReturn(invoiceResultJson);
		when(invoiceResponse.body()).thenReturn(customerResultJson);
	}

	@Test
	@DisplayName("Should return top customer")
	void testGetTopCustomer() {
		String result = """
				The customer who spent the most is Matt LeBlanc with a total of 1234 spent.
				""";
		when(customerService.getTopSpendingCustomer()).thenReturn(result);

		String topSpendingCustomerResult = customerService.getTopSpendingCustomer();

		assertEquals(result, topSpendingCustomerResult);
	}
}