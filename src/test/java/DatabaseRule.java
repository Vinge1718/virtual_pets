import org.junit.rules.ExternalResource;
import org.sql2o.*;

public class DatabaseRule extends ExternalResource {

    @Override
    protected void before() {
        DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/virtual_pets_test", "v", "1234");
    }

    @Override
    protected void after() {
        try(Connection con = DB.sql2o.open()) {
            String deletePersonsQuery = "DELETE FROM persons *;";
            con.createQuery(deletePersonsQuery).executeUpdate();
        }
    }

}