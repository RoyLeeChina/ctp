package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.UserInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户操作接口
 *
 * Created by wangfeng on 2017/3/13.
 * @version 1.0.0
 */
@Repository("stockUser")
public interface IStockUser {

    public List<UserInfo> selectAll() throws DataAccessException;
    public UserInfo select(@Param("phone") String phone) throws DataAccessException;

    public UserInfo selectByWeixin(@Param("weixin")String weixin) throws DataAccessException;

    /**
     * 创建一个新用户
     * @param user
     * @return
     */
    public int insert(UserInfo user) throws DataAccessException;

    public int update(UserInfo user) throws DataAccessException;
}
