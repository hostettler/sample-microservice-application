package ch.unige.pinfo.sample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.Account;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class UserResourceTest {

    @Test
    void testAll() {

        given().when().get("/users/all").then().statusCode(200).body("[0].firstName", equalTo("John"), "[0].lastName", equalTo("Doe"),
                "[1].firstName", equalTo("Jane"), "[1].lastName", equalTo("Doe"));
    }

    @Test
    void testGetById() {
        given().when().get("/users/1").then().statusCode(200).body("lastName", equalTo("Doe"), "firstName", equalTo("John"));
        given().when().get("/users/2").then().statusCode(200).body("lastName", equalTo("Doe"), "firstName", equalTo("Jane"));

        Account user1 = given().when().get("/users/1").then().statusCode(200).extract().body().as(Account.class);
        Account user2 = given().when().get("/users/2").then().statusCode(200).extract().body().as(Account.class);
        Account user1Bis = given().when().get("/users/1").then().statusCode(200).extract().body().as(Account.class);

        Assertions.assertNotEquals(user1, user2);
        Assertions.assertEquals(user1, user1Bis);

        Assertions.assertEquals(
                "User(id=1, userId=john.doe@doe.com, firstName=John, lastName=Doe, city=null, postalCode=null, address=null, birth=null, gender=Male, status=null)",
                user1.toString());
    }

    @Test
    void testAddUser() {
        List<Account> users = given().when().get("/users/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        long oldSize = users.size();

        given().contentType(ContentType.JSON).body("""
                {
                    "userId": "alex.doe@doe.com",
                    "firstName": "Alex",
                    "lastName": "Doe",
                    "city": null,
                    "postalCode": null,
                    "address": null,
                    "birth": null,
                    "gender": "Male",
                    "status": null
                },
                """).when().post("/users").then().statusCode(201).body("lastName", equalTo("Doe"), "firstName", equalTo("Alex"), "id",
                is(greaterThan(0)));

        users = given().when().get("/users/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        Assertions.assertEquals(oldSize + 1, users.size());
    }

    @Test
    void testUpdateUser() {
        Integer newId = given().contentType(ContentType.JSON).body("""
                {
                    "userId": "alex.doe@doe.com",
                    "firstName": "Alex",
                    "lastName": "Doe",
                    "city": null,
                    "postalCode": null,
                    "address": null,
                    "birth": null,
                    "gender": "Male",
                    "status": null
                }
                """).when().post("/users").then().statusCode(201).extract().body().jsonPath().get("id");

        String body = String.format("""
                 {
                    "id" : %d,
                    "userId": "alex.doe@doe.com",
                    "firstName": "Alex",
                    "lastName": "Doe",
                    "gender": "Female"
                }
                """, newId);

        String newGender = given().contentType(ContentType.JSON).body(body).when().put("/users").then().statusCode(201).extract().body().jsonPath()
                .get("gender");
        Assertions.assertEquals("Female", newGender);
        
    }
    
    @Test
    void testUknownUser() {
        String body = String.format("""
                {
                   "id" : %d,
                   "userId": "alex.doe@doe.com",
                   "firstName": "Alex",
                   "lastName": "Doe",
                   "gender": "Female"
               }
               """, 45875454);

       given().contentType(ContentType.JSON).body(body).when().put("/users").then().statusCode(404);       
    }

    @Test
    void testDeleteUser() {
        List<Account> users = given().when().get("/users/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        long oldSize = users.size();

        Integer newId = given().contentType(ContentType.JSON).body("""
                {
                    "userId": "alex.doe@doe.com",
                    "firstName": "Alex",
                    "lastName": "Doe",
                    "city": null,
                    "postalCode": null,
                    "address": null,
                    "birth": null,
                    "gender": "Male",
                    "status": null
                },
                """).when().post("/users").then().statusCode(201).extract().body().jsonPath().get("id");

        users = given().when().get("/users/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        Assertions.assertEquals(oldSize + 1, users.size());

        given().contentType(ContentType.JSON).when().delete("/users/" + newId).then().statusCode(204);

        users = given().when().get("/users/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        Assertions.assertEquals(oldSize, users.size());

    }
}