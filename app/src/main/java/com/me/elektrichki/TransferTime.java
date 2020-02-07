package com.me.elektrichki;

public class TransferTime {

        String time;
        Integer order;

    public TransferTime(String time, int order) {
        this.time = time;
        this.order = order;
    }


    public String getTime() {
        return time;
    }

    public Integer getOrder() {
        return order;
    }
}
