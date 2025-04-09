package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        Long size = stringRedisTemplate.opsForList().size(RedisConstants.CACHE_SHOP_TYPE_KEY);
        List<String> shopTypeJsonList = stringRedisTemplate.opsForList().range(RedisConstants.CACHE_SHOP_TYPE_KEY, 0, size - 1);

        List<ShopType> shopTypeList = new ArrayList<>();

        if(shopTypeJsonList != null && !shopTypeJsonList.isEmpty()){
            for(String shopTypeJson : shopTypeJsonList){
                shopTypeList.add(JSONUtil.toBean(shopTypeJson,ShopType.class));
            }
            return Result.ok(shopTypeList);
        }

        shopTypeList = query().orderByAsc("sort").list();

        if(shopTypeList == null || shopTypeList.isEmpty()){
            return Result.fail("没有查询到商铺类型信息");
        }

        for(ShopType shopType : shopTypeList){
            String shopTypeJson = JSONUtil.toJsonStr(shopType);
            stringRedisTemplate.opsForList().rightPush(RedisConstants.CACHE_SHOP_TYPE_KEY,shopTypeJson);
        }

        return Result.ok(shopTypeList);
    }
}
