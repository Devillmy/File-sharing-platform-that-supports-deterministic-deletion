package com.example.service;

import cn.hutool.json.JSONUtil;
import com.example.dao.TypeInfoDao;
import org.springframework.stereotype.Service;
import com.example.entity.TypeInfo;
import com.example.entity.AuthorityInfo;
import com.example.entity.Account;
import com.example.vo.TypeInfoVo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class TypeInfoService {

    @Resource
    private TypeInfoDao typeInfoDao;

    public TypeInfo add(TypeInfo typeInfo) {
        typeInfoDao.insertSelective(typeInfo);
        return typeInfo;
    }

    public void delete(Long id) {
        typeInfoDao.deleteByPrimaryKey(id);
    }

    public void update(TypeInfo typeInfo) {
        typeInfoDao.updateByPrimaryKeySelective(typeInfo);
    }

    public TypeInfo findById(Long id) {
        return typeInfoDao.selectByPrimaryKey(id);
    }

    public List<TypeInfoVo> findAll() {
        return typeInfoDao.findByName("all");
    }

    public PageInfo<TypeInfoVo> findPage(String name, Integer pageNum, Integer pageSize, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        List<TypeInfoVo> all = findAllPage(request, name);
        return PageInfo.of(all);
    }

    public List<TypeInfoVo> findAllPage(HttpServletRequest request, String name) {
		return typeInfoDao.findByName(name);
    }

}
