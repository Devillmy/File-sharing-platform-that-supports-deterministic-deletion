
package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import com.example.entity.NxSystemKeyFileInfo;
import com.example.exception.CustomException;
import com.example.service.NxSystemKeyFileInfoService;
import com.github.pagehelper.PageInfo;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/keyfiles")
public class NxSystemKeyFileController {

    private static final String BASE_PATH = System.getProperty("user.dir") + "/src/main/resources/static/keyFile/";

    @Resource
    private NxSystemKeyFileInfoService nxSystemKeyFileInfoService;

    @PostMapping("/upload1")
    public Result upload1(MultipartFile file, HttpServletRequest request) throws IOException {
        String originName = file.getOriginalFilename();
        // 1. 先查询有没有相同名称的文件
        NxSystemKeyFileInfo keyFileInfo = nxSystemKeyFileInfoService.findByFileName(originName);
        if (keyFileInfo != null) {
            throw new CustomException("1001", "文件名：\"" + originName + "\"已存在");
        }
        // 文件名加个时间戳
        String fileName = FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);

        // 2. 文件上传
        FileUtil.writeBytes(file.getBytes(), BASE_PATH + fileName);

        // 3. 信息入库，获取文件id
        NxSystemKeyFileInfo info = new NxSystemKeyFileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        NxSystemKeyFileInfo addInfo = nxSystemKeyFileInfoService.add(info);
        if (addInfo != null) {
            return Result.success(addInfo);
        } else {
            return Result.error("4001", "上传失败");
        }
    }

    @PostMapping("/notice/upload1")
    public Result<Map<String, String>> noticeUpload(MultipartFile file, HttpServletRequest request) throws IOException {
        String originName = file.getOriginalFilename();
        // 文件名加个时间戳
        String fileName = FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);
        // 2. 缩小尺寸
        FileUtil.mkdir(BASE_PATH);
        Thumbnails.of(file.getInputStream()).width(400).toFile(BASE_PATH + fileName);

        // 3. 信息入库，获取文件id
        NxSystemKeyFileInfo info = new NxSystemKeyFileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        NxSystemKeyFileInfo addInfo = nxSystemKeyFileInfoService.add(info);

        Map<String, String> map = new HashMap<>(2);
        map.put("src", "/files/download/" + addInfo.getId());
        map.put("title", originName);
        return Result.success(map);
    }

    @GetMapping("/page/{name}")
    public Result<PageInfo<NxSystemKeyFileInfo>> filePage(@PathVariable String name,
                                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<NxSystemKeyFileInfo> pageInfo = nxSystemKeyFileInfoService.findPage(name, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        if ("null".equals(id)) {
            throw new CustomException("1001", "您未上传文件");
        }
        NxSystemKeyFileInfo nxSystemKeyFileInfo = nxSystemKeyFileInfoService.findById(Long.parseLong(id));
        if (nxSystemKeyFileInfo == null) {
            throw new CustomException("1001", "未查询到该文件");
        }
        byte[] bytes = FileUtil.readBytes(BASE_PATH + nxSystemKeyFileInfo.getFileName());
        response.reset();
        // 设置response的Header
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(nxSystemKeyFileInfo.getOriginName(), "UTF-8"));
        response.addHeader("Content-Length", "" + bytes.length);
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        toClient.write(bytes);
        toClient.flush();
        toClient.close();
    }

    @DeleteMapping("/{id}")
    public Result deleteFile(@PathVariable String id) {
        NxSystemKeyFileInfo nxSystemFileInfo = nxSystemKeyFileInfoService.findById(Long.parseLong(id));
        if (nxSystemFileInfo == null) {
            throw new CustomException("1001", "未查询到该文件");
        }
        String name = nxSystemFileInfo.getFileName();
        // 先删除文件
        FileUtil.del(new File(BASE_PATH + name));
        // 再删除表记录
        nxSystemKeyFileInfoService.delete(Long.parseLong(id));

        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<NxSystemKeyFileInfo> getById(@PathVariable String id) {
        NxSystemKeyFileInfo nxSystemFileInfo = nxSystemKeyFileInfoService.findById(Long.parseLong(id));
        if (nxSystemFileInfo == null) {
            throw new CustomException("1001", "数据库未查到此文件");
        }
        try {
            FileUtil.readBytes(BASE_PATH + nxSystemFileInfo.getFileName());
        } catch (Exception e) {
            throw new CustomException("1001", "此文件已被您删除");
        }
        return Result.success(nxSystemFileInfo);
    }
}
