package com.progressoft.brix.domino.api.client.mvp.presenter;

public interface Presentable {
    default Presentable init(){
        return this;
    }
}
