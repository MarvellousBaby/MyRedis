package com.sailing.dscg.common.page;

/**
 * Description:导出Excel功能工具
 * <p>
 * Update by Panyu on 2018/8/6 下午 01:57:50
 */

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ApachePOIExcelWrite {

    public static boolean exportExcel(String file_name,String path,Object[][] data_Types, String sheet_name) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheet_name);
        Object[][] dataTypes = data_Types;

        int rowNum = 0;
        for(Object[] dataType : dataTypes){
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for(Object field : dataType){
                Cell cell = row.createCell(colNum++);
                if(field instanceof String){
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer){
                    cell.setCellValue((Integer) field);
                }
            }
        }
        try {
            File outDir = new File(path);
            outDir.mkdirs();
            FileOutputStream outputStream = new FileOutputStream(path+file_name);
            workbook.write(outputStream);
            workbook.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     *
     * @param zipPath zip文件路径
     * @param sourceFilePath 打包文件夹路径
     * @param zipFileName zip文件名称
     * @return
     */
    public static boolean fileToZip(String zipPath,String sourceFilePath,String zipFileName){
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        if (sourceFile.exists()== false){
            sourceFile.mkdirs();
        }

        if(sourceFile.exists()){
            try {
                File zipFile = new File(zipPath + File.separator + zipFileName);
                if(!zipFile.exists()){
                    File[] sourceFiles = sourceFile.listFiles();
                    if(sourceFiles!=null && sourceFiles.length>0){
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024*10];
                        for(int i=0;i<sourceFiles.length;i++){
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, 1024*10);
                            int read = 0;
                            while((read=bis.read(bufs, 0, 1024*10)) != -1){
                                zos.write(bufs,0,read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally{
                //关闭流
                try {
                    if(null != bis) bis.close();
                    if(null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }

    /**
     *
     * @param zipPath zip文件路径
     * @param sourceFilePath 打包文件夹路径
     * @param zipFileName zip文件名称
     * @return
     */
    public static boolean file2Zip(String zipPath,String sourceFilePath,String zipFileName){
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        if (sourceFile.exists()== false){
            sourceFile.mkdirs();
        }

        if(sourceFile.exists()){
            try {
                File zipFile = new File(zipPath + File.separator + zipFileName);
                if(!zipFile.exists()){
                    File[] sourceFiles = sourceFile.listFiles();
                    if(sourceFiles!=null && sourceFiles.length>0){
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024*10];
                        for(int i=0;i<sourceFiles.length;i++){
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, 1024*10);
                            int read = 0;
                            while((read=bis.read(bufs, 0, 1024*10)) != -1){
                                zos.write(bufs,0,read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally{
                //关闭流
                try {
                    if(null != bis) bis.close();
                    if(null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }


    //下载压缩文件
    public static void download(HttpServletResponse response, String fileName,String realName) throws IOException{
        //处理文件名
//        realName = realName.substring(1);
        realName = new String(realName.getBytes("iso8859-1"),"UTF-8");
        //设置响应头，控制浏览器下载该文件
        response.setHeader("content-Disposition", "attachment;filename="+ URLEncoder.encode(realName,"UTF-8"));
        //读取要下载的文件，保存到文件输入流
        FileInputStream in = new FileInputStream(fileName+"/"+realName);
        //创建输出流
        OutputStream out = response.getOutputStream();
        //创建缓冲区
        byte buffer[] = new byte[4096];
        int len = 0;
        //循环将输入流中的内容读取到缓冲区当中
        while((len = in.read(buffer))>0){
            //输出缓冲区的内容到浏览器，实现文件下载
            out.write(buffer,0,len);
        }
        //关闭文件输入流
        in.close();
        //关闭输出流
        out.close();
    }


}
