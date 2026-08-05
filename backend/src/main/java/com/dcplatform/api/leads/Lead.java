package com.dcplatform.api.leads;

import java.time.LocalDateTime;
import java.util.UUID;

import org.aspectj.apache.bcel.generic.Type;
import org.hibernate.annotations.SQLRestriction;

import io.micrometer.core.annotation.Counted;
import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "leads" ,
indexes = {
        @Index(name = "leads_email_uk", columnList = "email", unique = true),
        @Index(name = "leads_created_at_idx", columnList = "created_at DESC")
    }
)
@SQLRestriction("deleted_at IS NULL")
@Entity
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String email;
    @Column(name = "company_name")
    private String companyName;
    private String role;
    @Enumerated(EnumType.STRING)
    private Source source;
    @Column(name = "consent_at")
    private LocalDateTime consentAt;
    @Column(name = "consent_ip" ,columnDefinition = "inet")
    private String consentIp;
    @Column(name = "privacy_policy_version")
    private String privacyPolicyVersion;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "  updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
