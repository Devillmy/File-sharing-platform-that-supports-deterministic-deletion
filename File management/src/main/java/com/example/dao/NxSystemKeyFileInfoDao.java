package com.example.dao;

import com.example.entity.NxSystemKeyFileInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface NxSystemKeyFileInfoDao extends Mapper<NxSystemKeyFileInfo> {
    List<NxSystemKeyFileInfo> findByName(@Param("name") String name);
    NxSystemKeyFileInfo findByFileName(@Param("name") String name);
}
