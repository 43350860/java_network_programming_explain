package com.jjj.jnped.securecomm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * server�˳���
 * @author jm
 */
public class EchoServer {

  private static final Logger LOGGER = Logger.getLogger("EchoServer");
  private final static int PORT = 8000;
  private static final char PASSPHRASE[] = "654321".toCharArray();
  
  private SSLServerSocket sslServerSocket;
  private String keyPath;
  
  public EchoServer() throws Exception {
    //��JSSE�ĵ�����־
    System.setProperty("javax.net.debug", "all");
    //��ȡ֤���·��
    keyPath = EchoServer.class.getClassLoader().getResource("test.keys").getPath();
    //����ssl socket �����Ķ���
    SSLContext sslContext = createSSLSocketContext();
    //����һ��socket
    SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
    sslServerSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(PORT);
    LOGGER.info("����������ɣ�");
    LOGGER.info("�Ƿ�ʹ�ÿͻ���ģʽ��"+sslServerSocket.getUseClientMode());
    LOGGER.info("�Ƿ���Ҫ�ͻ����ύ֤�飿"+sslServerSocket.getNeedClientAuth());
    
    String[] supportedCipherSuites = sslServerSocket.getSupportedCipherSuites();
    sslServerSocket.setEnabledCipherSuites(supportedCipherSuites);
  }
  
  public SSLContext createSSLSocketContext() throws Exception {
    //����֤��
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(new FileInputStream(keyPath), PASSPHRASE);
    
    //֤�鰲ȫ����
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, PASSPHRASE);
    
    //������֤�Է�֤��Ĺ�����
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(ks);
    
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    return sslContext;
  }
  
  private String echo(String msg) {
    return "echo:"+msg;
  }
  
  private PrintWriter getWriter(Socket socket) throws IOException {
    return new PrintWriter(socket.getOutputStream(),true);
  }
  
  private BufferedReader getReader(Socket socket) throws IOException {
    return new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }
  
  private void service() {
    while ( true ) {
      Socket socket = null;
      try {
        socket = sslServerSocket.accept();
        LOGGER.info("�յ�һ���µ����ӡ�Host:  "+socket.getInetAddress()+", �˿�:"+socket.getPort());
        
        BufferedReader reader = getReader(socket);
        PrintWriter writer = getWriter(socket);
        
        for (String content = null;  null != (content=reader.readLine());) {
          LOGGER.info(content);
          writer.println(echo(content));
        }
      } catch (Exception e) {
        LOGGER.log(Level.INFO, "", e);
      }
    }
  }
  
  public static void main(String[] args) throws Exception {
    EchoServer server = new EchoServer();
    server.service();
  }
  
}
