package com.pan.drools.pojo;

/**
 * @author panjb
 */
public class Applicant {
    private String name;
    private int age;
    private boolean isValid = true;

    public Applicant() {
    }

    public Applicant(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
