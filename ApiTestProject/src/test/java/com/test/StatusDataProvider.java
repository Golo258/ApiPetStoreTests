package com.test;

import org.testng.annotations.DataProvider;


public class StatusDataProvider {


    @DataProvider(name = "PostFormatAndScenarioProvider")
    public static Object[][] getSpecificationTypes() {
        return new Object[][]{
                {"json", "json", "positive"},
                {"json", "xml", "positive"},
                {"xml", "xml", "positive"},
                {"xml", "json", "positive"},

                {"json", "json", "negative"},
                {"json", "xml", "negative"},
                {"xml", "xml", "negative"},
                {"xml", "json", "negative"}
        };
    }

    @DataProvider(name = "GetStatusFormatAndScenarioProvider")
    public static Object[][] getStatusAndAppTypes() {
        String[] types = {"json", "xml"};

        return new Object[][]{
                {"available", types, "withApiKey", "positive"},
                {"pending", types, "withApiKey", "positive"},
                {"sold", types, "withApiKey", "positive"},
                {"not_available", types, "withApiKey", "status_negative"},
                {"not_pending", types, "withApiKey", "status_negative"},
                {"not_sold", types, "withApiKey", "status_negative"}
        };
    }


    @DataProvider(name = "PostParamsFormatAndScenarioProvider")
    public static Object[][] getPostParamsSpecificationTypes() {

        return new Object[][]{
                {"json", "app_form", "positive"},
                {"xml", "app_form", "positive"},
                {"json", "app_form", "negative"},
                {"xml", "app_form", "negative"}

        };
    }

    @DataProvider(name = "PostPetUploadFormatAndScenarioProvider")
    public static Object[][] getPostByUploadingImageSpecificationTypes() {
        return new Object[][]{
                {"json", "uploadingImg", "positive"},
                {"json", "uploadingImg", "negative"}
        };
    }
    @DataProvider(name = "OnlyAcceptFormatAndScenarioProvider")
    public static Object[][] getPostUsers() {
        String emptyStr = "";
        return new Object[][]{
                //accept/ content / codeType
                {"json", emptyStr, "positive"},
                {"xml",  emptyStr, "positive"}, // 200
                {"json",  emptyStr, "negative"},
                {"xml",  emptyStr, "negative"}, // 404
                {"json",  emptyStr, "negative_order"}, // 400
                {"xml",  emptyStr, "negative_order"},
        };
    }
    @DataProvider(name = "GetLoginFormatAndScenarioProvider")
    public static Object[][] getGetLogin() {
        String emptyStr = "";
        return new Object[][]{
                //accept/ content / codeType
                {"json", emptyStr, "positive"},
                {"xml",  emptyStr, "positive"}, // 200
                {"json",  emptyStr, "negative_order"}, // 400
                {"xml",  emptyStr, "negative_order"},
        };
    }
    @DataProvider(name = "PostUserFormatAndScenarioProvider")
    public static Object[][] getGetPetStoreOrderById() {
        String emptyStr = "";
        return new Object[][]{
                //accept/ content / codeType
                {"json", "json", "positive"},
                {"json",  "xml", "positive"},
                {"json",  emptyStr, "negative_order"},
                {"xml",  emptyStr, "negative_order"}
        };
    }
    @DataProvider(name = "OnlyPositiveFormatAndScenarioProvider")
    public static Object[][] getGetUserLogout() {
        return new Object[][]{
                {"json", "json", "positive"},
                {"json",  "xml", "positive"},
        };
    }
    @DataProvider(name = "PostOrderFormatAndScenarioProvider")
    public static Object[][] getPostPetStoreOrder() {
        return new Object[][]{
                {"json", "json", "positive"},
                {"json", "xml", "positive"},
                {"json", "json", "negative_order"},
                {"json", "xml", "negative_order"}

        };
    }
    @DataProvider(name = "UpdateUserFormatAndScenarioProvider")
    public static Object[][] getPutUserSpecification() {
        return new Object[][]{
                {"json", "json", "positive"},
                {"json", "xml", "positive"},
                {"json", "json", "negative"},
                {"json", "xml", "negative"},
                {"json", "json", "negative_order"},
                {"json", "xml", "negative_order"}

        };
    }
}
