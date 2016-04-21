package com.jims.wx.facade;

import com.google.inject.persist.Transactional;
import com.jims.wx.BaseFacade;
import com.jims.wx.entity.AppUser;
import com.jims.wx.entity.AppUserGroups;
import weixin.popular.bean.user.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2016/3/14.
 */
public class AppUserFacade extends BaseFacade {
    private EntityManager entityManager;

    @Inject
    public AppUserFacade(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * 保存关注人信息到数据库
     * @param obj
     * @return
     */
    @Transactional
    public void save(List<AppUser> obj) {
        //删除所有同步前的数据
        List<AppUser> deleteData = super.findAll(AppUser.class);
        if (deleteData != null && deleteData.size() > 0) {
            List<String> ids = new ArrayList<>();
            for (AppUser temp : deleteData) {
                ids.add(temp.getId());
            }
            super.removeByStringIds(AppUser.class, ids);
        }
        //保存新同步来的数据
        if(obj != null && obj.size() > 0){
            for (AppUser user:obj) {
                super.merge(user);
            }
        }
    }

    /**
     * 修改备注名
     * @param user
     */
    @Transactional
    public AppUser updateTip(AppUser user) {
        AppUser old = get(AppUser.class,user.getId());
        old.setRemark(user.getRemark());
        user = super.merge(old);
        return user;
    }
    /**
     * 根据分组groupId查找用户
     * @param groupId
     * @return
     */
    public List<AppUser> findByGroupId(String groupId){
        String hql = "FROM AppUser u WHERE 1=1";
        if (groupId != null && !groupId.equals("")) {
            hql += " and u.groupId = '" + groupId + "'";
        }

        List<AppUser> result = entityManager.createQuery(hql).getResultList();
        return result;
    }

    /**
     * 根据分组openId查找用户
     * @param openId
     * @return
     */
    public List<AppUser> findByOpenId(String openId){
        String hql = "FROM AppUser u WHERE 1=1";
        if (openId != null && !openId.equals("")) {
            hql += " and u.openId = '" + openId + "'";
        }

        List<AppUser> result = entityManager.createQuery(hql).getResultList();
        return result;
    }

    @Transactional
    public void createUser(User user) {
        String hql = "from AppUser as user where user.openId = '"+user.getOpenid()+"'" ;
        AppUser appUser =null ;
        List<AppUser> appUsers = createQuery(AppUser.class,hql,new ArrayList<Object>()).getResultList() ;
        if(appUsers.size()>0){
            appUser = appUsers.get(0) ;
        }else{
            appUser = new AppUser() ;
        }

        appUser.setCity(user.getCity());
        appUser.setCountry(user.getCountry());
        appUser.setGroupId(user.getGroupid());
        appUser.setHeadImgUrl(user.getHeadimgurl());
        appUser.setLanguage(user.getLanguage());
        appUser.setNickName(user.getNickname());
        appUser.setOpenId(user.getOpenid());
        appUser.setProvince(user.getProvince());
        appUser.setSex(user.getSex());
        appUser.setRemark(user.getRemark());
        appUser.setSubscribe(user.getSubscribe());
        appUser.setSubscrbeTime(user.getSubscribe_time());
        merge(appUser) ;
    }

    /***
     * 修改用户的分组
     *  @param openId
     * @param targetId
     * @param currentId
     */
    @Transactional
    public void updateGroup(String openId, String targetId, String currentId) {
        AppUser appUser = getAppUserByOpenId(openId) ;
        AppUserGroups appUserGroups = getAppuserGroupsByGroupId(targetId) ;
        AppUserGroups currentGroups = getAppuserGroupsByGroupId(currentId) ;
        if(appUser !=null){
            appUser.setGroupId(Integer.parseInt(targetId));
            appUserGroups.setCount(appUserGroups.getCount() +1 );
            currentGroups.setCount(appUserGroups.getCount() -1 );
            merge(appUser) ;
            merge(appUserGroups) ;
            merge(currentGroups) ;
        }
    }

    /**
     * 根据分组Id找到对应的分组
     * @param targetId
     * @return
     */
    private AppUserGroups getAppuserGroupsByGroupId(String targetId) {
        String hql = "from AppUserGroups as group where group.groupId = '"+targetId+"'" ;
        List<AppUserGroups> appUserGroupses = createQuery(AppUserGroups.class,hql,new ArrayList<Object>()).getResultList() ;
        if(appUserGroupses.size()>0){
            return appUserGroupses.get(0) ;
        }
        return null;
    }

    /***
     * 根据微信的OPENID找对应的用户
     * @param openId
     * @return
     */
    private AppUser getAppUserByOpenId(String openId) {
        String hql = "from AppUser as user where user.openId = '"+openId+"'" ;
        List<AppUser> appUsers = createQuery(AppUser.class,hql,new ArrayList<Object>()).getResultList() ;
        if(appUsers.size()>0){
            return appUsers.get(0) ;
        }
        return null;
    }




}
