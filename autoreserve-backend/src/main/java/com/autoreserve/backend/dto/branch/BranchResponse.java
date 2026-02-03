package com.autoreserve.backend.dto.branch;

public class BranchResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private Integer carCount;

    public BranchResponse(Long id, String name, String address, String city, String phone, Integer carCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.carCount = carCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Integer getCarCount() { return carCount; }
    public void setCarCount(Integer carCount) { this.carCount = carCount; }
}