package no.priv.bang.repros.liquibaseviewderby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.assertj.db.type.AssertDbConnectionFactory;
import org.junit.jupiter.api.Test;

class LiquibaseClassPathChangeLogRunnerTest {

    @Test
    void testCreateSchema() throws Exception {
        var sampleappLiquibase = new LiquibaseClassPathChangeLogRunner();
        var datasource = createDataSource("sampleapp");
        var assertjConnection = AssertDbConnectionFactory.of(datasource).create();

        sampleappLiquibase.createInitialSchema(datasource.getConnection());

        var measures = assertjConnection.table("measures").build();
        assertThat(measures).exists().isEmpty();

        var measuresView = assertjConnection.request("select * from measures_view").build();
        assertThat(measuresView).hasNumberOfColumns(22);
    }

    private DataSource createDataSource(String dbname) throws Exception {
        var datasource = new EmbeddedDataSource();
        datasource.setDatabaseName("memory:" + dbname);
        datasource.setCreateDatabase("create");
        return datasource;
    }

}
