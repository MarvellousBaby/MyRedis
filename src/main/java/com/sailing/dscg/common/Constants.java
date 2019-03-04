package com.sailing.dscg.common;

public class Constants {

    public static final String PRIVATEKEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALAuVd6FOfZ2DlyY5EM9Lw1SzbvrJdKZDa0naq3" +
            "Jbu39m1QUiXwa2TKMidMbu/LtHuTQXqh2RBm0/j6nFbjURt54/C73Ek72K/I9ctR2klK1GvKjYByGX+i3Gp6RZaoBU0XqaDqBf04SCQih8PlvQC0Ll+7Rpbq9" +
            "UVxuxREnv/S5AgMBAAECgYBRbiksP2GqA6jhrZ0bYuUjgFks+SOzMiC7HpPVHVxTTbuEYjWlkfc/wOtXEuONBYIY+iI2NOuHAjvdE3cbxam3Fxsgfj1f5479dQ" +
            "tIduovKcP9pHH7f3VigUq1ZKPAHkBTB0i00u0qK1/J9RaYVJ5BNyKrGnn6HPafItLDpARRAQJBAOfYEB1MJn+N46bysaUMQG7+Ps5BEyFDGF1Xtr99II28vdJBR" +
            "mg9C5yOS0UDH0D9w9hx0lucrDYJ8yTql7eE3JkCQQDCiZUZVFEskUlvNDRpmbHcejJFAOH7igocJAXRvx5ulusDJxz4918P63yGdn2AV60LQmvTl1jkdRcLamfNY" +
            "c0hAkA73tPqfOvELMQJLMsrMArDnuxhHvTttgHouOn8cTei4oaycSDZcko5V5WgcLn1cpLMpjgIaGEgY1liwyICxlChAkEApLnnfMk+6yQjMHzeHMU+LFl0k6WJt" +
            "XtP0EBYhSD2XeKBIexOxiqBXnOiwCE5aCHVr4EQ2NyBMwJK/ymct/amwQJBAKPuVPVXXxa/dLFPrYvIcFt9q1djn2yPN6/OVjEGJHUFSuvw5QO9LyDB7zb9/NIHUYy+PSrtFkMceD9R+A3Vg8c=";
    public static final String PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwLlXehTn2dg5cmORDPS8NUs276yXSmQ2tJ2qtyW7t/ZtUFIl8GtkyjInTG7vy" +
            "7R7k0F6odkQZtP4+pxW41EbeePwu9xJO9ivyPXLUdpJStRryo2Achl/otxqekWWqAVNF6mg6gX9OEgkIofD5b0AtC5fu0aW6vVFcbsURJ7/0uQIDAQAB";

    /****本地服务基本端口*/
    public static final Integer PORT_ZOOKEEPER = 2181;
    public static final Integer PORT_BUS = 8000;



    /***集群配置类型常量*/
    public static final String CLUSTER_CONFIG_TYPE_VSCG = "Node";
    public static final String CLUSTER_CONFIG_TYPE_GATEWAY = "gateway";

    /***交换配置状态常量*/
    public static final String UNDEPLOY = "undeploy";
    public static final String DEPLOY = "deploy";
    public static final String STARTUP = "startup";
    public static final String START = "start";
    public static final String STOP = "stop";

    /***集群节点类型*/
    public static final String INOUT_IN = "in";
    public static final String INOUT_OUT = "out";

    /**网闸类型**/
    public static final String SIGNAL = "signal";
    public static final String VIDEO = "video";

    /**本地或者异地网闸**/
    public static final String LOCATE = "locate";
    public static final String ALLO = "allo";
    public static final String LOCAL = "local";


    /***节点连接类型*/
    public static final String CONNSTATUS_TYPE_REQP = "reqP"; //请求平台
    public static final String CONNSTATUS_TYPE_REQNODE = "reqNode"; //请求节点
    public static final String CONNSTATUS_TYPE_GATEWAY = "gateway"; //网闸
    public static final String CONNSTATUS_TYPE_RESPNODE = "respNode"; //响应节点
    public static final String CONNSTATUS_TYPE_RESPP = "respP"; //响应平台
}
