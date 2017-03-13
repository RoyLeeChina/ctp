package org.hotwheel.stock.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.stock.model.User;
import org.springframework.stereotype.Repository;

/**
 * 用户操作接口
 *
 * Created by wangfeng on 2017/3/13.
 * @version 1.0.0
 */
@Repository("stockUser")
public interface IStockUser {

    public User select(@Param("phone") String phone);

    /**
     * 创建一个新用户
     * @param user
     * @return
     */
    public int insert(User user);
}
