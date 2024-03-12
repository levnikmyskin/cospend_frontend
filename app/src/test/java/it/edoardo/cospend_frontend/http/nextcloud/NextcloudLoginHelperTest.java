package it.edoardo.cospend_frontend.http.nextcloud;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import it.edoardo.cospend_frontend.http.HttpSingleton;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

class NextcloudLoginHelperTest {

    NextcloudLoginCallback callback;
    NextcloudLoginHelper ncHelper;
    MockedStatic<HttpSingleton> singleton;

    @BeforeEach
    void setUp() {
        callback = mock(NextcloudLoginCallback.class);
        singleton = mockStatic(HttpSingleton.class);

        ncHelper = new NextcloudLoginHelper(callback);
    }

    @AfterEach
    void tearDown() {
        singleton.close();
    }

    @Test
    void buildLoginFlowUrl() {
        HttpUrl url = NextcloudLoginHelper.buildLoginFlowUrl("http://domain.com");
        assertNotNull(url);
        assertEquals(url.toString(), "http://domain.com/" + NextcloudLoginHelper.LOGIN_FLOW_INIT_PATH);

        url = NextcloudLoginHelper.buildLoginFlowUrl("http://domain.com:8080");
        assertNotNull(url);
        assertEquals(url.port(), 8080);
        assertEquals(url.toString(), "http://domain.com:8080/" + NextcloudLoginHelper.LOGIN_FLOW_INIT_PATH);

        url = NextcloudLoginHelper.buildLoginFlowUrl("randomtext");
        assertNull(url);

        // TODO this should work
        url = NextcloudLoginHelper.buildLoginFlowUrl("domain.com");
        assertNotNull(url);
    }

    @Test
    void loginFlow() {
    }

    @Test
    void initiateLoginFlowv2() {
    }

    @Test
    void loginFlowPoll() {
    }
}