package com.example.chymchatapp;

//driver class to get and set the users and their information
public class Users {
    String profilepic;
    String mail;
    String username;
    String password;
    String userId;
    String lastMessage;
    String status;
    String cPassword;

    public Users(){}//required emptyconstructor for firebase
    public Users(String imageUri, String emaill, String namee, String passwordd, String id, String status, String cPasswordd){
        this.profilepic=imageUri;
        this.mail=emaill;
        this.username=namee;
        this.password=passwordd;
        this.userId=id;
        this.status=status;
        this.cPassword=cPasswordd;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {

        return userId!=null? userId : "";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getcPassword() {
        return cPassword;
    }

    public void setcPassword(String cPassword) {
        this.cPassword = cPassword;
    }
}
