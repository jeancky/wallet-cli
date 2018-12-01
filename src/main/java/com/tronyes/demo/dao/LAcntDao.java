package org.tron.dao;

import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.mysql.BaseDao;
import com.tronyes.nettyrest.mysql.MySelect;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class LAcntDao extends BaseDao {

    private Long    id;
    private String nick;
    private String address;
    private String udid;
    private Integer version;
    private String crypto;
    private Integer state;
    private String  pswd;
    private Timestamp c_t;
    private Timestamp u_t;

    public static LAcntDao getById(Integer id) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("id = ", id);
        return getOne(qs, conds);
    }

    public static LAcntDao getByAddress(String address) throws ApiException {
        Map<String, Object> conds = new HashMap<>();
        conds.put("address = ", address);
        return getOne(qs, conds);
    }

    public static long insert(LAcntDao dao) throws ApiException {
        Map<String, Object> values = new HashMap<>();
        if (!StringUtil.isNullOrEmpty(dao.getNick())){
            values.put("nick", dao.getNick());
        }
        if (!StringUtil.isNullOrEmpty(dao.getAddress())){
            values.put("address", dao.getAddress());
        }
        if (!StringUtil.isNullOrEmpty(dao.getUdid())){
            values.put("udid", dao.getUdid());
        }
        if (dao.getVersion() != null){
            values.put("version", dao.getVersion());
        }
        if (!StringUtil.isNullOrEmpty(dao.crypto)){
            values.put("crypto", dao.crypto);
        }
        if (dao.getState() != null){
            values.put("state", dao.getState());
        }
        if (!StringUtil.isNullOrEmpty(dao.getPswd())){
            values.put("pswd", dao.getPswd());
        }
        return insert((String)qs.get(KEY_TABLENAME), values, "id = VALUES(`id`)");
    }

    public static int update(LAcntDao dao) throws ApiException {
        Map<String, Object> values = new HashMap<>();
        if (!StringUtil.isNullOrEmpty(dao.getNick())){
            values.put("nick", dao.getNick());
        }
        if (!StringUtil.isNullOrEmpty(dao.getAddress())){
            values.put("address", dao.getAddress());
        }
        if (!StringUtil.isNullOrEmpty(dao.getUdid())){
            values.put("udid", dao.getUdid());
        }
        if (dao.getVersion() != null){
            values.put("version", dao.getVersion());
        }
        if (!StringUtil.isNullOrEmpty(dao.crypto)){
            values.put("crypto", dao.crypto);
        }
        if (dao.getState() != null){
            values.put("state", dao.getState());
        }
        if (!StringUtil.isNullOrEmpty(dao.getPswd())){
            values.put("pswd", dao.getPswd());
        }
        Map<String, Object> conds = new HashMap<>();
        conds.put("address = ", dao.getAddress());
        return update(qs, values, conds);
    }

    public static int update(Map<String, Object> valus, Map<String, Object> conds) throws ApiException {
        return update(qs, valus, conds);
    }

    public static int update(Map<String, Object> valus, String conds) throws ApiException {
        return update(qs, valus, conds);
    }

    private static Map<String, Object> qs = new HashMap<>();
    static {
        qs.put(KEY_TABLENAME, "l_account");
        qs.put(KEY_DBSELECTOR, new MySelect<>(new LAcntDao()));
        qs.put(KEY_COLUMNS, new String[]{"id","nick","address","udid","version","crypto","state", "pswd","c_t","u_t"});
    }

    public Long getId() {
        return id;
    }

    public LAcntDao setId(Long id) {
        this.id = id;
        return this;
    }

    public String getNick() {
        return nick;
    }

    public LAcntDao setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public LAcntDao setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getUdid() {
        return udid;
    }

    public LAcntDao setUdid(String udid) {
        this.udid = udid;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public LAcntDao setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public String getCrypto() {
        return StringEscapeUtils.unescapeJavaScript(crypto);
    }

    public LAcntDao setCrypto(String crypto) {
        this.crypto = StringEscapeUtils.escapeJavaScript(crypto);
        return this;
    }

    public Integer getState() {
        return state;
    }

    public LAcntDao setState(Integer state) {
        this.state = state;
        return this;
    }

    public Timestamp getC_t() {
        return c_t;
    }

    public LAcntDao setC_t(Timestamp c_t) {
        this.c_t = c_t;
        return this;
    }

    public Timestamp getU_t() {
        return u_t;
    }

    public LAcntDao setU_t(Timestamp u_t) {
        this.u_t = u_t;
        return this;
    }

    public String getPswd() {
        return pswd;
    }

    public LAcntDao setPswd(String pswd) {
        this.pswd = pswd;
        return this;
    }
}
