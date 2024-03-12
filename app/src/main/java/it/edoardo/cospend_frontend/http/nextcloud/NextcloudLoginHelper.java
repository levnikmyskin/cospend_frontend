package it.edoardo.cospend_frontend.http.nextcloud;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it.edoardo.cospend_frontend.http.HttpConsts;
import it.edoardo.cospend_frontend.http.HttpSingleton;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Helper class to deal with Nextcloud Login flow v2 (<a href="https://docs.nextcloud.com/server/latest/developer_manual/client_apis/LoginFlow/index.html#login-flow-v2">ref</a>).
 * In short:
 * 1. We first do a POST request to domain.name/index.php/login/v2. This will return a json which we serialize into NextcloudLoginInitResponse;
 * 2. User should be redirected to the `login` endpoint (the activity will do this);
 * 2.a In the meantime, we repeatedly poll the `poll` endpoint with the `token`, in a POST request. This returns 404 until the user has given consent;
 * 3. Once we have our `NextcloudLoginFinalResponse`, the goal of this class is fulfilled.
 */
public class NextcloudLoginHelper {
    public static final String LOGIN_FLOW_INIT_PATH = "index.php/login/v2";
    private static final String TAG = NextcloudLoginHelper.class.getSimpleName();
    // TODO: replace this with a proper translatable resource string
    private static final String LOGIN_INIT_FAILED_STR = "Login initialization failed. Is your server ok?";
    private static final String LOGIN_JSON_ERR_STR = "Internal error. Login failed";
    private static final String LOGIN_FINAL_FAILED_STR = "Login finalization failed.";

    private final NextcloudLoginCallback callback;

    public NextcloudLoginHelper(NextcloudLoginCallback callback) {
        this.callback = callback;
    }

    @Nullable
    public static HttpUrl buildLoginFlowUrl(String host) {
        // TODO: check that we actually only received the host.
        // In general, let's be smart about this and try to build the proper url
        // from what we got in input
        HttpUrl url = HttpUrl.parse(host);
        if (url == null)
            return null;
        return url.newBuilder().addPathSegments(LOGIN_FLOW_INIT_PATH).build();
    }

    public void loginFlow(HttpUrl server) {
        NextcloudLoginInitResponse initResp = initiateLoginFlowv2(server);
        if (initResp == null)
            return;

        loginFlowPoll(initResp);
    }

    @Nullable
    public NextcloudLoginInitResponse initiateLoginFlowv2(HttpUrl server) {
        Request request = new Request.Builder().url(server).post(RequestBody.create(new byte[]{})).build();
        OkHttpClient client = HttpSingleton.getInstance().client;

        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "Got response from login init");
            if (!response.isSuccessful() || response.body() == null) {
                Log.e(TAG, "Login init was not successful.");
                callback.onLoginInitFail(LOGIN_INIT_FAILED_STR);
                return null;
            }

            Gson gson = new Gson();
            NextcloudLoginInitResponse nextcloudResp = gson.fromJson(response.body().charStream(), NextcloudLoginInitResponse.class);
            callback.onLoginInitSucces(nextcloudResp);

            return nextcloudResp;
        } catch (IOException ex) {
            Log.e(TAG, ex.toString());
            callback.onLoginInitFail(LOGIN_INIT_FAILED_STR);
        } catch (JsonSyntaxException ex) {
            Log.e(TAG, ex.toString());
            callback.onLoginInitFail(LOGIN_JSON_ERR_STR);
        }
        return null;
    }

    public void loginFlowPoll(@NonNull NextcloudLoginInitResponse nextcloudResp) {
        try {
            while (true) {
                NextcloudLoginFinalResponse response = doPollRequest(nextcloudResp.poll.endpoint, nextcloudResp.poll.token);
                if (response != null) {
                    callback.onLoginFinalSuccess(response);
                    return;
                }

                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException | IOException | JsonSyntaxException ex) {
            Log.e(TAG, ex.toString());
            callback.onLoginFinalFail(LOGIN_FINAL_FAILED_STR);
        }
    }

    @Nullable
    private NextcloudLoginFinalResponse doPollRequest(String url, String token) throws IOException, JsonSyntaxException {
        Log.i(TAG, "Polling Nextcloud for login");
        String data = "token=" + token;
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder().url(url).post(RequestBody.create(data, mediaType)).build();
        Response response = HttpSingleton.getInstance().client.newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            return null;
        }
        Gson gson = new Gson();
        NextcloudLoginFinalResponse resp = gson.fromJson(response.body().charStream(), NextcloudLoginFinalResponse.class);
        response.close();
        return resp;
    }
}