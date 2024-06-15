package ch.unige.pinfo.sample.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * This utility class converts Camel case into snake case with an 's' and the end of the name.
 */
public class SamplePhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalColumnName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSequenceName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        Identifier id = convertToSnakeCase(identifier);
        assert id != null;
        String table = id.toString();
        if (!table.substring(table.length() -1 ).equals("s")) {
            if (table.substring(table.length() - 1).equals("y")) {
                table = table.substring(0, table.length() - 1) + "ies";
            } else if (table.substring(table.length() - 1).equals("h")) {
                table += "es";
            } else if (table.substring(table.length() - 1).equals("x")) {
                table += "es";
            } else {                
                table += "s";
            }
        }
        return Identifier.toIdentifier(table);
    }

    private Identifier convertToSnakeCase(final Identifier identifier) {
        if (identifier == null)
            return null;
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText()
          .replaceAll(regex, replacement)
          .toLowerCase();
        return Identifier.toIdentifier(newName);
    }
}