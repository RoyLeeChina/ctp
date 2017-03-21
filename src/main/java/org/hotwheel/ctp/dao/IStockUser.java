package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.User;
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

    public List<User> selectAll() throws DataAccessException;
    public User select(@Param("phone") String phone) throws DataAccessException;

    /**
     * 创建一个新用户
     * @param user
     * @return
     */
    public int insert(User user) throws DataAccessException;

    public int update(User user) throws DataAccessException;
}
