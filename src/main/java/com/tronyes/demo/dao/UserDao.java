package org.tron.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserDao extends BaseDao {

    private Long id;
    private String nick;
    private String address;
    private long lucky_h;
    private long lucky_m;
    private long lucky_l;

    private long total_bet;
    private long total_reward;
    private long round_count;

    private Timestamp c_t;
    private Timestamp u_t;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNick() { return nick; }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getLucky_h() { return lucky_h; }

    public void setLucky_h(long lucky_h) {
        this.lucky_h = lucky_h;
    }

    public long getLucky_m() {
        return lucky_m;
    }

    public void setLucky_m(long lucky_m) {
        this.lucky_m = lucky_m;
    }

    public long getLucky_l() {
        return lucky_l;
    }

    public void setLucky_l(long lucky_l) {
        this.lucky_l = lucky_l;
    }

    public Date getC_t() {
        return c_t;
    }

    public void setC_t(Timestamp c_t) {
        this.c_t = c_t;
    }

    public Date getU_t() {
        return u_t;
    }

    public void setU_t(Timestamp u_t) {
        this.u_t = u_t;
    }

    public long getRound_count() { return round_count; }

    public void setRound_count(long round_count) { this.round_count = round_count; }

    public long getTotal_reward() { return total_reward; }

    public void setTotal_reward(long total_reward) { this.total_reward = total_reward; }

    public long getTotal_bet() { return total_bet; }

    public void setTotal_bet(long total_bet) { this.total_bet = total_bet;}


    public static UserDao getByAddress(String address) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("address = ", address);
        return getOne(qs, conds);
    }

    public static UserDao getById(long iid) throws ApiException {
        return getOne(qs, iid);
    }

    public static long insert(UserDao user) throws ApiException {
        Map<String, Object> values = new HashMap<>();
        values.put("address", user.getAddress());
        values.put("lucky_h", ""+user.getLucky_h());
        values.put("lucky_m", ""+user.getLucky_h());
        values.put("lucky_l", ""+user.getLucky_h());
        values.put("state", "1");

        return insert((String)qs.get(KEY_TABLENAME), values, "id = VALUES(`id`)");
    }

    public static int update(Map<String, Object> valus, Map<String, Object> conds) throws ApiException {
        return update(qs, valus, conds);
    }

    public static int update(Map<String, Object> valus, String conds) throws ApiException {
        return update(qs, valus, conds);
    }

    protected static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "m_user");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new UserDao()));
        qs.put(KEY_COLUMNS, new String[]{"id", "nick", "address", "lucky_h", "lucky_l", "lucky_m", "total_bet", "total_reward", "round_count", "c_t", "u_t"});
    }
}
