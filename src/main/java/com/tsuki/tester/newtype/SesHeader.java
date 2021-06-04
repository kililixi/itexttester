package com.tsuki.tester.newtype;

/**
 * @program: tester
 * @description: 印章头
 * @author: startsi
 * @create: 2021-06-02 16:27
 **/
public class SesHeader {
    private String id;

    private String version;

    private String vid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }
}
