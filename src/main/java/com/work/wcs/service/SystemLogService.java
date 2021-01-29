package com.work.wcs.service;

import com.work.wcs.mapper.SystemLogMapper;
import com.work.wcs.model.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemLogService {
    @Autowired
    private SystemLogMapper systemLogMapper;

    // 查询所有日志
    public List<SystemLog> findAllSystemLog() {
        return systemLogMapper.selectAll();
    }

}
