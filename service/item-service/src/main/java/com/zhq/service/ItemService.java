package com.zhq.service;

import com.zhq.api.IitemService;
import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.Product;
import com.zhq.mapper.ProductMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 使用freemarker生成静态详情页，需要定期进行维护
 */
@Service
@DubboService
public class ItemService implements IitemService {

    @Autowired(required = false)
    private ProductMapper productMapper;

    @Autowired
    private Configuration configuration;

    @Value("${html.locations}")
    private String htmlLocations;

    //获取服务器的cup核数
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    //JDK的线程池（根据服务器的硬件条件设置参数）
    private ExecutorService pool = new ThreadPoolExecutor(corePoolSize, corePoolSize*2,
            0L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));

    //多线程的调用
    private class CreateHTMLTask implements Callable<Boolean>{

        private Long productId;

        public CreateHTMLTask(Long productId) {
            this.productId = productId;
        }

        @Override
        public Boolean call(){

            Product product = productMapper.selectByPrimaryKey(productId);

            try {
                Template template = configuration.getTemplate("item.ftl");
                Map<String, Object> data = new HashMap<>();
                data.put("product", product);
                FileWriter writer = new FileWriter(htmlLocations+productId+".html");
                template.process(data, writer);
            } catch (IOException | TemplateException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    //批量生成静态页面
    @Override
    public ResultBean batchCreateHTML(List<Long> list) {

        List<Future> futureList = new ArrayList<>(list.size());

        for (Long id : list) {
            //提交任务给线程池
            futureList.add(pool.submit(new CreateHTMLTask(id)));
        }

        return ResultBean.success("批量生成静态页面成功！");
    }

    //生成静态页面
    @Override
    public ResultBean createHTMLById(Long id) {

        //获取商品的信息
        Product product = productMapper.selectByPrimaryKey(id);

        try {
            //使用item.ftl，生成详情页模板
            Template template = configuration.getTemplate("item.ftl");
            Map<String, Object> data = new HashMap<>();
            data.put("product", product);
            FileWriter writer = new FileWriter(htmlLocations+id+".html");
            //生成详情页，传递数据和生成的详情页位置
            template.process(data, writer);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return ResultBean.error("系统繁忙！");
        }

        return ResultBean.success("生成静态页面成功！");
    }
}
