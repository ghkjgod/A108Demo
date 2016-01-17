package com.siecom.framework.constconfig;

/**
 * Created by zhq on 2015/11/27.
 */
public class ErrorDefine {

    public final static int BLUE_DEVICE_NULL = -10000;
    public final static int BLUE_CONN_ERR = -10001;
    public final static int BLUE_NO_PAIR = -10002;
    public final static int SEND_NEED_PARAMETERS = -10003;
    public final static int NEED_CONN_FIRST = -10004;
    public final static int ERR_SEND_REQ_FAILED = -10005;
    public final static int ERR_RECEIVE_RSP_FAILED = -10006;

    public final static int SERIAL_OPEN_FAIL = -10007;
    public final static int SERIAL_DISCONN    = -10008;
    public final static int SERIAL_NULL    = -10009;
    public final static int SERIAL_SEND_FAIL    = -10010;
    public final static int SERIAL_NEED_CONTEXT    = -10011;
    public static String  getErrorDescribe(int error){
        String describe = null;
        switch (error) {
            case BLUE_DEVICE_NULL:
                describe = "BLUE_DEVICE_NULL";
                break;
            case BLUE_CONN_ERR:
                describe = "BLUE_CONN_ERR";
                break;
            case BLUE_NO_PAIR:
                describe = "BLUE_NO_PAIR";
                break;
            case SEND_NEED_PARAMETERS:
                describe = "SEND_NEED_PARAMETERS";
                break;
            case NEED_CONN_FIRST:
                describe = "NEED_CONN_FIRST";
                break;
            case ERR_SEND_REQ_FAILED:
                describe = "ERR_SEND_REQ_FAILED";
                break;
            case ERR_RECEIVE_RSP_FAILED:
                describe = "ERR_RECEIVE_RSP_FAILED";
                break;
        }
        return describe;
    }




}
