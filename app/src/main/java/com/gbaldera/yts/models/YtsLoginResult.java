package com.gbaldera.yts.models;


public class YtsLoginResult extends YtsBaseModel {
    public YtsLoginResultData data;

    public class YtsLoginResultData {
        public String username;
        public String email;
        public String user_key;
    }
}
