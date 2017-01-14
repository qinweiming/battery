/**
 *
 */
package utils;

import java.io.*;
/**
 * Created by xudongmei on 2017/1/14.
 *
 * 文件管理操作实用类
 */
public class FileUtils {


    /**
     * 读文件到字节数组中
     *
     * @param file
     * @throws Exception
     */
    public static byte[] fileToByte(File file) throws FileUtilException {
        FileInputStream is = null;
        try {
            byte[] dist = null;
            if (file.exists()) {
                is = new FileInputStream(file);
                dist = new byte[is.available()];
                is.read(dist);
            }
            return dist;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileUtilException("文件转化字节数组错误!");
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 读文件到字节数组中
     *
     * @param filePath
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws FileUtilException {
        FileInputStream is = null;
        try {
            File file = new File(filePath);
            byte[] dist = null;
            if (file.exists()) {
                is = new FileInputStream(file);
                dist = new byte[is.available()];
                is.read(dist);
            }
            return dist;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileUtilException("文件转化字节数组错误!");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据 byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath,String fileName) throws FileUtilException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists() && dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+"\\"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileUtilException("文件转化字节数组错误!");
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 文件的写入
     *
     * @param filePath(文件路径)
     * @param fileName(文件名)
     * @param args
     * @throws IOException
     */
    public static void writeFile(String filePath, String fileName, String[] args) throws IOException {
        FileWriter fw = new FileWriter(filePath + fileName);
        PrintWriter out = new PrintWriter(fw);
        for (int i = 0; i < args.length; i++) {
            out.write(args[i]);
            out.println();
            out.flush();
        }
        fw.close();
        out.close();
    }

    /**
     * 文件的写入
     *
     * @param filePath(文件路径)
     * @param fileName(文件名)
     * @param args
     * @throws IOException
     */
    public static void writeFile(String filePath, String fileName, String args) throws IOException {
        FileWriter fw = new FileWriter(filePath + fileName);
        fw.write(args);
        fw.close();
    }

    /**
     * 文件的写入
     *
     * @param filePath(文件路径+文件名)
     * @param args
     * @throws IOException
     */
    public static void writeFile(String filePath, String args) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(args);
        fw.close();
    }

    /**
     * 文件的写入
     *
     * @param filePath(文件路径+文件名)
     * @param args 要写入的内容
     * @param isUTF8 是否以UTF-8的文件编码写入文件
     * @throws IOException
     */
    public static void writeFile(String filePath, String args,boolean isUTF8) throws IOException {
        if(isUTF8){
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8");
            out.write(args);
            out.flush();
            out.close();
        }else{
            FileWriter fw = new FileWriter(filePath);
            fw.write(args);
            fw.close();
        }
    }
    /**
     * 文件的写入
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param args 要写入的内容
     * @param isUTF8 是否以UTF-8的文件编码写入文件
     * @throws IOException
     */
    public static void writeFile(String filePath,String fileName, String args,boolean isUTF8) throws IOException {
        File f = new File(filePath);
        if(!f.exists()){
            f.mkdirs();
        }
        if(isUTF8){
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath+fileName),"UTF-8");
            out.write(args);
            out.flush();
            out.close();
        }else{
            FileWriter fw = new FileWriter(filePath+fileName);
            fw.write(args);
            fw.close();
        }
    }

}
