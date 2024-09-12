package com.version1.topcustomer.controllers;

import com.version1.topcustomer.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		String result = """
				The customer who spent the most is Matt LeBlanc with a total of 1234 spent.
				""";

		when(customerService.getTopSpendingCustomer()).thenReturn(result);
	}

	@Test
	@DisplayName("Should return top customer")
	void testGetTopCustomer() throws Exception {
		String result = """
				The customer who spent the most is Matt LeBlanc with a total of 1234 spent.
				""";

		mockMvc.perform(get("/api/v1/customers/topCustomer")).andExpect(status().isOk())
			   .andExpect(content().string(result));
	}
}