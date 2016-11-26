/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unicon.tools;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Scott
 */
public class Unicon2DWindowManager extends Game
{
    private Unicon2DWindow currentWindow, lastWindow;
    private String initAttribs[];
    private final static Object monitor = new Object();
    
    private volatile String vAttribs[];
    private volatile Unicon2DWindow newWindow;
    private volatile boolean newWindowRequested;

    public Unicon2DWindowManager() {
        newWindowRequested = false;
    }
    
    public Unicon2DWindowManager(String attribs[])
    {
        newWindowRequested = false;
        initAttribs = attribs;
    }
    
    @Override
    public void create() 
    {
        currentWindow = new Unicon2DWindow(initAttribs);
        setScreen(currentWindow);
    }
    
    public Unicon2DWindow getCurrentWindow(){
        return currentWindow;
    }
    
    public Unicon2DWindow newWindow(String attributes[])
    {
        vAttribs = attributes;
        newWindowRequested = true;
        synchronized(monitor)
        {
            monitor.notify();
            try {
                monitor.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Unicon2DWindowManager.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        return newWindow;
    }
    
    public void updateCurrentWindow()
    {
        lastWindow = currentWindow;
        currentWindow = (Unicon2DWindow) getScreen();
    }
    
    @Override
    public void render()
    {
        getScreen().render(Gdx.graphics.getDeltaTime());
        if(newWindowRequested)
        {
            synchronized(monitor)
            {
                newWindow = new Unicon2DWindow(vAttribs);
                newWindowRequested = false;
                monitor.notify();
            }
        }
    }
}
