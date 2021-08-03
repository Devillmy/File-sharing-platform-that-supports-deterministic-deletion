package com.example.dao;

import com.example.entity.KeyFileInfo;
import com.example.vo.KeyFileInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface KeyFileInfoDao extends Mapper<KeyFileInfo> {
    List<KeyFileInfoVo> findByNameAndId(@Param("name") String name,
                                     @Param("typeId") Long typeId,
                                     @Param("id") Long id);
}
