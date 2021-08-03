package com.example.dao;

import com.example.entity.FileInfo;
import com.example.vo.FileInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface FileInfoDao extends Mapper<FileInfo> {
    List<FileInfoVo> findByNameAndId(@Param("name") String name,
                                     @Param("typeId") Long typeId,
                                     @Param("id") Long id);
}
