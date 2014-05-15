package h2app;

public class Customer {
    private long id;
    private String firstName, lastName;
    private boolean valid;


    public Customer(long id, String firstName, String lastName, boolean valid) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.valid = valid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", valid=" + valid +
                '}';
    }
}

