package com.eazybyte.loans.service;

import com.eazybyte.loans.dto.LoansDto;

public interface LoansService {

    void createLoan(String mobileNumber);
    LoansDto fetchLoan(String mobileNumber);
    boolean updateLoan(LoansDto loanDto);
    boolean deleteLoan(String mobileNumber);
}
