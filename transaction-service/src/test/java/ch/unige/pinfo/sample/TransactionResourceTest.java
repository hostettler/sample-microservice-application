package ch.unige.pinfo.sample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;

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
//                    "type": "Credit",
//                    "currency" : "EUR"
//                },
//                """).when().post("/transactions").then().statusCode(201).body("id",
//                is(greaterThan(0)));
//
//    }
}