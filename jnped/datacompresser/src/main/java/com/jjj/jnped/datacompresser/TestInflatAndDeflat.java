package com.jjj.jnped.datacompresser;

import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by jm on 14-10-27.
 */
public class TestInflatAndDeflat {

    static final byte[] DICTIONARY;
    static {
        try {
            DICTIONARY = ("\u0000\u0000\u0000\u0007options\u0000\u0000\u0000\u0004hea"
                    + "d\u0000\u0000\u0000\u0004post\u0000\u0000\u0000\u0003put\u0000\u0000\u0000\u0006dele"
                    + "te\u0000\u0000\u0000\u0005trace\u0000\u0000\u0000\u0006accept\u0000\u0000\u0000"
                    + "\u000Eaccept-charset\u0000\u0000\u0000\u000Faccept-encoding\u0000\u0000\u0000\u000Fa"
                    + "ccept-language\u0000\u0000\u0000\raccept-ranges\u0000\u0000\u0000\u0003age\u0000"
                    + "\u0000\u0000\u0005allow\u0000\u0000\u0000\rauthorization\u0000\u0000\u0000\rcache-co"
                    + "ntrol\u0000\u0000\u0000\nconnection\u0000\u0000\u0000\fcontent-base\u0000\u0000"
                    + "\u0000\u0010content-encoding\u0000\u0000\u0000\u0010content-language\u0000\u0000"
                    + "\u0000\u000Econtent-length\u0000\u0000\u0000\u0010content-location\u0000\u0000\u0000"
                    + "\u000Bcontent-md5\u0000\u0000\u0000\rcontent-range\u0000\u0000\u0000\fcontent-type"
                    + "\u0000\u0000\u0000\u0004date\u0000\u0000\u0000\u0004etag\u0000\u0000\u0000\u0006expe"
                    + "ct\u0000\u0000\u0000\u0007expires\u0000\u0000\u0000\u0004from\u0000\u0000\u0000"
                    + "\u0004host\u0000\u0000\u0000\bif-match\u0000\u0000\u0000\u0011if-modified-since"
                    + "\u0000\u0000\u0000\rif-none-match\u0000\u0000\u0000\bif-range\u0000\u0000\u0000"
                    + "\u0013if-unmodified-since\u0000\u0000\u0000\rlast-modified\u0000\u0000\u0000\blocati"
                    + "on\u0000\u0000\u0000\fmax-forwards\u0000\u0000\u0000\u0006pragma\u0000\u0000\u0000"
                    + "\u0012proxy-authenticate\u0000\u0000\u0000\u0013proxy-authorization\u0000\u0000"
                    + "\u0000\u0005range\u0000\u0000\u0000\u0007referer\u0000\u0000\u0000\u000Bretry-after"
                    + "\u0000\u0000\u0000\u0006server\u0000\u0000\u0000\u0002te\u0000\u0000\u0000\u0007trai"
                    + "ler\u0000\u0000\u0000\u0011transfer-encoding\u0000\u0000\u0000\u0007upgrade\u0000"
                    + "\u0000\u0000\nuser-agent\u0000\u0000\u0000\u0004vary\u0000\u0000\u0000\u0003via"
                    + "\u0000\u0000\u0000\u0007warning\u0000\u0000\u0000\u0010www-authenticate\u0000\u0000"
                    + "\u0000\u0006method\u0000\u0000\u0000\u0003get\u0000\u0000\u0000\u0006status\u0000"
                    + "\u0000\u0000\u0006200 OK\u0000\u0000\u0000\u0007version\u0000\u0000\u0000\bHTTP/1.1"
                    + "\u0000\u0000\u0000\u0003url\u0000\u0000\u0000\u0006public\u0000\u0000\u0000\nset-coo"
                    + "kie\u0000\u0000\u0000\nkeep-alive\u0000\u0000\u0000\u0006origin100101201202205206300"
                    + "302303304305306307402405406407408409410411412413414415416417502504505203 Non-Authori"
                    + "tative Information204 No Content301 Moved Permanently400 Bad Request401 Unauthorized"
                    + "403 Forbidden404 Not Found500 Internal Server Error501 Not Implemented503 Service Un"
                    + "availableJan Feb Mar Apr May Jun Jul Aug Sept Oct Nov Dec 00:00:00 Mon, Tue, Wed, Th"
                    + "u, Fri, Sat, Sun, GMTchunked,text/html,image/png,image/jpg,image/gif,application/xml"
                    + ",application/xhtml+xml,text/plain,text/javascript,publicprivatemax-age=gzip,deflate,"
                    + "sdchcharset=utf-8charset=iso-8859-1,utf-,*,enq=0.").getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    /**
     * @param args
     * @throws UnsupportedEncodingException
     */
    public static void main(String args[]) throws UnsupportedEncodingException, DataFormatException {

        String content = "blahblahblah";
        System.out.println("content: "+content+", origin data length: "+content.getBytes("UTF-8").length);

        //压缩
        byte result[] = new byte[100];
        Deflater deflater = new Deflater();
        deflater.setInput(content.getBytes("UTF-8"));
        deflater.finish();
        int compressDataLength = deflater.deflate(result);
        System.out.println("compressDataLength: "+compressDataLength);
        System.out.println("compress data : "+new String(result,"utf-8"));


        //解压
        byte inflateResult[] = new byte[100];
        Inflater inflater = new Inflater();
        inflater.setInput(result, 0, compressDataLength);
        int dcompressDataLength = inflater.inflate(inflateResult);
        inflater.end();
        System.out.println("dcompressDataLength: "+dcompressDataLength);
        System.out.println("decompress data : "+new String(inflateResult,0,dcompressDataLength));
    }

}
