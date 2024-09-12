package com.version1.topcustomer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
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
    ;

    private CustomerService customerService;

    HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);
        customerService = new CustomerService(httpClient);
    }

    @Test
    @DisplayName("Should return single top customer when there is only one")
    void testGetTopCustomer() throws Exception {

        var invoiceResultJson = """             
                {
                	"invoices": [
                	{ "ID": 0, "customerId": 0, "amount": 1234 },
                	{ "ID": 1, "customerId": 1, "amount": 20 }
                	]
                }
                """;

        var customerResultJson = """
                {
                	"customers": [
                		{ "ID": 0, "name": "Matt", "surname": "LeBlanc" }, { "ID": 1, "name": "Bob", "surname": "Smith" }
                	]
                }
                """;

        HttpResponse<String> invoiceResponse = mock(HttpResponse.class);
        HttpResponse<String> customerResponse = mock(HttpResponse.class);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(customerResponse)
                .thenReturn(invoiceResponse);

        when(invoiceResponse.body()).thenReturn(invoiceResultJson);
        when(customerResponse.body()).thenReturn(customerResultJson);

        String result = "The customer(s) who spent the most is Matt LeBlanc with a total of 1234.0 spent.";

        String topSpendingCustomerResult = customerService.getTopSpendingCustomer();

        assertEquals(result, topSpendingCustomerResult);
    }

    @Test
    @DisplayName("Should return 2 top customers when there are 2 of them")
    void testGetTopCustomerCouple() throws Exception {

        var invoiceResultJson = """             
                {
                	"invoices": [
                	{ "ID": 0, "customerId": 0, "amount": 1234 },
                	{ "ID": 1, "customerId": 1, "amount": 1234 }
                	]
                }
                """;

        var customerResultJson = """
                {
                	"customers": [
                		{ "ID": 0, "name": "Matt", "surname": "LeBlanc" }, { "ID": 1, "name": "Bob", "surname": "Smith" }
                	]
                }
                """;

        HttpResponse<String> invoiceResponse = mock(HttpResponse.class);
        HttpResponse<String> customerResponse = mock(HttpResponse.class);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(customerResponse)
                .thenReturn(invoiceResponse);

        when(invoiceResponse.body()).thenReturn(invoiceResultJson);
        when(customerResponse.body()).thenReturn(customerResultJson);

        String result = "The customer(s) who spent the most are Matt LeBlanc and Bob Smith with a total of 1234.0 spent.";

        String topSpendingCustomerResult = customerService.getTopSpendingCustomer();

        assertEquals(result, topSpendingCustomerResult);
    }

    @Test
    @DisplayName("Should return multiple top customers when there are multiple")
    void testGetTopCustomerMultiple() throws Exception {

        var invoiceResultJson = """             
                {
                	"invoices": [
                	{ "ID": 0, "customerId": 0, "amount": 1234 },
                	{ "ID": 1, "customerId": 1, "amount": 1234 },
                	{ "ID": 2, "customerId": 2, "amount": 1234 }
                	]
                }
                """;

        var customerResultJson = """
                {
                	"customers": [
                		{ "ID": 0, "name": "Matt", "surname": "LeBlanc" }, { "ID": 1, "name": "Bob", "surname": "Smith" }, { "ID": 2, "name": "Jason", "surname": "Bourne" }
                	]
                }
                """;

        HttpResponse<String> invoiceResponse = mock(HttpResponse.class);
        HttpResponse<String> customerResponse = mock(HttpResponse.class);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(customerResponse)
                .thenReturn(invoiceResponse);

        when(invoiceResponse.body()).thenReturn(invoiceResultJson);
        when(customerResponse.body()).thenReturn(customerResultJson);

        String result = "The customer(s) who spent the most are Matt LeBlanc, Bob Smith and Jason Bourne with a total of 1234.0 spent.";

        String topSpendingCustomerResult = customerService.getTopSpendingCustomer();

        assertEquals(result, topSpendingCustomerResult);
    }

    @Test
    @DisplayName("Should throw exception if any service is down")
    void testGetTopCustomerInvoiceServiceDown() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException());
        try {
            customerService.getTopSpendingCustomer();
        } catch (Exception e) {
            assertEquals("Failed to send GET request to http://localhost:9090", e.getMessage());
        }
    }
}