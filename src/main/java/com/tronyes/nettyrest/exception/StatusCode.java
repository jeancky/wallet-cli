package com.tronyes.nettyrest.exception;

import io.netty.util.internal.StringUtil;
import com.tronyes.nettyrest.exception.ApiException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoumengkang on 7/1/16.
 */
public class StatusCode {

    private static final Map<Integer, String> codeMap = new HashMap<>();

    public static final int CREATED_SUCCESS       = 201;
    public static final int DB_SQL_ERR            = 202;
    public static final int UNKNOWN_ERROR         = 1000;
    public static final int API_NOT_FOUND         = 1001;
    public static final int API_CAN_NOT_BE_NULL   = 1002;
    public static final int PARAM_ERROR = 1003;
    public static final int VERSION_IS_TOO_LOW    = 1005;
    public static final int REQUEST_MODE_ERROR    = 1006;
    public static final int API_SERVER_ERROR      = 1007;

    public static final int USER_NOT_EXIST = 2000;
    public static final int BET_FAILED = 2001;
    public static final int GAME_NOT_EXIST = 2002;
    public static final int GAME_CLOSED = 2003;
    public static final int ADDRESS_EMPTY = 2004;
    public static final int GAME_MORE_5 = 2005;
    public static final int DB_PSWD_EMPTY = 2006;

    static {
        codeMap.put(CREATED_SUCCESS, "created success");
        codeMap.put(DB_SQL_ERR, "sql error");
        codeMap.put(UNKNOWN_ERROR, "unknown error");
        codeMap.put(API_NOT_FOUND, "the api can't be found");
        codeMap.put(API_CAN_NOT_BE_NULL, "can't request without a api name");
        codeMap.put(PARAM_ERROR, "param : %s error");
        codeMap.put(VERSION_IS_TOO_LOW, "version is too low, please update your client");
        codeMap.put(REQUEST_MODE_ERROR, "the http request method is not allow");
        codeMap.put(API_SERVER_ERROR, "api server error");
        codeMap.put(USER_NOT_EXIST, "user not exist");
        codeMap.put(BET_FAILED, "bet failed, please retry");
        codeMap.put(GAME_NOT_EXIST, "game not exist");
        codeMap.put(GAME_CLOSED, "game is closed");
        codeMap.put(ADDRESS_EMPTY, "address is empty!");
        codeMap.put(GAME_MORE_5, "no more than 5 bet per round.");
        codeMap.put(DB_PSWD_EMPTY, "DB password mapping not found.");
    }

    public static ApiException buildException(int code, String parameter){
        return new ApiException(code, StatusCode.getMessage(code, parameter));
    }

    public static final String getMessage(int code){
        return StatusCode.codeMap.get(code);
    }

    public static final String getMessage(int code, String parameter){
        String msg = StatusCode.codeMap.get(code);
        return StringUtil.isNullOrEmpty(msg) ? parameter : String.format(msg, parameter);
    }
}
