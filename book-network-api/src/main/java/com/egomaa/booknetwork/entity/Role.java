package com.egomaa.booknetwork.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role{

    @Id
    @GeneratedValue
    private int id;
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore // Prevents infinite loop
    private List<User> users;

}
