package com.sailing.dscg.common;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @Description: CSV文件工具类
 * @Auther:史俊华
 * @Date:2018/8/2913
 */
public class CSVUtils {
    private static String charsetName = "GBK";

    /**
     * 获取文件路径
     * @param path
     * @return
     */
    public static String getFilePath(String path){
        String[] pathAr = path.split("/");
        String filePath = "";
        for (int i = 0; i < pathAr.length; i++) {
            filePath += pathAr[i]+File.separator;
        }
        return filePath;
    }

    public static float writeCSV(String filePath,String fileName,String[] headers,List<Object[]> datas,Boolean append){
        CSVPrinter csvPrinter = null;
        OutputStreamWriter osw = null;
        FileOutputStream fos = null;
        try {
            File dirPath = new File(filePath);
            if(!dirPath.exists()){
                dirPath.mkdirs();
            }
            String path = filePath+File.separator+fileName;
            File csvFile = new File(path);
            CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
            //如果文件已存在且是追加则不再创建标题
            if(csvFile.exists() && append){
                csvFormat = CSVFormat.DEFAULT;
            }
            fos  = new FileOutputStream(path,append);
            osw = new OutputStreamWriter(fos,charsetName);
            csvPrinter = new CSVPrinter(osw,csvFormat);
            for (int i = 0; i <datas.size() ; i++) {
                csvPrinter.printRecord(datas.get(i));
            }
            csvPrinter.flush();
            File file = new File(path);
            long fileSize = file.length();
            DecimalFormat df = new DecimalFormat("0.000");
            String size = df.format((float)fileSize/(1024*1024));
            return Float.parseFloat(size);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(csvPrinter!=null){
                try {
                    csvPrinter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(osw!=null){
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
//
//    public static void main(String[] args){
//        String filePath = "/opt/dscg/web/excel/text.csv";
//        String[] header = {"姓名","性别","年龄"};
//        List<Object[]> list = new ArrayList<>();
//        for (int i = 0; i <10000 ; i++) {
//            Object[] obj = {"hehe","xx",i};
//            list.add(obj);
//        }
//        writeCSV(filePath,header,list,true);
//    }
}
