package org.tron.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTxDao extends BaseDao {

    private Long id;
    private Long u_id;
    private Long t_id;
    private Integer t_type;
    private Long val;
    private Integer plat;
    private String tx_id;
    private Integer tx_state;
    private Timestamp c_t;
    private Timestamp u_t;

    public static long insert(UserTxDao tx) throws ApiException {
        Map<String, Object> values = new HashMap<>();
        values.put("u_id", tx.u_id);
        values.put("t_id", tx.t_id);
        values.put("t_type", tx.t_type);
        values.put("val", tx.val);
        values.put("plat", tx.plat);
        values.put("tx_id", tx.tx_id);
        values.put("tx_state", tx.tx_state);

        return insert((String)qs.get(KEY_TABLENAME), values, "id = VALUES(`id`)");
    }

    public static List<UserTxDao> getListByConds(Map<String, Object>conds, String orderBy, Integer limit, Integer start) throws ApiException {
        return getList(qs, conds, orderBy, limit, start);
    }

    public static UserTxDao getByConds(Map<String, Object>conds, String orderBy) throws ApiException {
        return getOne(qs, conds, orderBy);
    }

    public static int update(Map<String, Object> valus, Map<String, Object> conds) throws ApiException {
        return update(qs, valus, conds);
    }

    public static int update(Map<String, Object> valus, String conds) throws ApiException {
        return update(qs, valus, conds);
    }

    protected static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "m_user_tx");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new UserTxDao()));
        qs.put(KEY_COLUMNS, new String[]{"id", "u_id", "t_id", "t_type", "val", "plat", "tx_id", "tx_state", "c_t", "u_t"});
    }

    public Long getId() {
        return id;
    }

    public UserTxDao setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getU_id() {
        return u_id;
    }

    public UserTxDao setU_id(Long u_id) {
        this.u_id = u_id;
        return this;
    }

    public Long getT_id() {
        return t_id;
    }

    public UserTxDao setT_id(Long t_id) {
        this.t_id = t_id;
        return this;
    }

    public Integer getT_type() {
        return t_type;
    }

    public UserTxDao setT_type(Integer t_type) {
        this.t_type = t_type;
        return this;
    }

    public Long getVal() {
        return val;
    }

    public UserTxDao setVal(Long val) {
        this.val = val;
        return this;
    }

    public Integer getPlat() {
        return plat;
    }

    public UserTxDao setPlat(Integer plat) {
        this.plat = plat;
        return this;
    }

    public String getTx_id() {
        return tx_id;
    }

    public UserTxDao setTx_id(String tx_id) {
        this.tx_id = tx_id;
        return this;
    }

    public Integer getTx_state() {
        return tx_state;
    }

    public UserTxDao setTx_state(Integer tx_state) {
        this.tx_state = tx_state;
        return this;
    }

    public Date getC_t() {
        return c_t;
    }

    public UserTxDao setC_t(Timestamp c_t) {
        this.c_t = c_t;
        return this;
    }

    public Date getU_t() {
        return u_t;
    }

    public UserTxDao setU_t(Timestamp u_t) {
        this.u_t = u_t;
        return this;
    }
}
