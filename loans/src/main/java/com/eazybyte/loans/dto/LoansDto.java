package com.eazybyte.loans.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(
        name = "Loans",
        description = "Schema to hold Loans information"
)
public class LoansDto {

    @NotEmpty(message = "Mobile number can not be null or empty")
    @Pattern(regexp= "(^$|[0-9]{9})",message = "Mobile number must be 9 digits")
    @Schema(description = "Mobile number of Customer",example = "717715674")
    private String mobileNumber;

    @NotEmpty(message = "Loan number can not be null or empty")
    @Pattern(regexp = "^$|[0-9]{12}",message = "Loan number must be 12 digits")
    @Schema(description = "Loan number of Customer",example = "548732457654")
    private String loanNumber;

    @NotEmpty(message = "Loan type can not be null or empty")
    @Schema(description = "Type of a loan",example = "Home loan")
    private String loanType;

    @Positive(message = "Total loan amount should be greater than zero")
    @Schema(description = "Total loan amount",example = "100000")
    private int totalLoan;

    @PositiveOrZero(message = "Total loan amount paid should be equal or greater than zero")
    @Schema(description = "Total loan amount paid",example = "1000")
    private int amountPaid;

    @PositiveOrZero(message = "Total outstanding amount should be equal or greater than zero")
    @Schema(description = "Total outstanding amount against a loan", example = "99000")
    private int outstandingAmount;
}
