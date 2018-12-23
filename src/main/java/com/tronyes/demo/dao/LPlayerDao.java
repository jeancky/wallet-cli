package com.tronyes.demo.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class LPlayerDao extends BaseDao {

    private Integer id; //int(11) NOT NULL AUTO_INCREMENT,
    private Long u_id;// bigint(20) NOT NULL DEFAULT '0',
    private String address; //varchar(64) NOT NULL,
    private String udid; //varchar(64) NOT NULL,
    private Integer version; //int(11) NOT NULL DEFAULT '0',
    private String crypto; //varchar(512) NOT NULL DEFAULT '',
    private Integer state; //tinyint(3) unsigned NOT NULL DEFAULT '0',
    private Long alb_amt; //bigint(20) NOT NULL DEFAULT '0' COMMENT '可用余额',
    private Long bet_amt; //bigint(20) NOT NULL DEFAULT '0' COMMENT '总投注的 trx, 单位: SUN',
    private Integer bet_5s_cnt; //int(11) NOT NULL DEFAULT '0' COMMENT '总投注次数',
    private Integer bet_5m_cnt; //int(11) NOT NULL DEFAULT '0' COMMENT '总投注次数',
    private Long rwd_amt; //bigint(20) NOT NULL DEFAULT '0' COMMENT '总获奖的 trx, 单位: SUN',
    private Integer rwd_5s_cnt; //int(11) NOT NULL DEFAULT '0' COMMENT '总投注次数',
    private Integer rwd_5m_cnt; //int(11) NOT NULL DEFAULT '0' COMMENT '总投注次数',
    private Integer idx; //int(11) NOT NULL DEFAULT '0' COMMENT '排序, index, 越大越高',
    private Long last_gr_id;// bigint(20) NOT NULL DEFAULT '0',
    private Timestamp lb_t; //datetime NOT NULL COMMENT 'last bet timestamp',
    private Timestamp c_t; //datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    private Timestamp u_t; //datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,


    public long saveObj(boolean autoUpdate) throws ApiException {
        return super.saveObj(qs, autoUpdate);
    }

    public static LPlayerDao getById(Integer id) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("id = ", id);
        return getOne(qs, conds);
    }

    public static LPlayerDao getRandom(Map<String, Object> conds) throws ApiException {
        return getOne(qs, conds, "RAND()");
    }

    public static LPlayerDao getByAddress(String address) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("address = ", address);
        return getOne(qs, conds);
    }

    public static int update(Map<String, Object> valus, Map<String, Object> conds) throws ApiException {
        return update(qs, valus, conds);
    }

    public static int update(Map<String, Object> valus, String conds) throws ApiException {
        return update(qs, valus, conds);
    }

    private static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "l_player");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new LPlayerDao()));
        qs.put(KEY_COLUMNS, new String[]{"id","u_id","address","udid","version","crypto","state", "alb_amt","bet_amt","bet_5s_cnt","bet_5m_cnt","rwd_amt","rwd_5s_cnt","rwd_5m_cnt","idx","last_gr_id","lb_t","c_t","u_t"});
    }

    ///////////////////////
    public Integer getId() {
        return id;
    }

    public LPlayerDao setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public LPlayerDao setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getUdid() {
        return udid;
    }

    public LPlayerDao setUdid(String udid) {
        this.udid = udid;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public LPlayerDao setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public String getCrypto() {
        return StringEscapeUtils.unescapeJavaScript(crypto);
    }

    public LPlayerDao setCrypto(String crypto) {
        this.crypto = StringEscapeUtils.escapeJavaScript(crypto);
        return this;
    }

    public Integer getState() {
        return state;
    }

    public LPlayerDao setState(Integer state) {
        this.state = state;
        return this;
    }

    public Long getBet_amt() {
        return bet_amt;
    }

    public LPlayerDao setBet_amt(Long bet_amt) {
        this.bet_amt = bet_amt;
        return this;
    }

    public Long getRwd_amt() {
        return rwd_amt;
    }

    public LPlayerDao setRwd_amt(Long rwd_amt) {
        this.rwd_amt = rwd_amt;
        return this;
    }

    public Integer getIdx() {
        return idx;
    }

    public LPlayerDao setIdx(Integer idx) {
        this.idx = idx;
        return this;
    }

    public Timestamp getLb_t() {
        return lb_t;
    }

    public LPlayerDao setLb_t(Timestamp lb_t) {
        this.lb_t = lb_t;
        return this;
    }

    public Timestamp getC_t() {
        return c_t;
    }

    public LPlayerDao setC_t(Timestamp c_t) {
        this.c_t = c_t;
        return this;
    }

    public Timestamp getU_t() {
        return u_t;
    }

    public LPlayerDao setU_t(Timestamp u_t) {
        this.u_t = u_t;
        return this;
    }

    public Long getU_id() {
        return u_id;
    }

    public LPlayerDao setU_id(Long u_id) {
        this.u_id = u_id;
        return this;
    }

    public Long getLast_gr_id() {
        return last_gr_id;
    }

    public LPlayerDao setLast_gr_id(Long last_gr_id) {
        this.last_gr_id = last_gr_id;
        return this;
    }

    public Integer getBet_5s_cnt() {
        return bet_5s_cnt;
    }

    public LPlayerDao setBet_5s_cnt(Integer bet_5s_cnt) {
        this.bet_5s_cnt = bet_5s_cnt;
        return this;
    }

    public Integer getBet_5m_cnt() {
        return bet_5m_cnt;
    }

    public LPlayerDao setBet_5m_cnt(Integer bet_5m_cnt) {
        this.bet_5m_cnt = bet_5m_cnt;
        return this;
    }

    public Integer getRwd_5s_cnt() {
        return rwd_5s_cnt;
    }

    public LPlayerDao setRwd_5s_cnt(Integer rwd_5s_cnt) {
        this.rwd_5s_cnt = rwd_5s_cnt;
        return this;
    }

    public Integer getRwd_5m_cnt() {
        return rwd_5m_cnt;
    }

    public LPlayerDao setRwd_5m_cnt(Integer rwd_5m_cnt) {
        this.rwd_5m_cnt = rwd_5m_cnt;
        return this;
    }

    public Long getAlb_amt() {
        return alb_amt;
    }

    public LPlayerDao setAlb_amt(Long alb_amt) {
        this.alb_amt = alb_amt;
        return this;
    }
}
