package com.sailing.dscg.dao;


import com.sailing.dscg.entity.Servicelog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description:
 * @Auther:史俊华
 * @Date:2018/9/3013
 */
@Mapper
public interface IServiceLogDao {
    @Select({"select * from log_operationlog"})
    List<Servicelog> queryAll();

    @Select({"<script> " +
            "select * from log_operationlog where 1=1 " +
            "<if test='accessIp!=null and accessIp!=\"\"'> " +
            " and accessIp like CONCAT('%',#{accessIp}, '%')" +
            "</if> " +
            "<if test='loginName!=null and loginName!=\"\"'> " +
            " and loginName like CONCAT('%',#{loginName}, '%')" +
            "</if> " +
            "<if test='userName!=null and userName!=\"\"'> " +
            " and userName like CONCAT('%',#{userName}, '%')" +
            "</if> " +
            "<if test='roleName!=null and roleName!=\"\"'> " +
            " and roleName like CONCAT('%',#{roleName}, '%')" +
            "</if> " +
            "<if test='operateType!=null and operateType!=\"\"'> " +
            " and operateType = #{operateType}" +
            "</if> " +
            "<if test='operateType==null or operateType==\"\"'> " +
            " and operateType != '' " +
            "</if> " +
            "<if test='stateMsg!=null and stateMsg==\"异常\" and stateMsg!=\"\"'> " +
            " and stateMsg != '成功' " +
            "</if> " +
            "<if test='stateMsg!=null and stateMsg!=\"异常\" and stateMsg!=\"\"'> " +
            " and stateMsg = #{stateMsg} " +
            "</if> " +
            "<if test='flag==0'> " +
            " and operateType != '管理员登陆' " +
            "</if> " +
            "<if test='flag==1'> " +
            " and operateType = '管理员登陆' " +
            "</if> " +
            "<if test='startDate!=null'> " +
            " and createTime <![CDATA[>=]]> #{startDate} " +
            "</if> " +
            "<if test='endDate!=null'> " +
            " and createTime <![CDATA[<=]]> #{endDate} " +
            "</if> " +
            " order by createTime desc " +
            "<if test='pageSize!=null and pageNum!=null'>" +
            " LIMIT #{pageNum},#{pageSize}" +
            "</if>" +
            "</script> "})
    List<Servicelog> queryList(Servicelog servicelog);

    @Select({"<script> " +
            "select count(id) from log_operationlog where 1=1 " +
            "<if test='accessIp!=null and accessIp!=\"\"'> " +
            " and accessIp like CONCAT('%',#{accessIp}, '%')" +
            "</if> " +
            "<if test='loginName!=null and loginName!=\"\"'> " +
            " and loginName like CONCAT('%',#{loginName}, '%')" +
            "</if> " +
            "<if test='userName!=null and userName!=\"\"'> " +
            " and userName like CONCAT('%',#{userName}, '%')" +
            "</if> " +
            "<if test='roleName!=null and roleName!=\"\"'> " +
            " and roleName like CONCAT('%',#{roleName}, '%')" +
            "</if> " +
            "<if test='operateType!=null and operateType!=\"\"'> " +
            " and operateType = #{operateType}" +
            "</if> " +
            "<if test='operateType==null or operateType==\"\"'> " +
            " and operateType != '' " +
            "</if> " +
            "<if test='stateMsg!=null and stateMsg==\"异常\" and stateMsg!=\"\"'> " +
            " and stateMsg != '成功' " +
            "</if> " +
            "<if test='stateMsg!=null and stateMsg!=\"异常\" and stateMsg!=\"\"'> " +
            " and stateMsg = #{stateMsg} " +
            "</if> " +
            "<if test='flag==0'> " +
            " and operateType != '管理员登陆' " +
            "</if> " +
            "<if test='flag==1'> " +
            " and operateType = '管理员登陆' " +
            "</if> " +
            "<if test='startDate!=null'> " +
            " and createTime <![CDATA[>=]]> #{startDate} " +
            "</if> " +
            "<if test='endDate!=null'> " +
            " and createTime <![CDATA[<=]]> #{endDate} " +
            "</if> " +
            " order by createTime desc " +
            "</script> "})
    int count(Servicelog servicelog);

    @Select({"select operateType from log_operationlog where operateType!='管理员登陆' group by operateType "})
    List<String> getOperateTypes();

    @Select({"select * from log_operationlog where id=#{id} "})
    Servicelog get(Servicelog servicelog);

    @Select({"select count(id) from log_operationlog where operateType=#{operateType} "})
    long getOperateTypeCount(Servicelog servicelog);

    @Insert({ "insert into log_operationlog(id, stateMsg, loginName, userName,roleName,accessIp,accessPath," +
            "accessMethod,operateType,accessTime,operateContent,createUser,createTime) " +
            "values(#{id}, #{stateMsg}, #{loginName}, #{userName}, #{roleName},#{accessIp},#{accessPath}," +
            "#{accessMethod},#{operateType},#{accessTime,jdbcType=TIMESTAMP},#{operateContent},#{createUser},#{createTime, jdbcType=TIMESTAMP})" })
    int insert(Servicelog servicelog);

    @Delete("delete from log_operationlog where id=#{id}")
    int delete(Servicelog servicelog);
    @Delete("delete from log_operationlog where operateType!='管理员登陆'")
    int deleteAll();
    @Delete("delete from log_operationlog where operateType='管理员登陆'")
    int delAllLoginLog();

}
