import org.sql2o.Connection;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Monster {
    public String name;
    public int personId;
    public int id;
    public int foodLevel;
    public int sleepLevel;
    public int playLevel;

    public Timestamp birthday;
    public Timestamp lastSlept;
    public Timestamp lastAte;
    public Timestamp lastPlayed;

    public static final int MAX_FOOD_LEVEL = 3;
    public static final int MAX_SLEEP_LEVEL = 8;
    public static final int MAX_PLAY_LEVEL = 12;
    public static final int MIN_ALL_LEVELS = 0;

    public Timer timer;
    public String type;

//    public Monster(String name, int personId) {
//        this.name = name;
//        this.personId = personId;
//        playLevel = MAX_PLAY_LEVEL / 2;
//        sleepLevel = MAX_SLEEP_LEVEL / 2;
//        foodLevel = MAX_FOOD_LEVEL / 2;
//        timer = new Timer();
//    }

    public String getName(){
        return name;
    }

    public int getPersonId(){
        return personId;
    }

    @Override
    public boolean equals(Object otherMonster){
        if (!(otherMonster instanceof Monster)) {
            return false;
        } else {
            Monster newMonster = (Monster) otherMonster;
            return this.getName().equals(newMonster.getName()) &&
                    this.getPersonId() == newMonster.getPersonId();
        }
    }

    public void save() {
        try(Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO monsters (name, personId, birthday, type) VALUES (:name, :personId, now(), :type)";
            this.id = (int) con.createQuery(sql, true)
                    .addParameter("name", this.name)
                    .addParameter("personId", this.personId)
                    .addParameter("type", this.type)
                    .executeUpdate()
                    .getKey();
        }
    }

    public int getId(){
        return id;
    }

    public int getPlayLevel(){
        return playLevel;
    }

    public int getSleepLevel(){
        return sleepLevel;
    }

    public int getFoodLevel(){
        return foodLevel;
    }

    public boolean isAlive() {
        if (foodLevel <= MIN_ALL_LEVELS ||
                playLevel <= MIN_ALL_LEVELS ||
                sleepLevel <= MIN_ALL_LEVELS) {
            return false;
        }
        return true;
    }

    public void depleteLevels(){
        if (isAlive()) {
            playLevel--;
            foodLevel--;
            sleepLevel--;
        }
    }

    public void play(){
        if (playLevel >= MAX_PLAY_LEVEL){
            throw new UnsupportedOperationException("You cannot play with monster anymore!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastplayed = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        playLevel++;
    }

    public void sleep(){
        if (sleepLevel >= MAX_SLEEP_LEVEL){
            throw new UnsupportedOperationException("You cannot make your monster sleep anymore!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastslept = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        sleepLevel++;
    }

    public void feed(){
        if (foodLevel >= MAX_FOOD_LEVEL){
            throw new UnsupportedOperationException("You cannot feed your monster anymore!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastate = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        foodLevel++;
    }

    public Timestamp getBirthday(){
        return birthday;
    }

    public Timestamp getLastSlept(){
        return lastSlept;
    }

    public Timestamp getLastAte(){
        return lastAte;
    }

    public Timestamp getLastPlayed(){
        return lastPlayed;
    }

    public void startTimer(){
        Monster currentMonster = this;
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                if (!currentMonster.isAlive()){
                    cancel();
                }
                depleteLevels();
            }
        };
        this.timer.schedule(timerTask, 0, 600);
    }

    public void delete() {
        try(Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM monsters WHERE id = :id;";
            con.createQuery(sql)
                    .addParameter("id", this.id)
                    .executeUpdate();
        }
    }
}