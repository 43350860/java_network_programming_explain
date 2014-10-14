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
 * server端程序
 * @author jm
 */
public class EchoServer {

  private static final Logger LOGGER = Logger.getLogger("EchoServer");
  private final static int PORT = 8000;
  private static final char PASSPHRASE[] = "654321".toCharArray();
  
  private SSLServerSocket sslServerSocket;
  private String keyPath;
  
  public EchoServer() throws Exception {
    //打开JSSE的调试日志
    System.setProperty("javax.net.debug", "all");
    //获取证书的路径
    keyPath = EchoServer.class.getClassLoader().getResource("test.keys").getPath();
    //创建ssl socket 上下文对象
    SSLContext sslContext = createSSLSocketContext();
    //创建一个socket
    SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
    sslServerSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(PORT);
    LOGGER.info("服务启动完成！");
    LOGGER.info("是否使用客户端模式？"+sslServerSocket.getUseClientMode());
    LOGGER.info("是否需要客户端提交证书？"+sslServerSocket.getNeedClientAuth());
    
    String[] supportedCipherSuites = sslServerSocket.getSupportedCipherSuites();
    sslServerSocket.setEnabledCipherSuites(supportedCipherSuites);
  }
  
  public SSLContext createSSLSocketContext() throws Exception {
    //加载证书
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(new FileInputStream(keyPath), PASSPHRASE);
    
    //证书安全管理
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, PASSPHRASE);
    
    //用于验证对方证书的管理类
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
        LOGGER.info("收到一个新的连接。Host:  "+socket.getInetAddress()+", 端口:"+socket.getPort());
        
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
