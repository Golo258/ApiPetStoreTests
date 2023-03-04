package com.test;

//import org.junit.Test;

import java.util.*;

import com.rest.pets.Category;
import com.rest.pets.PetsData;
import com.rest.pets.Tag;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath; // to validate json
import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath; // to validate xml


public class ApiTestPet {
    public static int indexCounter;
    public static int deletePetIndex;

    @BeforeClass
    public void BasicSetUp() {
        RestAssured.baseURI = "https://petstore.swagger.io";
    }
//POST /PET---------------------------------------------------------------------------------------------------

    @Test(priority = 5, dataProvider = "PostFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testCreatePet(String accept,
                              String contentType,
                              String codeType) {
        System.out.println("TestCreatePet started: ");
        long[] petAvailableIds = {123, 234, 345, 456, 567, 678, 789, 890};
        long petID = 0;
        String path = (codeType.equals("positive")) ? "/pet" : "/bad_argument";
        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(accept, contentType, codeType);

        if (accept.equals("json")) {
            petID = petAvailableIds[indexCounter++];
            Tag firstTag = new Tag(990, "cutest_animals"),
                    secondTag = new Tag(1040, "funniest_animals");
            List<Tag> tags = new ArrayList<>();
            tags.add(firstTag);
            List<String> photos = new ArrayList<>();//        urls
            photos.add("http://FunnyDoggies.png");
            PetsData petDataJsonBody = new PetsData(petID, "GoodDoggyBoy",
                    new Category("Dogs", 5) , photos, tags, "available");


            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response createNewPetJsonResponse = given().
                        spec(entry.getKey()).body(petDataJsonBody)
                        .expect().spec(entry.getValue())
                        .when().post("v2" + path);
                System.out.println("From " + petDataJsonBody +
                        " Json response: " + createNewPetJsonResponse.asPrettyString());

                System.out.println("Code status: " + createNewPetJsonResponse.getStatusCode());
                // respond validation
                if (codeType.equals("positive"))
                    if (contentType.equals("json"))
                        createNewPetJsonResponse.then().body(matchesJsonSchemaInClasspath("json_json_Valid.json"));
                    else // xml
                        createNewPetJsonResponse.then().body(matchesXsdInClasspath("json_xml_valid.xsd"));
            }
        } else if (accept.equals("xml")) {
            petID = petAvailableIds[indexCounter++];
            String responseBodyXML = """
                    <Pet>
                        <id>""" + petID + """
                         <category><id>1523612</id> <name>Dogs</name></category></id><name>GoodBoy1234</name><photoUrls>
                         <photoUrl>https://good_dog123.png</photoUrl></photoUrls><status>available</status>
                         <tags><tag><id>321</id><name>BigOnes</name></tag></tags>
                    </Pet>
                    """;
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response createNewPetXMLResponse = given().
                        spec(entry.getKey()).body(responseBodyXML)
                        .expect().spec(entry.getValue())
                        .when().post("v2" + path);
                System.out.println("From " + responseBodyXML +
                        "\nJson response: " + createNewPetXMLResponse.asPrettyString() +
                        "\nResponse code status:" + createNewPetXMLResponse.getStatusCode());
                if (codeType.equals("positive"))
                    if (contentType.equals("json"))
                        createNewPetXMLResponse.then().body(matchesJsonSchemaInClasspath("xml_json_valid.json"));
                    else  // xml
                        createNewPetXMLResponse.then().body(matchesXsdInClasspath("xml_xml_validate.xsd"));
            }
        }

    }

    //POST /PET/ID ---------------------------------------------------------------------------------------------------
    @Test(dataProvider = "PostParamsFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testCreatePetById(String accept,
                                  String contentType,
                                  String codeType) {
        System.out.println("testCreatePetById started: ");

        long petId = (codeType.equals("positive")) ? 13 : -125126651;
        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(accept, contentType,
                codeType);
        for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
            Response createNewPetByIdResponse = given().
                    spec(entry.getKey()).
                    expect().spec(entry.getValue()).
                    when().post("v2/pet/" + petId);
            System.out.println("New pet response: " + createNewPetByIdResponse.asPrettyString());
        }
    }
//DELETE /PET/ID ---------------------------------------------------------------------------------------------------

    @Test(dependsOnMethods = {"testCreatePet"},
            dataProvider = "OnlyAcceptFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testDeletePetById(String acceptedType,
                                  String contentType,
                                  String codeType) {
        System.out.println("testDeletePetById started: ");
        long[] petAvailableIds = {123, 234, 345, 456, 567, 678, 789, 890};

        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType,
                codeType);
        long PetID = petAvailableIds[deletePetIndex++];
        for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
            if (codeType.equals("negative")) PetID = -512;
            else if (codeType.equals("negative_order")) {
                System.out.println("Invalid ID supplied error");
                break;
            }
            System.out.println("Pet id : " + PetID);
            Response deletePetResponse = given().spec(entry.getKey())
                    .expect().spec(entry.getValue())
                    .when().delete("v2/pet/" + PetID);
            System.out.println((codeType.equals("positive")) ? deletePetResponse.asPrettyString() : "Response status is 404");
        }
    }

    //POST /PET/ID/UPLOAD_IMG ---------------------------------------------------------------------------------------------------
    @Test(priority = 0,
            dataProvider = "PostPetUploadFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testCreatePetByUploadedImg(String accept,
                                           String contentType,
                                           String codeType) {
        System.out.println("testCreatePetByUploadedImg started: ");

        long petID = (codeType.equals("positive")) ? 13L : -5123;
        String Path = (codeType.equals("positive")) ? "pet" : "bad_path2";
        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(accept, contentType,
                codeType);
        for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
            Response createNewPetByUploadImgResp = given().
                    spec(entry.getKey()).
                    expect().spec(entry.getValue()).
                    when().post("v2/" + Path + "/" + petID + "/uploadImage");
            System.out.println("Responde view: " + createNewPetByUploadImgResp.asPrettyString());
        }
    }

