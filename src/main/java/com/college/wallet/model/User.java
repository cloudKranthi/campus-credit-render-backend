package com.college.wallet.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

   @NotBlank(message="Enter username")
   @Column(unique=true,nullable=false)
    private String  username;
@NotBlank(message="Enter email")
@Column(unique=true,nullable=false)
@Email(message="Enter valid fromat email")
    private  String email;
    @Column(unique=true,nullable = false)
    private String phoneNumber;
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message="Enter password")
    @Column(nullable=false)
    private  String password;
    @OneToOne(mappedBy="user")
    private Purse purse;
    private String refreshToken;
    @Enumerated(EnumType.STRING)
   private Role role=Role.USER;

}

                                                          

