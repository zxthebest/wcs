package com.work.wcs.controller;

import com.work.wcs.model.SystemLog;
import com.work.wcs.service.SystemLogService;
import com.work.wcs.util.CommonResult;
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping("/getAllSystemLog")
    @ResponseBody
    public Object getAllSystemLog() {
        CommonResult cr = new CommonResult();
        try {
            List<SystemLog> list = systemLogService.findAllSystemLog();
            cr.setSTATUS(CommonResult.getSuccesscode());
            cr.setDatas(list);
            cr.setMessage("查询日志列表成功");
            cr.setReturnCode(1);
            return cr;
        } catch (Exception e) {
            cr.setMessage("查询日志列表失败");
            cr.setReturnCode(0);
            return cr;
        }
    }
}
