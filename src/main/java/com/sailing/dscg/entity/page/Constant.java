package com.sailing.dscg.entity.page;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


@Slf4j
public class Constant {
    public static final String RESPONSE_OK = "ok";
    public static final String RESPONSE_ERROR = "error";

    public static List<String> lastStartServiceID=  new ArrayList<>();

    public static Map responseBody(String interfaceName,String interfaceStatus,String message){
        log.info("包装返回值，接口名称>>>{},接口状态>>>{}",interfaceName,message);
        Map<String,String> response = new HashMap<>();
        response.put("InterfaceName",interfaceName);
        response.put("InterfaceStatus",interfaceStatus);
        response.put("Message",message);
        log.info("respose具体的内容>>>{}",response);
        return response;
    }
    /**
     * 将文件夹打包压缩成为zip文件
     * @param files 需要压缩的文件数组
     * @param destDirectory 目标文件目录
     * @param zipName 打包压缩文件的名称
     */
    public static void toZipFile(File[] files, String destDirectory, String zipName){


        log.info("接收到的文件的数>>>{},文件目的地目录>>>{},文件名>>>{}",files.length);
        if(files.length < 1){
            System.out.print("源文件目录不存在或者源文件为空");
            return;
        }
        File destDirectoryFile = new File(destDirectory);
        //如果目标文件目录不存在，则创建该目录
        if(!destDirectoryFile.exists()){
            destDirectoryFile.mkdirs();
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        ZipOutputStream zipOut = null;
        String destFile = destDirectory + "/" + zipName + ".zip";
        try{
            out = new BufferedOutputStream(new FileOutputStream(destFile));
            zipOut = new ZipOutputStream(out);
            for(File file : files){
                //zipOut根据zipEntry确定文件夹中的哪个文件需要压缩
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);
                in = new BufferedInputStream(new FileInputStream(file));
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = (in.read(bytes))) != -1){
                    zipOut.write(bytes,0,len);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(in != null){
                try{
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(zipOut != null){
                try{
                    zipOut.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
    /**
     * 使用zipEntry解压缩
     * @param sourcePath 源文件路径
     * @param destPath 目标文件目录
     */
    public static void unZip(String sourcePath,String destPath){
        log.info("需要解压缩的源文件路径{},解压的目标路径{}",sourcePath,destPath);
        File sourceFile = new File(sourcePath);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        ZipInputStream zipInputStream = null;
        try{
            //构建zipInputStream的输入流
            in = new BufferedInputStream(new FileInputStream(sourceFile));
            zipInputStream = new ZipInputStream(in);
            ZipEntry zipEntry = null;
            //循环控制所有的zipEntry并且其不为目录
            while ((zipEntry = zipInputStream.getNextEntry()) != null
                    && !zipEntry.isDirectory()){
                System.out.println(zipEntry.getName());
                File target = new File(destPath,zipEntry.getName());
                //如果目标目录不存在，则创建目标目录
                if(!target.getParentFile().exists()){
                    target.getParentFile().mkdirs();
                }
                out = new BufferedOutputStream(new FileOutputStream(target));
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = zipInputStream.read(bytes)) != -1){
                    out.write(bytes,0,len);
                }
                out.flush();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(zipInputStream != null){
                try{
                    zipInputStream.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if(out != null){
                try{
                    out.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 上传文件的客户端
     * @param file 需要上传的文件的路径
     * @param url 上传路径
     */
    public static void uploadFileClient(String file,String url){
        log.info("上传的文件路径>>>{},上传的目标url>>>{}",file,url);
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            //创建客户端对象
            client = HttpClients.createDefault();
            //构建post提交方式
            HttpPost post = new HttpPost(url);
            //构建上传文件相当于<input type="file"/>
            FileBody fileBody = new FileBody(new File(file));
            //构建request请求实体
            HttpEntity httpEntity = MultipartEntityBuilder.create().addPart("file",fileBody).build();
            post.setEntity(httpEntity);
            //执行post方法，并获取响应对象
            response = client.execute(post);
            //回写状态码，判断响应状态，作为日志记录
            System.out.print(response.getStatusLine().getStatusCode());
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(response != null){
                try{
                    response.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 根据url获取相应的文件资源
     *
     * @param str      url路径
     * @param destPath 目标路径
     */
    public static void  getFileFromUrl(String str, String destPath) {
        log.info("url路径{},目标路径{}",str,destPath);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
//            创建url对象
            URL urlz = new URL(str);
            String fileName = str.substring(str.lastIndexOf("/"));
            in = new BufferedInputStream(urlz.openStream());
            File destFile = new File(destPath, fileName);
            out = new BufferedOutputStream(new FileOutputStream(destFile));
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据子网掩码的网络位数转换为十进制
     * @param prefix 例如"32","28"
     * @return “255.255.255.0”
     */
    public static String PrefixToNetmask(String prefix){
        String ipMask = null;
        int inetMask = Integer.valueOf(prefix);
        int part = inetMask / 8;
        int remainder = inetMask % 8;
        int sum = 0;
        for (int i = 8; i > 8 - remainder; i--) {
            sum = sum + (int) Math.pow(2, i - 1);
        }
        if (part == 0) {
            ipMask = sum + ".0.0.0";
        } else if (part == 1) {
            ipMask = "255." + sum + ".0.0";
        } else if (part == 2) {
            ipMask = "255.255." + sum + ".0";
        } else if (part == 3) {
            ipMask = "255.255.255." + sum;
        } else if (part == 4) {
            ipMask = "255.255.255.255";
        }
        return ipMask;
    }

    /**
     * 将字符串写入文件中
     * @param str 字符串
     * @param path 目录
     * @param fileName 文件名
     */
    public static Boolean writeStringToFile(String str,String path,String fileName){
        log.info("字符串为>>>{},目标目录{},目标文件名{}",str,path,fileName);
        BufferedWriter writer = null;
        try{
            File file = new File(path,fileName);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(str);
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            if(writer != null){
                try{
                    writer.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }




}
