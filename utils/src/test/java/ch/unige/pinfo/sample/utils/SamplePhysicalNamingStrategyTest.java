package ch.unige.pinfo.sample.utils;

import org.hibernate.boot.model.naming.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.util.SamplePhysicalNamingStrategy;

class SamplePhysicalNamingStrategyTest {

    @Test
    void test() {
        Assertions.assertEquals("branches", new SamplePhysicalNamingStrategy().toPhysicalTableName(new Identifier("branch", false), null).toString());
        Assertions.assertEquals("entities", new SamplePhysicalNamingStrategy().toPhysicalTableName(new Identifier("entity", false), null).toString());
        Assertions.assertEquals("use_r", new SamplePhysicalNamingStrategy().toPhysicalCatalogName(new Identifier("useR", false), null).toString());
        Assertions.assertEquals("use_rs", new SamplePhysicalNamingStrategy().toPhysicalCatalogName(new Identifier("useRs", false), null).toString());
        Assertions.assertEquals("users", new SamplePhysicalNamingStrategy().toPhysicalCatalogName(new Identifier("users", false), null).toString());
        Assertions.assertEquals("first_name", new SamplePhysicalNamingStrategy().toPhysicalColumnName(new Identifier("firstName", false), null).toString());
        Assertions.assertEquals("user", new SamplePhysicalNamingStrategy().toPhysicalSchemaName(new Identifier("USER", false), null).toString());
        Assertions.assertEquals("users", new SamplePhysicalNamingStrategy().toPhysicalSchemaName(new Identifier("users", false), null).toString());        
        Assertions.assertEquals("use_rs", new SamplePhysicalNamingStrategy().toPhysicalTableName(new Identifier("useRs", false), null).toString());
        Assertions.assertEquals("users", new SamplePhysicalNamingStrategy().toPhysicalTableName(new Identifier("user", false), null).toString());
        Assertions.assertEquals("balances", new SamplePhysicalNamingStrategy().toPhysicalTableName(new Identifier("Balance", false), null).toString());
        Assertions.assertEquals("user_permissions", new SamplePhysicalNamingStrategy().toPhysicalTableName(new Identifier("UserPermission", false), null).toString());
        Assertions.assertEquals("user_permissions", new SamplePhysicalNamingStrategy().toPhysicalTableName(new Identifier("UserPermissions", false), null).toString());
        
    }
}
