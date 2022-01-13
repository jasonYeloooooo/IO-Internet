package com.nx.netty.splitpacket;

/**
 * @author mkp
 * @Description:自定义协议包
 */
public class MessageProtocol {
    /**
     * 定义一次发送的长度
     *
     */
    private int len;
    /**
     * 发送的内容
     */
    private byte[] content;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
