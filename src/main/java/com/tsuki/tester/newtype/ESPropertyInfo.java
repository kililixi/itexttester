package com.tsuki.tester.newtype;

import java.util.Date;
import java.util.List;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-03 13:54
 **/
public class ESPropertyInfo {

    private int type;

    private String name;

    private int certListType;

    // 可以存证书的hash或是
    private List<CertDigest> certList;

    private Date createDate;

    private Date validStart;

    private Date validEnd;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCertListType() {
        return certListType;
    }

    public void setCertListType(int certListType) {
        this.certListType = certListType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getValidStart() {
        return validStart;
    }

    public void setValidStart(Date validStart) {
        this.validStart = validStart;
    }

    public Date getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(Date validEnd) {
        this.validEnd = validEnd;
    }

    public List<CertDigest> getCertList() {
        return certList;
    }

    public void setCertList(List<CertDigest> certList) {
        this.certList = certList;
    }
}
