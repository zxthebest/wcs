package com.work.wcs.controller;

import com.work.wcs.model.SystemLog;
import com.work.wcs.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/")
public class SystemLogController {
    @Autowired
    private SystemLogService systemLogService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping("/getAllSystemLog")
    @ResponseBody
    public Object getAllSystemLog() {
        List<SystemLog> list=systemLogService.findAllSystemLog();
        return list;

    }
}
