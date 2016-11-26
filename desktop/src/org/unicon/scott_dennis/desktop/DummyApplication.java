/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unicon.scott_dennis.desktop;

import static com.badlogic.gdx.Input.Keys.ENTER;
import org.unicon.tools.Unicon2DWindow;
import org.unicon.tools.UniconEvent;

/**
 *
 * @author Scott
 */
public class DummyApplication 
{

    public static void main() 
    {
        WindowGenerator winGen = new WindowGenerator();
        Unicon2DWindow w = winGen.open("label = Unicon2DWindow Test", "Bg = opaque light brownish white", "Fg = opaque light brownish brown", "size = 700,700");
        Unicon2DWindow w2 = winGen.open("bg = opaque light whitish blue", "fg = black");
        
        for (int i = 0; i < 65; i++) 
        {
            w.drawLine(-100, 600 - i * 20, 800, 1400 - i * 20);
        }
        
        w.WAttrib("fg = black", "bg = white");

        w.WAttrib("dx = 10", "dy = 10");
        w.EraseArea(0, 0, 125, 125);
        w.drawRectangle(0, 0, 125, 125);
        w.Clip(1, 1, 124, 124);
        w.write("Hello,");
        w.GotoRC(0, 1);
        w.write("World");
        w.drawCircle(60, 60, 50, 0, 360);
        w.fillRectangle(60, 60, 300, 300);
        w.EraseArea(80, 80, 30, 300);
        w.Clip();

        w.WAttrib("dx = 145", "dy = 10");
        w.Bg("opaque light greyish grey");
        w.EraseArea(0, 0, 125, 125);
        w.Bg("white");
        w.drawRectangle(0, 0, 125, 125);
        w.fillCircle(0, 0, 50, 0, 360);
        w.Clip(1, 1, 123, 123);
        w.GotoRC(0, 0);

        w.write("Fonts...");

        w.Font("serif, 16");
        w.GotoRC(0, 1);
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
        w.EraseArea(0, 0, 125, 125);
        w.Bg("white");
        w.drawRectangle(0, 0, 125, 125);
        w.Clip(1, 1, 123, 123);
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
        w.EraseArea(0, 0, 125, 125);
        w.drawRectangle(0, 0, 125, 125);
        w.Clip(1, 1, 123, 123);
        w.CopyArea(-340, 30, 200, 125, 0, 0);

        w.Clip();
        w.WAttrib("dx = 550", "dy = 10");

        w.WAttrib("dx = 0", "dy = 0");
        
        UniconEvent event;
        while (true) {
            boolean bSwitch = true;
            event = w.Event();
            System.out.println("Keypressed: " + event.keycode);
            if(event.keycode == ENTER)
            {
                if(bSwitch)
                {
                    w2.Raise();
                    bSwitch = false;
                }
                else
                {
                    w.Raise();
                    bSwitch = true;
                }
            }
        }
    }

}
