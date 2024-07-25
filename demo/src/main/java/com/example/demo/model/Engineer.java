package com.example.demo.model;

import java.io.Serializable;

public class Engineer implements Serializable {

	private Long engineerId;
	private String name;
	
	public Engineer() {
	}

	public Engineer(Long engineerId, String name) {
		this.engineerId = engineerId;
		this.name = name;
	}

	public Long getEngineerId() {
		return engineerId;
	}

	public void setEngineerId(Long engineerId) {
		this.engineerId = engineerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
