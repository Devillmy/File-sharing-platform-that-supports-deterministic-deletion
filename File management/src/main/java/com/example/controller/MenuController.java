package com.example.controller;

import com.example.common.Result;
import com.example.service.AdminInfoService;
import com.example.service.FileInfoService;
import com.example.service.TypeInfoService;
import com.example.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MenuController {

	@Resource
	private AdminInfoService adminInfoService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private FileInfoService fileInfoService;

    @GetMapping(value = "/getTotal", produces="application/json;charset=UTF-8")
    public Result<Map<String, Integer>> getTotle() {
        Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("adminInfo", adminInfoService.findAll().size());
		resultMap.put("userInfo", userInfoService.findAll().size());
		resultMap.put("fileInfo", fileInfoService.findAll().size());

        return Result.success(resultMap);
    }
}
