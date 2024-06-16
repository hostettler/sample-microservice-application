package ch.unige.pinfo.sample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.BusinessEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.SecurityAttribute;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
class BusinessEntityResourceTest {

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testAll() {
        given().when().get("/business-entities/all").then().statusCode(200).body("[0].name", equalTo("business entity 11"));
    }

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = { "user" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testGetById() {
        given().when().get("/business-entities/1").then().statusCode(200).body("name", equalTo("business entity 11"));
        given().when().get("/business-entities/2").then().statusCode(200).body("name", equalTo("business entity 12"));

        BusinessEntity be1 = given().when().get("/business-entities/1").then().statusCode(200).extract().body().as(BusinessEntity.class);
        BusinessEntity be2 = given().when().get("/business-entities/2").then().statusCode(200).extract().body().as(BusinessEntity.class);
        BusinessEntity be1bis = given().when().get("/business-entities/1").then().statusCode(200).extract().body().as(BusinessEntity.class);

        Assertions.assertNotEquals(be1, be2);
        Assertions.assertEquals(be1, be1bis);

        Assertions.assertEquals(
                "BusinessEntity(super=AbstractOrganizationStructureElement(name=business entity 11, address=Rue du pont 42A, city=Peta ouschnock les bains, postalCode=1232), branches=[])",
                be1.toString());
    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "user", "admin" })
    void testAddBusinessEntity() {
        List<BusinessEntity> bes = given().when().get("/business-entities/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        long oldSize = bes.size();

        given().contentType(ContentType.JSON).body("""
                {
                    "name": "branch 120",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                """).when().post("/business-entities").then().statusCode(201).body("name", equalTo("branch 120"));

        bes = given().when().get("/business-entities/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        Assertions.assertEquals(oldSize + 1, bes.size());

    }

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = { "user", "admin" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testUpdateBusinessEntity() {
        Integer newId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "business entity 130",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                """).when().post("/business-entities").then().statusCode(201).extract().body().jsonPath().get("id");
        String body = String.format("""
                 {
                    "id" : %d,
                    "address": "Chemin de la terasse 1000000000000A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                }
                """, newId);

        String newAdrress = given().contentType(ContentType.JSON).body(body).when().put("/business-entities").then().statusCode(201).extract().body()
                .jsonPath().get("address");
        Assertions.assertEquals("Chemin de la terasse 1000000000000A", newAdrress);

    }

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = { "admin" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testAddBranchToBusinessEntity() {

        Integer newBEId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "business entity 14",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                """).when().post("/business-entities").then().statusCode(201).extract().body().jsonPath().get("id");

        Integer newBId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "branch 116",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                """).when().post("/branches").then().statusCode(201).extract().body().jsonPath().get("id");
        String url = String.format("/business-entities/add-branch/%d/%d", newBEId, newBId);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(201);

        newBId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "branch 117",
                    "address": "Chemin de la terasse 17A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                """).when().post("/branches").then().statusCode(201).extract().body().jsonPath().get("id");
        url = String.format("/business-entities/add-branch/%d/%d", newBEId, newBId);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(201);

        given().when().get(String.format("/business-entities/%d", newBEId)).then().statusCode(200).body("name", equalTo("business entity 14"),
                "branches.size()", equalTo(2));

    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin", "user" })
    void testUknownBranchEntity() {
        String body = String.format("""
                 {
                    "id" : %d,
                    "name": "business entity 114",
                    "address": "Chemin de la terasse 1000000000000A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                }
                """, 45875454);

        given().contentType(ContentType.JSON).body(body).when().put("/business-entities").then().statusCode(404);
    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testListAllIds() {
        List<String> ids = given().when().get("/business-entities/ids").then().statusCode(200).extract().body().jsonPath().getList(".");
        Assertions.assertTrue(ids.size() > 0);
    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testUpdateNonExistingBusinessentity() {
        String body = String.format("""
                 {
                    "id" : %d,
                    "address": "Chemin de la terasse 1000000000000A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                }
                """, 6666);

        given().contentType(ContentType.JSON).body(body).when().put("/business-entities").then().statusCode(404);

    }

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = { "admin" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testAddNonExistingBranchToBusinessEntity() {

        Integer newBEId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "business entity 1408",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                },
                """).when().post("/business-entities").then().statusCode(201).extract().body().jsonPath().get("id");

        String url = String.format("/business-entities/add-branch/%d/%d", newBEId, 666);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(404);
        url = String.format("/business-entities/add-branch/%d/%d", 777, 666);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(404);

    }

}