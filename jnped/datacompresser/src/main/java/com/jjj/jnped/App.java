package com.jjj.jnped;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        URL targetUrl = new URL("https://www.alipay.com/index.html");
        int targetPort = targetUrl.getPort() == -1 ? targetUrl.getDefaultPort() : targetUrl
                .getPort();
        System.out.println("host: "+targetUrl.getHost());
        System.out.println("port: "+targetUrl.getDefaultPort());
        System.out.println("protocol: "+targetUrl.getProtocol());
    }
}
