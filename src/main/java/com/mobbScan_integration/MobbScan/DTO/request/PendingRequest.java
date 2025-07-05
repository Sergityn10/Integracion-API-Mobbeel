package com.mobbScan_integration.MobbScan.DTO.request;

import com.mobbScan_integration.MobbScan.DTO.PersonalData;
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

