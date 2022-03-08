package com.example.skylar.model;

public  class UserInformation {

    String email, name, password, number, accountId;


    public UserInformation() {
    }

    public UserInformation(String email, String name, String password, String number, String accountId) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.number = number;
        this.accountId = accountId;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


}


