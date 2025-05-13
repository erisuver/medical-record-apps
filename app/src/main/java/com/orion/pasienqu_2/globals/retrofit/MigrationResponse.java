package com.orion.pasienqu_2.globals.retrofit;

import com.google.gson.annotations.SerializedName;

public class MigrationResponse {
    @SerializedName("status")
    private String status;
//
//    @SerializedName("data")
//    private Data data;

    public String getStatus() {
        return status;
    }

//    public Data getData() {
//        return data;
//    }
//
//    public static class Data {
//        @SerializedName("message")
//        private String message;
//
//        @SerializedName("id")
//        private int id;
//
//        public String getMessage() {
//            return message;
//        }
//
//        public int getId() {
//            return id;
//        }
//    }
}
