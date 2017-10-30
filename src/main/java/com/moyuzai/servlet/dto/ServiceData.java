package com.moyuzai.servlet.dto;

public class ServiceData {

    //如果经过DAO查询的结果存在则为true，为空或不符合期望则为false
    private  boolean state;

    //状态码，用于区分数据的结果类型（默认为0）
    private int stateNum;

    //DAO查询到的数据
    private Object data;

    //构造函数，表示DAO查询数据为空
    public ServiceData() {
        this(false,-100,null);
    }

    public ServiceData(boolean state, Object data) {
        this(state,0,data);
    }

    public ServiceData(boolean state, int stateNum, Object data) {
        this.state = state;
        this.stateNum = stateNum;
        this.data = data;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getStateNum() {
        return stateNum;
    }

    public void setStateNum(int stateNum) {
        this.stateNum = stateNum;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
