package com.orion.pasienqu_2.utility;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("message")
        private String message;

        public String getMessage() {
            return message;
        }
    }
}
