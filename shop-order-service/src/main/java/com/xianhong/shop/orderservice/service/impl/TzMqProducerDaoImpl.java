package com.xianhong.shop.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xianhong.shop.dao.TzMqProduceMapper;
import com.xianhong.shop.dao.TzOrderMapper;
import com.xianhong.shop.orderservice.service.TzMqProducerDao;
import com.xianhong.shop.orderservice.service.TzOrderDao;
import com.xianhong.shop.pojo.TzMqProduce;
import com.xianhong.shop.pojo.TzOrder;
import org.springframework.stereotype.Service;

/**
 * @author xianhong
 * @date 2022/1/11
 */
@Service
public class TzMqProducerDaoImpl extends ServiceImpl<TzMqProduceMapper, TzMqProduce> implements TzMqProducerDao {
}
