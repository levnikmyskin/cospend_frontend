package it.edoardo.cospend_frontend.http.nextcloud;

public interface NextcloudLoginCallback {

    void onLoginInitSucces(NextcloudLoginInitResponse response);
    void onLoginInitFail(String msg);

    void onLoginFinalSuccess(NextcloudLoginFinalResponse response);
    void onLoginFinalFail(String msg);
}
