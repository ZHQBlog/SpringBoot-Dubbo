package com.zhq.userservice.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhq.api.IUserService;
import com.zhq.common.base.BaseServiceImpl;
import com.zhq.common.base.IBaseDao;
import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.User;
import com.zhq.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@DubboService
public class UserService extends BaseServiceImpl<User> implements IUserService {

    @Autowired(required = false)
    private UserMapper userMapper;

    //加密工具类，对密码进行加密保存
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public IBaseDao<User> getBaseDao() {
        return userMapper;
    }

    /**
     * 校验登录
     * @param user
     * @return
     */
    @Override
    public User checkLogin(User user) {
        User currentUser = userMapper.selectByUsername(user.getUsername());

        if (currentUser != null){
            //解密比较数据库中的密码
            if (passwordEncoder.matches(user.getPassword(), currentUser.getPassword())){
                return currentUser;
            }
        }
        return null;
    }

    /**
     * 校验登录状态，因为该功能需要在别的系统异步调用，所以抽离成了service
     * 或者在controller中使用httpclient模拟http请求
     * @param uuid
     * @return
     */
    public ResultBean checkIsLogin(String uuid){
        //通过uuid查询redis中的用户
        StringBuilder redisKey = new StringBuilder("userToken::").append(uuid);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        User currentUser = (User) redisTemplate.opsForValue().get(redisKey.toString());

        if (currentUser != null){
            //刷新凭证的有效期
            redisTemplate.expire(redisKey.toString(), 30, TimeUnit.MINUTES);
            return new ResultBean(200, currentUser);
        }
        return  ResultBean.error("用户未登录!");
    }

    @Override
    public User queryUser(String name) {
        return userMapper.selectByUsername(name);
    }

    @Override
    public PageInfo<User> page(Integer pageIndex, Integer pageSize) {
        //开启分页功能
        PageHelper.startPage(pageIndex,pageSize);
        //查询需要分页列表
        List<User> list = userMapper.selectAll();
        // 获取分页信息，指定分页栏显示三个数字
        PageInfo<User> pageInfo = new PageInfo<>(list, 3);

        return pageInfo;
    }

    @Override
    public void deleteAll(Long[] userId) {
        for (Long id : userId) {
            userMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public void delete(Long pId) {
        userMapper.deleteByPrimaryKey(pId);
    }

    @Override
    public int insertSelective(User user) {

        //对密码进行加密存储
        String password = user.getPassword();
        //对密码进行加密存储
        String encode = passwordEncoder.encode(password);
        user.setPassword(encode);

        return userMapper.insertSelective(user);
    }
}
