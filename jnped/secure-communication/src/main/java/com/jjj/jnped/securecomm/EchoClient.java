package com.jjj.jnped.securecomm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class EchoClient {
  
  private static final Logger LOGGER = Logger.getLogger("EchoClient");
  
  private static final String HOST = "spanner.d88.alipay.net";
  
  private static final int PORT = 8456;
  
  private SSLSocket sslSocket;
  
  private String certPath = "";
  
  public EchoClient() throws UnknownHostException, IOException {
    
    certPath = EchoClient.class.getClassLoader().getResource("test.keys").getPath();
    System.setProperty("javax.net.ssl.trustStore", certPath);
    
    SocketFactory sslSocketFactory = SSLSocketFactory.getDefault();
    sslSocket = (SSLSocket) sslSocketFactory.createSocket(HOST, PORT);
    
    LOGGER.info("SSLSocket创建完成");
    String[] supportedCipherSuites = sslSocket.getSupportedCipherSuites();
    sslSocket.setEnabledCipherSuites(supportedCipherSuites);
    
    
    sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
      
      @Override
      public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
        LOGGER.info("握手结束");
        LOGGER.info("会话："+handshakeCompletedEvent.getSession());
      }
    });
  }

  public BufferedReader getReader(Socket socket) throws IOException {
    return new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }
  
  public PrintWriter getWriter(Socket socket) throws IOException {
    return new PrintWriter(socket.getOutputStream(),true);
  }
  
  public void talk() {
    
    try {
      
      BufferedReader reader = getReader(sslSocket);
      PrintWriter writer = getWriter(sslSocket);
      
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      LOGGER.info("请输入字符：");
      for (String inputText = null; null != (inputText = bufferedReader.readLine());) {
        writer.println(inputText);
        LOGGER.info("服务端返回:"+reader.readLine());
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "", e);
    }
    
  }
  
  
  public static void main(String[] args) throws UnknownHostException, IOException {
    EchoClient ec = new EchoClient();
    ec.talk();
  }
  
}
