package com.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;

public class ApiTestStore {

    @BeforeClass
    public void BasicSetUp() {
        RestAssured.baseURI = "https://petstore.swagger.io";
    }

    @Test(dataProvider = "PostOrderFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testCreatingPetStoreOrder(String accept,
                                          String contentType, String codeType) {
        System.out.println("testCreatingPetStoreOrder started: ");

        long petID = 8908918220980319000L;
        String complitedStatus = (codeType.equals("positive")) ? "true" : "bad_argument",
                petOrderBody = "{\"id\": 3, \"" + petID + "\": 1, \"quantity\": 1," +
                        " \"shipDate\": \"2023-03-01T00:00:00.000Z\", \"status\": \"placed\", \"complete\": " + complitedStatus + "}";

        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(accept, contentType, codeType);
        for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
            Response createNewPetStoreOrderResp = given().
                    spec(entry.getKey()).body(petOrderBody)
                    .expect().spec(entry.getValue())
                    .when().post("v2/store/order");
            System.out.println("From " + createNewPetStoreOrderResp +
                    "Json response: " + createNewPetStoreOrderResp.asPrettyString());
            if (codeType.equals("positive"))
                if (contentType.equals("json"))
                    createNewPetStoreOrderResp.then().
                            body(matchesJsonSchemaInClasspath("json_json_order_valid.json"));
                else
                    createNewPetStoreOrderResp.then().
                            body(matchesXsdInClasspath("json_xml_order_valid.xsd"));
        }
    }


    @Test(dataProvider = "OnlyAcceptFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testGettingPetStoreOrderByID(String accept,
                                             String contentType, String codeType) {
        System.out.println("testGettingPetStoreOrderByID started: ");

        int petID = 0;
        if (codeType.equals("positive")) petID = 3;
        else if (codeType.equals("negative")) petID = 5;
        else petID = 12;
        try {
            int validPetID = getProperId(petID);
            SpecificationSetUp specBase = new SpecificationSetUp();
            HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(accept, contentType, codeType);
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response getPetStoreOrderByIdResp = given().
                        spec(entry.getKey())
                        .expect().spec(entry.getValue())
                        .when().get("v2/store/order/" + validPetID);
                System.out.println("Test Get PetStoreOrderById response: "+ getPetStoreOrderByIdResp.asPrettyString());
                if (codeType.equals("positive"))
                    if (contentType.equals("json"))
                        getPetStoreOrderByIdResp.then().body(matchesJsonSchemaInClasspath("json_json_order_valid.json"));
                    else
                        getPetStoreOrderByIdResp.then().body(matchesXsdInClasspath("json_xml_order_valid.xsd"));
            }
        } catch (Exception exception) {
            System.out.println("An error occurred: " + exception.getMessage());
        }
    }

    public static int getProperId(int petID) throws Exception {
        if (petID > 10) {
            throw new Exception("Numbers bigger than 10 are not valid");
        }
        return petID;
    }

    @Test(dataProvider = "OnlyAcceptFormatAndScenarioProvider",
            dataProviderClass = StatusDataProvider.class)
    public void testDeletingPetStoreOrderByID(String accept,
                                              String contentType,
                                              String codeType) {
        System.out.println("testDeletingPetStoreOrderByID started: ");

        int petID = 0;
        if (codeType.equals("positive")) petID = 3;
        else if (codeType.equals("negative")) petID = 5;
        else petID = 12;

        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(accept, contentType, codeType);
        try {
            int validPetID = getProperId(petID);
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response deletePetStoreOrderByIDresp = given().
                        spec(entry.getKey())
                        .expect().spec(entry.getValue())
                        .when().get("v2/store/order/" + validPetID);
                System.out.println("Test Delete PetStoreOrderById response: "+deletePetStoreOrderByIDresp.asPrettyString());
                if (codeType.equals("positive"))
                    if (contentType.equals("json"))
                        deletePetStoreOrderByIDresp.then().body(matchesJsonSchemaInClasspath("json_json_order_valid.json"));
                    else  // xml
                        deletePetStoreOrderByIDresp.then().body(matchesXsdInClasspath("json_xml_order_valid.xsd"));
            }
        } catch (Exception exception) {
            System.out.println("An error occurred: " + exception.getMessage());
        }
    }

    @Test
    public void testGettingStoreInventory() {
        System.out.println("testGettingStoreInventory started: ");
        Response getPetStoreInventoryResp = given().
                header("Accept", "application/json").
                when().get("v2/store/inventory");
        System.out.println("Test Get PetStore Inventory response: "+getPetStoreInventoryResp.asPrettyString());
        assertEquals(getPetStoreInventoryResp.getContentType(), "application/json");
        assertEquals(getPetStoreInventoryResp.getStatusCode(), 200);
    }


}
