package ch.unige.pinfo.sample;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

@QuarkusTest
class OrgPermissionResourceTest {

    @Test
    @TestSecurity(user = "boss@boss.com", roles = { "admin" })
    void testAll() {
        given().when().get("/permissions/me").then().statusCode(200).body("size()", org.hamcrest.Matchers.is(0));
    }
    
}
