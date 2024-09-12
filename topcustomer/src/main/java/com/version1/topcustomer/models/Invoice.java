package com.version1.topcustomer.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Invoice {

	private int id;
	private int customerId;
	private double amount;
}
