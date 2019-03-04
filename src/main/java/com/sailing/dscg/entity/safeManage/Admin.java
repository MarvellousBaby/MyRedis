package com.sailing.dscg.entity.safeManage;

import com.sailing.dscg.entity.BaseEntity;
import com.sailing.dscg.zookeeper.Node;
import lombok.Data;
import java.util.Date;

/*
 * 管理员实体类
 *  @author 李帅
 *  @version 2018-04-19
 *  @param username 管理员登录名
 *  @param passwd 登录密码
 *  @param type 管理员级别
 *  @param realname 管理员中文名
 *  @param mobile 手机手机号码
 *  @param state 状态标记 默认0=表示可用；1=禁用
 *  @param rightlist 权限列表
 *  @param createtime 创建时间（系统时间）
 *  */

@Data
@Node(name="safeMng_Admin")
public class Admin extends BaseEntity {
    private String loginName;        //username - - loginName
    private String passwd;
    private Role role;
    private String userName;        //realname - - userName
    private String mobile;
    private Integer status;            //0-启用，1-禁用
    private String rightlist;
    private String createtime;
    private String authCode;
    private Integer failCount;
    private Date lastLoginTime;

}
