package com.example.dao;

import com.example.entity.TypeInfo;
import com.example.vo.TypeInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface TypeInfoDao extends Mapper<TypeInfo> {
    List<TypeInfoVo> findByName(@Param("name") String name);
    
    
    
    Integer count();
}
