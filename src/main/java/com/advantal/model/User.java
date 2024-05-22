package com.advantal.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "phoneNo")
	private String phoneNo;

	@Column(name = "deviceId")
	private String deviceId;

	@Column(name = "deviceToken")
	private String deviceToken;

	@Column(name = "deviceType")
	private String deviceType;

	@Column(name = "countryCode")
	private String countryCode;

	@CreationTimestamp
	@Column(name = "creationDate")
	private Date creationDate;

	@Column(name = "updationDate")
	private Date updationDate;

	@Column(name = "status")
	private Short status;

	private String name;

	private String imageUrl;

	private String email;

	@OneToMany(targetEntity = Transaction.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "userIdFk", referencedColumnName = "id")
	private List<Transaction> transactionList;
	
	@OneToMany(targetEntity = Portfolio.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "userIdFk", referencedColumnName = "id")
	private List<Portfolio> portfolioList;

}
