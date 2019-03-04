package com.sailing.dscg.dao;

import com.sailing.dscg.entity.warnMessage.WarnMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description:
 * @Auther:史俊华
 * @Date:2018/10/611
 */
@Mapper
public interface IWarnMessageDao {
    @Delete("delete from log_warnmessage")
    int deleteAll();

    @Select("select code " +
            "from log_warnmessage " +
            "GROUP BY code")
    List<Integer> getCodes();

    @Select({"<script> " +
            "select * from log_warnmessage where 1=1 " +
            "<if test='ipAddress!=null'> " +
            " and ipAddress=#{ipAddress} " +
            "</if> " +
            "<if test='verifyType!=null'> " +
            " and verifyType=#{verifyType} " +
            "</if> " +
            "<if test='startDate!=null'> " +
            " and createTime <![CDATA[>=]]> #{startDate} " +
            "</if> " +
            "<if test='endDate!=null'> " +
            " and createTime <![CDATA[<=]]> #{endDate} " +
            "</if> " +
            " order by verifyDate desc " +
            "<if test='pageSize!=null and pageNum!=null'>" +
            " LIMIT #{pageNum},#{pageSize}" +
            "</if>" +
            "</script> "})
    List<WarnMessage> queryList(WarnMessage warnMessage);

    @Select({"<script> " +
            "select count(id) from log_warnmessage where 1=1 " +
            "<if test='ipAddress!=null'> " +
            " and ipAddress=#{ipAddress} " +
            "</if> " +
            "<if test='verifyType!=null'> " +
            " and verifyType=#{verifyType} " +
            "</if> " +
            "<if test='startDate!=null'> " +
            " and createTime <![CDATA[>=]]> #{startDate} " +
            "</if> " +
            "<if test='endDate!=null'> " +
            " and createTime <![CDATA[<=]]> #{endDate} " +
            "</if> " +
            " order by verifyDate desc " +
            "</script> "})
    int count(WarnMessage warnMessage);

    @Delete("delete from log_auditcounthour where id=#{id}")
    int delete(@Param("id") String id);
}
