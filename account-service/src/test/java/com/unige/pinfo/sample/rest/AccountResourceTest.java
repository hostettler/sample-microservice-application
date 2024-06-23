package com.unige.pinfo.sample.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.math.BigDecimal;

import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.Account;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(InMemoryConnectorLifecycleManager.class)
class AccountResourceTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector inMemoryConnector;

    @BeforeEach
    void init() throws InterruptedException {
        InMemorySource<String> userUpdate = inMemoryConnector.source("user-update");
        userUpdate.send("1234");
        userUpdate.send("6789");

        InMemorySource<String> orgUpdate = inMemoryConnector.source("org-update");
        orgUpdate.send("4567");
        Thread.sleep(1000);
    }

    @Test
    void testCreateAccount() {
        given().contentType(ContentType.JSON).body("""
                {
                    "accountHolderUserId": "1234",
                    "branchId": "4567",
                    "accountManagerUserId": "6789",
                    "iban": "9101112",
                    "creationDate": "2021-09-01",
                    "balance": 1000.0,
                    "interest": 0.0,
                    "balanceUpdatedDate": "2021-09-01",
                    "currency": "CHF",
                    "type": "CHECKING"
                },
                """).when().post("/accounts").then().statusCode(201);
    }

    @Test
    void testCreateAccountWithUknownParameters() {
        given().contentType(ContentType.JSON).body("""
                {
                    "accountHolderUserId": "1234X",
                    "branchId": "4567",
                    "accountManagerUserId": "6789",
                    "iban": "9101112",
                    "creationDate": "2021-09-01",
                    "balance": 1000.0,
                    "interest": 0.0,
                    "balanceUpdatedDate": "2021-09-01",
                    "currency": "CHF",
                    "type": "CHECKING"
                },
                """).when().post("/accounts").then().statusCode(404);

        given().contentType(ContentType.JSON).body("""
                {
                    "accountHolderUserId": "1234",
                    "branchId": "4567X",
                    "accountManagerUserId": "6789",
                    "iban": "9101112",
                    "creationDate": "2021-09-01",
                    "balance": 1000.0,
                    "interest": 0.0,
                    "balanceUpdatedDate": "2021-09-01",
                    "currency": "CHF",
                    "type": "CHECKING"
                },
                """).when().post("/accounts").then().statusCode(404);

        given().contentType(ContentType.JSON).body("""
                {
                    "accountHolderUserId": "1234",
                    "branchId": "4567",
                    "accountManagerUserId": "6789X",
                    "iban": "9101112",
                    "creationDate": "2021-09-01",
                    "balance": 1000.0,
                    "interest": 0.0,
                    "balanceUpdatedDate": "2021-09-01",
                    "currency": "CHF",
                    "type": "CHECKING"
                },
                """).when().post("/accounts").then().statusCode(404);
    }

    @Test
    void testUpdateBalance() {
        given().contentType(ContentType.JSON).body("""
                {
                    "accountHolderUserId": "1234",
                    "branchId": "4567",
                    "accountManagerUserId": "6789",
                    "iban": "9101112",
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
                    "iban": "9101112",
                    "amount": 100.0,
                    "type": "CREDIT",
                    "originalCurrency": "CHF"
                },
                """).when().put("/accounts/update").then().statusCode(204);

        Account a1 = given().contentType(ContentType.JSON).when().get("/accounts/9101112").then().statusCode(200).body("iban", equalTo("9101112"))
                .extract().as(Account.class);
        Assertions.assertEquals(0, BigDecimal.valueOf(1100.0).compareTo(a1.getBalance()));
    }
}