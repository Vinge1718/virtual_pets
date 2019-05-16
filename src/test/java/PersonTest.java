import org.junit.*;
import static org.junit.Assert.*;

public class PersonTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();

    @Test
    public void person_instantiatesCorrectly_true() {
        Person testPerson = new Person("Henry", "[email protected]");
        assertEquals(true, testPerson instanceof Person);
    }

    @Test
    public void getName_personInstantiatesWithName_Henry() {
        Person testPerson = new Person("Henry", "[email protected]");
        assertEquals("Henry", testPerson.getName());
    }

    @Test
    public void getEmail_personInstantiatesWithEmail_String() {
        Person testPerson = new Person("Henry", "[email protected]");
        assertEquals("[email protected]", testPerson.getEmail());
    }

    @Test
    public void equals_returnsTrueIfNameAndEmailAreSame_true() {
        Person firstPerson = new Person("Henry", "[email protected]");
        Person anotherPerson = new Person("Henry", "[email protected]");
        assertTrue(firstPerson.equals(anotherPerson));
    }

    @Test
    public void save_insertsObjectIntoDatabase_Person() {
        Person testPerson = new Person("Henry", "[email protected]");
        testPerson.save();
        assertTrue(Person.all().get(0).equals(testPerson));
    }

    @Test
    public void all_returnsAllInstancesOfPerson_true() {
        Person firstPerson = new Person("Henry", "henry@henry.com");
        firstPerson.save();
        Person secondPerson = new Person("Harriet", "harriet@harriet.com");
        secondPerson.save();
        assertEquals(true, Person.all().get(0).equals(firstPerson));
        assertEquals(true, Person.all().get(1).equals(secondPerson));
    }

    @Test
    public void save_assignsIdToObject() {
        Person testPerson = new Person("Henry", "henry@henry.com");
        testPerson.save();
        Person savedPerson = Person.all().get(0);
        assertEquals(testPerson.getId(), savedPerson.getId());
    }

    @Test
    public void find_returnsPersonWithSameId_secondPerson() {
        Person firstPerson = new Person("Henry", "henry@henry.com");
        firstPerson.save();
        Person secondPerson = new Person("Harriet", "harriet@harriet.com");
        secondPerson.save();
        assertEquals(Person.find(secondPerson.getId()), secondPerson);
    }

}
