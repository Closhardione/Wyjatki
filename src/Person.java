import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Person implements Serializable {
    private String name;
    private LocalDate birth, death;
    private Person parents[] = new Person[2];

    public Person(String name, LocalDate birth) {
        this(name, birth, null);
    }

    public Person(String name, LocalDate birth, LocalDate death) {
        this.name = name;
        this.birth = birth;
        this.death = death;
        try {
            if (birth.isAfter(death)) {
                throw new NegativeLifespanException(birth, death, "Possible time-space loophole.");
            }
        } catch (NullPointerException e) {}
    }

    public Person(String name, LocalDate birth, LocalDate death, Person parent1, Person parent2) throws IncestException, ParentingAgeException {
        this(name, birth, death);
        parents[0] = parent1;
        Duration diff = Duration.between(parent1.death,parent1.birth);
        int years = (int)(diff.toDays()/365);
        if(years>15||years<50){
            throw new ParentingAgeException("Age exeption, age: "+years);
        }
        parents[1] = parent2;

        checkForIncest();
    }

    public Person(String name, LocalDate birth, Person parent1, Person parent2) throws IncestException {
        this(name, birth, null, parent1, parent2);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", birth=" + birth +
                ", death=" + death +
                ", parents=" + Arrays.toString(parents) +
                '}';
    }

    void checkForIncest() throws IncestException {
        if(parents[0] == null || parents[1] == null)
            return;
        for(var leftSideParent : parents[0].parents) {
            if (leftSideParent == null) continue;
            for (var rightSideParent : parents[1].parents) {
                if (rightSideParent == null) continue;
                if (leftSideParent == rightSideParent)
                    throw new IncestException(leftSideParent, this);
            }
        }
    }
    public static List<TemportaryPerson> people = new ArrayList<>();
    public static Person getPersonFromFile(String path) throws FileNotFoundException, AmbigiousPersonException, IncestException {
        File file = new File(path);
        Scanner scanner = null;
        scanner = new Scanner(file);
        String nameAndLastName = scanner.nextLine();
        LocalDate birthdayDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalDate deathDate = null;
        Person firstPerson=null, secondPerson = null;
        if(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(!"Rodzice:".equals(line)){
                deathDate = LocalDate.parse(line, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
            if(scanner.hasNextLine()){
                scanner.nextLine();
            }
            if(scanner.hasNextLine()){
                String firstPersonName = scanner.nextLine();
                firstPerson = findPerson(firstPersonName);
                if(scanner.hasNextLine()){
                    String secondPersonName = scanner.nextLine();
                    if(secondPersonName !=null){
                        secondPerson = findPerson(secondPersonName);
                    }
                }
            }
        }
        for(var person :people){
            if(person.person.name.compareTo(nameAndLastName)==0){
                throw new AmbigiousPersonException(person.person.name,path, person.path);
            }
        }
        Person person = new Person(nameAndLastName,birthdayDate,deathDate,firstPerson,secondPerson);
        people.add(new TemportaryPerson(person,path));
        return person;
    }
    private static Person findPerson(String name){
        for(TemportaryPerson person : people){
            if(person.person.name.equals(name)){
                return person.person;
            }
        }
        return null;
    }
    public static List<Person> checkFamilyConnections(List<String> paths) throws FileNotFoundException, AmbigiousPersonException, IncestException {
        List<Person> people = new ArrayList<>();
        for(String path : paths){
            people.add(getPersonFromFile(path));
        }
        return people;
    }
}
