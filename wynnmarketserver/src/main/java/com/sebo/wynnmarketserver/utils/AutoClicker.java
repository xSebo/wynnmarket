package com.sebo.wynnmarketserver.utils;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoClicker implements Runnable{

    private Robot r;
    public AutoClicker(){
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void click(){
        try {
            Thread.sleep(200);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(200);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        MultithreadVariables.clickMouse.getAndSet(true);
        while(MultithreadVariables.clickMouse.get()){
            click();
        }
    }
}
