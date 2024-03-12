package it.edoardo.cospend_frontend.http.nextcloud;

import java.io.Serializable;

public class NextcloudLoginInitResponse implements Serializable {
    public Poll poll;
    public String login;

    public class Poll {
        public String token;
        public String endpoint;
    }
}
