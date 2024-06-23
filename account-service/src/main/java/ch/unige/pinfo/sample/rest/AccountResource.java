package ch.unige.pinfo.sample.rest;

import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.model.JournalEntry;
import ch.unige.pinfo.sample.service.BalanceService;
import ch.unige.pinfo.sample.service.ConsistencyCheckService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
        if (!checkService.checkUserExists(account.getAccountHolderUserId())) {
            throw new NotFoundException("The account holder id is not know in the system.");
        }
        if (!checkService.checkUserExists(account.getAccountManagerUserId())) {
            throw new NotFoundException("The account manager id is not know in the system.");
        }
        if (!checkService.checkOrganisationBranchId(account.getBranchId())) {
            throw new NotFoundException("The branch is not know in the system.");
        }   
        
        account.persist();
        return account;
    }
       
    @GET
    @Path("/{iban}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseStatus(200)
    public Account getAccount(@PathParam("iban") String iban) {         
        return Account.find("iban",iban).firstResult();
    }
    
    
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void updateBalance(JournalEntry entry) {     
        balanceService.updateBalance(entry);
    }
    
}