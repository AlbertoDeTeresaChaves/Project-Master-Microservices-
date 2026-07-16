package com.eazybyte.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(
        name = "Accounts",
        description = "Schema to hold Accounts information"
)
@Data
public class AccountsDto {

    @NotEmpty(message = "Account number can not be null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})",message = "Account number must be 10 digits")
    @Schema(
            description = "AccountNumber of Bank account",example = "3454433243"
    )
    private Long accountNumber;

    @NotEmpty(message = "Account type can not be null or empty")
    @Schema(
            description = "AccountType of Bank account",example = "Saving"
    )
    private String accountType;

    @NotEmpty(message = "Branch address can not be null or empty")
    @Schema(
            description = "BranchAddress of Bank account",example = "123 NewYork"
    )
    private String branchAddress;
}
