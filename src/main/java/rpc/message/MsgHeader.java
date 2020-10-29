package rpc.message;

import rpc.utils.Helper;

import java.io.Serializable;

public class MsgHeader implements Serializable {

    private static final long serialVersionUID = 7520195733965036416L;

    public static final int CALL_TYPE = 0x14141414, RET_TYPE = 0x14141424;
    public static final int HEADER_LEN = 87;

    private int flag; // 协议类型
    private long requestID; // 发送线程ID
    private int dataLen; // 发送的数据内容长度

    public static MsgHeader constructHeader(byte[] msg, int type) {
        MsgHeader header = new MsgHeader();
        header.setFlag(type);
        header.setRequestID(Helper.getRandomID());
        header.setDataLen(msg.length);
        return header;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getRequestID() {
        return requestID;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }
}
