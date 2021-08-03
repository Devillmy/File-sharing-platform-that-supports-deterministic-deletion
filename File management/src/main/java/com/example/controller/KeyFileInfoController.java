package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.KeyFileInfo;
import com.example.service.KeyFileInfoService;
import com.example.vo.KeyFileInfoVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;



@RestController
@RequestMapping("/keyFileInfo")
class KeyFileInfoController {

    @Resource
    private NxSystemKeyFileController nxSystemKeyFileController;
    @Resource
    private KeyFileInfoService keyFileInfoService;
    @PostMapping
    public Result<KeyFileInfo> add(@RequestBody KeyFileInfo info, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        info.setUserName(account.getName());
        info.setLevel(account.getLevel());
        info.setUploadUserId(account.getId());
        keyFileInfoService.add(info);
        return Result.success(info);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        KeyFileInfo info = keyFileInfoService.findById(id);
        if (!account.getLevel().equals(info.getLevel()) || !account.getId().equals(info.getUploadUserId())) {
            return Result.error("1001", "不能删除他人的记录");
        }
        keyFileInfoService.delete(id);
        // 删除对应文件记录
        if (info.getFileId() != null) {
            nxSystemKeyFileController.deleteFile(info.getFileId().toString());
        }
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody KeyFileInfo info, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (!account.getLevel().equals(info.getLevel()) || !account.getId().equals(info.getUploadUserId())) {
            return Result.error("1001", "不能修改他人的记录");
        }
        keyFileInfoService.update(info);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<KeyFileInfoVo> detail(@PathVariable Long id) {
        KeyFileInfoVo info = keyFileInfoService.findById(id);
        return Result.success(info);
    }

    @GetMapping
    public Result<List<KeyFileInfoVo>> all() {
        return Result.success(keyFileInfoService.findAll());
    }

    @GetMapping("/page/{name}/{typeId}")
    public Result<PageInfo<KeyFileInfoVo>> page(@PathVariable String name,
                                                @PathVariable Long typeId,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                HttpServletRequest request) {
        return Result.success(keyFileInfoService.findPage(name, typeId, pageNum, pageSize));
    }

}

