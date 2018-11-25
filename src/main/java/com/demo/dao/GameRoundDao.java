package com.demo.dao;

import com.demo.nettyrest.exception.ApiException;
import com.demo.nettyrest.mysql.BaseDao;
import com.demo.nettyrest.mysql.MySelect;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRoundDao extends BaseDao {
    private Long id;
    private Long r_id;
    private Integer state;

    private Integer type;
    private Long sum_bet;
    private Long sum_reward;
    private Long user_cnt;
    private Long game_cnt;

    private String chosen;
    private Timestamp lot_t;
    private Timestamp c_t;
    private Timestamp u_t;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getR_id() {
        return r_id;
    }

    public void setR_id(Long r_id) {
        this.r_id = r_id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getChosen() {
        return chosen;
    }

    public void setChosen(String chosen) {
        this.chosen = chosen;
    }

    public Timestamp getLot_t() {
        return lot_t;
    }

    public void setLot_t(Timestamp lot_t) {
        this.lot_t = lot_t;
    }


    public Integer getType() { return type; }

    public void setType(Integer type) { this.type = type; }

    public Long getSum_bet() { return sum_bet; }

    public void setSum_bet(Long sum_bet) { this.sum_bet = sum_bet; }

    public Long getSum_reward() { return sum_reward; }

    public void setSum_reward(Long sum_reward) { this.sum_reward = sum_reward; }

    public Long getUser_cnt() { return user_cnt; }

    public void setUser_cnt(Long user_cnt) { this.user_cnt = user_cnt; }

    public Long getGame_cnt() { return game_cnt; }

    public void setGame_cnt(Long game_cnt) { this.game_cnt = game_cnt; }

    public Timestamp getC_t() { return c_t; }

    public void setC_t(Timestamp c_t) { this.c_t = c_t; }

    public Timestamp getU_t() { return u_t; }

    public void setU_t(Timestamp u_t) { this.u_t = u_t; }

    // 获取指定期
    public static GameRoundDao get(Long roundId) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("r_id =", roundId);
        return getOne(qs, conds);
    }

    public static List<GameRoundDao> getListByConds(Map<String, Object>conds, String orderBy, Integer limit, Integer start) throws ApiException {
        return getList(qs, conds, orderBy, limit, start);
    }

    public static GameRoundDao getByConds(Map<String, Object>conds, String orderBy) throws ApiException {
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
        qs.put(KEY_TABLENAME, "m_game_round");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new GameRoundDao()));
        qs.put(KEY_COLUMNS, new String[]{"id", "r_id", "state", "type", "sum_bet", "sum_reward", "user_cnt", "game_cnt", "chosen", "lot_t", "c_t", "u_t"});
    }

}
