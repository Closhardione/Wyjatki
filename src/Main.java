import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void save(String path) {
        List<Person> people = new ArrayList<>();
        Person janusz = new Person("Janusz",
                LocalDate.of(1975, 1, 1));
        people.add(janusz);

        Person grazyna = new Person("Grażyna",
                LocalDate.of(1975, 1, 1));
        people.add(grazyna);

        Person krystyna = new Person("Krystyna",
                LocalDate.of(1975, 1, 1));
        people.add(krystyna);

//            Person seba = new Person("Seba",
//                    LocalDate.of(1990, 1, 1),
//                    janusz, grazyna);
//            people.add(seba);

        Person edzio = new Person("Edzio",
                LocalDate.of(1975, 1, 1));
        people.add(edzio);

//            Person karyna = new Person("Karyna",
//                    LocalDate.of(1995, 1, 1),
//                    edzio, krystyna);
//            people.add(karyna);
//
//            Person brajan = new Person("Brajan",
//                    LocalDate.of(2011, 1, 1),
//                    seba, karyna);
//            people.add(brajan);

        // System.out.println(brajan);

        try {
            FileOutputStream stream = new FileOutputStream(path);
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(people);
            objectStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void load(String path) {
        List<Person> people;
        try {
            FileInputStream file = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(file);

            people = (List<Person>) in.readObject();

            System.out.println(people);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Person filePerson = null;
        try {
            filePerson = Person.getPersonFromFile("test/test_same_osoby/Alicja Stefanek.txt");
            filePerson = Person.getPersonFromFile("test/test_same_osoby/Alicja Stefanek2.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (AmbigiousPersonException e) {
            System.out.println(e.conflictPath1);
            System.out.println(e.conflictPath2);
        } catch (IncestException e) {
            throw new RuntimeException(e);
        } catch (ParentingAgeException e) {
            Scanner answear = new Scanner(System.in);
            System.out.println("Wrong data, proceed? (Y/N)");

            if(answear.nextLine()!="Y"){
                throw new RuntimeException(e);
            }
            else{
                System.out.println("Proceeded!");
            }

        } catch (UndefinedPersonReferenceException e) {
            throw new RuntimeException(e);
        }
        System.out.println(filePerson.toString());
    }
}