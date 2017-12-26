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

       @Override

    public int hashCode(int tableSize, int attempt) throws IllegalArgumentException {
        if (attempt < 0 || attempt >= tableSize)
            throw new IllegalArgumentException();
        return (hash1(tableSize) + attempt*hash2(tableSize)) % tableSize;
    }

    public int hash1(int tableSize){
        return (Math.abs(this.hashCode()) % tableSize);
    }

    public int hash2(int tableSize){
        int temp = Math.abs(this.hashCode1()) % (tableSize-1);
        if(temp%2 == 1)
            return temp;
        return temp+1;
    }

    public enum  Gender {
        MALE, FEMALE
    }

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

    public long getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Gender getGender() {
        return this.gender;
    }

    public LocalDate getBirthday() {
        return this.birthday;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getYearOfAdmission() {
        return this.yearOfAdmission;
    }

    public String getPhotoReference() {
        return this.photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (this.id != student.id) return false;
        if (this.groupId != student.groupId) return false;
        if (this.yearOfAdmission != student.yearOfAdmission) return false;
        if (!this.firstName.equals(student.firstName)) return false;
        if (!this.lastName.equals(student.lastName)) return false;
        if (this.gender != student.gender) return false;
        if (!this.birthday.equals(student.birthday)) return false;
        if (this.photoReference != null ? !photoReference.equals(student.photoReference) : student.photoReference != null)
            return false;
        if (this.email != null ? !this.email.equals(student.email) : student.email != null) return false;
        return this.mobile != null ? this.mobile.equals(student.mobile) : student.mobile == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (this.id ^ (this.id >>> 32));
        result = 31 * result + this.firstName.hashCode();
        result = 31 * result + this.lastName.hashCode();
        result = 31 * result + this.gender.hashCode();
        result = 31 * result + this.birthday.hashCode();
        result = 31 * result + this.groupId;
        result = 31 * result + this.yearOfAdmission;
        result = 31 * result + (this.photoReference != null ? this.photoReference.hashCode() : 0);
        result = 31 * result + (this.email != null ? this.email.hashCode() : 0);
        result = 31 * result + (this.mobile != null ? this.mobile.hashCode() : 0);
        return result;
    }

    public int hashCode1() {
        int result;
        result = (this.mobile != null ? this.mobile.hashCode() : 0);
        result = 31 * result + (this.email != null ? this.email.hashCode() : 0);
        result = 31 * result + (this.photoReference != null ? this.photoReference.hashCode() : 0);
        result = 31 * result + this.yearOfAdmission;
        result = 31 * result + this.groupId;
        result = 31 * result + this.birthday.hashCode();
        result = 31 * result + this.gender.hashCode();
        result = 31 * result + this.lastName.hashCode();
        result = 31 * result + this.firstName.hashCode();
        result = 31 * result + (int) (this.id ^ (this.id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + this.id +
                ", firstName='" + this.firstName + '\'' +
                ", lastName='" + this.lastName + '\'' +
                ", gender=" + this.gender +
                ", birthday=" + this.birthday +
                ", groupId=" + this.groupId +
                ", yearOfAdmission=" + this.yearOfAdmission +
                ", photoReference='" + this.photoReference + '\'' +
                ", email='" + this.email + '\'' +
                ", mobile='" + this.mobile + '\'' +
                '}';
    }
}