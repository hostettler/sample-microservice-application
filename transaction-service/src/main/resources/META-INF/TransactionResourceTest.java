package ch.unige.pinfo.sample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.JournalEntry;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class TransactionResourceTest {

//    @Test
//    void testPutTransaction() {
//
//        given().contentType(ContentType.JSON).body("""
//                {
//                    "sourceIBAN": "IBAN1",
//                    "targetIBAN": "IBAN2",
//                    "description": "Thanks for lending me money for the restaurant",
//                    "amount": 200,
//                    "type": "CREDIT"
//                },
//                """).when().post("/transactions").then().statusCode(201).body("id",
//                is(greaterThan(0)));
//
//    }
}