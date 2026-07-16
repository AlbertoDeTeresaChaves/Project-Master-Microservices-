package com.eazybyte.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(
        name="CustomerDetails",
        description = "Schema to hold Customer, Account, Cards and Loans information"
)
@Data
public class CustomerDetailsDto {

    @NotEmpty(message ="Name can not be null or empty")
    @Size(min = 5, max = 30, message = "The length of the customer name should be between 5 and 30")
    @Schema(
            description = "Name of the customer",example = "Alberto"
    )
    private String name;

    @NotEmpty(message ="Email address can not be null or empty")
    @Email(message = "Email address should be a valid value")
    @Schema(
            description = "Email of the customer",example = "albertodtc01@gmail.com"
    )
    private String email;

    @Pattern(regexp = "(^$|[0-9]{9})",message = "Mobile number must be 9 digits")
    @Schema(
            description = "Mobile number of the customer",example = "717715674"
    )
    private String mobileNumber;

    @Schema(
            description = "Account details of the customer"
    )
    private AccountsDto accountsDto;

    @Schema(
            description = "Cards details of the customer"
    )
    private CardsDto CardsDto;

    @Schema(
            description = "Loans details of the customer"
    )
    private LoansDto LoansDto;



}
