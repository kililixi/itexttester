package com.tsuki.tester.newtype;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-03 14:15
 **/
public class ESPictureInfo {

    /**
     * 图像数据格式类型： GIF BMP JPG PNG SVG
     */
    private String type;

    private String data;

    private int width;

    private int height;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
