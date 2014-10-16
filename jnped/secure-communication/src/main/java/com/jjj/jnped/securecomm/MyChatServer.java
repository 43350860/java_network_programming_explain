package com.jjj.jnped.securecomm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MyChatServer {

  private static final Logger LOGGER = Logger.getLogger("MySSLServer");

  private static final int PORT = 2443;

  private static final char PASSWORD[] = "654321".toCharArray();

  private SSLServerSocket sslServerSocket;

  private Socket currentSocket;
  
  private Thread writerThread;

  public MyChatServer() throws Exception {

    URL keysPath = MyChatServer.class.getClassLoader().getResource("test.keys");

    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(new FileInputStream(keysPath.getPath()), PASSWORD);

    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(keyStore, PASSWORD);

    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(keyStore);

    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

    SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
    sslServerSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(PORT);
    
    sslServerSocket.setEnabledCipherSuites(sslServerSocket.getSupportedCipherSuites());
  }

  public void service() throws IOException {
    while (true) {
      try {
        currentSocket = sslServerSocket.accept();
        
        if (writerThread == null) {
          writerThread = new Thread(new WriteJob());
          writerThread.start();
        }
        
        BufferedReader reader = getReader(currentSocket);
        for (String line = null; (line = reader.readLine()) != null;) {
          System.out.println("客户端:" + line);
        }
      } catch (Exception e) {
        LOGGER.log(Level.FINER, "", e);
      }
    }
  }

  private BufferedReader getReader(Socket socket) throws IOException {
    return new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  private PrintWriter getWriter(Socket socket) throws IOException {
    return new PrintWriter(socket.getOutputStream(),true);
  }


  class WriteJob implements Runnable {

    @Override
    public void run() {
      try {
        System.out.println("请输入聊天内容:\n");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (String line = null; (line = reader.readLine()) != null;) {
          if (currentSocket != null && !currentSocket.isClosed()) {
            PrintWriter writer = getWriter(currentSocket);
            writer.println(line);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  
  public static void main(String[] args) throws Exception {
    MyChatServer sslServer = new MyChatServer();
    sslServer.service();
  }
}
