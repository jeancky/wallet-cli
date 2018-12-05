package com.tronyes.demo.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SystemDao extends BaseDao {

    public static final int KEY_TOTAL_REWARD = 1;
    public static final int KEY_FRONT_STATE  = 2;

    public Long id;
    public String name;
    public Integer key;
    public Long val_int;
    public String val_str;
    public Integer state;
    public Timestamp c_t;
    public Timestamp u_t;


    public static SystemDao getByKey(Integer key) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("`key` = ", key);
        return getOne(qs, conds);
    }

    public static int saveByKey(Integer key, long val_int) throws ApiException {
        Map<String, Object> values = new HashMap<>();
        values.put("val_int", val_int);

        Map<String, Object> conds = new HashMap<>();
        conds.put("`key` = ", key);
        return update(qs, values, conds);
    }

    protected static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "m_sys");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new SystemDao()));
        qs.put(KEY_COLUMNS, new String[]{"id", "name", "`key`", "val_int", "val_str", "state", "c_t", "u_t"});
    }
}
