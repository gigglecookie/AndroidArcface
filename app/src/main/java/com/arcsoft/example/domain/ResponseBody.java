package com.arcsoft.example.domain;

public class ResponseBody {

    // 错误信息
    private String error;
    // 状态码
    private int status;
    // 其他信息
    private String message;
    // 数据
    private Object data;

    @Override
    public String toString() {
        return "ResponseBody{" +
                "error='" + error + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ResponseBody() {
    }

    public ResponseBody(String error, int status, String message, Object data, String timestamp) {
        this.error = error;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    // 时间
    private String timestamp;

}
