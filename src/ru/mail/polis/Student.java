package ru.mail.polis;

import java.time.LocalDate;

/**
 * Created by Nechaev Mikhail
 * Since 13/12/2017.
 */
public class Student extends CheckedOpenHashTableEntity {

    private static int counter = 0;

    //NotNullable поля
    private long id; //Уникальный идентификатор студента
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private int groupId; //Идентификатор группы в которой учится студент
    private int yearOfAdmission; //Год поступления
    //Nullable поля
    private String photoReference; //Ссылка на фотографию студента
    private String email;
    private String mobile; //Номер телефона

    public Student(String firstName, String lastName, Gender gender, LocalDate birthday, int groupId, int yearOfAdmission) {
        this.id = counter++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.groupId = groupId;
        this.yearOfAdmission = yearOfAdmission;
    }

    public Student(String firstName, String lastName, Gender gender, LocalDate birthday, int groupId,
                   int yearOfAdmission, String photoReference, String email, String mobile) {
        this(firstName, lastName, gender, birthday, groupId, yearOfAdmission);
        this.photoReference = photoReference;
        this.email = email;
        this.mobile = mobile;
    }

    @Override
    public int hashCode(int tableSize, int probId) throws IllegalArgumentException {
        if (tableSize < 0 || probId < 0) throw new IllegalArgumentException();

        return (Math.abs(hashCode(31) +
                probId * h2(hashCode2(57), tableSize)) % tableSize);
    }

    private int h2(int k, int tableSize) {
        return k % (tableSize - 1) + 1;
    }


    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getYearOfAdmission() {
        return yearOfAdmission;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        if (groupId != student.groupId) return false;
        if (yearOfAdmission != student.yearOfAdmission) return false;
        if (!firstName.equals(student.firstName)) return false;
        if (!lastName.equals(student.lastName)) return false;
        if (gender != student.gender) return false;
        if (!birthday.equals(student.birthday)) return false;
        if (photoReference != null ? !photoReference.equals(student.photoReference) : student.photoReference != null)
            return false;
        if (email != null ? !email.equals(student.email) : student.email != null) return false;
        return mobile != null ? mobile.equals(student.mobile) : student.mobile == null;
    }

    public int hashCode(int k) {
        int result = (int) (id ^ (id >>> k + 1));
        result = k * result + firstName.hashCode();
        result = k * result + lastName.hashCode();
        result = k * result + gender.hashCode();
        result = k * result + birthday.hashCode();
        result = k * result + groupId;
        result = k * result + yearOfAdmission;
        result = k * result + (photoReference != null ? photoReference.hashCode() : 0);
        result = k * result + (email != null ? email.hashCode() : 0);
        result = k * result + (mobile != null ? mobile.hashCode() : 0);
        return result;
    }

    public int hashCode2(int k) {
        int result = (int) (id >>> k + 1);
//        result = k * result + firstName.hashCode();
//        result = k * result + lastName.hashCode();
//        result = k * result + gender.hashCode();
//        result = k * result + birthday.hashCode();
//        result = k * result + groupId;
//        result = k * result + yearOfAdmission;
//        result = k * result + (photoReference != null ? photoReference.hashCode() : 0);
//        result = k * result + (email != null ? email.hashCode() : 0);
//        result = k * result + (mobile != null ? mobile.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", groupId=" + groupId +
                ", yearOfAdmission=" + yearOfAdmission +
                ", photoReference='" + photoReference + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }

    public enum Gender {
        MALE, FEMALE
    }
}
