package com.tronyes.demo.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;

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


    public boolean recordBet(Long bet_amount) throws ApiException {
        if (bet_amount == null || bet_amount <= 0){
            return false;
        }
        this.sum_bet = sum_bet + bet_amount;
        this.game_cnt = game_cnt + 1;
        return (super.saveObj(qs, true) > 0);
    }

    public long saveObj(boolean autoUpdate) throws ApiException {
        return super.saveObj(qs, autoUpdate);
    }

    // 获取指定期
    public static GameRoundDao get(Long roundId) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("r_id =", roundId);
        return getOne(qs, conds);
    }

    // 获取未开奖列表
    public static GameRoundDao getOpenGame() throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("state =", 1);
        List<GameRoundDao> list = getList(qs, conds, "id DESC",1);
        return (list == null || list.size() == 0)? null : list.get(0);
    }

    public static GameRoundDao getLastGame() throws ApiException {
        return getOne(qs,null, "id DESC");
    }

    public static int update(Map<String, Object> valus, Map<String, Object> conds) throws ApiException{
        return update(qs, valus, conds);
    }

    public static int update(Map<String, Object> valus, String conds) throws ApiException{
        return update(qs, valus, conds);
    }

    protected static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "m_game_round");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new GameRoundDao()));
        qs.put(KEY_COLUMNS, new String[]{"id", "r_id", "state", "type", "sum_bet", "sum_reward", "user_cnt", "game_cnt", "chosen", "lot_t", "c_t", "u_t"});
    }

    public Long getId() {
        return id;
    }

    public GameRoundDao setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getR_id() {
        return r_id;
    }

    public GameRoundDao setR_id(Long r_id) {
        this.r_id = r_id;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public GameRoundDao setState(Integer state) {
        this.state = state;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public GameRoundDao setType(Integer type) {
        this.type = type;
        return this;
    }

    public Long getSum_bet() {
        return sum_bet;
    }

    public GameRoundDao setSum_bet(Long sum_bet) {
        this.sum_bet = sum_bet;
        return this;
    }

    public Long getSum_reward() {
        return sum_reward;
    }

    public GameRoundDao setSum_reward(Long sum_reward) {
        this.sum_reward = sum_reward;
        return this;
    }

    public Long getUser_cnt() {
        return user_cnt;
    }

    public GameRoundDao setUser_cnt(Long user_cnt) {
        this.user_cnt = user_cnt;
        return this;
    }

    public Long getGame_cnt() {
        return game_cnt;
    }

    public GameRoundDao setGame_cnt(Long game_cnt) {
        this.game_cnt = game_cnt;
        return this;
    }

    public String getChosen() {
        return chosen;
    }

    public GameRoundDao setChosen(String chosen) {
        this.chosen = chosen;
        return this;
    }

    public Timestamp getLot_t() {
        return lot_t;
    }

    public GameRoundDao setLot_t(Timestamp lot_t) {
        this.lot_t = lot_t;
        return this;
    }

    public Timestamp getC_t() {
        return c_t;
    }

    public GameRoundDao setC_t(Timestamp c_t) {
        this.c_t = c_t;
        return this;
    }

    public Timestamp getU_t() {
        return u_t;
    }

    public GameRoundDao setU_t(Timestamp u_t) {
        this.u_t = u_t;
        return this;
    }
}
