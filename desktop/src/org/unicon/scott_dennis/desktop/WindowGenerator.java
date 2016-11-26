package org.unicon.scott_dennis.desktop;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.unicon.tools.Unicon2DWindow;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.unicon.tools.Unicon2DWindowManager;

/**
 *
 * @author Scott
 */
public class WindowGenerator
{
    private boolean firstWinOpened;
    private final static Object monitor = new Object();
    private static String initAttribs[];
    
    Thread renderThread = new Thread(new Runnable()
    {
        @Override
        public void run()
        {
            synchronized(monitor)
            {

                try {
                    monitor.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(WindowGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }

                monitor.notify();
            }
            openFirstWindow(initAttribs);
        }
    });
    
    public WindowGenerator()
    {
        firstWinOpened = false;
    }
    
    public Unicon2DWindow open(String... attributes)
    {
        initAttribs = attributes;
        if(!firstWinOpened)
        {
            renderThread.start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WindowGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            synchronized(monitor)
            {
                monitor.notifyAll();        //Tell renderThread to create new application
                try {
                    monitor.wait();         //Wait for confirmation
                } catch (InterruptedException ex) {
                    Logger.getLogger(WindowGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {                       //Recieved notify, window should be opening. Sleep to wait for initialization.                   
                    Thread.sleep(7000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WindowGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }         
            firstWinOpened = true;
            return (Unicon2DWindow)(((Unicon2DWindowManager) Gdx.app.getApplicationListener()).getCurrentWindow());
        } 
        else
        {
            return (Unicon2DWindow)(((Unicon2DWindowManager) Gdx.app.getApplicationListener()).newWindow(attributes));
        }
    }
    
    private static void openFirstWindow(String attribs[])
    {
        Lwjgl3ApplicationConfiguration config;        
        config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(new Unicon2DWindowManager(attribs), config);
        System.exit(0);
    }
}
