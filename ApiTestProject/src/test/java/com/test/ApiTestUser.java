package com.test;

import com.rest.pets.User;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.nio.charset.CoderResult;
import java.util.*;

import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.RestAssured.given;


public class ApiTestUser {
    public String userNameValid;
    public String userNameInvalid;
    public static HashMap<String, String> isUserLogged;

    @BeforeClass
    public void BasicSetUp() {
        RestAssured.baseURI = "https://petstore.swagger.io";
    }

    //    POST -------------------------------------------------------------------------------
    @Test(dataProvider = "PostUserFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testCreateUsersArray(String acceptedType,
                                     String contentType,
                                     String codeType) {
        System.out.println("testCreateUsersArray started: ");
        User[] userArray = {
                new User(0, "", "userGG", "lastGG", "user@gmail.com", "", "123-432-543", 0),
                new User(0, "", "userWW", "lastWW", "user2@gmail.com", "", "321-432-726", 0)
        };
        boolean condition = codeType.equals("positive");
        String[] loginUserData = {condition ? "user123" : "", condition ? "osiemjedynek" : ""};
        int[] digitalData = {condition ? 2 : -1, condition ? 5 : -5};

        for (User user : userArray) {
            user.setUsername(loginUserData[0]);
            user.setPassword(loginUserData[1]);
            user.setId(digitalData[0]);
            user.setUserStatus(digitalData[1]);
        }
        try {
            boolean isAnyInvalidUsers = Arrays.stream(userArray)
                    .anyMatch(User::isValid);
            isNotValid(isAnyInvalidUsers);
            SpecificationSetUp specBase = new SpecificationSetUp();
            HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response createUsersArrayResponse = given().
                        spec(entry.getKey()).
                        body(userArray).
                        expect().spec(entry.getValue()).
                        when().post("v2/user/createWithArray");
                System.out.println("Test Create UserArray response: " + createUsersArrayResponse.asPrettyString());
            }
        } catch (Exception e) {
            System.out.println("Error occurred. " + e.getMessage());
        }
    }

///---------------------------------------------------------------------------------------------------------------------

    @Test(priority = 5,
            dataProvider = "PostUserFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testCreateUsersList(String acceptedType,
                                    String contentType,
                                    String codeType) {
        System.out.println("testCreateUsersList started: ");

        ArrayList<User> usersList = new ArrayList<>(
                Arrays.asList(
                        new User(0, "", "userGG", "lastGG",
                                "user@gmail.com", "", "123-432-543", 0),
                        new User(0, "", "userWW", "lastWW",
                                "user2@gmail.com", "", "321-432-726", 0)
                ));
        boolean condition = codeType.equals("positive");
        String[] loginUserData = {condition ? "user123" : "", condition ? "qwaszx123" : ""};
        int[] digitalData = {condition ? 2 : -1, condition ? 5 : -5};
        for (User user : usersList) {
            user.setUsername(loginUserData[0]);
            user.setPassword(loginUserData[1]);
            user.setId(digitalData[0]);
            user.setUserStatus(digitalData[1]);
        }
        try {
            boolean isAnyInvalidUsers = usersList.stream().
                    anyMatch(User::isValid);
            isNotValid(isAnyInvalidUsers);
            SpecificationSetUp specBase = new SpecificationSetUp();
            HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response createUsersListResponse = given().
                        spec(entry.getKey()).
                        body(usersList).
                        expect().spec(entry.getValue()).
                        when().post("v2/user/createWithList");
                System.out.println("Test Create UserList response: " + createUsersListResponse.asPrettyString());
            }
        } catch (Exception e) {
            System.out.println("Error occurred. " + e.getMessage());
        }

    }
