package com.cardio.cardiotrack.model;
import jakarta.persistence.Entity;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="patient")
public class Patient {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message="Patient name is required")
	private String name;
	
	@Min(value=0, message="Age cannot be negative")
	private Integer age;
	private String gender;
	
	@OneToOne
	@JoinColumn(name = "user_id", unique = true)
	private User user;
	
	public Long getId() {
	    return id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public Integer getAge() {
	    return age;
	}

	public void setAge(Integer age) {
	    this.age = age;
	}

	public String getGender() {
	    return gender;
	}

	public void setGender(String gender) {
	    this.gender = gender;
	}
	
	public User getUser() {
	    return user;
	}

	public void setUser(User user) {
	    this.user = user;
	}
	
}
