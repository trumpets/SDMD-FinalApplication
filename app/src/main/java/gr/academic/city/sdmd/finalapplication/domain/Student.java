package gr.academic.city.sdmd.finalapplication.domain;

/**
 * Created by trumpets on 5/19/16.
 */
public class Student {

    public Long _id;

    public String firstName;
    public String lastName;
    public String age;

    /**
     * No args constructor for use in serialization
     */
    public Student() {

    }

    public Student(String firstName, String lastName, String age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    @Override
    public String toString() {
        // This is called by the ArrayAdapter class to get a textual description of the item
        return firstName + " " + lastName;
    }
}
