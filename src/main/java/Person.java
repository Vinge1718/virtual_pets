import org.sql2o.Connection;

import java.util.ArrayList;
import java.util.List;

public class Person implements DatabaseManagement{
    private String name;
    private String email;
    private int id;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object otherPerson){
        if (!(otherPerson instanceof Person)) {
            return false;
        } else {
            Person newPerson = (Person) otherPerson;
            return this.getName().equals(newPerson.getName()) &&
                    this.getEmail().equals(newPerson.getEmail());
        }
    }

    @Override
    public void save() {
        try(Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO persons (name, email) VALUES (:name, :email)";
            this.id = (int) con.createQuery(sql, true)
                    .addParameter("name", this.name)
                    .addParameter("email", this.email)
                    .executeUpdate()
                    .getKey();
        }
    }

    public static List<Person> all() {
        String sql = "SELECT * FROM persons";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(Person.class);
        }
    }

    public int getId() {
        return id;
    }

    public static Person find(int id) {
        try(Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM persons where id=:id";
            Person person = con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Person.class);
            return person;
        }
    }

    public List<Object> getMonsters() {
        List<Object> allMonsters = new ArrayList<Object>();

        try(Connection con = DB.sql2o.open()) {
            String sqlFire = "SELECT * FROM monsters WHERE personId=:id AND type='fire';";
            List<FireMonster> fireMonsters = con.createQuery(sqlFire)
                    .addParameter("id", this.id)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(FireMonster.class);
            allMonsters.addAll(fireMonsters);

            String sqlWater = "SELECT * FROM monsters WHERE personId=:id AND type='water';";
            List<WaterMonster> waterMonsters = con.createQuery(sqlWater)
                    .addParameter("id", this.id)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(WaterMonster.class);
            allMonsters.addAll(waterMonsters);
        }

        return allMonsters;
    }

    public List<Community> getCommunities() {
        try(Connection con = DB.sql2o.open()){
            String joinQuery = "SELECT community_id FROM communities_persons WHERE person_id = :person_id";
            List<Integer> communityIds = con.createQuery(joinQuery)
                    .addParameter("person_id", this.getId())
                    .executeAndFetch(Integer.class);

            List<Community> communities = new ArrayList<Community>();

            for (Integer communityId : communityIds) {
                String communityQuery = "SELECT * FROM communities WHERE id = :communityId";
                Community community = con.createQuery(communityQuery)
                        .addParameter("communityId", communityId)
                        .executeAndFetchFirst(Community.class);
                communities.add(community);
            }
            return communities;
        }
    }

    public void leaveCommunity(Community community){
        try(Connection con = DB.sql2o.open()){
            String joinRemovalQuery = "DELETE FROM communities_persons WHERE community_id = :communityId AND person_id = :personId;";
            con.createQuery(joinRemovalQuery)
                    .addParameter("communityId", community.getId())
                    .addParameter("personId", this.getId())
                    .executeUpdate();
        }
    }

    @Override
    public void delete() {
        try(Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM persons WHERE id = :id;";
            con.createQuery(sql)
                    .addParameter("id", this.id)
                    .executeUpdate();
            String joinDeleteQuery = "DELETE FROM communities_persons WHERE person_id = :personId";
            con.createQuery(joinDeleteQuery)
                    .addParameter("personId", this.getId())
                    .executeUpdate();
        }
    }

}