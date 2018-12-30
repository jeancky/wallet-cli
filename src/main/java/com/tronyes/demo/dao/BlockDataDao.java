package com.tronyes.demo.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;
import org.tron.walletserver.AutoClient;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockDataDao extends BaseDao {
    private Long id;
    private Long b_h;
    private String block_hash;
    private Integer six_number;
    private Timestamp c_t;
    private Timestamp u_t;
    private Integer state;

    private final static int NORMAL_BLOCK_SEC = 3000;

    protected static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "m_block_data");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new BlockDataDao()));
        qs.put(KEY_COLUMNS, new String[]{"id", "b_h", "block_hash", "c_t", "u_t", "six_number", "state"});
    }

    public long saveObj(boolean autoUpdate) throws ApiException {
        return super.saveObj(qs, autoUpdate);
    }

    public static Map<String, Object> packageBlock(BlockDataDao dao) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("b_h", dao.getB_h());
        obj.put("block_hash", dao.getBlock_hash());
        obj.put("result", dao.getSix_number());
        obj.put("t_x", dao.getC_t());
        return obj;
    }

    public static BlockDataDao getByBlockHeight(long blochHeight) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("b_h = ", blochHeight);
        return getOne(qs, conds);
    }

    // 获取最新区块数据列表
    public static List<BlockDataDao> getLastList() throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("block_hash != ", "");
        return getList(qs, conds, "b_h DESC", 50);
    }

    // 获取一个blockhash为空的区块
    public static BlockDataDao getBlockWithEmptyHash() throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("block_hash = ", "");
        conds.put("state = ", 0);
        conds.put("c_t < ", new Timestamp(System.currentTimeMillis() - NORMAL_BLOCK_SEC));
        return getOne(qs, conds);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getB_h() {
        return b_h;
    }

    public void setB_h(Long b_h) {
        this.b_h = b_h;
    }

    public String getBlock_hash() {
        return block_hash;
    }

    public void setBlock_hash(String block_hash) {
        this.block_hash = block_hash;
    }

    public Integer getSix_number() {
        return six_number;
    }

    public void setSix_number(Integer six_number) {
        this.six_number = six_number;
    }

    public Timestamp getC_t() {
        return c_t;
    }

    public void setC_t(Timestamp c_t) {
        this.c_t = c_t;
    }

    public Timestamp getU_t() {
        return u_t;
    }

    public void setU_t(Timestamp u_t) {
        this.u_t = u_t;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
