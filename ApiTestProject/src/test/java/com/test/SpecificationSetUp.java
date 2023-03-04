package com.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

public class SpecificationSetUp {

    public HashMap<RequestSpecification, ResponseSpecification> builders = new HashMap<>();
    public RequestSpecification reqSpec;
    public ResponseSpecification respSpec;

    //       JSON TO JSON base specification
    public HashMap<RequestSpecification, ResponseSpecification> getMapSpec(String accept,
                                                                           String content,
                                                                           String codeStatus) {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        ResponseSpecBuilder respBuilder = new ResponseSpecBuilder();
//----------------------------------------------------------------------------------------

        if (accept.equals("json")) {
            if (content.isEmpty()){
                builder.setAccept("application/json");
                builder.setContentType("application/json");
            }

            switch (content) {
                case "json" -> { // json_json
                    builder.setAccept("application/json");
                    builder.setContentType("application/json");
                    respBuilder.expectContentType("application/json");
                }
                case "xml" -> { // json_xml
                    builder.setAccept("application/xml");
                    builder.setContentType("application/json");
                    respBuilder.expectContentType("application/xml");
                }
                case "withApiKey" -> {
                    builder.setAccept("application/json");
                    builder.addHeader("api_key", "abcdef13234");
                    respBuilder.expectContentType("application/json");
                }
                case "app_form" -> {
                    builder.setAccept("application/json");
                    builder.setContentType("application/x-www-form-urlencoded");
                    respBuilder.expectContentType("application/json");
                }

                case "uploadingImg" -> {
                  String path = null;
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        String absolutePath = selectedFile.getAbsolutePath();
                        System.out.println("Selected file: " + absolutePath);
                        path = absolutePath;
                    }
                    assert path != null;
                    File fileToUpload = new File(path);
                    if (fileToUpload.exists()) {
//                        For now json is the only accepting format
                        builder.setAccept("application/json");
                        builder.setContentType("multipart/form-data");
                        builder.addFormParam("additionalMetadata", "uploadingTest");
                        builder.addMultiPart("file", fileToUpload, "image/jpg");

                        respBuilder.expectContentType("application/json");
                    } else {
                        System.out.println("File " + fileToUpload.getPath() + "was not found. Give proper path");
                    }
                }
            }
        } else if (accept.equals("xml")) {
            if (content.isEmpty()){
                builder.setAccept("application/xml");
                builder.setContentType("application/xml");
            }
            switch (content) {
                case "json" -> { // xml_json
                    builder.setAccept("application/json");
                    builder.setContentType("application/xml");
                    respBuilder.expectContentType("application/json");
                }
                case "xml" -> {  // xml_xml
                    builder.setAccept("application/xml");
                    builder.setContentType("application/xml");
                    respBuilder.expectContentType("application/xml");
                }
                case "withApiKey" -> {
                    builder.setAccept("application/xml");
                    builder.addHeader("api_key", "abcdef13234");
                    respBuilder.expectContentType("application/xml");
                }
                case "app_form" -> {
                    builder.setAccept("application/xml");
                    builder.setContentType("application/x-www-form-urlencoded");
                    respBuilder.expectContentType("application/xml");
                }
            }
        }

        switch (codeStatus) {
            case "positive" -> {
                respBuilder.expectStatusCode(200);
                System.out.println("Code 200");
            }
            case "negative" -> {
                respBuilder.expectStatusCode(404);
                System.out.println("Code 404");
            }
            case "negative_order" ->{
                respBuilder.expectStatusCode(400);
                System.out.println("Code 400");
            }
            case "negative_server" ->{
                respBuilder.expectStatusCode(500);
                System.out.println("Code 500");
            }
        }

//----------------------------------------------------------------------------------------
        reqSpec = builder.build();
        respSpec = respBuilder.build();
        builders.put(reqSpec, respSpec);
        return builders;
//----------------------------------------------------------------------------------------

    }
}
