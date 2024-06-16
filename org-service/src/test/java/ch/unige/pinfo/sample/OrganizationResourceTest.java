package ch.unige.pinfo.sample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.Organization;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.SecurityAttribute;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
class OrganizationResourceTest {

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testAll() {
        given().when().get("/organizations/all").then().statusCode(200).body("[0].name", equalTo("organization 1"));
    }

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = { "user" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testGetById() {
        given().when().get("/organizations/1").then().statusCode(200).body("name", equalTo("organization 1"));
        given().when().get("/organizations/2").then().statusCode(200).body("name", equalTo("organization 2"));

        Organization organization1 = given().when().get("/organizations/1").then().statusCode(200).extract().body().as(Organization.class);
        Organization organization2 = given().when().get("/organizations/2").then().statusCode(200).extract().body().as(Organization.class);
        Organization organization1Bis = given().when().get("/organizations/1").then().statusCode(200).extract().body().as(Organization.class);

        Assertions.assertNotEquals(organization1, organization2);
        Assertions.assertEquals(organization1, organization1Bis);

        Assertions.assertEquals(
                "Organization(super=AbstractOrganizationStructureElement(name=organization 1, address=Rue du pont 42A, city=Peta ouschnock les bains, postalCode=1232), businessEntities=[])",
                organization1.toString());
    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testAddOrganization() {
        List<Organization> organizations = given().when().get("/organizations/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        long oldSize = organizations.size();
        given().contentType("application/json").body("""
                {
                    "name": "organization 3",
                    "address": "Rue du pont 42A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                }
                """).when().post("/organizations").then().statusCode(201).body("name", equalTo("organization 3"));

        organizations = given().when().get("/organizations/all").then().statusCode(200).extract().body().jsonPath().getList(".");
        Assertions.assertEquals(oldSize + 1, organizations.size());
    }

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testUpdateOrganization() {

        Integer id = given().contentType("application/json").body("""
                {
                    "name": "organization 5458",
                    "address": "Rue du pont 42A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                }
                """).when().post("/organizations").then().statusCode(201).extract().body().jsonPath().get("id");

        Organization organization = given().when().get(String.format("/organizations/%d", id)).then().statusCode(200).extract().body()
                .as(Organization.class);
        organization.setPostalCode("6687");
        given().contentType("application/json").body(organization).when().put("/organizations").then().statusCode(201).body("postalCode",
                equalTo("6687"));
    }
    
    

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testUpdateNonExistingOrganization() {

        Integer id = given().contentType("application/json").body("""
                {
                    "name": "organization 8798",
                    "address": "Rue du pont 42A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"
                }
                """).when().post("/organizations").then().statusCode(201).extract().body().jsonPath().get("id");

        Organization organization = given().when().get(String.format("/organizations/%d", id)).then().statusCode(200).extract().body()
                .as(Organization.class);
        organization.setPostalCode("6687");
        organization.id= 666l;
        given().contentType("application/json").body(organization).when().put("/organizations").then().statusCode(404);
    }

    

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = {"admin" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testAddBusinessEntityToOrg() {
        

        
        Integer orgId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "organization 8",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"    
                },
                """).when().post("/organizations").then().statusCode(201).extract().body().jsonPath().get("id");

        
        
        Integer newBEId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "business entity 81",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"    
                },
                """).when().post("/business-entities").then().statusCode(201).extract().body().jsonPath().get("id");
        String url = String.format("/organizations/add-business-entity/%d/%d", orgId, newBEId);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(201);
        
        
        newBEId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "business entity 82",
                    "address": "Chemin de la terasse 17A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"    
                },
                """).when().post("/business-entities").then().statusCode(201).extract().body().jsonPath().get("id");
        url=String.format("/organizations/add-business-entity/%d/%d", orgId, newBEId);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(201);
        
        
        given().when().get(String.format("/organizations/%d", orgId)).then().statusCode(200).body("name", equalTo("organization 8"), "businessEntities.size()", equalTo(2));

    }
    
    
    

    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = {"admin" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testAddNonExistingBusinessEntityToOrg() {
                
        Integer orgId = given().contentType(ContentType.JSON).body("""
                {
                    "name": "organization 48",
                    "address": "Chemin de la terasse 1A",
                    "city": "Peta ouschnock les bains",
                    "postalCode": "1232"    
                },
                """).when().post("/organizations").then().statusCode(201).extract().body().jsonPath().get("id");
        
        String url = String.format("/organizations/add-business-entity/%d/%d", orgId, 666);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(404);
    }
    
    
    @Test
    @TestSecurity(user = "john.doe@doe.com", roles = {"admin" }, attributes = { @SecurityAttribute(key = "answer", value = "42") })
    void testAddExistingBusinessEntityToNonExistingOrg() {
        String url = String.format("/organizations/add-business-entity/%d/%d", 666, 666);
        given().contentType(ContentType.JSON).when().put(url).then().statusCode(404);
    }
    
}