///---------------------------------------------------------------------------------------------------------------------

    @Test(priority = 5,
            dataProvider = "PostUserFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testCreateUser(String acceptedType,
                               String contentType,
                               String codeType) {
        System.out.println("testCreateUser started: ");

        boolean condition = codeType.equals("positive");
        userNameValid = "user123";
        userNameInvalid = "";
        User user = new User(0, "", "userGG", "lastGG",
                "user@gmail.com", "", "123-432-543", 1);
        user.setUsername(condition ? userNameValid : userNameInvalid);
        user.setId(condition ? 2 : -1);
        user.setPassword(condition ? "osiemjedynek" : "");
        user.setUserStatus(condition ? 5 : -5);
        try {
            boolean isUserValid = user.isValid();
            isNotValid(isUserValid);
            SpecificationSetUp specBase = new SpecificationSetUp();
            HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response createUserResponse = given().
                        spec(entry.getKey()).
                        body(user).
                        expect().spec(entry.getValue()).
                        when().post("v2/user");
                System.out.println("Test CreateUser response: " + createUserResponse.asPrettyString());
            }
        } catch (Exception e) {
            System.out.println("Error occurred. " + e.getMessage());

        }
    }
///---------------------------------------------------------------------------------------------------------------------

    @Test(priority = 4, dependsOnMethods = {"testCreateUser"},
            dataProvider = "OnlyAcceptFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testGetUserByName(String acceptedType,
                                  String contentType,
                                  String codeType) {
        System.out.println("testGetUserByName started: ");

        String username = (codeType.equals("positive")) ? "user123" : "userBadArgument";
        if (!codeType.equals("negative_order")) {
            SpecificationSetUp specBase = new SpecificationSetUp();
            HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response getUserByNameResponse = given().
                        spec(entry.getKey()).
                        expect().spec(entry.getValue()).
                        when().get("v2/user/" + username);
                System.out.println("Test GetUserByName response: " + getUserByNameResponse.asPrettyString());
                if (codeType.equals("positive")) {
                    switch (getUserByNameResponse.getStatusCode()) {
                        case 200 -> userNameValid = username;
                        case 404 -> userNameInvalid = username;
                    }
                    if (acceptedType.equals("json"))
                        getUserByNameResponse.then().body(matchesJsonSchemaInClasspath("json_user_validation.json"));
                    else // xml
                        getUserByNameResponse.then().body(matchesXsdInClasspath("xml_user_validation.xsd"));
                }
            }
        } else {
            System.out.println("Code type 400. Invalid output");
        }
    }

    ///---------------------------------------------------------------------------------------------------------------------
//                                       to create a given User  / to be sure that get name is invalid
    @Test(priority = 3, dependsOnMethods = {"testGetUserByName", "testCreateUser"},
            dataProvider = "GetLoginFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testGetUserByLogin(String acceptedType,
                                   String contentType,
                                   String codeType) {
        System.out.println("testGetUserByLogin started: ");

        String username = (codeType.equals("positive")) ? userNameValid : userNameInvalid,
                password = (codeType.equals("positive")) ? "osiemjedynek" : "";
        if (!codeType.equals("negative_order")) {
            try {
                if (username != null) {
                    SpecificationSetUp specBase = new SpecificationSetUp();
                    HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
                    for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                        Response getUserByLoginResponse = given().
                                spec(entry.getKey()).
                                expect().spec(entry.getValue()).
                                when().get("v2/user/login?username=" + username + "&password=" + password);
                        System.out.println("Test Get UserByLogin response: " + getUserByLoginResponse.asPrettyString());
                        if (getUserByLoginResponse.statusCode() == 200)
                            isUserLogged = new HashMap<>(Map.of(username, "logged"));
                    }
                }
                else InvalidUserException();
            } catch (Exception e) {
                System.out.println("Error occurred. " + e.getMessage());
            }
        }
        else System.out.println("Code type 400. Invalid User username or password");
    }
