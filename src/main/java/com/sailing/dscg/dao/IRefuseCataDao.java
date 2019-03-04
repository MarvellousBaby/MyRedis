package com.sailing.dscg.dao;

import com.sailing.dscg.entity.monitoring.RefuseCata;
import org.apache.ibatis.annotations.*;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/11/8 上午 09:44:06
 */
@Mapper
public interface IRefuseCataDao {


    @Select({"select * from server_strategy where serviceId = #{serviceId}"})
    RefuseCata getRefuseCata(@Param("serviceId") String serviceId);

    @Insert({"insert into server_strategy(serviceId,refuse) values(#{serviceId},#{refuse})"})
    int insert(RefuseCata refuseCata);

    @Results({@Result(property = "serviceId", column = "serviceId"),
            @Result(property = "refuse", column = "refuse")})
    @Update({"update server_strategy set refuse = #{refuse} where serviceId = #{serviceId}"})
    int update(RefuseCata refuseCata);

    @Delete({"delete from server_strategy where serviceId = #{serviceId}"})
    int delete(@Param("serviceId") String serviceId);
}
