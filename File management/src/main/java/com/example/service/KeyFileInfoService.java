package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import com.example.dao.KeyFileInfoDao;
import com.example.entity.KeyFileInfo;
import com.example.vo.KeyFileInfoVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class KeyFileInfoService {

    @Resource
    private KeyFileInfoDao keyFileInfoDao;

    public KeyFileInfo add(KeyFileInfo info) {
        keyFileInfoDao.insertSelective(info);
        return info;
    }

    public void delete(Long id) {
        keyFileInfoDao.deleteByPrimaryKey(id);
    }

    public void update(KeyFileInfo info) {
        keyFileInfoDao.updateByPrimaryKeySelective(info);
    }

    public KeyFileInfoVo findById(Long id) {
        List<KeyFileInfoVo> list = keyFileInfoDao.findByNameAndId(null, null,  id);
        if (!CollectionUtil.isEmpty(list)) {
            return list.get(0);
        }
        return new KeyFileInfoVo();
    }

    public List<KeyFileInfoVo> findAll() {
        return keyFileInfoDao.findByNameAndId("all", null, null);
    }

    public PageInfo<KeyFileInfoVo> findPage(String name, Long typeId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<KeyFileInfoVo> info = keyFileInfoDao.findByNameAndId(name, typeId, null);
        return PageInfo.of(info);
    }
}