//PUT /PET ---------------------------------------------------------------------------------------------------
    @Test(priority = 4, dataProvider = "PostFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testUpdatePet(String accept,
                              String contentType,
                              String codeType) {
        System.out.println("testUpdatePet started: ");
        long petID = (codeType.equals("positive")) ? 13 : -125126651;

        SpecificationSetUp specBase = new SpecificationSetUp();
        HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(accept, contentType, codeType);
        try {
            long properId = getProperId(petID);
            String responseBodyJSON = "{ \"id\" : " + properId + ", \"category\" : {\"id\":1523612, \"name\": \"Dogs\" }," +
                    "\"name\" : \"GoodBoy1234\", \"photoUrls\" : [\"https://good_dog123.png\"]," +
                    "\"tags\" : [{\"id\" : 321,\"name\": \"BigOnes\"}], \"status\":\"available\" }";
            if (accept.equals("json")) {
                for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                    Response updatePetJsonResp = given().
                            spec(entry.getKey()).body(responseBodyJSON)
                            .expect().spec(entry.getValue()).
                            when().put("v2/pet");
                    System.out.println("From: " + responseBodyJSON +
                            " Json response: " + updatePetJsonResp.asPrettyString());

                    if (codeType.equals("positive"))
                        if (contentType.equals("json"))
                            updatePetJsonResp.then().body(matchesJsonSchemaInClasspath("json_json_Valid.json"));
                        else // xml
                            updatePetJsonResp.then().body(matchesXsdInClasspath("json_xml_valid.xsd"));
                }
            } else if (accept.equals("xml")) {
                String responseBodyXML = """
                        <Pet>
                             <id>""" + properId + """
                             <category> <id>1523612</id> <name>Dogs</name></category></id><name>GoodBoy1234</name><photoUrls>
                             <photoUrl>https://good_dog123.png</photoUrl></photoUrls><status> available</status> <tags><tag><id>321</id><name>BigOnes</name></tag> </tags>
                        </Pet>""";
                for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                    Response updatePetXmlResp = given().
                            spec(entry.getKey()).body(responseBodyXML)
                            .expect().spec(entry.getValue()).
                            when().put("v2/pet");
                    System.out.println("From " + responseBodyXML +
                            " Json response: " + updatePetXmlResp.asPrettyString());
                    System.out.println(updatePetXmlResp.asPrettyString());
                    if (codeType.equals("positive"))
                        if (contentType.equals("json"))
                            updatePetXmlResp.then().body(matchesJsonSchemaInClasspath("xml_json_valid.json"));
                        else  // xml
                            updatePetXmlResp.then().body(matchesXsdInClasspath("xml_xml_validate.xsd"));
                }
            }
        } catch (Exception exception) {
            System.out.println("An error occurred: " + exception.getMessage());
        }
    }
    public static long getProperId(long petID) throws Exception {
        if (petID < 0) {
            throw new Exception("Negative numbers are not valid");
        }
        return petID;
    }
//GET /PET/STATUS ---------------------------------------------------------------------------------------------------

    @Test(dataProvider = "GetStatusFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testGetPetByStatus(String status, String[] acceptedTypes,
                                   String contentType, String codeType) {
        System.out.println("testGetPetByStatus started: ");

        for (String app_type : acceptedTypes) {
            System.out.println("Data from app:" + app_type);
            try {
                SpecificationSetUp specBase = new SpecificationSetUp();
                HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(app_type,
                        contentType, codeType);
                for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                    Response getPetByStatusResp = given().
                            spec(entry.getKey()).
                            expect().spec(entry.getValue()).
                            when().get("v2/pet/findByStatus?status=" + status);
                    String propStatus = getProperStatus(status);
                    System.out.println("Test status response:" + getPetByStatusResp.asPrettyString()); // If you want to show results | A lot of Data
                }
            } catch (Exception exception) {
                System.out.println("An error occurred: " + exception.getMessage());
            }
        }
    }

    public String getProperStatus(String status) throws Exception {
        List<String> possibleStatus = new ArrayList<>(Arrays.asList("available", "pending", "sold"));
        if (!possibleStatus.contains(status)) {
            throw new Exception("Status value:" + status + " is not valid");
        }
        return status;
    }
//GET /PET/ID ---------------------------------------------------------------------------------------------------

    @Test(dataProvider = "OnlyAcceptFormatAndScenarioProvider", dataProviderClass = StatusDataProvider.class)
    public void testGetPetById(String acceptedType,
                               String contentType,
                               String codeType) {
        System.out.println("testGetPetById started: ");
        long petID = (codeType.equals("positive")) ? 13 : -125126651;

        if (!codeType.equals("negative_order")) {
            SpecificationSetUp specBase = new SpecificationSetUp();
            HashMap<RequestSpecification, ResponseSpecification> specProvider = specBase.getMapSpec(acceptedType, contentType, codeType);
            for (Map.Entry<RequestSpecification, ResponseSpecification> entry : specProvider.entrySet()) {
                Response getPetByIdResp = given().spec(entry.getKey()).
                        expect().spec(entry.getValue()).
                        when().get("v2/pet/" + petID);
                System.out.println("Test Get Pet by id " + petID +
                        "response: " + getPetByIdResp.asPrettyString());
            }
        } else {
            System.out.println("Code type 400. Invalid output");
        }
    }
//END OF TESTING FILE--------------------------------------------------
}