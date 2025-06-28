package com.alpengotter.dodo_project.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "surname")
    private String surname;
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private Set<UserClinicMapEntity> userClinicMap;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "lemons")
    private Integer lemons;
    @Column(name = "diamonds")
    private Integer diamonds;
    @Column(name = "user_role")
    private String userRole;
    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "is_active")
    private Boolean isActive;

}
