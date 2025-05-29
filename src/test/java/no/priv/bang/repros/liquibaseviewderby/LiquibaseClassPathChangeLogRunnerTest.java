package no.priv.bang.repros.liquibaseviewderby;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.assertj.db.type.AssertDbConnectionFactory;
import org.junit.jupiter.api.Test;
import liquibase.exception.LiquibaseException;

class LiquibaseClassPathChangeLogRunnerTest {

    @Test
    void testCreateSchema() throws Exception {
        var sampleappLiquibase = new LiquibaseClassPathChangeLogRunner();
        var datasource = createDataSource("sampleapp");
        var assertjConnection = AssertDbConnectionFactory.of(datasource).create();

        sampleappLiquibase.createInitialSchema(datasource.getConnection());

        var accounts1 = assertjConnection.table("measures").build();
        assertThat(accounts1).exists().isEmpty();

        var accounts2 = assertjConnection.request("select * from measures_view").build();
        assertThat(accounts2).hasNumberOfColumns(22);
    }

    @Test
    void testCreateSchemaAndFail() throws Exception {
        var datasource = createDataSource("sampleapp");
        var connection = spy(datasource.getConnection());
        // A Derby JDBC connection wrapped in a Mockito spy() fails om Connection.setAutoClosable()

        var sampleappLiquibase = new LiquibaseClassPathChangeLogRunner();

        var ex = assertThrows(
            LiquibaseException.class,
            () -> sampleappLiquibase.createInitialSchema(connection));
        assertThat(ex.getMessage()).startsWith("java.sql.SQLException: Cannot set Autocommit On when in a nested connection");
    }

    @Test
    void testCreateSchemaAndFailOnConnectionClose() throws Exception {
        var datasource = createDataSource("sampleapp2");
        var connection = spy(datasource.getConnection());
        doNothing().when(connection).setAutoCommit(anyBoolean());
        doThrow(Exception.class).when(connection).close();

        var sampleappLiquibase = new LiquibaseClassPathChangeLogRunner();

        var ex = assertThrows(
            LiquibaseException.class,
            () -> sampleappLiquibase.createInitialSchema(connection));
        assertThat(ex.getMessage()).startsWith("java.lang.Exception");
    }

    private DataSource createDataSource(String dbname) throws Exception {
        var datasource = new EmbeddedDataSource();
        datasource.setDatabaseName("memory:" + dbname);
        datasource.setCreateDatabase("create");
        return datasource;
    }

}
