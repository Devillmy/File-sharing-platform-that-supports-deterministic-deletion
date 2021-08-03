package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.FileInfo;
import com.example.service.FileInfoService;
import com.example.entity.KeyFileInfo;
import com.example.service.KeyFileInfoService;
import com.example.vo.FileInfoVo;
import com.example.vo.KeyFileInfoVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/fileInfo")
public class FileInfoController {

    @Resource
    private NxSystemFileController nxSystemFileController;
    @Resource
    private FileInfoService fileInfoService;
    @PostMapping
    public Result<FileInfo> add(@RequestBody FileInfo info, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        info.setUserName(account.getName());
        info.setLevel(account.getLevel());
        info.setUploadUserId(account.getId());
        fileInfoService.add(info);
        return Result.success(info);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        FileInfo info = fileInfoService.findById(id);
        if (!account.getLevel().equals(info.getLevel()) || !account.getId().equals(info.getUploadUserId())) {
            return Result.error("1001", "不能删除他人的记录");
        }
        fileInfoService.delete(id);
        // 删除对应文件记录
        if (info.getFileId() != null) {
            nxSystemFileController.deleteFile(info.getFileId().toString());
        }
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody FileInfo info, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (!account.getLevel().equals(info.getLevel()) || !account.getId().equals(info.getUploadUserId())) {
            return Result.error("1001", "不能修改他人的记录");
        }
        fileInfoService.update(info);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<FileInfoVo> detail(@PathVariable Long id) {
        FileInfoVo info = fileInfoService.findById(id);
        return Result.success(info);
    }

    @GetMapping
    public Result<List<FileInfoVo>> all() {
        return Result.success(fileInfoService.findAll());
    }

    @GetMapping("/page/{name}/{typeId}")
    public Result<PageInfo<FileInfoVo>> page(@PathVariable String name,
                                             @PathVariable Long typeId,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "5") Integer pageSize,
                                             HttpServletRequest request) {
        return Result.success(fileInfoService.findPage(name, typeId, pageNum, pageSize));
    }

}


