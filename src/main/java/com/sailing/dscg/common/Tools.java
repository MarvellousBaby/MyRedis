package com.sailing.dscg.common;

import com.sailing.dscg.entity.RespData;
import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
import lombok.extern.log4j.Log4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j
public class Tools {

    public static String generateServiceID(String moduleType){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuffer serviceID = new StringBuffer();
        serviceID.append(moduleType);
        serviceID.append("-");
        serviceID.append(dateFormat.format(new Date()));
        return serviceID.toString();
    }

    /***
     * 获取UUID
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().toUpperCase();
    }

    /***
     * pingIp
     * @param ip
     * @return
     */
    public static Boolean pingIp(String ip){
        Boolean result = false;
        try {
            int  timeOut =  3000 ;  //超时应该在3钞以上
            result = InetAddress.getByName(ip).isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 测试IP、端口是否可达
     * @param host
     * @param port
     * @return
     */
    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port),3000);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 对字符串md5加密(小写+字母)
     *
     * @param str 传入要加密的字符串
     * @return MD5加密后的字符串
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //文件上传工具类服务方法

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception{

        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }


    //上传文件
    public static RespData uploadCert(MultipartFile file, String filePath){
        RespData resp = new RespData();

        if (file == null) {
            resp.setRespCode(RespCodeEnum.FAIL);
            resp.setData("上传失败，文件为空！");
            return resp;
        }

        String fileName = file.getOriginalFilename();  //文件名称
        List<String> files = new ArrayList<>();
        //调用文件处理类FileUtil，处理文件，将文件写入指定位置
        try {
            uploadFile(file.getBytes(), filePath, fileName);
            resp.setRespCode(RespCodeEnum.SUCCESS);
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
        }
        files.add(fileName);
        files.add(filePath);
        resp.setData(files);


        return resp;
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = Tools.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = Tools.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
//        if (dirFile.delete()) {
//            System.out.println("删除目录" + dir + "成功！");
//            return true;
//        } else {
//            return false;
//        }
        return true;
    }

    public static List<String> getByteSize(double byteSize) {
        List<String> strs = new ArrayList<>();
        String data = "";
        String unit = "";
        if (byteSize > Math.pow(1024, 1) && byteSize < Math.pow(1024, 2)) {
            data = new BigDecimal(byteSize / Math.pow(1024, 1)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "KB";
        }else if (byteSize > Math.pow(1024, 2) && byteSize < Math.pow(1024, 3)) {
            data = new BigDecimal(byteSize / Math.pow(1024, 2)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "MB";
        } else if (byteSize > Math.pow(1024, 3) && byteSize < Math.pow(1024, 4)) {
            data = new BigDecimal(byteSize / Math.pow(1024, 3)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "GB";
        } else if (byteSize > Math.pow(1024, 4) && byteSize < Math.pow(1024, 5)) {
            data = new BigDecimal(byteSize / Math.pow(1024, 4)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "TB";
        } else if (byteSize > Math.pow(1024, 5) && byteSize < Math.pow(1024, 6)) {
            data = new BigDecimal(byteSize / Math.pow(1024, 5)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "PB";
        } else if (byteSize > Math.pow(1024, 6) && byteSize < Math.pow(1024, 7)) {
            data = new BigDecimal(byteSize / Math.pow(1024, 6)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "EB";
        } else if (byteSize > Math.pow(1024, 7) && byteSize < Math.pow(1024, 8)) {
            data = new BigDecimal(byteSize / Math.pow(1024, 7)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "ZB";
        } else if (byteSize < 1024) {
            data = new BigDecimal(byteSize ).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
            unit = "B";
        } else {
            data = "数据超过数据大小ZB:" + byteSize;
        }
        strs.add(data);
        strs.add(unit);

        return strs;
    }

    public static List<String> getIpAndPort(String ipAll,int model){
        List<String> ipAndPorts = new ArrayList<>();
        List<String> ipss = new ArrayList<>();
        if (ipAll.contains(";")) {
            String[] ips = ipAll.split(";");
            ipss = Arrays.asList(ips);
        }else{
            ipss.add(ipAll);
        }
        if (model == 0) {
            for (String ip : ipss) {
                ipAndPorts.add(ip);
            }
        }else{
            for (String ip : ipss) {
                String ipNotPort = ip.split(":")[0];
                ipAndPorts.add(ipNotPort);
            }
        }
        return ipAndPorts;
    }
}
