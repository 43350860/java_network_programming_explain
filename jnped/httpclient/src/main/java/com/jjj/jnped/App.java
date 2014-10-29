package com.jjj.jnped;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {

        printLog("开始。。。");

        CookieStore cookieStore = new BasicCookieStore();

        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE,cookieStore);

        HttpUriRequest httpUriRequest = new HttpGet("http://mobilegw-1-64.test.alipay.net/mgw");

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(new HttpHost("mobilegw-1-64.test.alipay.net", 80, "http"), httpUriRequest, httpContext);
        String responseString = EntityUtils.toString(response.getEntity());
        //printLog(responseString);

        List<Cookie> cookies = cookieStore.getCookies();
        if (!cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                printLog("cookieName: "+cookie.getName()+",   cookieValue:"+cookie.getValue());
            }
        }
    }

    private static void printLog(String c) {
        Logger.getLogger(App.class.getName()).info(c);
    }
}
