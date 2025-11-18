package com.hs.lab3.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth_users")
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            length = 20)
    private String login;

    @Column(
            nullable = false,
            length = 100)
    private String password;

    @Column(
            nullable = false,
            length = 20)
    private String firstName;

    @Column(
            nullable = false,
            length = 20)
    private String lastName;

    @Column(
            unique = true,
            nullable = false,
            length = 20)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles;

}
