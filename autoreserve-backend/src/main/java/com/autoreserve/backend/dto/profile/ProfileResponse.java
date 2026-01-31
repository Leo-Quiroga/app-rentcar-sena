package com.autoreserve.backend.dto.profile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProfileResponse {

    // User
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    // UserProfile
    private String address;
    private String city;
    private LocalDate birthDate;
    private String drivingLicense;

    public ProfileResponse(
            String firstName,
            String lastName,
            String email,
            String phone,
            String address,
            String city,
            LocalDate birthDate,
            String drivingLicense,
            LocalDateTime createdAt
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.birthDate = birthDate;
        this.drivingLicense = drivingLicense;
        this.createdAt = createdAt;
    }

    /* ================= GETTERS ================= */

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getDrivingLicense() { return drivingLicense; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