///---------------------------------------------------------------------------------------------------------------------

    @Test(priority = 1,
            dataProvider = "OnlyPositiveFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testGetUserByLogOut(String acceptedType,
                                    String contentType,
                                    String codeType) {
        System.out.println("testGetUserByLogOut started: ");

        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
        for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
            Response getUserByLogout = given().
                    spec(entry.getKey()).
                    expect().spec(entry.getValue()).
                    when().get("v2/user/logout");
            if (getUserByLogout.statusCode() == 200 && isUserLogged != null) {
                for (Map.Entry<String, String> user : isUserLogged.entrySet()) {
                    if (user.getValue().equals("logged")) {
                        isUserLogged.put(user.getKey(), "logged_out");
                        System.out.println("Test Get UserByLogOut response: " + getUserByLogout.asPrettyString());
                    }
                }
            } else {
                System.out.println("User cannot log out, because is not logged. \nCode 400");
            }
        }
    }

//    PUT-------------------------------------------------------------------------------

    //    @Test(dependsOnMethods = {"testGetUserByName"},
    @Test(dependsOnMethods = {"testGetUserByLogin", "testGetUserByName", "testCreateUser"},
            dataProvider = "UpdateUserFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testUpdateUserByName(String acceptedType,
                                     String contentType,
                                     String codeType) {
        System.out.println("testUpdatePetByName started: ");
        String UserName;
        if (codeType.equals("positive")) UserName = userNameValid; // User found  200
        else if (codeType.equals("negative")) UserName = userNameInvalid; // User not found
        else UserName = null;          // 400 -> invalid input

        User user = new User(12, UserName, "userGG", "lastGG",
                "user@gmail.com", "osiemjedynek", "123-432-543", 1);
        try {
            if (codeType.equals("negative_order"))
                InvalidUserException();
            else {
                if (isUserLogged.containsValue("logged") && codeType.equals("positive")){
                    SpecificationSetUp specBase = new SpecificationSetUp();
                    HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
                    for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                        Response updateUserResponse = given().
                                spec(entry.getKey()).
                                body(user).
                                expect().spec(entry.getValue()).
                                when().put("v2/user/" + UserName);
                        System.out.println("Test Update UseByName response: " + updateUserResponse.asPrettyString());
                    }
                }
                else { // 404
                    testGetUserByLogOut("json","json", "positive");
                    System.out.println("User is not logged. Cannot update user \nCode 404");
                }
            }
        } catch (Exception exception) {
            System.out.println("Error occurred. " + exception.getMessage());
        }
    }

    //       DELETE -------------------------------------------------------------------------------
    @Test(dependsOnMethods = {"testGetUserByLogin", "testCreateUser", "testGetUserByName"},
            dataProvider = "OnlyAcceptFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testDeleteUserByName(String acceptedType,
                                     String contentType,
                                     String codeType) {
        System.out.println("testDeleteUserByName started: ");
        String deletedUser = "testUser";

        if (!codeType.equals("negative_order")) {
            if (isUserLogged.containsValue("logged") && codeType.equals("positive")) {
                String responseBody = """
                        {
                          "code": 200,
                          "type": "unknown",
                          "message": "user123"
                        }""";
            } else {
                SpecificationSetUp specBase = new SpecificationSetUp();
                HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
                for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                    Response deletedUserByName = given().
                            spec(entry.getKey()).
                            expect().spec(entry.getValue()).
                            when().delete("v2/user/" + deletedUser);
                    System.out.println("Test Delete UserByName response: " + deletedUserByName.asPrettyString());
                }
            }
        } else {
            System.out.println("Error occurred");
        }

    }

    /// Useful Functions ---------------------------------------------------------------------------------------------------------------------

    public static void isNotValid(boolean userValidation) throws Exception {
        if (!userValidation)
            throw new Exception("Invalid User data. Status_Code (400) ");
    }

    public static void InvalidUserException() throws Exception {
        throw new Exception("Invalid User username. Status_Code (400) ");
    }

//    -------------------------------------------------------------------------------

}
