package com.dcplatform.api.leads.model;

import com.dcplatform.api.leads.Source;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "leads",
		indexes = {
				@Index(name = "leads_email_uk", columnList = "email", unique = true),
				@Index(name = "leads_created_at_idx", columnList = "created_at DESC")
		}
)
@SQLRestriction("deleted_at IS NULL")
@Entity
public class LeadEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	private String email;
	@Column(name = "company_name")
	private String companyName;
	private String role;
	@Enumerated(EnumType.STRING)
	private Source source;
	@Column(name = "consent_at")
	private LocalDateTime consentAt;
	@Column(name = "consent_ip", columnDefinition = "inet")
	@JdbcTypeCode(SqlTypes.INET)
	private String consentIp;
	@Column(name = "privacy_policy_version")
	private String privacyPolicyVersion;
	@Column(name = "created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;
	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public LeadEntity() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public LocalDateTime getConsentAt() {
		return consentAt;
	}

	public void setConsentAt(LocalDateTime consentAt) {
		this.consentAt = consentAt;
	}

	public String getConsentIp() {
		return consentIp;
	}

	public void setConsentIp(String consentIp) {
		this.consentIp = consentIp;
	}

	public String getPrivacyPolicyVersion() {
		return privacyPolicyVersion;
	}

	public void setPrivacyPolicyVersion(String privacyPolicyVersion) {
		this.privacyPolicyVersion = privacyPolicyVersion;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}
}
