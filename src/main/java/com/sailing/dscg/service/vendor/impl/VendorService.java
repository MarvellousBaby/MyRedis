package com.sailing.dscg.service.vendor.impl;

import com.sailing.dscg.common.Tools;
import com.sailing.dscg.entity.vendor.Vendor;
import com.sailing.dscg.service.vendor.IVendorService;
import com.sailing.dscg.zookeeper.ZookeeperServer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/11/7 下午 02:36:23
 */
@Service
public class VendorService implements IVendorService {

    @Autowired
    private ZookeeperServer<Vendor> zookeeperServer;

    @Override
    public List<Vendor> queryAll() throws Exception {
        return zookeeperServer.queryAll(Vendor.class);
    }

    @Override
    public List<Vendor> queryList(Vendor vendor) throws Exception {
        return null;
    }

    @Override
    public Vendor get(Vendor vendor) throws Exception {
        return null;
    }

    @Override
    public Boolean save(Vendor vendor) throws Exception {
        if (StringUtils.isNotBlank(vendor.getId())) {
            return zookeeperServer.update(vendor.getId(), vendor, Vendor.class);
        } else {
            vendor.setId(Tools.getUUID());
            return zookeeperServer.create(vendor.getId(), vendor, Vendor.class);
        }
    }

    @Override
    public Boolean delete(Vendor vendor) throws Exception {
        return null;
    }

    @Override
    public Boolean deleteAll() throws Exception {
        return null;
    }
}
