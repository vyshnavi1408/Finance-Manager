package com.project.financemanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;

    @Column(unique = true)
    private String email; //no duplicates of email should be there.
    private String password;
    private String profileImageUrl;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;//timestaps shouldn't be updated
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private String activationToken;

    @PrePersist
    public void prePersist(){
        if(this.isActive == null){
            isActive = false;

        }
    }



}
