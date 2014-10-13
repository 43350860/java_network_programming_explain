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
    //���Ȼ�ȡSSLSocketFactory
    mSSLSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    LOGGER.info("mSSLSocketFactory:"+mSSLSocketFactory.getClass().getName());
    //��ͨ��factory��ȡSSLSocket����
    mSSLSocket = (SSLSocket) mSSLSocketFactory.createSocket(host, port);
    mSSLSocket.setSoTimeout(10000);
    LOGGER.info("mSSLSocket:"+mSSLSocket.getClass().getName());
    InetAddress inetAddress = mSSLSocket.getInetAddress();
    LOGGER.info("�����ϣ�"+inetAddress.toString());
    //ͨ��SSLSocketȡ����ǰ֧�ֵ����м��ܵķ�ʽ
    String[] enabledCipherSuites = mSSLSocket.getSupportedCipherSuites();
    //���õ�ǰsocket֧�ֵļ��ܷ�ʽ
    mSSLSocket.setEnabledCipherSuites(enabledCipherSuites);
    mSSLSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
      
      @Override
      public void handshakeCompleted(HandshakeCompletedEvent event) {
        LOGGER.info("���ֽ���.");
        LOGGER.info("�����׼���"+event.getCipherSuite());
        LOGGER.info("�ỰΪ��"+event.getSession());
        LOGGER.info("ͨ�ŶԷ�Ϊ��"+event.getSession().getPeerHost());
      }
    });
    
    //�������־
    //printLog(enabledCipherSuites);
  }
  
  
  public void communicate() throws IOException {
    //��������
    StringBuilder httpProtocolBuilder = new StringBuilder();
    httpProtocolBuilder.append("GET https://"+host+" HTTP/1.1 \r\n");
    httpProtocolBuilder.append("Host:"+host+"\r\n");
    httpProtocolBuilder.append("Accept:*/*\r\n");
    httpProtocolBuilder.append("\r\n");
    
    OutputStream outputStream = mSSLSocket.getOutputStream();
    outputStream.write(httpProtocolBuilder.toString().getBytes());
    outputStream.flush();
    LOGGER.info("���ݷ������!");
    
    //���ջظ�������
    ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
    byte buff[] = new byte[1024];
    
    InputStream inputStream = mSSLSocket.getInputStream();
    for (int size = -1; (size=inputStream.read(buff)) != -1;) {
      byteArrayOutput.write(buff, 0, size);
    }
    LOGGER.info("��ӡ����˷��ص�����:");
    LOGGER.info(new String(byteArrayOutput.toByteArray()));
  }

  
  public static void main(String[] args) throws UnknownHostException, IOException {
    HttpsClient hc = new HttpsClient();
    hc.createSoket();
    hc.communicate();
  }
  
  
  
  private void printLog(String[] enabledCipherSuites) {
    StringBuilder sBuilder = new StringBuilder();
    sBuilder.append("��ǰ֧�ֵļ��ܷ�ʽ��\n");
    for (String cipher : enabledCipherSuites) {
      sBuilder.append(cipher).append("\n");
    }
    LOGGER.info(sBuilder.toString());
  }

}
