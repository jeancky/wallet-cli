package com.tronyes.lottery.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRoundDao extends BaseDao {

    private Long id;
    private Long gr_id;
    private Long u_id;
    private String address;    // 用户的 address
    private Integer bet_state;  // 1:进行中，2:已结束
    private String bet_id;
    private Long bet_val;
    private String bet_tx;
    private String bet_num;
    private Integer lot_type; // 0: 未中奖，123等奖
    private String lot_tx;
    private Long lot_val;
    private String lot_num;
    private Integer rwd_state;
    private String rwd_err;
    private Timestamp c_t;

    public static Long insert(Map<String, Object> values, String onDup) throws ApiException {
        return insert((String)qs.get(KEY_TABLENAME), values, onDup);
    }

    public static List<UserRoundDao> getUserGameRoundList(Long uid, Long gr_id) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("uid = ", uid);
        conds.put("gr_id = ", gr_id);
        return getListByCond(conds, " id DESC ", 10, 0);
    }

    public static List<UserRoundDao> getListByCond(Map<String, Object>conds, String orderBy, Integer limit, Integer start) throws ApiException {
        return getList(qs, conds, orderBy, limit, start);
    }

    public static UserRoundDao getById(Long iid) throws ApiException {
        return getOne(qs, iid);
    }

    public static Map<String, Object> packageUserRound(UserRoundDao dao) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("gr_id", dao.getGr_id());
        obj.put("uid", dao.getU_id());
        obj.put("addr", dao.getAddress());
        obj.put("bet_val", dao.getBet_val());
        obj.put("bet_id", dao.getBet_id());
        obj.put("bet", dao.getBet_num());
        obj.put("state", dao.getBet_state());
        obj.put("lottery", dao.getLot_type());
        obj.put("lot_val", dao.getLot_val());
        obj.put("result", dao.getLot_num());
        obj.put("rwd_state", dao.getRwd_state());
        obj.put("ts", dao.getC_t());
        return obj;
    }

    protected static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "m_user_round");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new UserRoundDao()));
        qs.put(KEY_COLUMNS, new String[]{"id", "gr_id", "u_id", "address", "bet_state", "bet_id", "bet_val", "bet_tx", "lot_type", "lot_tx", "lot_val", "bet_num", "lot_num", "rwd_state", "rwd_err", "c_t", "u_t"});
    }

    //////////////////////////////
    public Long getId() {
        return id;
    }

    public UserRoundDao setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getGr_id() {
        return gr_id;
    }

    public UserRoundDao setGr_id(Long gr_id) {
        this.gr_id = gr_id;
        return this;
    }

    public Long getU_id() {
        return u_id;
    }

    public UserRoundDao setU_id(Long u_id) {
        this.u_id = u_id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserRoundDao setAddress(String address) {
        this.address = address;
        return this;
    }

    public Integer getBet_state() {
        return bet_state;
    }

    public UserRoundDao setBet_state(Integer bet_state) {
        this.bet_state = bet_state;
        return this;
    }

    public String getBet_id() {
        return bet_id;
    }

    public UserRoundDao setBet_id(String bet_id) {
        this.bet_id = bet_id;
        return this;
    }

    public Long getBet_val() {
        return bet_val;
    }

    public UserRoundDao setBet_val(Long bet_val) {
        this.bet_val = bet_val;
        return this;
    }

    public String getBet_tx() {
        return bet_tx;
    }

    public UserRoundDao setBet_tx(String bet_tx) {
        this.bet_tx = bet_tx;
        return this;
    }

    public String getBet_num() {
        return bet_num;
    }

    public UserRoundDao setBet_num(String bet_num) {
        this.bet_num = bet_num;
        return this;
    }

    public Integer getLot_type() {
        return lot_type;
    }

    public UserRoundDao setLot_type(Integer lot_type) {
        this.lot_type = lot_type;
        return this;
    }

    public String getLot_tx() {
        return lot_tx;
    }

    public UserRoundDao setLot_tx(String lot_tx) {
        this.lot_tx = lot_tx;
        return this;
    }

    public Long getLot_val() {
        return lot_val;
    }

    public UserRoundDao setLot_val(Long lot_val) {
        this.lot_val = lot_val;
        return this;
    }

    public String getLot_num() {
        return lot_num;
    }

    public UserRoundDao setLot_num(String lot_num) {
        this.lot_num = lot_num;
        return this;
    }

    public Integer getRwd_state() {
        return rwd_state;
    }

    public UserRoundDao setRwd_state(Integer rwd_state) {
        this.rwd_state = rwd_state;
        return this;
    }

    public String getRwd_err() {
        return rwd_err;
    }

    public UserRoundDao setRwd_err(String rwd_err) {
        this.rwd_err = rwd_err;
        return this;
    }

    public Timestamp getC_t() {
        return c_t;
    }

    public UserRoundDao setC_t(Timestamp c_t) {
        this.c_t = c_t;
        return this;
    }
}
