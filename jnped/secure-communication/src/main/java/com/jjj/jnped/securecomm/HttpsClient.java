package com.jjj.jnped.securecomm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class HttpsClient {
  
  static final Logger LOGGER = Logger.getLogger("HttpsClient");
  String host = "23.59.134.219";//www.usps.com
  int port = 443;
  SSLSocketFactory mSSLSocketFactory;
  SSLSocket mSSLSocket ;

  public void createSoket() throws UnknownHostException, IOException {
    //首先获取SSLSocketFactory
    mSSLSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    LOGGER.info("mSSLSocketFactory:"+mSSLSocketFactory.getClass().getName());
    //在通过factory获取SSLSocket对象
    mSSLSocket = (SSLSocket) mSSLSocketFactory.createSocket(host, port);
    mSSLSocket.setSoTimeout(10000);
    LOGGER.info("mSSLSocket:"+mSSLSocket.getClass().getName());
    InetAddress inetAddress = mSSLSocket.getInetAddress();
    LOGGER.info("连接上："+inetAddress.toString());
    //通过SSLSocket取到当前支持的所有加密的方式
    String[] enabledCipherSuites = mSSLSocket.getSupportedCipherSuites();
    //设置当前socket支持的加密方式
    mSSLSocket.setEnabledCipherSuites(enabledCipherSuites);
    mSSLSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
      
      @Override
      public void handshakeCompleted(HandshakeCompletedEvent event) {
        LOGGER.info("握手结束.");
        LOGGER.info("加密套件："+event.getCipherSuite());
        LOGGER.info("会话为："+event.getSession());
        LOGGER.info("通信对方为："+event.getSession().getPeerHost());
      }
    });
    
    //随便打个日志
    //printLog(enabledCipherSuites);
  }
  
  
  public void communicate() throws IOException {
    //发送数据
    StringBuilder httpProtocolBuilder = new StringBuilder();
    httpProtocolBuilder.append("GET https://"+host+" HTTP/1.1 \r\n");
    httpProtocolBuilder.append("Host:"+host+"\r\n");
    httpProtocolBuilder.append("Accept:*/*\r\n");
    httpProtocolBuilder.append("\r\n");
    
    OutputStream outputStream = mSSLSocket.getOutputStream();
    outputStream.write(httpProtocolBuilder.toString().getBytes());
    outputStream.flush();
    LOGGER.info("数据发送完毕!");
    
    //接收回复的数据
    ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
    byte buff[] = new byte[1024];
    
    InputStream inputStream = mSSLSocket.getInputStream();
    for (int size = -1; (size=inputStream.read(buff)) != -1;) {
      byteArrayOutput.write(buff, 0, size);
    }
    LOGGER.info("打印服务端返回的数据:");
    LOGGER.info(new String(byteArrayOutput.toByteArray()));
  }

  
  public static void main(String[] args) throws UnknownHostException, IOException {
    HttpsClient hc = new HttpsClient();
    hc.createSoket();
    hc.communicate();
  }
  
  
  
  private void printLog(String[] enabledCipherSuites) {
    StringBuilder sBuilder = new StringBuilder();
    sBuilder.append("当前支持的加密方式：\n");
    for (String cipher : enabledCipherSuites) {
      sBuilder.append(cipher).append("\n");
    }
    LOGGER.info(sBuilder.toString());
  }

}
