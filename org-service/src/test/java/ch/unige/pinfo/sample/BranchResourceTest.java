package ch.unige.pinfo.sample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.Branch;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.SecurityAttribute;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
class BranchResourceTest {

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "user" })
    void testAll() {
        given().when().get("/branches/all").then().statusCode(200).body("[0].name", equalTo("branch 111"));
    }

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = { "user" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testGetById() {
        given().when().get("/branches/1").then().statusCode(200).body("name", equalTo("branch 111"));
        given().when().get("/branches/2").then().statusCode(200).body("name", equalTo("branch 112"));

        Branch branch1 = given().when().get("/branches/1").then().statusCode(200).extract().body().as(Branch.class);
        Branch branch2 = given().when().get("/branches/2").then().statusCode(200).extract().body().as(Branch.class);
        Branch branch1Bis = given().when().get("/branches/1").then().statusCode(200).extract().body().as(Branch.class);

        Assertions.assertNotEquals(branch1, branch2);
        Assertions.assertEquals(branch1, branch1Bis);

        Assertions.assertEquals(
                "Branch(super=AbstractOrganizationStructureElement(name=branch 111, address=Chemin de la terasse 1A, city=Peta ouschnock les bains, postalCode=1232))",
                branch1.toString());
    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testAddBranch() {
        List<Branch> branches = given().when().get("/branches/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        long oldSize = branches.size();

        given().contentType(ContentType.JSON).body("""
                {
                    "name": "branch 119",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"    
                },
                """).when().post("/branches").then().statusCode(201).body("name", equalTo("branch 119"));

        branches = given().when().get("/branches/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        Assertions.assertEquals(oldSize + 1, branches.size());

    }

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = {  "admin" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testUpdateBranch() {
        Integer newId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "branch 140",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"    
                },
                """).when().post("/branches").then().statusCode(201).extract().body().jsonPath().get("id");
        String body = String.format("""
                 {
                    "id" : %d,
                    "address": "Chemin de la terasse 1000000000000A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                }
                """, newId);

        String newAdrress = given().contentType(ContentType.JSON).body(body).when().put("/branches").then().statusCode(201).extract().body().jsonPath()
                .get("address");
        Assertions.assertEquals("Chemin de la terasse 1000000000000A", newAdrress);

    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin", "user" })
    void testUknownBranch() {
        String body = String.format("""
                 {
                    "id" : %d,
                    "name": "branch 198",
                    "address": "Chemin de la terasse 1000000000000A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                }
                """, 45875454);

        given().contentType(ContentType.JSON).body(body).when().put("/branches").then().statusCode(404);
    }

    
    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testListAllIds() {       
        List<String> ids = given().when().get("/branches/ids").then().statusCode(200).extract().body().jsonPath().getList(".");
        System.out.println(ids);
        Assertions.assertEquals(6, ids.size());
    }

}