package com.mobbScan_integration.MobbScan.DTO.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectRequest {
    private String rejectionComment;
    private String type;
    private String verificationId;
}

