package com.tronyes.demo.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class UserDao extends BaseDao {

    private Long id;
    private String nick;
    private String address;
    private Long lucky_h;
    private Long lucky_m;
    private Long lucky_l;
    private Integer rtype;
    private Integer state;

    private Long total_bet;
    private Long total_reward;
    private Long round_count;

    private Integer game_type;
    private Long six_total_bet;
    private Long six_total_reward;
    private Long six_round_count;

    private Long token_all;
    private Long token_frozen;

    private Timestamp c_t;
    private Timestamp u_t;


    public long saveObj(boolean autoUpdate) throws ApiException {
        return super.saveObj(qs, autoUpdate);
    }

    public boolean recordBet(Long bet_amount) throws ApiException {
        if (bet_amount == null || bet_amount <= 0){
            return false;
        }
        this.total_bet = total_bet + bet_amount;
        this.round_count = round_count + 1;
        return (super.saveObj(qs, true) > 0);
    }

    public static UserDao getByAddress(String address) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("address = ", address);
        return getOne(qs, conds);
    }

    public static UserDao getById(long iid) throws ApiException {
        return getOne(qs, iid);
    }

    public static int update(Map<String, Object> valus, Map<String, Object> conds) throws ApiException{
        return update(qs, valus, conds);
    }

    public static int update(Map<String, Object> valus, String conds) throws ApiException{
        return update(qs, valus, conds);
    }

    protected static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "m_user");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new UserDao()));
        qs.put(KEY_COLUMNS, new String[]{"id",
            "nick",
            "address",
            "lucky_h",
            "lucky_l",
            "lucky_m",
            "rtype",
            "state",
            "total_bet",
            "total_reward",
            "round_count",
            "game_type",
            "six_total_bet",
            "six_total_reward",
            "six_round_count",
            "token_all",
            "token_frozen",
            "c_t", "u_t",});
    }

    public Long getId() {
        return id;
    }

    public UserDao setId(Long id) {
        this.id = id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserDao setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getNick() {
        return nick;
    }

    public UserDao setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public long getLucky_h() {
        return lucky_h;
    }

    public UserDao setLucky_h(long lucky_h) {
        this.lucky_h = lucky_h;
        return this;
    }

    public long getLucky_m() {
        return lucky_m;
    }

    public UserDao setLucky_m(long lucky_m) {
        this.lucky_m = lucky_m;
        return this;
    }

    public long getLucky_l() {
        return lucky_l;
    }

    public UserDao setLucky_l(long lucky_l) {
        this.lucky_l = lucky_l;
        return this;
    }

    public int getState() {
        return state;
    }

    public UserDao setState(int state) {
        this.state = state;
        return this;
    }

    public long getTotal_bet() {
        return total_bet;
    }

    public UserDao setTotal_bet(long total_bet) {
        this.total_bet = total_bet;
        return this;
    }

    public long getTotal_reward() {
        return total_reward;
    }

    public UserDao setTotal_reward(long total_reward) {
        this.total_reward = total_reward;
        return this;
    }

    public long getRound_count() {
        return round_count;
    }

    public UserDao setRound_count(long round_count) {
        this.round_count = round_count;
        return this;
    }

    public Timestamp getC_t() {
        return c_t;
    }

    public UserDao setC_t(Timestamp c_t) {
        this.c_t = c_t;
        return this;
    }

    public Timestamp getU_t() {
        return u_t;
    }

    public UserDao setU_t(Timestamp u_t) {
        this.u_t = u_t;
        return this;
    }

    public Integer getGame_type() {
        return game_type;
    }

    public void setGame_type(Integer game_type) {
        this.game_type = game_type;
    }

    public Long getSix_total_bet() {
        return six_total_bet;
    }

    public void setSix_total_bet(Long six_total_bet) {
        this.six_total_bet = six_total_bet;
    }

    public Long getSix_total_reward() {
        return six_total_reward;
    }

    public void setSix_total_reward(Long six_total_reward) {
        this.six_total_reward = six_total_reward;
    }

    public Long getSix_round_count() {
        return six_round_count;
    }

    public void setSix_round_count(Long six_round_count) {
        this.six_round_count = six_round_count;
    }

    public Integer getRtype() {
        return rtype;
    }

    public UserDao setRtype(Integer rtype) {
        this.rtype = rtype;
        return this;
    }

    public Long getToken_all() {
        return token_all;
    }

    public UserDao setToken_all(Long token_all) {
        this.token_all = token_all;
        return this;
    }

    public Long getToken_frozen() {
        return token_frozen;
    }

    public UserDao setToken_frozen(Long token_frozen) {
        this.token_frozen = token_frozen;
        return this;
    }
}
