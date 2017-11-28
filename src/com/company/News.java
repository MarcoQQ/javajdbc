package com.company;
import java.sql.Date;

/**
 * Created by marco sun on 2017/11/16.
 */
public class News {
    private int id;
    private String title;
    private String content;
    private Date time;
    private String adminName;
    public News(){

    }

    public News(int id, String title, String content, Date time, String adminName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.adminName = adminName;
    }

    public int getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getTime() {
        return time;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String toString() {
        return getClass().getName()
                + "[id=" + id
                + ",title=" + title
                + ",content=" + content
                + ",time=" + time
                + ",adminName=" + adminName
                + "]";
    }


}
