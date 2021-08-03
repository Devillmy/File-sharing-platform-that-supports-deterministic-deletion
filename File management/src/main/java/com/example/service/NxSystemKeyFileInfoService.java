package com.example.service;

import com.example.dao.NxSystemKeyFileInfoDao;
import com.example.entity.NxSystemKeyFileInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NxSystemKeyFileInfoService {

    @Value("${authority.info}")
    private String authorityInfo;

    @Resource
    private NxSystemKeyFileInfoDao nxSystemKeyFileInfoDao;

    public NxSystemKeyFileInfo add(NxSystemKeyFileInfo nxSystemKeyFileInfo) {
        nxSystemKeyFileInfoDao.insertSelective(nxSystemKeyFileInfo);
        return nxSystemKeyFileInfo;
    }

    public void delete(Long id) {
        nxSystemKeyFileInfoDao.deleteByPrimaryKey(id);
    }

    public void update(NxSystemKeyFileInfo nxSystemFileInfo) {
        nxSystemKeyFileInfoDao.updateByPrimaryKeySelective(nxSystemFileInfo);
    }

    public NxSystemKeyFileInfo findById(Long id) {
        return nxSystemKeyFileInfoDao.selectByPrimaryKey(id);
    }
    
    public NxSystemKeyFileInfo findByFileName(String name) {
        return nxSystemKeyFileInfoDao.findByFileName(name);
    }

    public List<NxSystemKeyFileInfo> findAll() {
        return nxSystemKeyFileInfoDao.findByName("all");
    }

    public PageInfo<NxSystemKeyFileInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<NxSystemKeyFileInfo> all = nxSystemKeyFileInfoDao.findByName(name);
        return PageInfo.of(all);
    }
}
