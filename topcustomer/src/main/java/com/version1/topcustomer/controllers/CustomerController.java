package com.version1.topcustomer.controllers;

import com.version1.topcustomer.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@AllArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	@GetMapping("/topCustomer")
	public String getTopCustomer() throws Exception {
		return customerService.getTopSpendingCustomer();
	}
}
