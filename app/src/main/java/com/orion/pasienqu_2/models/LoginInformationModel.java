package com.orion.pasienqu_2.models;

public class LoginInformationModel {
    int uid;
    int companyId;
    String username;
    String database;
    String tz;
    String host;
    String name;
    String sessionId;
    String archivedUntil;
    String uuidLocation;
    String cookie;
    String password;
    boolean usePin;
    String pinProtection;
    boolean autoGeneratePatientId;
    boolean is_subuser;
    String language;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isUsePin() {
        return usePin;
    }

    public void setUsePin(boolean usePin) {
        this.usePin = usePin;
    }

    public String getPinProtection() {
        return pinProtection;
    }

    public void setPinProtection(String pinProtection) {
        this.pinProtection = pinProtection;
    }

    public boolean isAutoGeneratePatientId() {
        return autoGeneratePatientId;
    }

    public void setAutoGeneratePatientId(boolean autoGeneratePatientId) {
        this.autoGeneratePatientId = autoGeneratePatientId;
    }

    public boolean getIs_subuser() {
        return is_subuser;
    }
    public void setIs_subuser(boolean is_subuser) {
        this.is_subuser = is_subuser;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LoginInformationModel(int uid, int companyId, String username, String database, String tz, String host, String name, String sessionId, String activeUntil, String uuidLocation, boolean usePin, String pinProtection, String language, boolean autoGeneratePatientId) {
        this.uid = uid;
        this.companyId = companyId;
        this.username = username;
        this.database = database;
        this.tz = tz;
        this.host = host;
        this.name = name;
        this.sessionId = sessionId;
        this.archivedUntil = archivedUntil;
        this.uuidLocation = uuidLocation;
        this.usePin = usePin;
        this.pinProtection = pinProtection;
        this.autoGeneratePatientId = autoGeneratePatientId;
        this.language = language;
    }


    public LoginInformationModel() {
        this.uid = 0;
        this.companyId = 0;
        this.username = "";
        this.database = "";
        this.tz = "";
        this.host = "";
        this.name = "";
        this.sessionId = "";
        this.archivedUntil = "";
        this.uuidLocation = "";
        this.usePin = false;
        this.pinProtection = "";
        this.autoGeneratePatientId = false;
        this.is_subuser = false;
        this.language = "";
    }

    public String getCookie() {
        return cookie;
    }

    public String getActiveUntil() {
        return archivedUntil;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setActiveUntil(String archivedUntil) {
        this.archivedUntil = archivedUntil;
    }

    public String getUuidLocation() {
        return uuidLocation;
    }

    public void setUuidLocation(String uuidLocation) {
        this.uuidLocation = uuidLocation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
