package org.unicon.scott_dennis.desktop;


public class DesktopLauncher 
{   /*     
    static Lwjgl3Application app;
    static Lwjgl3ApplicationConfiguration config;
    private final static Object monitor = new Object();
    static GLCapabilities caps;
    
    static Thread renderThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized(monitor)
                {
                    System.out.println("Ren: renderThread running. Waiting...");
                    try {
                      monitor.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DesktopLauncher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Ren: Notify recieved, notifying, leaving monitor, calling open()");
                    monitor.notify();
                }
                open();
            }
        });
    
    static Thread appThread = new Thread(new Runnable()
        {
            Unicon2DWindow w;
            @Override
            public void run()
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DesktopLauncher.class.getName()).log(Level.SEVERE, null, ex);
                }
                synchronized(monitor)
                {
                    System.out.println("App: appThread running. Waiting for input.");
                    Scanner scan = new Scanner(System.in);
                    while(true)
                    {
                        
                        String s = scan.next();
                        if(s.equals("open"))
                        {
                            System.out.println("App: Input recieved. Notifying...");
                            monitor.notifyAll();
                            try {
                                monitor.wait();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(DesktopLauncher.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("App: Recieved notify, window should be open. Sleeping for 7000.");
                            try {
                                Thread.sleep(7000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(DesktopLauncher.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("App: Done sleeping.");
                            w = ((Unicon2DWindowTest) Gdx.app.getApplicationListener()).w;
                            
                            
                            for(int i = 0; i < 65; i++)
                            {
                                w.drawLine(-100,600-i*20, 800, 1400-i*20);
                            }
                            w.WAttrib("fg = black", "bg = white");
        
                            w.WAttrib("dx = 10", "dy = 10");
                            w.EraseArea(0,0,125,125);
                            w.drawRectangle(0, 0, 125, 125);
                            w.fillCircle(10,10,50,0,360);
                            w.drawCircle(50, 89, 50, 0, 360);
                            w.Clip(1,1,124,124);
                            //w.write("Hello,");
                            w.GotoRC(0, 1);
                            w.write("World");
                            //w.drawCircle(60, 60, 50, 0, 360);
                            w.fillRectangle(60, 60, 300, 300);
                            w.EraseArea(80, 80, 30, 300);
                            w.Clip();

                            w.WAttrib("dx = 145", "dy = 10");
                            w.Bg("opaque light greyish grey");
                            w.EraseArea(0,0,125,125);
                            w.Bg("white");
                            w.drawRectangle(0, 0, 125, 500);
                            w.fillCircle(0,0,50,0,360);
                            //w.drawCircle(100, 100, 40, 0, 360);
                            w.Clip(1,1,123,123);
                            w.GotoRC(0, 0);
        
                            w.write("Fonts...");
        
                            w.Font("serif, 16");
                            w.GotoRC(0,1);
                            w.write("AaBbCcDdEeFfGgHhKk");
        
                            w.Font("serif, italic, 16");
                            w.GotoRC(0, 2);
                            w.write("AaBbCcDdEeFfGgHhKk");
        
                            w.Font("serif, bold, 16");
                            w.GotoRC(0, 3);
                            w.write("AaBbCcDdEeFfGgHhKk");
        
                            w.Font("serif, italic, bold, 16");
                            w.GotoRC(0, 4);
                            w.write("AaBbCcDdEeFfGgHhKk");
                            w.Clip();
        
                            w.WAttrib("dx = 280", "dy = 10");
                            w.Bg("opaque light greyish grey");
                            w.EraseArea(0,0,125,125);
                            w.Bg("white");
                            w.drawRectangle(0, 0, 125, 125);
                            w.Clip(1,1,123,123);
                            w.GotoRC(0, 0);
        
                            w.Font("serif, 16");
                            w.GotoRC(0, 0);
                            w.write("Serif");
        
                            w.Font("sans, 16");
                            w.GotoRC(0, 1);
                            w.write("Sans");
        
                            w.Font("Typewriter, 16");
                            w.GotoRC(0, 2);
                            w.write("Typewriter");
        
                            w.Font("mono, 16");
                            w.GotoRC(0, 3);
                            w.write("Mono");
                            w.Clip();
        
                            w.WAttrib("dx = 415", "dy = 10");
                            w.EraseArea(0,0,125,125);
                            w.drawRectangle(0, 0, 125, 125);
                            w.Clip(1,1,123,123);
                            w.CopyArea(-340, 30, 200, 125, 0, 0);
        
                            w.Clip();
                            w.WAttrib("dx = 550", "dy = 10");
        
                            w.WAttrib("dx = 0", "dy = 0");
                            UniconEvent event;
                            while(true)
                            {
                                event = w.Event();
                                System.out.println("Keypressed: " + event.keycode);
                            }
                            //w2.CopyArea(w, 0, 0, 125, 125, 400, 400);
                            
                            
                        }
                    }
                }
            }
        });*/
    
    
    public static void main(String[] args)
    {   
        //GLFW.
        //GL.createCapabilities(); 
        //caps = GL.getCapabilities();
        //System.out.println("Starting the threads...");
        //renderThread.start();
        //appThread.start();
        DummyApplication.main();
    }
    /*
    static void open()
    {
        System.out.println("Open() called.");
        config = new Lwjgl3ApplicationConfiguration();
        app = new Lwjgl3Application(new Unicon2DWindowTest(), config);
        System.exit(0);
    }
    
    /*public static void main (String[] arg) 
    {
        app = null;
        config = new Lwjgl3ApplicationConfiguration();
              
        Thread applicationThread = new Thread(new Runnable() 
            {  
                @Override
                public synchronized void run() 
                { 
                    int counter = 0;
                    Scanner scan = new Scanner(System.in);
                    while(true)
                    {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(DesktopLauncher.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        counter++;
                        System.out.println("App: checking if app == null " + String.valueOf(counter));
                        if(app != null)
                        {
                            System.out.println("App: app isn't null, we're in");
                            if(((Unicon2DWindowTest) app.getApplicationListener()).w.isInitialized());
                                ((Unicon2DWindowTest) app.getApplicationListener()).w.drawRectangle(0, 0, 500, 500);
                            break;
                        }
                    
                        /*
                        System.out.println("App: ApplicationThread entered, waiting for input");
                        String s = scan.next();
                        if(s.equals("open"))
                        {
                            System.out.println("App: Open recieved, notifying and waiting");
                                notifyAll();
                                try {
                                    wait();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(DesktopLauncher.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                System.out.println("App: checking if app == null");
                                if(app != null)
                                {
                                    System.out.println("App: app isn't null, we're in");
                                    if(((Unicon2DWindowTest) app.getApplicationListener()).w.isInitialized());
                                    //((Unicon2DWindowTest) app.getApplicationListener()).w.drawRectangle(0, 0, 500, 500
                                }
                                else System.out.println("App: app = null");
                        }
                    }        
                }
            }
        );*/
        /*
        Thread renderThread = new Thread(new Runnable()
            {
                @Override
                public synchronized void run()
                {
                    app = new Lwjgl3Application(new Unicon2DWindowTest(), config);
                    System.exit(0);
                }
            }
        );
        
        //applicationThread.start();
        renderThread.start();
    }*/
}
