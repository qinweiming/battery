package controllers.interceptor;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Finally;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * response gzip压缩.
 */
public class Compress extends Controller {

    @Finally
    public static void compress() throws IOException {
        if(Play.mode.isDev()){
            Logger.warn("!!!!! Running in DEV mode, Don't gzip response  !!!!");
            return ;
        }
        String text = response.out.toString();

//        if ("text/xml".equals(response.contentType)) {
//            text = new com.googlecode.htmlcompressor.compressor.XmlCompressor().compress(text);
//        } else if ("text/html; charset=utf-8".equals(response.contentType)) {
//            text = new com.googlecode.htmlcompressor.compressor.HtmlCompressor().compress(text);
//        }


        final ByteArrayOutputStream gzip = gzip(text);
        response.setHeader("Content-Encoding", "gzip");
        response.setHeader("Content-Length", gzip.size() + "");
        response.out = gzip;
    }

    public static ByteArrayOutputStream gzip(final String input)
            throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        final ByteArrayOutputStream stringOutputStream = new ByteArrayOutputStream((int) (input.length() * 0.75));
        final OutputStream gzipOutputStream = new GZIPOutputStream(stringOutputStream);

        final byte[] buf = new byte[5000];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            gzipOutputStream.write(buf, 0, len);
        }

        inputStream.close();
        gzipOutputStream.close();

        return stringOutputStream;
    }

}
