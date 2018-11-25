package com.demo.nettyrest.mysql;

import com.demo.nettyrest.exception.ApiException;
import com.demo.nettyrest.exception.StatusCode;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDao {
    protected final static String KEY_COLUMNS = "columns";
    protected final static String KEY_TABLENAME = "tableNames";
    protected final static String KEY_DBSELECTOR = "dbSelector";

    protected static <A> A  getOne(Map<String, Object> qs, Long iid) throws ApiException {
        Map<String, Object> map = new HashMap<>();
        map.put("id=", iid.toString());

        return getOne(qs, map, null);
    }

    protected static <A> A getOne(Map<String, Object> qs, Map<String, Object> conds) throws ApiException {
        return getOne(qs, conds, null);
    }

    protected static <A> A getOne(Map<String, Object> qs, Map<String, Object> conds, String orderBy) throws ApiException {
        String[] columns = (String[])qs.get(KEY_COLUMNS);
        MySelect myselect = (MySelect)qs.get(KEY_DBSELECTOR);
        String tableName = (String)qs.get(KEY_TABLENAME);

        if (myselect == null || StringUtil.isNullOrEmpty(tableName) || ArrayUtils.isEmpty(columns)){
            throw new ApiException(StatusCode.API_SERVER_ERROR, "columns is empty");
        }

        String sql = "SELECT " + org.apache.commons.lang3.StringUtils.join(columns, ',')
                + " FROM " + tableName
                + " WHERE " + packConds(conds, true);

        if (!StringUtil.isNullOrEmpty(orderBy)){
            sql = sql + " ORDER BY " + orderBy;
        }

        sql += " LIMIT 1";
        return (A)myselect.get(sql);
    }

    protected static <A> List<A> getList(Map<String, Object> qs, Map<String, Object> conds, Integer limit) throws ApiException {
        return getList(qs, conds, null, limit);
    }

    protected static <A> List<A> getList(Map<String, Object> qs, Map<String, Object> conds, String orderBy, Integer limit) throws ApiException {
        return getList(qs, conds, orderBy, limit, 0);
    }

    protected static <A> List<A> getList(Map<String, Object> qs, Map<String, Object> conds, String orderBy, Integer limit, Integer start) throws ApiException {

        String[] columns = (String[])qs.get(KEY_COLUMNS);
        MySelect myselect = (MySelect)qs.get(KEY_DBSELECTOR);
        String tableName = (String)qs.get(KEY_TABLENAME);

        if (myselect == null || StringUtil.isNullOrEmpty(tableName) || ArrayUtils.isEmpty(columns)){
            throw new ApiException(StatusCode.API_SERVER_ERROR, "columns is empty");
        }
        String sql = "SELECT " + org.apache.commons.lang3.StringUtils.join(columns, ',') + " FROM " + tableName + " WHERE " + packConds(conds, true);
        if (!StringUtil.isNullOrEmpty(orderBy)){
            sql = sql + " ORDER BY " + orderBy;
        }
        if (limit != null){
            sql = sql + " LIMIT " + ( start == null ? 0: start) + ", " + limit;
        }
        return (List<A>)myselect.list(sql);
    }


    public static int update(Map<String, Object> qs, Map<String, Object> values, Map<String, Object> conds) throws ApiException {
        return update(qs, values, conds, null);
    }

    public static int update(Map<String, Object> qs, Map<String, Object> values, String condStr) throws ApiException {
        return update(qs, values, condStr, null);
    }

    public static int update(Map<String, Object> qs, Map<String, Object> values, Map<String, Object> conds, String appends) throws ApiException {
        return update(qs, values, packConds(conds, false), appends);
    }
    public static int update(Map<String, Object> qs, Map<String, Object> values, String condStr, String appends) throws ApiException {
        String tableName = (String)qs.get(KEY_TABLENAME);
        if (StringUtil.isNullOrEmpty(tableName) || values == null || values.size() <= 0){
            throw new ApiException(StatusCode.API_SERVER_ERROR, "columns is empty");
        }

        if (StringUtil.isNullOrEmpty(condStr)){
            throw new ApiException(StatusCode.API_SERVER_ERROR, "cannot update all rows");
        }

        String sqls = "UPDATE `" + tableName + "` SET " + packKeyVal(values) + "  WHERE " + condStr;
//        String condStr = packConds(conds, false);
        if (! StringUtil.isNullOrEmpty(appends)) {
            sqls = sqls + " " + appends;
        }
        return MySelect.update(sqls);
    }


    public static Long insert(String tableName, Map<String, Object> values) throws ApiException {
        return insert(tableName, values, "");
    }

    public static Long insert(String tableName, Map<String, Object> values, Map<String, Object> onDup) throws ApiException {
        String onDupStr = "";
        if (onDup != null && onDup.size() > 0) {
            onDupStr = packKeyVal(onDup);
        }
        return insert(tableName, values, onDupStr);
    }

    public static Long insert(String tableName, Map<String, Object> values, String onDup) throws ApiException {
//        String tableName = (String)qs.get(KEY_TABLENAME);
        if (StringUtil.isNullOrEmpty(tableName) || values == null || values.size() <= 0){
            throw new ApiException(StatusCode.API_SERVER_ERROR, "columns is empty");
        }

        String sqls = "INSERT INTO `" + tableName + "` SET " + packKeyVal(values);

        if (!StringUtil.isNullOrEmpty(onDup)) {
            sqls = sqls + " ON DUPLICATE KEY UPDATE " + onDup;
        }
        return MySelect.insert(sqls);
    }

    public static boolean batchInsert(Map<String, Object> qs, List<Map<String, Object>> batchVals) throws ApiException {
        return batchInsert(qs, batchVals, "");
    }

    public static boolean batchInsert(Map<String, Object> qs, List<Map<String, Object>> batchVals, Map<String, Object> onDup) throws ApiException {
        String onDupStr = "";
        if (onDup != null && onDup.size() > 0) {
            onDupStr = packKeyVal(onDup);
        }
        return batchInsert(qs, batchVals, onDupStr);
    }

    public static boolean batchInsert(Map<String, Object> qs, List<Map<String, Object>> batchVals, String onDup) throws ApiException {
        String tableName = (String)qs.get(KEY_TABLENAME);
        if (StringUtil.isNullOrEmpty(tableName) || batchVals == null || batchVals.size() <= 0){
            throw new ApiException(StatusCode.API_SERVER_ERROR, "columns is empty");
        }
        String columnStr = "";
        Map valu1st = batchVals.get(0);
        int vsize = valu1st.size();
        String[] allKeys = new String[vsize];

        int i = 0;
        for (Map.Entry<String, Object> entry: batchVals.get(0).entrySet()) {
            allKeys[i++] = "`" + entry.getKey() + "`";
        }

        if (StringUtil.isNullOrEmpty(columnStr)){
            throw new ApiException(StatusCode.API_SERVER_ERROR, "columns doesn't match");
        }

        String[] allValArr = new String[batchVals.size()];
        i = 0;
        for (Map<String, Object> values: batchVals) {
            String[] valArr = new String[vsize];
            for (int j = 0; j < vsize; j++) {
                String k = allKeys[j];
                if (!values.containsKey(k)){
                    throw new ApiException(StatusCode.API_SERVER_ERROR, "columns doesn't match");
                }
                Object v = values.get(k);
                valArr[j] = (v == null) ? "\"\"" : ("\"" + v + "\"");
            }
            allValArr[i++] = "( " + org.apache.commons.lang3.StringUtils.join(valArr, ',') + " )";
        }
        String sqls = "INSERT INTO `" + tableName + "` ( " + org.apache.commons.lang3.StringUtils.join(allKeys, ',') + ") VALUES " + org.apache.commons.lang3.StringUtils.join(allValArr, ',');
        if (!StringUtil.isNullOrEmpty(onDup)) {
            sqls = sqls + " ON DUPLICATE KEY UPDATE " + onDup;
        }
        return MySelect.insertRaw(sqls);
    }

    ////////////////////////////////////////////////////////////
    private static String packKeyVal(Map<String, Object> values){
        String str = "";
        for (Map.Entry<String, Object> entry: values.entrySet()) {
            if (str.length() > 0){
                str = str + ", `" + entry.getKey() + "` = \"" + entry.getValue() + "\" ";
            }else{
                str = "`" + entry.getKey() + "` = \"" + entry.getValue() + "\" ";
            }
        }
        return str;
    }

    private static String packConds(Map<String, Object> conds, boolean autoTrue){
        String str = "";
        if (conds != null && conds.size() > 0){

            for (Map.Entry<String, Object> entry: conds.entrySet()) {
                if (str.length() > 0){
                    str = str + " AND " + entry.getKey() + " \"" + entry.getValue() + "\" ";
                }else{
                    str = entry.getKey() + " \"" + entry.getValue() + "\" ";
                }
            }
        }else if (autoTrue){
            str = "1 = 1";
        }
        return str;
    }
}
