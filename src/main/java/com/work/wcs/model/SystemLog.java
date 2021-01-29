package com.work.wcs.model;

import java.util.Date;

public class SystemLog {
    private Integer id;

    private Integer type;

    private String logcontent;

    private String useraccount;

    private String time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLogcontent() {
        return logcontent;
    }

    public void setLogcontent(String logcontent) {
        this.logcontent = logcontent == null ? null : logcontent.trim();
    }

    public String getUseraccount() {
        return useraccount;
    }

    public void setUseraccount(String useraccount) {
        this.useraccount = useraccount == null ? null : useraccount.trim();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}