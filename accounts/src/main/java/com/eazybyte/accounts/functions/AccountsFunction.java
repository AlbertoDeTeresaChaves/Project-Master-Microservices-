package com.eazybyte.accounts.functions;


import com.eazybyte.accounts.service.IAccountService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountsFunction {
    private static final Logger log = LoggerFactory.getLogger(AccountsFunction.class);


    @Bean
    public Consumer<Long> updateCommunication(IAccountService accountService){
        return accountNumber -> {
            log.info("Updating Communication status for the account number " +  accountNumber);
            accountService.updateCommunicationStatus(accountNumber);
        };
    }
}
