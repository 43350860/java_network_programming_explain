package com.jjj.jnped.securecomm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MyChatClient {
  
  private static final String URL = "localhost";
  
  private static final int PORT = 2443;
  
  private SSLSocket mSocket;

  public MyChatClient() throws UnknownHostException, IOException {
    
    System.setProperty("javax.net.ssl.trustStore", EchoClient.class.getClassLoader().getResource("test.keys").getPath());
    
    SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
    mSocket = (SSLSocket) sf.createSocket(URL, PORT);
    mSocket.setEnabledCipherSuites(mSocket.getSupportedCipherSuites());
  }
  
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
  }
  
  public PrintWriter getWriter() throws IOException {
    PrintWriter pw =  new PrintWriter(mSocket.getOutputStream(),true);
    return pw;
  }
  
  public void talk() throws IOException {
    
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            BufferedReader reader = getReader();
            for (String line = null; (line = reader.readLine()) != null;) {
              System.out.println("服务端:\n"+line);
            }
            Thread.sleep(1000);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });
    thread.start();
    
    System.out.println("可以开始聊天了 ：\n");
    PrintWriter writer = getWriter();
    BufferedReader sysInput = new BufferedReader(new InputStreamReader(System.in));
    for(String line = null; (line = sysInput.readLine()) != null;) {
      writer.println(line);
    }
  }
  
  
  public static void main(String[] args) throws Exception {
    MyChatClient client = new MyChatClient();
    client.talk();
  }
}
