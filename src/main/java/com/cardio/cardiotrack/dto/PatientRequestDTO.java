package com.cardio.cardiotrack.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public class PatientRequestDTO {
	@NotBlank(message= "Patient name is required")
	private String name;
	
	@Min(value=0, message="Age cannot be negative")
	private Integer age;
	
	private String gender;
	
	public PatientRequestDTO() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
		
	}
	
	public Integer getAge() {
		return age;
	}
	
	public void setAge(Integer age) {
		this.age=age;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender=gender;
	}
}
