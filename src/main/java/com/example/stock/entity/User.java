package com.example.stock.entity;


import com.example.stock.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String surname;
    int age;
    String phone;
    @Column(unique = true, nullable = false)
    String email;
    String password;

    @Enumerated(EnumType.STRING)
    Role role;

    Double balance;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    Basket basket;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Favorite> favoriteList;
}
