package com.zhq.backgroud.controller;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zhq.common.pojo.MultiUploadResultBean;
import com.zhq.common.pojo.ResultBean;
import com.zhq.common.pojo.WangResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 处理图片等静态资源保存在FastDFS中
 */
@Controller
@RequestMapping("file")
public class FileController {

    @Value("${image.server}")
    private String image_server;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 上传图片
     * @param file
     * @return
     */
    @RequestMapping("upload")
    @ResponseBody
    public ResultBean upload(MultipartFile file){
        //获取文件名称
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀类型
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        try {
            //获取文件对象，上传到Fastdfs上
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extName, null);
            //将保存地址返回给客户端
            String fullPath = storePath.getFullPath();  //只获取文件在服务器中的存储路径
            //需要加上前缀（服务的地址）
            String path = new StringBuilder(image_server).append(fullPath).toString();
            return ResultBean.success(path);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultBean.error("网络错误！");
        }
    }

    @RequestMapping("multiUpload")
    @ResponseBody
    public MultiUploadResultBean multiUpload(MultipartFile[] files) {

        MultiUploadResultBean resultBean = new MultiUploadResultBean();
        WangResultObject[] data = new WangResultObject[files.length];
        WangResultObject wangResultObject = new WangResultObject();
        for (int i = 0; i < files.length; i++) {
            //获取文件名称
            String originalFilename = files[i].getOriginalFilename();
            //获取文件后缀类型
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            try {
                //获取文件对象，上传到Fastdfs上
                StorePath storePath = fastFileStorageClient.uploadFile(files[i].getInputStream(),
                        files[i].getSize(), extName, null);
                //将保存地址返回给客户端
                String fullPath = storePath.getFullPath();  //只获取文件在服务器中的存储路径
                //需要加上前缀（服务的地址）
                String path = new StringBuilder(image_server).append(fullPath).toString();
                wangResultObject.setUrl(path);
                data[i] = wangResultObject;
            } catch (IOException e) {
                e.printStackTrace();
                resultBean.setErrno("-1");
            }
        }
        resultBean.setErrno("0");
        resultBean.setData(data);
        return resultBean;
    }
}
