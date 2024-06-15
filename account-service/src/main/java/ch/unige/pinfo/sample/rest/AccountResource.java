package ch.unige.pinfo.sample.rest;

import java.math.BigDecimal;

import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.service.BalanceService;
import ch.unige.pinfo.sample.service.ConsistencyCheckService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/accounts")
public class AccountResource {

    BalanceService balanceService;
    ConsistencyCheckService checkService;
    
    @Inject
    public AccountResource(BalanceService balanceService, ConsistencyCheckService checkService) {
        this.balanceService = balanceService;
        this.checkService = checkService;
    }
    
    @POST
    @Transactional
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(Account account) {
        //Check whether the user ids refers to existing ones
        if (!checkService.checkUserExists(account.getAccountHolderUserId())) {
            throw new IllegalArgumentException("The account holder id is not know in the system.");
        }
        if (checkService.checkUserExists(account.getAccountManagerUserId())) {
            throw new IllegalArgumentException("The account manager id is not know in the system.");
        }
        if (checkService.checkOrganisationBranchId(account.getBranchId())) {
            throw new IllegalArgumentException("The branch is not know in the system.");
        }   
        
        account.persist();
        return account;
    }
    
    @PUT
    public void updateBalance(@PathParam("accountId") String accountId, @PathParam("amount") BigDecimal amount) { 
        Account account = Account.findById(accountId);
        if (account == null) {
            throw new NotFoundException();
        }
        
        if (account.getBalance().add(amount).doubleValue() < 0) {
            throw new IllegalArgumentException("The balance must remain positive");
        }
        balanceService.updateBalance(accountId, amount);
    }
    
}