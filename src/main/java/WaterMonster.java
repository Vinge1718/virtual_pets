import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import org.sql2o.*;

public class WaterMonster extends Monster implements DatabaseManagement {
    private int waterLevel;
    public Timestamp lastWater;
    public static final int MAX_WATER_LEVEL = 8;
    public static final String DATABASE_TYPE = "water";

    public WaterMonster(String name, int personId) {
        this.name = name;
        this.personId = personId;
        playLevel = MAX_PLAY_LEVEL / 2;
        sleepLevel = MAX_SLEEP_LEVEL / 2;
        foodLevel = MAX_FOOD_LEVEL / 2;
        waterLevel = MAX_WATER_LEVEL / 2;
        timer = new Timer();
        type = DATABASE_TYPE;
    }

    public static List<WaterMonster> all() {
        String sql = "SELECT * FROM monsters WHERE type='water';";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(WaterMonster.class);
        }
    }

    public static WaterMonster find(int id) {
        try(Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM monsters where id=:id";
            WaterMonster monster = con.createQuery(sql)
                    .addParameter("id", id)
                    .throwOnMappingFailure(false)
                    .executeAndFetchFirst(WaterMonster.class);
            return monster;
        }
    }

    public int getWaterLevel(){
        return waterLevel;
    }

    public void water(){
        if (waterLevel >= MAX_WATER_LEVEL){
            throw new UnsupportedOperationException("You cannot water your pet any more!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastwater = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        waterLevel++;
    }

    @Override
    public void depleteLevels(){
        if (isAlive()){
            playLevel--;
            foodLevel--;
            sleepLevel--;
            waterLevel--;
        }
    }

    @Override
    public boolean isAlive() {
        if (foodLevel <= MIN_ALL_LEVELS ||
                playLevel <= MIN_ALL_LEVELS ||
                waterLevel <= MIN_ALL_LEVELS ||
                sleepLevel <= MIN_ALL_LEVELS) {
            return false;
        }
        return true;
    }

    public Timestamp getLastWater(){
        return lastWater;
    }

}