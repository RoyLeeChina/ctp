package org.hotwheel.weixin.bean;

/**
 * Created by wangfeng on 2017/3/27.
 * @version 1.0.2
 */
public class SyncKeyEntity {
    private int Count;
    /**
     * Key : 1
     * Val : 641466310
     */

    private java.util.List<ListEntity> List;

    public void setCount(int Count) {
        this.Count = Count;
    }

    public void setList(java.util.List<ListEntity> List) {
        this.List = List;
    }

    public int getCount() {
        return Count;
    }

    public java.util.List<ListEntity> getList() {
        return List;
    }

    public static class ListEntity {
        private int Key;
        private int Val;

        public void setKey(int Key) {
            this.Key = Key;
        }

        public void setVal(int Val) {
            this.Val = Val;
        }

        public int getKey() {
            return Key;
        }

        public int getVal() {
            return Val;
        }
    }
}
