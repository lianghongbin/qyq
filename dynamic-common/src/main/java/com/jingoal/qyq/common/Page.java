package com.jingoal.qyq.common;

/**
 * Created by lianghb on 16/3/18.
 * Description:
 */
public class Page {

    private int size = 10;
    private int num = 1;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getOffset() {
        return (num -1) * size;
    }
}
