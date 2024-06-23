package ch.unige.pinfo.sample.rest;

import org.jboss.resteasy.reactive.ResponseStatus;

import ch.unige.pinfo.sample.model.Transaction;
import ch.unige.pinfo.sample.service.TransactionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/transactions")
public class TransactionResource {

    TransactionService transactionService;

    @Inject
    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @POST
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    public void put(Transaction transaction) {
        transactionService.execute(transaction);
    }


}