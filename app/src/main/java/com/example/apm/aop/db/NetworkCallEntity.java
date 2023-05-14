package com.example.apm.aop.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * HTTP 统计实体类
 *
 * Created by Nelson on 2019-11-29.
 */
@Entity(tableName = NetworkCallEntity.TABLE_NAME)
public class NetworkCallEntity {

    public static final String TABLE_NAME = "networkCall";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    // 当前用户 id
    @ColumnInfo(name = "user_id")
    public String userId;

    // 当前用户 name
    @ColumnInfo(name = "user_name")
    public String userName;

    // 请求链接
    @ColumnInfo(name = "url")
    public String url;

    // 请求域名
    @ColumnInfo(name = "domain")
    public String domain;

    // 请求类型 GET、POST
    @ColumnInfo(name = "method")
    public String method;

    // 网络请求/响应字节数
    @ColumnInfo(name = "total_bytes")
    public long totalBytes;

    // 网络请求字节数
    @ColumnInfo(name = "request_size")
    public long requestSize;

    // 网络响应字节数
    @ColumnInfo(name = "response_size")
    public long responseSize;

    // 网络响应状态码
    @ColumnInfo(name = "response_code")
    public int responseCode;

    // 请求时间戳
    @ColumnInfo(name = "timestamp")
    public long timestamp;

    // 请求格式化时间 yyyy-MM-dd HH:mm:ss
    @ColumnInfo(name = "format_ts")
    public String formatTimestamp;

    @ColumnInfo(name = "fetch_duration")
    public long fetch_duration; //请求发出到拿到数据，不包括本地排队时间

    @ColumnInfo(name = "dns_duration")
    public long dns_duration; //dns解析时间

    @ColumnInfo(name = "connect_duration")
    public long connect_duration; // 连接时间，创建socket通道，三次握手，ssl

    @ColumnInfo(name = "secure_duration")
    public long secure_duration; // ssl握手时间，connect_duration包含secure_duration

    @ColumnInfo(name = "request_duration")
    public long request_duration; // writeBytes的时间

    @ColumnInfo(name = "response_duration")
    public long response_duration; // readBytes的时间

    @ColumnInfo(name = "serve_duration")
    public long serve_duration; // 相当于responseStartDate - requestEndDate，发送数据到接收到数据

    @Override
    public String toString() {
        return "NetworkCallEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", url='" + url + '\'' +
                ", domain='" + domain + '\'' +
                ", method='" + method + '\'' +
                ", totalBytes=" + totalBytes +
                ", requestSize=" + requestSize +
                ", responseSize=" + responseSize +
                ", responseCode=" + responseCode +
                ", timestamp=" + timestamp +
                ", formatTimestamp='" + formatTimestamp + '\'' +
                ", fetch_duration=" + fetch_duration +
                ", dns_duration=" + dns_duration +
                ", connect_duration=" + connect_duration +
                ", secure_duration=" + secure_duration +
                ", request_duration=" + request_duration +
                ", response_duration=" + response_duration +
                ", serve_duration=" + serve_duration +
                '}';
    }
}
