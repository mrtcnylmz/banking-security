package com.mrtcn.bankingSystem.Responses;

import lombok.Builder;
import lombok.Data;

public
@Data
@Builder
class AccountCreateResponse {
    public String message;
    public long accountNumber;
}
