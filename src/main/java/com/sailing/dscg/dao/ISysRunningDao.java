package com.sailing.dscg.dao;

import com.sailing.dscg.entity.sysRunning.RouteAudit;
import com.sailing.dscg.entity.sysRunning.SysRunning;
import com.sailing.dscg.entity.videoTransmission.VideoTransmission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/19 上午 10:18:58
 */
@Mapper
public interface ISysRunningDao {


    @Select({"<script> " +
            "select count(*) " +
            "from log_routeaudit a inner join  (select * from log_videotransmission where callID in  " +
            "(select callID from log_videotransmission group by callID having count(callID) = 2 order by callID) order by transportStartTime desc) b  " +
            "on a.callId = b.callID " +
            "where 1=1 "+
            "<if test='serviceIds!=null'> " +
            " and a.serviceID in(${serviceIds}) " +
            "</if> " +
            "<if test='transferPaths!=null'> " +
            " and a.requestIPPort in(${transferPaths}) " +
            "</if> " +
            "<if test='callIds!=null'> " +
            " and a.callId in (${callIds}) " +
            "</if> " +
            "order by a.requestTime desc,a.id" +
            "</script> "})
    long getSysRunListCount(SysRunning sysRun);

    @Select({"<script> " +
            "select a.id,a.serviceID,a.requestIPPort,b.transportStartTime,b.transportStopTime,b.transportBitStream,b.requestType " +
            "from log_routeaudit a inner join  (select * from log_videotransmission where callID in " +
            "(select callID from log_videotransmission group by callID having count(callID) = 2 order by callID) order by transportStartTime desc) b " +
            "on a.callId = b.callID " +
            "where 1=1 "+
            "<if test='serviceIds!=null'> " +
            " and a.serviceID in (${serviceIds}) " +
            "</if> " +
            "<if test='transferPaths!=null'> " +
            " and a.requestIPPort in (${transferPaths}) " +
            "</if> " +
            "<if test='callIds!=null'> " +
            " and a.callId in (${callIds}) " +
            "</if> " +
            "order by a.requestTime desc,a.id " +
            "limit #{pageNum},#{pageSize} "+
            "</script> "})
    List<SysRunning> getSysRunList(SysRunning sysRun);

    @Select({"SELECT Distinct a.callID FROM  log_videotransmission a where a.callID in (SELECT callID FROM log_videotransmission " +
            "WHERE transportStartTime>=#{transportStartTime}) and a.transportStopTime<=#{transportStopTime}"})
    List<String> getCallIdByDate(SysRunning sysRun);

    @Select({"select * from log_routeaudit"})
    List<RouteAudit> getRoute();

    @Select({"select lv.* from log_videotransmission lv left join log_routeaudit lr on lv.callID = lr.callId where lr.callID = #{callID}"})
    List<VideoTransmission> getVideo(String callID);

    @Delete("delete from log_routeaudit where id=#{id}")
    int delete(String id);

    @Delete("delete from log_routeaudit")
    int deleteAll();


}
