package com.zhq.common.pojo;

/**
 * 封装批量上传图片
 */
public class MultiUploadResultBean {
    private String errno;
    private WangResultObject[] data;

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public WangResultObject[] getData() {
        return data;
    }

    public void setData(WangResultObject[] data) {
        this.data = data;
    }
}
