package com.example.scalephoto.cache.models;

import java.io.Serializable;

/**
 * @author xiaxueliang
 */
public class SnsBiLogData implements Serializable {
    private static final long serialVersionUID = 1L;

    public String ctime;//string	毫秒级别的时间戳，13位
    public String pos;//string	埋点所在的页面，详细参见
    public String event;    //string	事件名
    public String ip;//string 访问ip地址
    public String network;//string	wifi、3G等
    public String app_version;//string app版本
    public String operator;//string	运营商
    // 必须实现Serializable接口(用于对象序列化)
    public Object data;// data	object	具体事件的参数



    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ctime: ");
        sb.append(ctime);
        sb.append(" pos: ");
        sb.append(pos);
        sb.append(" event: ");
        sb.append(event);
        sb.append(" ip: ");
        sb.append(ip);
        sb.append(" network: ");
        sb.append(network);
        sb.append(" app_version: ");
        sb.append(app_version);
        sb.append(" operator: ");
        sb.append(operator);
        sb.append(" data: ");
        sb.append(data.toString());
        return sb.toString();
    }
}
