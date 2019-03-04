package com.sailing.dscg.entity.vendor;

import com.sailing.dscg.zookeeper.Node;
import lombok.Data;

import java.util.Date;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/11/7 下午 01:42:45
 */
@Data
@Node(name = "sys_Vendor")
public class Vendor {

    private String id;
    //平台编号
    private String ptbh;
    //平台名称
    private String ptmc;
    //负责人
    private String fzr;
    //联系方式
    private String lxfs;
    //地区代码
    private String dqdm;
    //业务部门
    private String ywbm;
    //链路代码
    private String lldm;
    //承建厂商
    private String cjcs;
    //内网IP
    private String nbllip1;
    private String nbllip2;
    private String nbllip3;
    //外网IP
    private String wbllip1;
    private String wbllip2;
    private String wbllip3;
    //专线类型
    private String zxlx;
    //外部专线IP
    private String wbzxip1;
    private String wbzxip2;
    private String wbzxip3;
    //更新时间
    private Date gxsj;
    //入库时间
    private Date rksj;




}
