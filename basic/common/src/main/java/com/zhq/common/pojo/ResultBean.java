package com.zhq.common.pojo;

import java.io.Serializable;

/**
 * 给客户端返回信息的类
 */
public class ResultBean<T> implements Serializable {
    //返回的状态码
    private Integer statusCode;
    //成功返回的额数据
    private T data;
    //失败返回失败信息
    private String msg;

    public ResultBean() {
    }

    public ResultBean(Integer statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public static ResultBean success(String data){
        ResultBean resultBean = new ResultBean();
        resultBean.setStatusCode(200);
        resultBean.setData(data);
        return resultBean;
    }

    public static ResultBean error(String msg){
        ResultBean resultBean = new ResultBean();
        resultBean.setStatusCode(500);
        resultBean.setMsg(msg);
        return resultBean;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
