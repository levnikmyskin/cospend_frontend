package it.edoardo.cospend_frontend.http.nextcloud;

import java.io.Serializable;

public class NextcloudLoginFinalResponse implements Serializable {
    public String server;
    public String loginName;
    public String appPassword;
}
