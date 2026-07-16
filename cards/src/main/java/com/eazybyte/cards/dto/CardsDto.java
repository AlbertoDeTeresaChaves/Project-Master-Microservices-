package com.eazybyte.cards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Schema(
        name = "Cards",
        description = "Schema to hold Cards information"
)
@Data
public class CardsDto {

    @NotEmpty(message = "Mobile number can not be null or empty")
    @Pattern(regexp = "(^$[0-9]{9})",message = "Mobile number must be 9 digits")
    @Schema(description = "Mobile number of Customer",example = "717715674")
    private String mobileNumber;

    @NotEmpty(message = "Cards number can not be null or empty")
    @Pattern(regexp = "(^$[0-9]{12})",message = "Card number must be 12 digits")
    @Schema(description = "Card number of Customer",example = "100646930341")
    private String cardNumber;

    @NotEmpty(message = "Card type can not be null or empty")
    @Schema(description = "Card type of Customer",example = "Credit Card")
    private String cardType;

    @Positive(message = "Total card limit should be greater than zero")
    @Schema(description = "Total amount limit available against a card",example = "100000")
    private int totalLimit;

    @PositiveOrZero(message = "Total amount used should be greater or equals than zero")
    @Schema(description = "Total amount used by Customer",example = "1000")
    private int amountUsed;

    @PositiveOrZero(message = "Total available amount should be greater or equals than zero")
    @Schema(description = "Total available amount against a card",example = "90000")
    private int availableAmount;
}
