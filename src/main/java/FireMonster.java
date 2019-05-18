import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import org.sql2o.*;

public class FireMonster extends Monster implements DatabaseManagement {
    private int fireLevel;
    public static final int MAX_FIRE_LEVEL = 10;
    public static final String DATABASE_TYPE = "fire";
    public Timestamp lastKindling;


    public FireMonster(String name, int personId) {
        this.name = name;
        this.personId = personId;
        playLevel = MAX_PLAY_LEVEL / 2;
        sleepLevel = MAX_SLEEP_LEVEL / 2;
        foodLevel = MAX_FOOD_LEVEL / 2;
        fireLevel = MAX_FIRE_LEVEL / 2;
        timer = new Timer();
        type = DATABASE_TYPE;
    }

    public static List<FireMonster> all() {
        String sql = "SELECT * FROM monsters WHERE type='fire';";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(FireMonster.class);
        }
    }

    public static FireMonster find(int id) {
        try(Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM monsters where id=:id";
            FireMonster monster = con.createQuery(sql)
                    .addParameter("id", id)
                    .throwOnMappingFailure(false)
                    .executeAndFetchFirst(FireMonster.class);
            return monster;
        }
    }

    public int getFireLevel(){
        return fireLevel;
    }

    public void kindling(){
        if (fireLevel >= MAX_PLAY_LEVEL){
            throw new UnsupportedOperationException("You cannot give any more kindling!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastkindling = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        fireLevel++;
    }

    @Override
    public void depleteLevels(){
        if (isAlive()){
            playLevel--;
            foodLevel--;
            sleepLevel--;
            fireLevel--;
        }
    }

    @Override
    public boolean isAlive() {
        if (foodLevel <= MIN_ALL_LEVELS ||
                playLevel <= MIN_ALL_LEVELS ||
                fireLevel <= MIN_ALL_LEVELS ||
                sleepLevel <= MIN_ALL_LEVELS) {
            return false;
        }
        return true;
    }

    public Timestamp getLastKindling(){
        return lastKindling;
    }
}