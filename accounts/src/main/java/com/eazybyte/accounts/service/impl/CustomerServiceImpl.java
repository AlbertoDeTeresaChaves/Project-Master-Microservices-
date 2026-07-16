package com.eazybyte.accounts.service.impl;

import com.eazybyte.accounts.dto.AccountsDto;
import com.eazybyte.accounts.dto.CardsDto;
import com.eazybyte.accounts.dto.CustomerDetailsDto;
import com.eazybyte.accounts.dto.LoansDto;
import com.eazybyte.accounts.entity.Accounts;
import com.eazybyte.accounts.entity.Customer;
import com.eazybyte.accounts.exception.ResourceNotFoundException;
import com.eazybyte.accounts.mapper.AccountsMapper;
import com.eazybyte.accounts.mapper.CustomerMapper;
import com.eazybyte.accounts.repository.AccountsRepository;
import com.eazybyte.accounts.repository.CustomerRepository;
import com.eazybyte.accounts.service.ICustomerService;
import com.eazybyte.accounts.service.client.CardsFeignClient;
import com.eazybyte.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                ()-> new ResourceNotFoundException("Account","customerId",customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer,new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<CardsDto> cardsResponseDto = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);

        if (null != cardsResponseDto) {
        customerDetailsDto.setCardsDto(cardsResponseDto.getBody());
        }

        ResponseEntity<LoansDto> loansResponseDto = loansFeignClient.fetchLoanDetails(correlationId,mobileNumber);

        if (null != loansResponseDto) {
            customerDetailsDto.setLoansDto(loansResponseDto.getBody());
        }

        return customerDetailsDto;
    }
}
