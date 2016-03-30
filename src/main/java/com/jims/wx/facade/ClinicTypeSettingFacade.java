package com.jims.wx.facade;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.jims.wx.BaseFacade;
import com.jims.wx.entity.ClinicTypeSetting;
import com.jims.wx.vo.BeanChangeVo;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ClinicTypeSettingFacade extends BaseFacade {
    /**
     * 名称模糊查询
     * @param name,hospitalId
     * @return
     */
    public List<ClinicTypeSetting> findAll(String name, String hospitalId) {
        String hql = "from  ClinicTypeSetting cs where 1=1";
        if (null != name && !name.trim().equals("")) {
            hql += " and cs.clinicType like '%" + name.trim() + "%'";
        }
        if (null != hospitalId && !hospitalId.trim().equals("")) {
            hql += " and cs.hospitalId ='" + hospitalId.trim() + "'";
        }
        return entityManager.createQuery(hql).getResultList();
    }

    /**
     * 保存增删改
     *
     * @param beanChangeVo
     */
    @Transactional
    public List<ClinicTypeSetting> save(BeanChangeVo<ClinicTypeSetting> beanChangeVo) {
        List<ClinicTypeSetting> newUpdateDict = new ArrayList<>();
        List<ClinicTypeSetting> inserted = beanChangeVo.getInserted();
        List<ClinicTypeSetting> updated = beanChangeVo.getUpdated();
        List<ClinicTypeSetting> deleted = beanChangeVo.getDeleted();
        for (ClinicTypeSetting dict : inserted) {
            ClinicTypeSetting merge = merge(dict);
            newUpdateDict.add(merge);
        }

        for (ClinicTypeSetting dict : updated) {
            ClinicTypeSetting merge = merge(dict);
            newUpdateDict.add(merge);
        }

        List<String> ids = new ArrayList<>();

        for (ClinicTypeSetting dict : deleted) {
            ids.add(dict.getId());
            newUpdateDict.add(dict);
        }
        this.removeByStringIds(ClinicTypeSetting.class, ids);
        return newUpdateDict;
    }
}
