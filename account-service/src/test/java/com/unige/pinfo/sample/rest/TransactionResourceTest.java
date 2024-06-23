package com.unige.pinfo.sample.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.infinispan.Cache;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.Account;
import ch.unige.pinfo.sample.utils.UserCache;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(InMemoryConnectorLifecycleManager.class)
class TransactionResourceTest {

    private static final Logger LOG = Logger.getLogger(TransactionResourceTest.class);

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector inMemoryConnector;

    @Inject
    @UserCache
    Cache<String, String> users;

    @BeforeEach
    void init() {
        InMemorySource<String> userUpdate = inMemoryConnector.source("user-update");
        userUpdate.send("1234");
        userUpdate.send("6789");

        InMemorySource<String> orgUpdate = inMemoryConnector.source("org-update");
        orgUpdate.send("4567");

        Callable<Boolean> actualValueSupplier = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return users.get("6789") != null;
            }
        };

        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(actualValueSupplier, equalTo(true));

    }

    @Test
    void testTransctionExecution() {
        given().contentType(ContentType.JSON).body("""
                {
                    "accountHolderUserId": "1234",
                    "branchId": "4567",
                    "accountManagerUserId": "6789",
                    "iban": "9101112P",
                    "creationDate": "2021-09-01",
                    "balance": 1000.0,
                    "interest": 0.0,
                    "balanceUpdatedDate": "2021-09-01",
                    "currency": "CHF",
                    "type": "CHECKING"
                },
                """).when().post("/accounts").then().statusCode(201);

        given().contentType(ContentType.JSON).body("""
                {
                    "accountHolderUserId": "1234",
                    "branchId": "4567",
                    "accountManagerUserId": "6789",
                    "iban": "9101113P",
                    "creationDate": "2021-09-01",
                    "balance": 1000.0,
                    "interest": 0.0,
                    "balanceUpdatedDate": "2021-09-01",
                    "currency": "CHF",
                    "type": "SAVING"
                },
                """).when().post("/accounts").then().statusCode(201);

        given().contentType(ContentType.JSON).body("""
                {
                    "sourceIBAN": "9101112P",
                    "targetIBAN": "9101113P",
                    "amount": 100.0,
                    "currency": "CHF",
                    "transactionDate": "2021-09-01",
                    "description": "Transfer"
                },
                """).when().post("/transactions").then().statusCode(201);



        LOG.info(given().contentType(ContentType.JSON).when().get("/accounts/9101112P").then().statusCode(200).extract().asPrettyString());

        LOG.info(given().contentType(ContentType.JSON).when().get("/accounts/9101113P").then().statusCode(200).extract().asPrettyString());

        Account a1 = given().contentType(ContentType.JSON).when().get("/accounts/9101112P").then().statusCode(200).extract().as(Account.class);
        LOG.info(String.format("Expecting a balance of 900.0, got %s", a1.getBalance()));
        Assertions.assertEquals(0, BigDecimal.valueOf(900.0).compareTo(a1.getBalance()));
        Account a2 = given().contentType(ContentType.JSON).when().get("/accounts/9101113P").then().statusCode(200).extract().as(Account.class);
        LOG.info(String.format("Expecting a balance of 1100.0, got %s", a2.getBalance()));
        Assertions.assertEquals(0, BigDecimal.valueOf(1100.0).compareTo(a2.getBalance()));

    }

}