package com.mobbScan_integration.MobbScan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingRequest {
    private PersonalData personalData;
    private String verificationId;
}

