package online.classes.model;

public class UserDetails {

    private String userName,userMobile,userEmail,userPassword,joiningDate,userLocation;

    public UserDetails() {

    }
    public UserDetails(String userName, String userLocation, String userMobile, String userEmail, String userPassword, String joiningDate) {
        this.userName = userName;
        this.userLocation = userLocation;
        this.userMobile = userMobile;
        this.userEmail = userEmail;
        this.userPassword=userPassword;
        this.joiningDate=joiningDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }



    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }
}
