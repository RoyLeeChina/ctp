package org.hotwheel.stock.dao;

import org.hotwheel.stock.model.User;

/**
 * 用户操作接口
 *
 * Created by wangfeng on 2017/3/13.
 * @version 1.0.0
 */
public interface IStockUser {
    /**
     * 创建一个新用户
     * @param user
     * @return
     */
    public int insert(User user);
}
