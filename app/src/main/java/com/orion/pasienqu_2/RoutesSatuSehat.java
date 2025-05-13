package com.orion.pasienqu_2;

public class RoutesSatuSehat {

    /** SANDBOX ** || Buka baris dibawah ini jika ingin memakai mode sandbox*/
/*
    public static String AUTH_URL = "https://api-satusehat-stg.dto.kemkes.go.id/oauth2/v1";
    public static String BASE_URL = "https://api-satusehat-stg.dto.kemkes.go.id/fhir-r4/v1";
    public static String CONSENT_URL = "https://api-satusehat-stg.dto.kemkes.go.id/fhir-r4/v1";
    public static String CLIENT_ID = "TprEOjYvaPyqFALl8Yu0s5QhbL7qgVoctNmDIKXTZRnbBSx1";
    public static String CLIENT_SECRET = "B3mW8uu4kkgSXHZYsLsaj49CnuFwLMp67pK8wi9gryZXTzpr3wiLGexK5XABJym3";
    public static String GENERATE_TOKEN_URL = AUTH_URL+"/accesstoken?grant_type=client_credentials";
*/
    /** PRODUCTION ** || Buka baris dibawah ini jika ingin memakai mode production*/
    public static String AUTH_URL = "https://api-satusehat.kemkes.go.id/oauth2/v1";
    public static String BASE_URL = "https://api-satusehat.kemkes.go.id/fhir-r4/v1";
    public static String CONSENT_URL = "https://api-satusehat.dto.kemkes.go.id/consent/v1";
    public static String CLIENT_ID = "F5wgeGSaffYWJnJnJ84RfA9isEXVQTIDW5esg8rewdl70DdO";
    public static String CLIENT_SECRET = "Wp2npxP4QtjWA6n5xgepZS0upMZ47AqGv2WppgorxQPkbp1HUoa6i79kTneXx2MU";
    public static String GENERATE_TOKEN_URL = AUTH_URL+"/accesstoken?grant_type=client_credentials";


    public static String url_practitioner = BASE_URL+"/Practitioner";
    public static String url_organization = BASE_URL+"/Organization";
    public static String url_location = BASE_URL+"/Location";
    public static String url_patient = BASE_URL+"/Patient";
    public static String url_encounter = BASE_URL+"/Encounter";
    public static String url_condition = BASE_URL+"/Condition";

}
