package com.tokia.tranbot.base;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HThread  implements Runnable {

    private  ICallback callback;

    public void run()
    {
        if(callback== null) {
            return;
        }
        callback.run();
    }
}