package com.zhq.productservice.product;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhq.api.product.IProductService;
import com.zhq.api.vo.ProductVO;
import com.zhq.common.base.BaseServiceImpl;
import com.zhq.common.base.IBaseDao;
import com.zhq.entity.Product;
import com.zhq.entity.ProductDesc;
import com.zhq.mapper.ProductDescMapper;
import com.zhq.mapper.ProductMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service //spring中service类
@DubboService //dubbo中服务发布
public class ProductService extends BaseServiceImpl<Product> implements IProductService {

    @Autowired(required = false)
    public ProductDescMapper productDescMapper;

    @Autowired(required = false)
    public ProductMapper productMapper;

    //继承的BaseServiceImpl类中设置，mapper类型
    @Override
    public IBaseDao<Product> getBaseDao() {
        return productMapper;
    }

    /**
     * 分页查询
     * @param pageIndex 查询第几页
     * @param pageSize  每页几条数据
     * @return
     */
    @Override
    public PageInfo<Product> page(Integer pageIndex, Integer pageSize) {
        //开启分页功能
        PageHelper.startPage(pageIndex,pageSize);
        //查询需要分页列表
        List<Product> list = productMapper.selectAll();
        // 获取分页信息，指定分页栏显示三个数字
        PageInfo<Product> pageInfo = new PageInfo<>(list, 3);

        return pageInfo;
    }

    /**
     * 添加商品信息
     * @param productVO  视图层的接收数据的类
     * @return
     */
    @Override
    public Long add(ProductVO productVO) {
        //添加商品的基本信息
        Product product = productVO.getProduct();
        //设置基础信息
        product.setFlag(true);
        product.setCreateTime(new Date());
        product.setUpdateTime(product.getCreateTime());
        product.setCreateUser(1L);
        product.setUpdateUser(product.getCreateUser());
        //调用mapper插入
        productMapper.insertSelective(product);

        //添加商品的描述信息
        ProductDesc productDesc = productVO.getProductDesc();
        //主键回填得到的id填入类别中
        productDesc.setProductId(product.getId());
        productDescMapper.insertSelective(productDesc);
        //返回商品id
        return product.getId();
    }

    @Override
    public void update(ProductVO productVO) {
        //修改商品的基本信息
        Product product = productVO.getProduct();
        //设置基础信息
        product.setUpdateTime(new Date());
        product.setUpdateUser(1L);
        productMapper.updateByPrimaryKeySelective(product);

        //修改商品的描述信息
        ProductDesc productDesc = productVO.getProductDesc();
        ProductDesc productDesc1 = productDescMapper.selectByProductId(product.getId());
        productDesc1.setpDesc(productDesc.getpDesc());
        productDescMapper.updateByPrimaryKeySelective(productDesc1);
    }

    @Override
    public void deleteAll(Long[] productId) {
        //删除商品基本信息
        productMapper.deleteByPrimaryKeys(productId);
        //删除商品描述信息
        productDescMapper.deleteByPrimaryKeys(productId);
    }

    @Override
    public void delete(Long pId) {
        //删除商品基本信息
        productMapper.deleteByPrimaryKey(pId);
        //删除商品描述信息
        productDescMapper.deleteByPrimaryKey(pId);
    }
}
