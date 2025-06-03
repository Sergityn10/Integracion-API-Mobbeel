package com.mobbScan_integration.MobbScan.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalData {
    private String address;
    private String city;
    private String cityOfBirth;
    private String country;
    private String countryDocumentType;
    private String curp;
    private String dateOfBirth;
    private String dateOfExpiry;
    private String documentNumber;
    private String gender;
    private String mrz;
    private String name;
    private String personalNumber;
    private String postCode;
    private String region;
    private String regionOfBirth;
    private String firstSurname;
    private String secondSurname;
    private String stateOfBirth;
    private String surname;
}

