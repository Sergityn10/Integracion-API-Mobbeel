package com.mobbScan_integration.MobbScan.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptRequest {
    private PersonalData personalData;
    private String type;
    private String verificationId;
}
