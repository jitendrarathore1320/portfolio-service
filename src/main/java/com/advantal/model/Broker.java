package com.advantal.model;

import java.util.Date;

//ApiKeySecretEntity.java

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Broker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String broker;

	private String type;
	
	private Date creationDate;
	
	private Date updationDate;
	
	private Short status;
	
	private String logo;
	
//	private Long countryIdFk;

}
