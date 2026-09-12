package com.college.wallet.model;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
@MappedSuperclass
@Getter
@Setter
@Accessors(chain=true)
public  abstract  class BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Version
    private Long version;
    @CreationTimestamp
    @Column(updatable=false,nullable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
