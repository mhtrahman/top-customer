package com.version1.topcustomer.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {

	private int id;
	private String name;
	private String surname;
}
