/*
 *   Copyright (c) 2016, Scott Dennis
 *   All rights reserved
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *   This software is provided by the copyright holders and contributors
 *   "as is" and any express or implied warranties, including, but not
 *   limited to, the implied warranties of merchantability and fitness for
 *   a particular purpose are disclaimed. In no event shall the copyright
 *   holder or contributors be liable for any direct, indirect, incidental,
 *   special, exemplary, or consequential damages (including, but not
 *   limited to, procurement of substitute goods or services; loss of use,
 *   data, or profits; or business interruption) however caused and on any
 *   theory of liability, whether in contract, strict liability, or tort
 *   (including negligence or otherwise) arising in any way out of the use
 *   of this software, even if advised of the possibility of such damage.
 */
package org.unicon.tools;

//import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.Monitor;
import static com.badlogic.gdx.Input.Keys.Q;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import static com.badlogic.gdx.graphics.glutils.HdpiUtils.glViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Scott
 * 
 * For now, Unicon2DWindow implements Screen. Though this may stay as such,
 * eventually the ability to open multiple windows dynamically is desired, which
 * may require a structural change of the Unicon2DWindow class.
 * 
 */
public class Unicon2DCanvas extends InputAdapter implements Screen
{
    // General and Attrib Declarations
    //public static int windowNumber;
    Monitor primaryMonitor, currentMonitor;
    
    // Canvas Attribute Declarations
    String windowLabel;
    int height, width;
    String size;
    int lines, columns;     
    
    Vector2 cursorPos;
    int row, col;           //unused
    
    // Context Attribute Declarations
    Unicon2DContext coupledContext;
    float fgColor[],
          bgColor[];
    String fgString,
           bgString,
           rgbmode;
    boolean reverse, 
            drawop;
    float gamma;
    
    BitmapFont font;
    float fwidth, fheight,
            ascent, descent,
            leading;
    
    float linewidth;
    String linestyle,
            fillstyle,
            pattern;
    
    float clipx, clipy,
            cliph, clipw;
    int     dx, dy;

    
    
    //Rendering Declarations
    OrthographicCamera camera;
    SpriteBatch batch;
    PolygonSpriteBatch polyBatch;
    PolygonSprite polySprite;
    ShapeRenderer shapeRenderer;
    boolean flush;
    ArrayList<RenderedEntity> entityList;
    Array<RenderedEntity> waitList; 
    Rectangle scissor, clipBounds;
    boolean clipping;
    ArrayList<Integer> polyVertArray;
    
    
    //Font and Text Declarations
    float colWidth;
    BitmapFont serif, sans, typewriter, mono;
    int fontFlag;
    boolean scrollText, takeInput;
    FreeTypeFontGenerator serifGen, serifBGen,  serifIGen,  serifBIGen,
                          sansGen,  sansBGen,   sansIGen,   sansBIGen,
                          typewGen, typewBGen,  typewIGen,  typewBIGen,
                          monoGen,  monoBGen,   monoIGen,   monoBIGen;
    FreeTypeFontParameter fontParam;
    GlyphLayout layout;
    String currfType;
    int currfSize, fRendFlag;
    boolean currfBold, currfItalic;

    //Event Declarations
    volatile Queue<UniconEvent> eventQueue;
    
    //Color Declarations
    float rgbVal[], bgInit[];
    float r, g, b, a;
    static Map<String, Integer[]> colorTable;
    static Map<String, Integer> lightTable, satTable, transpTable;
    
    //Miscelaneous Declarations
    Sound alertSound;
    int delayTime;
    
    
    /**
     *Creates and initializes values for an instance of Unicon2DWindow.
     * Operates similar to a constructor currently, so the code may be transferred
     * to a constructor in the future.
     * @param attribList
     * @return
     */
    public boolean open(String... attribList)
    {        
                               /* Lwjgl3Application app = (Lwjgl3Application)Gdx.app;
				Lwjgl3WindowConfiguration config = new Lwjgl3WindowConfiguration();
				DisplayMode mode = Gdx.graphics.getDisplayMode();
				config.setWindowPosition(MathUtils.random(0, mode.width - 640), MathUtils.random(0, mode.height - 480));
				config.setTitle("Child window");
				Class clazz = childWindowClasses[MathUtils.random(0, childWindowClasses.length - 1)];
				ApplicationListener listener = createChildWindowClass(clazz);
				Lwjgl3Window window = app.newWindow(listener, config);
        
        */
        
        
        // Canvas Declarations
        height = Gdx.graphics.getHeight();
        width = Gdx.graphics.getWidth();
        size = String.valueOf(Gdx.graphics.getWidth()) + ", " + String.valueOf(Gdx.graphics.getHeight());
        
        
        
        //General Definitions
        //windowNumber = 0;
        primaryMonitor = Gdx.graphics.getPrimaryMonitor();
        currentMonitor = Gdx.graphics.getMonitor();
        //dx = 0;
        //dy = 0;
        
        // Context Definititions
        //fgColor = new float[4];
        //fgColor[0] = 1;
        //fgColor[1] = 1;
        //fgColor[2] = 1;
        //fgColor[3] = 1;
        //bgColor = new float[4];
        //bgColor[0] = 0;
        //bgColor[1] = 0;
        //bgColor[2] = 0;
        //bgColor[3] = 1;
        bgInit = new float[4];
        //rgbmode = "auto";
        //reverse = false;
        //drawop = false;
            //gamma = 1.0f;
            // Font, fheight, fwidth definitions given below under "Font and Text Definitions"
           
        // Canvas Definitions
        
        
        
        //Rendering Definitions
        camera      = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch       = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);  
        polyBatch   = new PolygonSpriteBatch();
        shapeRenderer = new ShapeRenderer();
        flush       = false;
        entityList  = new ArrayList();
        waitList    = new Array();
        scissor     = new Rectangle();
        clipBounds  = new Rectangle(0, 0, 0, 0);
        
        
        //Font and Text Definitions
        cursorPos   = new Vector2();
        cursorPos.x = 0-Gdx.graphics.getWidth()/2;
        cursorPos.y = Gdx.graphics.getHeight()/2;
        
        
        serifGen    = new FreeTypeFontGenerator(Gdx.files.internal("CMU_Serif/cmunrm.ttf"));
        serifBGen   = new FreeTypeFontGenerator(Gdx.files.internal("CMU_Serif/cmunbx.ttf"));
        serifIGen   = new FreeTypeFontGenerator(Gdx.files.internal("CMU_Serif/cmunti.ttf"));
        serifBIGen  = new FreeTypeFontGenerator(Gdx.files.internal("CMU_Serif/cmunbi.ttf"));
        
        sansGen     = new FreeTypeFontGenerator(Gdx.files.internal("Carlito/Carlito-Regular.ttf"));
        sansBGen    = new FreeTypeFontGenerator(Gdx.files.internal("Carlito/Carlito-Bold.ttf"));
        sansIGen    = new FreeTypeFontGenerator(Gdx.files.internal("Carlito/Carlito-Italic.ttf"));
        sansBIGen   = new FreeTypeFontGenerator(Gdx.files.internal("Carlito/Carlito-BoldItalic.ttf"));
        
        typewGen    = new FreeTypeFontGenerator(Gdx.files.internal("CourierCode/CourierCode-Roman.ttf"));
        typewBGen   = new FreeTypeFontGenerator(Gdx.files.internal("CourierCode/CourierCode-Bold.ttf"));
        typewIGen   = new FreeTypeFontGenerator(Gdx.files.internal("CourierCode/CourierCode-Italic.ttf"));
        typewBIGen  = new FreeTypeFontGenerator(Gdx.files.internal("CourierCode/CourierCode-BoldItalic.ttf"));

        monoGen     = new FreeTypeFontGenerator(Gdx.files.internal("Hack/Hack-Regular.ttf"));
        monoBGen    = new FreeTypeFontGenerator(Gdx.files.internal("Hack/Hack-Bold.ttf"));
        monoIGen    = new FreeTypeFontGenerator(Gdx.files.internal("Hack/Hack-RegularOblique.ttf"));
        monoBIGen   = new FreeTypeFontGenerator(Gdx.files.internal("Hack/Hack-BoldOblique.ttf"));
        
        
        
        
        fontParam = new FreeTypeFontParameter();
        fontParam.size = 16;
        fontParam.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789`~!@#$%^&*()_+-=[]\\;',./{}|:\">?";
        serif = serifGen.generateFont(fontParam);
        sans = sansGen.generateFont(fontParam);
        typewriter = typewGen.generateFont(fontParam);
        mono = monoGen.generateFont(fontParam);
        
        
        
        font = serif;
        fontFlag    = 1;
        currfType = "serif";
        currfSize = 16;
        currfBold = false;
        currfItalic = false; 
        scrollText  = false;
        takeInput   = false;
        
        
        layout = new GlyphLayout();
        layout.setText(font, "W");
        fheight = font.getLineHeight();
        fwidth  = (int) layout.width;
        colWidth = fwidth;
        
        
         //Event Definitions
        eventQueue = new Queue<UniconEvent>();
        Gdx.input.setInputProcessor(this);
        
        
        //Miscelancous Definitions
        delayTime = 0;
        alertSound = Gdx.audio.newSound(Gdx.files.internal("alertTone.mp3"));
        
        
        //Color Definitions
        rgbVal = new float[4];
        rgbVal[0] = 0;
        rgbVal[1] = 0;
        rgbVal[2] = 0;
        rgbVal[3] = 1;  
        
        // Color table with hue, lightness, and saturation values based off
        //  values found in rwindow.r
        //              Color                      H     L   S
        colorTable = new HashMap();
        colorTable.put("black",      new Integer[]{0,    0,  0});
        colorTable.put("blackish",   new Integer[]{0,    0,  0});
        colorTable.put("blue",       new Integer[]{240,  50, 100});
        colorTable.put("bluish",     new Integer[]{240,  50, 100});
        colorTable.put("brown",      new Integer[]{30,   25, 100});
        colorTable.put("brownish",   new Integer[]{30,   25, 100});
        colorTable.put("cyan",       new Integer[]{180,  50, 100});
        colorTable.put("cyanish",    new Integer[]{180,  50, 100});
        colorTable.put("gray",       new Integer[]{0,    50, 0});
        colorTable.put("grayish",    new Integer[]{0,    50, 0});
        colorTable.put("green",      new Integer[]{120,  50, 100});
        colorTable.put("greenish",   new Integer[]{120,  50, 100});
        colorTable.put("grey",       new Integer[]{0,    50, 0});
        colorTable.put("greyish",    new Integer[]{0,    50, 0});
        colorTable.put("magenta",    new Integer[]{300,  50, 100});
        colorTable.put("magentaish", new Integer[]{300,  50, 100});
        colorTable.put("orange",     new Integer[]{15,   50, 100});
        colorTable.put("orangish",   new Integer[]{15,   50, 100});
        colorTable.put("pink",       new Integer[]{345,  75, 100});
        colorTable.put("pinkish",    new Integer[]{345,  75, 100});
        colorTable.put("purple",     new Integer[]{270,  50, 100});
        colorTable.put("purplish",   new Integer[]{270,  50, 100});
        colorTable.put("red",        new Integer[]{0,    50, 100});
        colorTable.put("redish",     new Integer[]{0,    50, 100});
        colorTable.put("reddish",    new Integer[]{0,    50, 100});
        colorTable.put("violet",     new Integer[]{270,  75, 100});
        colorTable.put("violetish",  new Integer[]{270,  75, 100});
        colorTable.put("white",      new Integer[]{0,    100,0});
        colorTable.put("whitish",    new Integer[]{0,    100,0});
        colorTable.put("yellow",     new Integer[]{60,   50, 100});
        colorTable.put("yellowish",  new Integer[]{60,   50, 100});
        
        // lightness, saturation, and transparency tables based off values found
        //  in rwindow.r
        
        lightTable = new HashMap();
        lightTable.put("dark",            0);
        lightTable.put("deep",            0);
        lightTable.put("light",           100);
        lightTable.put("medium",          50);
        lightTable.put("pale",            100);
        
        satTable = new HashMap();
        satTable.put("moderate",        50);
        satTable.put("strong",          75);
        satTable.put("vivid",           100);
        satTable.put("weak",            25);
        
        transpTable = new HashMap();
        transpTable.put("dull",            75);
        transpTable.put("opaque",          100);
        transpTable.put("subtranslucent",  75);
        transpTable.put("subtransparent",  25);
        transpTable.put("translucent",     50);
        transpTable.put("transparent",     5);  
        
        for(String s: attribList)
        {
            WAttrib(s);
        }
        bgInit[0] = bgColor[0];
        bgInit[1] = bgColor[1];
        bgInit[2] = bgColor[2];
        bgInit[3] = bgColor[3];
        return true;
    }
    
    /**
     * Places given entity on the end of the render list.
     * @param entity 
     */
    public void addEntity(RenderedEntity entity)
    {
        //System.out.println("addEntity called.");
        if(entity.entityType.equals("extarea"))
        {
            entity.entityType = "area";
        }
        waitList.add(entity);
    }
    
    /**
     * Plays an alert sound to call attention or notify user.
     */
    public void Alert()
    {
        alertSound.play();
    }
    
    
    /**
     * Returns the current background color as a float[4] containing the values 
     * red, green, blue, and alpha, respectively.
     * @return 
     */
    public String Bg()
    {
        return bgString;
    }  
    
    /**
     * Sets the background color of the window. This will make no immediate 
     * changes to the window and will only affect entities drawn after this call.
     * Note: Should eventually return true for success and false for failure
     * @param colorPhrase 
     */
    public void Bg(String colorPhrase)
    {
        bgString = colorPhrase;
        bgColor = colorPhrase(colorPhrase);
        updateContext();
    }
    
    /**
     * Sets the background color of the window. This will make no immediate 
     * changes to the window and will only affect entities drawn after this call.
     * Note: Should eventually return true for success and false for failure
     * @param r
     * @param g
     * @param b
     * @param a 
     */
    public void Bg(float r, float g, float b, float a)
    {
        if(rgbmode.equals("auto"))
        {
            if(r<256 && g<256 && b<256 && a<256)
            {
                bgColor[0] = r/255;
                bgColor[1] = g/255;
                bgColor[2] = b/255;
                bgColor[3] = a/255;
            }
            else if(r>255 || g>255 || b>255 || a>255)
            {
                bgColor[0] = r/35565;
                bgColor[1] = g/35565;
                bgColor[2] = b/35565;
                bgColor[3] = a/35565;
            }
        }
        else if(rgbmode.equals("48"))
        {
            bgColor[0] = r/35565;
            bgColor[1] = g/35565;
            bgColor[2] = b/35565;
            bgColor[3] = a/35565;
        }
        else if(rgbmode.equals("24"))
        {
            bgColor[0] = r/255;
            bgColor[1] = g/255;
            bgColor[2] = b/255;
            bgColor[3] = a/255;
        }
        else if(rgbmode.equals("normalized")||
                rgbmode.equals("normal")||
                rgbmode.equals("norm"))
        {
        bgColor[0] = r;
        bgColor[1] = g;
        bgColor[2] = b;
        bgColor[3] = a;
        }
        updateContext();
    }
    
    public Unicon2DContext Clone(String... attributes)
    {
        return coupledContext.Clone(attributes);
    }
    
    /**
     * Closes the program that created an instance of Unicon2DWindow.
     * Note: In the future this should simply close the window, not the program.
     */
    public void WClose()
    {
        System.exit(0);
        //Gdx.app.exit();
    }
    
    public void CenterString(int x, int y, String string)
    {
        layout.setText(font, string);
        drawString((int)(x-layout.width/2),(int)(y-font.getLineHeight()/2),string); 
    }
    
    public void CenterString(Unicon2DContext context, int x, int y, String string)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        CenterString(x, y, string);
        setContext(original);
    }
    
    /**
     * Defines a rectangular area preventing rendering outside of that area. 
     * Affects all entities drawn after this call.
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void Clip(int x, int y, int w, int h)
    {
        clipx = x;
        clipy = y;
        cliph = h;
        clipw = w;
        y = Gdx.graphics.getHeight() - y - (int) dy;
        x = x + (int) dx;
        x -= (float) Gdx.graphics.getWidth()/2;
        y -= (float) Gdx.graphics.getHeight()/2;
        h = -h;
        
        clipBounds = new Rectangle(x, y, w, h);
        clipping = true;  
        updateContext();
    }
    
    /**
     * Removes current clipping bounds.
     */
    public void Clip()
    {
        clipping = false;
    }
    
    /**
     * Interprets a Unicon formatted colorPhrase
     * Copied/translated from colorphrase() in unicon\src\runtime\rwindow.r
     * Note: This function needs to be cleaned and commented more completely.
     * Note: This may need to be reworked to return true for success and false for failure.
     * @param clrph
     * @return 
    */ 
    public static float[] colorPhrase(String clrph)
    {
        int very, i;
        float lgt, sat, blend, bl2, m1, m2, alpha;
        float h1, l1, s1, h2, l2, s2, r2, g2, b2;
        //float r, g, b, a;        
        
        alpha = 1.0f;
        lgt = -1.0f;
        sat = 1.0f;
        blend = 0.0f;
        s1 = 0.0f;
        s2 = 0.0f;
        h1 = 0.0f;
        h2 = 0.0f;
        l1 = 0.0f;
        l2 = 0.0f;
        
        String[] phraseArray = clrph.split(" ");
        i = 0;
        
        Integer transparency = transpTable.get(phraseArray[i]);
        if(transparency != null)
        {
            alpha = (float) transparency / 100.0f;
            //System.out.println("phraseArray: " + phraseArray[0] + phraseArray[1] + phraseArray[2] + phraseArray[3]);
            //phraseArray[0] = phraseArray[1];
            i++;
            //System.out.println("phraseArray: " + phraseArray[0] + phraseArray[1] + phraseArray[2] + phraseArray[3]);
            //System.out.println("Transparency set to " + String.valueOf(alpha));
        }               
          
        if(phraseArray[i].equals("very"))
        {
            very = 1;
            i++;
        }
        else very = 0;
        
        
        Integer light = lightTable.get(phraseArray[i]);
        if(light != null)
        {
            if(phraseArray[i].equals("pale")||phraseArray[i].equals("deep"))
                very = 1;
            lgt = (float) light / 100.0f;
            i++;
            //System.out.println("Lightness set to " + String.valueOf(lgt));
        }
        
        Integer saturation = satTable.get(phraseArray[i]);
        //System.out.println(phraseArray[i]);
        if(saturation != null)
        {
            sat = (float) saturation / 100.0f;
            i++;
            //System.out.println("Saturation set to " + String.valueOf(sat));
        }
        
        if(colorTable.containsKey(phraseArray[i]))
        {
            if(phraseArray.length == 1)
                blend = h1 = l1 = s1 = 0.0f;      
            else
            {
                if(phraseArray[i].contains("ish"))
                    blend = 0.25f;
                else 
                    blend = 0.5f;
                Integer[] hlsArray = colorTable.get(phraseArray[i]);
                h1 = hlsArray[0];
                l1 = (float) hlsArray[1] / 100.0f;
                s1 = (float) hlsArray[2] / 100.0f;   
                i++;
            }
            
            if(colorTable.containsKey(phraseArray[i]))
            {
                
                Integer[] hlsArray = colorTable.get(phraseArray[i]);
                h2 = hlsArray[0];
                l2 = (float) hlsArray[1] / 100.0f;
                s2 = (float) hlsArray[2] / 100.0f;
                //System.out.println("thing: " + phraseArray[i]);
                //System.out.println("s2: " + String.valueOf(s2));
                
            }
        }
        
        if (blend > 0) 
        {
            bl2 = 1.0f - blend;

            if (s1 == 0.0); // use h2 unchanged 
            else if (s2 == 0.0)
                h2 = h1;
            else if (h2 - h1 > 180)
                h2 = blend * h1 + bl2 * (h2 - 360);
            else if (h1 - h2 > 180)
                h2 = blend * (h1 - 360) + bl2 * h2;
            else
                h2 = blend * h1 + bl2 * h2;
            if (h2 < 0)
                h2 += 360;

            l2 = blend * l1 + bl2 * l2;
            s2 = blend * s1 + bl2 * s2;
        }
        
        if(lgt >= 0.0) 
        {
            if(very==1)
                l2 = (2 * lgt + l2) / 3.0f;
            else
                l2 = (lgt + 2 * l2) / 3.0f;
        }
        s2 *= sat;
        
        /* convert h2,l2,s2 to r2,g2,b2 */
        /* from Foley & Van Dam, 1st edition, p. 619 */
        /* beware of dangerous typos in 2nd edition */
        //System.out.println("s2: " + String.valueOf(s2));
    
        if (s2 == 0)
            r2 = g2 = b2 = l2;
        else 
        {
            if(l2 < 0.5)
                m2 = l2 * (1 + s2);
            else
                m2 = l2 + s2 - l2 * s2;
            m1 = 2 * l2 - m2;
            r2 = rgbval(m1, m2, h2 + 120);
            g2 = rgbval(m1, m2, h2);
            b2 = rgbval(m1, m2, h2 - 120);
        } 

        
        /* scale and convert the calculated result */
        
        /*
        if(rgbmode.equals("auto")||rgbmode.equals("24"))
        {
            r = r2*255;
            g = g2*255;
            b = b2*255;
            a = alpha*255;
        }
        else if(rgbmode.equals("48"))
        {
            r = r2*65535;
            g = g2*65535;
            b = b2*65535;
            a = alpha*65535;
        }
        else if(rgbmode.equals("normalized")||rgbmode.equals("normal")||rgbmode.equals("norm"))
        {
            r = r2;
            g = g2;
            b = b2;
            a = alpha;
        }*/
    
        return new float[]{r2, g2, b2, alpha};
    }   
    
    /**
     * Returns the red, blue, and green components of a given color phrase as a
     * comma separated string.
     * @param colorPhrase
     * @return 
     */
    public String ColorValue(String colorPhrase)
    {
        String rgbString;
        float[] rgbArray = colorPhrase(colorPhrase);
        if(rgbmode.equals("auto")||rgbmode.equals("24"))
        {
            for(int i = 0; i<4; i++)
                rgbArray[i] *= 255;
            rgbString = String.valueOf((int)rgbArray[0]) + ","
                      + String.valueOf((int)rgbArray[1]) + ","
                      + String.valueOf((int)rgbArray[2]) + ",";
        }
        else if(rgbmode.equals("48"))
        {
            for(int i = 0; i<4; i++)
                rgbArray[i] *= 65535;
            rgbString = String.valueOf((int)rgbArray[0]) + ","
                      + String.valueOf((int)rgbArray[1]) + ","
                      + String.valueOf((int)rgbArray[2]) + ",";
        }
        else if(rgbmode.equals("normalized")||rgbmode.equals("normal")||rgbmode.equals("norm"))
            rgbString = String.valueOf((float)rgbArray[0]) + ","
                      + String.valueOf((float)rgbArray[1]) + ","
                      + String.valueOf((float)rgbArray[2]) + ",";
        else rgbString = "error";       //shoule never happen
        
        return rgbString;
    }
    
    public void CopyArea(int x1, int y1, int w, int h, int x2, int y2)
    {
        RenderedEntity area = new RenderedEntity("area", 0,0,0,0);
        
        area.x2 = x1 + dx;
        area.y2 = Gdx.graphics.getHeight() - y1 - h - dy;      
        area.x = (int) (x2 + dx - 0.5 * Gdx.graphics.getWidth());
        area.y = (int) (0.5* Gdx.graphics.getHeight() - y2 - h - dy);
        area.w = w;
        area.h = h;
        area.clipped = clipping;
        area.clipBounds = clipBounds;
        area.initialized = false;
        waitList.add(area);
    }
    
    public void CopyArea(Unicon2DWindow w2, int x1, int y1, int w, int h, int x2, int y2)
    {
        //System.out.println("Copy area called.");
        RenderedEntity extArea = new RenderedEntity("extarea", 0,0,0,0);
        extArea.x2 = x1 + Integer.valueOf(w2.WAttrib("dx"));
        extArea.y2 = Gdx.graphics.getHeight() - y1 - h - Integer.valueOf(w2.WAttrib(("dy")));      
        extArea.x = (int) (x2 + dx - 0.5 * Gdx.graphics.getWidth());
        extArea.y = (int) (0.5* Gdx.graphics.getHeight() - y2 - h - dy);
        extArea.w = w;
        extArea.h = h;
        extArea.window = w2;
        extArea.initialized = false;
        waitList.add(extArea);
    }
    
    
    /**
     * Disposes objects used by Unicon2DWindow.
     */
    @Override
    public void dispose() 
    {
        //font.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        serif.dispose(); 
        sans.dispose(); 
        typewriter.dispose(); 
        mono.dispose();
        serifGen.dispose();
        serifBGen.dispose();
        serifIGen.dispose();
        serifBIGen.dispose();
        sansGen.dispose();
        sansBGen.dispose();
        sansIGen.dispose();
        sansBIGen.dispose();
        typewGen.dispose(); 
        typewBGen.dispose();
        typewIGen.dispose();
        typewBIGen.dispose();
        monoGen.dispose();
        monoBGen.dispose();
        monoIGen.dispose();
        monoBIGen.dispose();
        polyBatch.dispose();
        alertSound.dispose();
        
    }
    
    /**
     * Draws a complete hollow ellipse within the given rectangle.
     * @param x
     * @param y
     * @param w
     * @param h 
     */
    public void drawArc(int x, int y, int w, int h)
    {
        RenderedEntity arc = new RenderedEntity("arc", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        arc.x = x;
        arc.y = y;
        arc.w = w;
        arc.h = h;
        arc.filled = false;
        arc.clipped = clipping;
        arc.clipBounds = clipBounds;
        arc.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getBackBufferHeight(), dx, dy);
        waitList.add(arc);
    }
    
    public void drawArc(Unicon2DContext context, int x, int y, int w, int h)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        drawArc(x, y, w, h);
        setContext(original);
        
    }
    
    /**
     * Draws a hollow circular curve with center at (x,y), a radius r, a starting angle 
     *  theta, and an ending angle alpha.
     * @param x
     * @param y
     * @param r
     * @param theta
     * @param alpha 
     */
    public void drawCircle(int x, int y, int r, int theta, int alpha)
    {
        RenderedEntity circle = new RenderedEntity("circle", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        circle.x = x;
        circle.y = y;
        circle.r = r;
        circle.theta = theta;
        circle.alpha = alpha;
        circle.filled = false;
        //if(clipping) System.out.println("Circle drawn, clipping on");
        //else System.out.println("Circle drawn, clipping off");
        circle.clipped = clipping;
        circle.clipBounds = new Rectangle(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        circle.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        waitList.add(circle);
        //entityList.add(circle);
    }
    
    public void drawCircle(Unicon2DContext context, int x, int y, int r, int theta, int alpha)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        drawCircle(x, y, r, theta, alpha);
        setContext(original);
    }
    
    /**
     * Draws a line beginning at (x1, y1) and ending at (x2, y2).
     * @param x1
     * @param y1
     * @param x2
     * @param y2 
     */
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        RenderedEntity line = new RenderedEntity("line", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        line.x = x1;
        line.y = y1;
        line.x2 = x2;
        line.y2 = y2;
        line.clipped = clipping;
        line.clipBounds = clipBounds;
        line.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        waitList.add(line);
    }
    
     public void drawLine(Unicon2DContext context, int x1, int y1, int x2, int y2)
     {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        drawLine(x1, y1, x2, y2);
        setContext(original);
     }
    
    /**
     * Draws a point at (x,y).
     * @param x
     * @param y 
     */
    public void drawPoint(int x, int y)
    {
        RenderedEntity point = new RenderedEntity("point", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        point.x = x;
        point.y = y;
        point.clipped = clipping;
        point.clipBounds = clipBounds;
        point.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        waitList.add(point);
    }
    
    public void drawPoint(Unicon2DContext context, int x, int y)
    {           
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        drawPoint(x, y);
        setContext(original);
    }
    
    /**
     * Draws a hollow polygon specified by an array of vertices.
     * Vertices must be given in such a manner that neighboring vertices in the polygon
     * are adjacent in the array. 
     * @param vertices 
     */
    public void drawPolygon(float... vertices)
    {
        RenderedEntity polygon = new RenderedEntity("polygon", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        polygon.vertices = vertices;
        polygon.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        polygon.filled = false;
        polygon.clipped = clipping;
        polygon.clipBounds = clipBounds;
        waitList.add(polygon);        
    }
    
    public void drawPolygon(Unicon2DContext context, float... vertices)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        drawPolygon(vertices);
        setContext(original);
    }
    
    /**
     * Draws a hollow rectangle specified by top left corner (x,y), 
     * width w, and height h. 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void drawRectangle(int x, int y, int w, int h)
    {
        
        RenderedEntity rectangle = new RenderedEntity("rectangle", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        rectangle.x = x;
        rectangle.y = y;
        rectangle.w = w;
        rectangle.h = h;
        rectangle.filled = false;
        rectangle.clipped = clipping;
        rectangle.clipBounds = clipBounds;
        rectangle.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        waitList.add(rectangle);
    }
    
    public void drawRectangle(Unicon2DContext context, int x, int y, int w, int h)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        drawRectangle(x, y, w, h);
        setContext(original);
    }
    
    /**
     * Draws given string beginning at (x,y). Does not change cursor location.
     * @param x
     * @param y
     * @param string
     */
    public void drawString(int x, int y, String string)
    {
        RenderedEntity text = new RenderedEntity("text", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        text.text = string;
        text.font = font;
        text.x = x;
        text.y = y;
        text.clipped = clipping;
        text.clipBounds = clipBounds;
        text.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        
        RenderedEntity textBg = new RenderedEntity("textbg", bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
        layout.setText(font, string);
        textBg.x = x;
        textBg.y = (int) (y - font.getLineHeight()*0.1);
        textBg.w = (int) layout.width;
        textBg.h = (int) font.getLineHeight();
        textBg.clipped = clipping;
        textBg.clipBounds = clipBounds;
        textBg.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        
        waitList.add(textBg);
        waitList.add(text);
    }
    
    public void drawString(Unicon2DContext context, int x, int y, String string)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        drawString(x, y, string);
        setContext(original);
    }
    
    
    /**
     * Adds an event with given parameters to the end of the of the event queue.
     * @param event
     * @param x
     * @param y
     * @param keycode
     * @param interval
     */
    public void Enqueue(UniconEvent event, int x, int y, int keycode, long interval)
    {
        event.x = x;
        event.y = y;
        event.keycode = keycode;     // Unicon doesn't use keycode it uses something else
        event.timeStamp = interval;  // I don't know if this is right
        eventQueue.addLast(event);
    }
    
    /**
     * Draws a filled rectangle using current background color.
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void EraseArea(int x, int y, int w, int h)
    {
       RenderedEntity erase = new RenderedEntity("erase", bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
       erase.x = x;
       erase.y = y;
       erase.w = w;
       erase.h = h;
       erase.filled = true;
       erase.clipped = clipping;
       erase.clipBounds = clipBounds;
       erase.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
       
       
       
       waitList.add(erase);
    }
    
    public void EraseArea(Unicon2DContext context, int x, int y, int w, int h)  
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        EraseArea(x, y, w, h);
        setContext(original);
    }
    
    /**
     * Returns the event at the head of the event queue. If the event queue is
     * empty, Event() will wait until an event is added.
     * @return
     */
    public UniconEvent Event()
    {
        Gdx.input.setInputProcessor(this);
        while(eventQueue.size == 0) 
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Unicon2DWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        return (UniconEvent) eventQueue.removeFirst();
    }
    
    /**
     * Returns the current foreground color phrase. 
     * @return 
     */
    public String Fg()
    {
        return fgString;
    }
    
    /**
     * Sets the foreground color for the window.
     * Affects entities drawn after this call.
     * @param colorPhrase 
     */
    public void Fg(String colorPhrase)
    {
        fgString = colorPhrase;
        fgColor = colorPhrase(colorPhrase);
        
        setFont(currfType, currfSize, currfBold, currfItalic);
        updateContext();
    }
    
     /**
     * Sets the foreground color for the window.
     * Affects entities drawn after this call.
     * @param r
     * @param g
     * @param b
     * @param a 
     */
    public void Fg(float r, float g, float b, float a)
    {
        if(rgbmode.equals("auto"))
        {
            if(r<256 && g<256 && b<256 && a<256)
            {
                fgColor[0] = r/255;
                fgColor[1] = g/255;
                fgColor[2] = b/255;
                fgColor[3] = a/255;
            }
            else if(r>255 || g>255 || b>255 || a>255)
            {
                fgColor[0] = r/35565;
                fgColor[1] = g/35565;
                fgColor[2] = b/35565;
                fgColor[3] = a/35565;
            }
        }
        else if(rgbmode.equals("48"))
        {
            fgColor[0] = r/35565;
            fgColor[1] = g/35565;
            fgColor[2] = b/35565;
            fgColor[3] = a/35565;
        }
        else if(rgbmode.equals("24"))
        {
            fgColor[0] = r/255;
            fgColor[1] = g/255;
            fgColor[2] = b/255;
            fgColor[3] = a/255;
        }
        else if(rgbmode.equals("normalized")||rgbmode.equals("normal")||rgbmode.equals("norm"))
        {
            fgColor[0] = r;
            fgColor[1] = g;
            fgColor[2] = b;
            fgColor[3] = a;
        }
        updateContext();
    }      
    
    /**
     * Draws a complete filled ellipse within the given rectangle.
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void fillArc(int x, int y, int w, int h)
    {
        drawArc(x, y, w, h);
        waitList.peek().filled = true;
    }
    
    public void fillArc(Unicon2DContext context, int x, int y, int w, int h)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        fillArc(x, y, w, h);
        setContext(original);
    }
    
    /**
     * Draws a filled circular curve with center at (x,y), a radius r, a starting angle 
     *  theta, and an ending angle alpha.
     * @param x
     * @param y
     * @param r
     * @param alpha
     * @param theta
     */
    public void fillCircle(int x, int y, int r, int alpha, int theta)
    {
        drawCircle(x, y, r, alpha, theta);
            //set the filled value of the circle we just added to true
        waitList.peek().filled = true;
    }
    
    public void fillCircle(Unicon2DContext context, int x, int y, int r, int alpha, int theta)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        fillCircle(x, y, r, alpha, theta);
        setContext(original);
    }
    
    /**
     * Draws a filled polygon specified by an array of vertices.
     * Vertices must be given in such a manner that neighboring vertices in the polygon
     * are adjacent in the array. 
     * @param vertices
     */
    public void fillPolygon(float... vertices)
    {
        RenderedEntity polygon = new RenderedEntity("polygon", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        polygon.vertices = vertices;
        polygon.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        polygon.filled = true;
        polygon.clipped = clipping;
        polygon.clipBounds = clipBounds;
        
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        pix.fill();
                
        Texture textureSolid = new Texture(pix);
        TextureRegion textureregion = new TextureRegion(textureSolid);
        FloatArray verts = new FloatArray(polygon.vertices);
                
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        ShortArray triangleIndices = triangulator.computeTriangles(verts);
                
        PolygonRegion polyReg = new PolygonRegion(textureregion, verts.toArray(), triangleIndices.toArray());
        polygon.polySprite = new PolygonSprite(polyReg);
        
        entityList.add(polygon);
        pix.dispose();
        //textureSolid.dispose();
    }
    
    public void fillPolygon(Unicon2DContext context, float... vertices)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        fillPolygon(vertices);
        setContext(original);
    }
    
    /**
     * Draws a filled rectangle specified by top left corner (x,y), 
     * width w, and height h. 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void fillRectangle(int x, int y, int w, int h)
    {
        drawRectangle(x, y, w, h);
                //set the filled value of the circle we just added to true
        waitList.peek().filled = true;
      
    }
    
    public void fillRectangle(Unicon2DContext context, int x, int y, int w, int h)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        fillRectangle(x, y, w, h);
        setContext(original);
    }
    
    /**
     * Sets a flag to force flushing upon reaching the top of render loop.
     * Note: This function should yield no real effect in program. May be rewritten
     * to be a true no-op.
     */
    public void Flush()
    {
        flush = true;
    }
        
     /**
     * Returns the current font in use as a string containing the name of that font.
     * Note: fonts may change
     * @return 
     */
    public String Font()
    {
        switch(fontFlag)
        {
            case 1: return "serif";
            case 2: return "sans";
            case 3: return "typewriter";
            case 4: return "mono";
        }
        return "No Font Found";
    }
    
    /**
     * Changes the current font being rendered in the window. 
     * Will return true for success, false for failure.
     * @param fontParams 
     * @return  
     */
    public boolean Font(String fontParams)            
    {
        String[] paramList = fontParams.split(",");
        String fontName;
        boolean fontBold = false, 
                fontItalic = false;
        
        String paramOne = paramList[0].toLowerCase().trim();
        if(paramOne.equals("serif"))
        {
            fontName = "serif";
            fontFlag = 1;
        }
        else if(paramOne.equals("sans"))
        {
            fontName = "sans";
            fontFlag = 2;
        }
        else if(paramOne.equals("typewriter"))
        {
            fontName = "typewriter"; 
            fontFlag = 3;
        }
        else if(paramOne.equals("mono"))
        {
            fontName = "mono";
            fontFlag = 4;
        }
        else return false;
        
        if(paramList.length == 1)
        {
            setFont(fontName, 12, fontBold, fontItalic);
        }
        else if(paramList.length == 2)
        {
            String paramTwo = paramList[1].toLowerCase().trim();
            if(0 < Integer.valueOf(paramTwo) && Integer.valueOf(paramTwo) < 200)
                setFont(fontName, Integer.valueOf(paramTwo), false, false);
            else return false;
        }
        else if(paramList.length == 3)
        {
            String paramTwo = paramList[1].toLowerCase().trim();
            String paramThree = paramList[2].toLowerCase().trim();
            if(paramTwo.equals("bold"))
            {
                fontBold = true;
            }
            else if(paramTwo.equals("italic"))
            {
                fontItalic = true;
            }
            if(0 < Integer.valueOf(paramThree) && Integer.valueOf(paramThree) < 200)
                setFont(fontName, Integer.valueOf(paramThree), fontBold, fontItalic);
            else return false;
        }
        else if(paramList.length == 4)
        {
            String paramTwo = paramList[1].toLowerCase().trim();
            String paramThree = paramList[2].toLowerCase().trim();
            String paramFour = paramList[3].toLowerCase().trim();
            if(paramTwo.equals("italic")||paramThree.equals("italic"))
                fontItalic = true;
            if(paramTwo.equals("bold")||paramThree.equals("bold"))
                fontBold = true;
            if(0 < Integer.valueOf(paramFour) && Integer.valueOf(paramFour) < 200)
                setFont(fontName,Integer.valueOf(paramFour), fontBold, fontItalic);
            else return false;
        }
        else if(paramList.length > 4)
            return false;
        return true;
    }
    
    
        
     /**
     * Returns the current height of the window, in pixels.
     * @return 
     */
    private int getHeight()
    {
        return Gdx.graphics.getHeight();
    }
    
    
    /**
     * Returns the current width of the window, in pixels.
     * @return 
     */
    private int getWidth()
    {
        return Gdx.graphics.getWidth();
    }
    
    /**
     * Sets the text output cursor to given row, column position.
     * @param r
     * @param c
     */
    public void GotoRC(int r, int c)
    {
        int x = r * (int) fwidth + dx;
        int y = (int) (Gdx.graphics.getHeight() - c * (int) fheight - dy - fheight*0.2);
        x -= (float) Gdx.graphics.getWidth()/2;
        y -= (float) Gdx.graphics.getHeight()/2;
        cursorPos.x = x;
        cursorPos.y = y;
    }
    
    /**
     * Sets the text output cursor to given x, y position.
     * @param x
     * @param y
     */
    public void GotoXY(int x, int y)
    {
        x = x + dx;
        y = Gdx.graphics.getHeight() - y - dy;
        x -= (float) Gdx.graphics.getWidth()/2;
        y -= (float) Gdx.graphics.getHeight()/2;
        cursorPos.x = x;
        cursorPos.y = y;
    }
    
    @Override
    public boolean keyDown (int keycode) 
    {
        //out.println("KeyDown called: " + String.valueOf(keycode));
        Enqueue(new UniconEvent(), 0, 0, keycode, TimeUtils.nanoTime());
        return true;
    }
    
    public void LeftString(int x, int y, String string)
    {
        layout.setText(font, string);
        RenderedEntity text = new RenderedEntity("text", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        text.text = string;
        text.font = font;
        text.font.setColor(new Color(fgColor[0], fgColor[1], fgColor[2], fgColor[3]));
        text.x = x;
        text.y = (int)(y-font.getLineHeight()/2);
        text.clipped = clipping;
        text.clipBounds = clipBounds;
        text.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        
        RenderedEntity textBg = new RenderedEntity("textbg", bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
        textBg.x = x;
        textBg.y = (int)(y-font.getLineHeight()/2);
        textBg.w = (int) layout.width;
        textBg.h = (int) font.getLineHeight();
        textBg.clipped = clipping;
        textBg.clipBounds = clipBounds;
        textBg.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        
        waitList.add(textBg);
        waitList.add(text);
    }
    
    public void LeftString(Unicon2DContext context, int x, int y, String string)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        LeftString(x, y, string);
        setContext(original);
    }
    
    /**
     * Sets the name of the window to specified string.
     * @param name 
     */
    private void rename(String name)
    {
        windowLabel = name;
        Gdx.graphics.setTitle(windowLabel);
    }
    
    public int parsePattern(String s, int len, int width, int nbits, int bits)
    {
        return 0;
    }
    
    /**
     * Returns the entire event queue.
     * @return
     */
    public Queue Pending()
    {
        return eventQueue;
    }
    
    @Override
    public void render(float delta) 
    {        
        boolean scissorsPushed = false;
        for (RenderedEntity e : waitList)
        {
            entityList.add(e);
        }
        waitList.clear();
        
        
        //Sets the glviewport upon resizing to prevent distortion, needed for lwjgl3
        glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        polyBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        Gdx.gl.glClearColor(bgInit[0], bgInit[1], bgInit[2], bgInit[3]);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if(flush)
        {
            batch.begin();
            batch.flush();
            batch.end();
            shapeRenderer.begin();
            shapeRenderer.flush();
            shapeRenderer.end();
            polyBatch.begin();
            polyBatch.flush();
            polyBatch.end();
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(Unicon2DCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
            flush = false;
            delayTime = 0;
        }
        
        
        
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        
        for(RenderedEntity e : entityList)
        {
            // Draw arcs
            if(e.entityType.equals("arc"))
            {
                if(e.filled) shapeRenderer.set(ShapeType.Filled);
                else shapeRenderer.set(ShapeType.Line);
                
                shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                shapeRenderer.ellipse(e.x, e.y, e.w, e.h);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                    
                }
            }
           
            // Draw copied areas
            if(e.entityType.equals("area"))
            {
                shapeRenderer.end();
                batch.begin(); 
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                if(!e.initialized)
                {
                    Image screenShot = new Image(ScreenUtils.getFrameBufferTexture(e.x2, e.y2, e.w, e.h));
                    screenShot.setX(e.x);
                    screenShot.setY(e.y);
                    e.image = screenShot;
                    e.initialized = true;
                }
                e.image.draw(batch, 1);
                if(e.clipped)
                {
                    batch.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                }
                batch.end();
                shapeRenderer.begin();
            }
            
            // Send copied areas to new window
            
           if(e.entityType.equals("extarea"))
            {
                if(!e.initialized)
                {
                    System.out.println("Intializing and sending extarea.");
                    Image screenShot = new Image(ScreenUtils.getFrameBufferTexture(e.x2, e.y2, e.w, e.h));
                    screenShot.setX(e.x);
                    screenShot.setY(e.y);
                    e.image = screenShot;
                    e.initialized = true;
                    e.window.addEntity(e);
                }
            }
            
            // Draw circles/circular curves
            if(e.entityType.equals("circle"))
            {
                if(e.filled) shapeRenderer.set(ShapeType.Filled);
                else shapeRenderer.set(ShapeType.Line);
                
                shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                if(e.clipped)
                {
                    //System.out.println("Clipping a circle.");
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                if(scissorsPushed)
                    //System.out.println("Scissorstack is not empty.");
                //System.out.println("Drawing a circle.");
                shapeRenderer.circle(e.x, e.y, e.r);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;   
                }
            }
            
            // Draw erased areas
            if(e.entityType.equals("erase"))
            {
                shapeRenderer.set(ShapeType.Filled);
                shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                shapeRenderer.rect(e.x, e.y, e.w, e.h);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                    
                }
            }
            
            // Draw images
            if(e.entityType.equals("image"))
            {
                shapeRenderer.end();
                batch.begin(); 
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                e.image.draw(batch, 1);
                if(e.clipped)
                {
                    batch.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                }
                batch.end();
                shapeRenderer.begin();
            }
            
            // Draw lines
            if(e.entityType.equals("line"))
            {
                shapeRenderer.set(ShapeType.Line);
                shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                shapeRenderer.line(e.x, e.y, e.x2, e.y2);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false; 
                }
            }
            
            // Draw points
            if(e.entityType.equals("point"))
            {
                shapeRenderer.set(ShapeType.Point);
                shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                shapeRenderer.point(e.x, e.y, 0);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                }
            }
            
            // Draw polygons
            if(e.entityType.equals("polygon"))
            {
                if(e.filled)
                {
                    shapeRenderer.end();
                    polyBatch.begin();
                    if(e.clipped)
                    {
                        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                        scissorsPushed = ScissorStack.pushScissors(scissor);
                    }
                    e.polySprite.draw(polyBatch);
                    if(e.clipped)
                    {
                        polyBatch.flush();
                        if(scissorsPushed)
                            ScissorStack.popScissors();
                        scissorsPushed = false;
                    
                    }
                    
                    polyBatch.end();
                    shapeRenderer.begin();
                }
                else
                {
                    shapeRenderer.set(ShapeType.Line);
                    shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                    
                    if(e.clipped)
                    {
                        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                        scissorsPushed = ScissorStack.pushScissors(scissor);
                    }
                    shapeRenderer.polygon(e.vertices);
                    if(e.clipped)
                    {
                        shapeRenderer.flush();
                        if(scissorsPushed)
                            ScissorStack.popScissors();
                        scissorsPushed = false;
                    }
                }
            }
            
            // Draw rectangles
            if(e.entityType.equals("rectangle"))
            {
                
                if(e.filled) shapeRenderer.set(ShapeType.Filled);
                else shapeRenderer.set(ShapeType.Line);
                
                shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e. clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                shapeRenderer.rect(e.x, e.y, e.w, e.h);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                }
            }
            
            // Draw text
            if(e.entityType.equals("text"))
            {
                if(!e.initialized)
                {
                    switch (e.fontType)
                    {
                        case 1:  e.font = serifGen.generateFont(fontParam); break;
                        case 2:  e.font = serifBGen.generateFont(fontParam); break;
                        case 3:  e.font = serifIGen.generateFont(fontParam); break;
                        case 4:  e.font = serifBIGen.generateFont(fontParam); break;
                        case 5:  e.font = sansGen.generateFont(fontParam); break;
                        case 6:  e.font = sansBGen.generateFont(fontParam); break;
                        case 7:  e.font = sansIGen.generateFont(fontParam); break;
                        case 8:  e.font = sansBIGen.generateFont(fontParam); break;
                        case 9:  e.font = typewGen.generateFont(fontParam); break;
                        case 10: e.font = typewBGen.generateFont(fontParam); break;
                        case 11: e.font = typewIGen.generateFont(fontParam); break;
                        case 12: e.font = typewBIGen.generateFont(fontParam); break;
                        case 13: e.font = monoGen.generateFont(fontParam); break;
                        case 14: e.font = monoBGen.generateFont(fontParam); break;
                        case 15: e.font = monoIGen.generateFont(fontParam); break;
                        case 16: e.font = monoBIGen.generateFont(fontParam); break;
                    }
                    e.font.setColor(new Color(e.color[0], e.color[1], e.color[2], e.color[3]));
                    layout.setText(e.font, e.text);
                    e.textBg.w = (int) layout.width;
                    e.textBg.h = (int) e.font.getLineHeight();
                    fheight = e.font.getLineHeight();
                    e.initialized = true;
                }
                if(scrollText)
                {
                    e.y += fheight;
                    e.textBg.y += fheight;
                }
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                shapeRenderer.set(ShapeType.Filled);
                shapeRenderer.setColor(e.textBg.color[0], e.textBg.color[1], e.textBg.color[2], e.textBg.color[3]);
                shapeRenderer.rect(e.textBg.x, e.textBg.y, e.textBg.w, e.textBg.h);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                }
                shapeRenderer.end();
                batch.begin();
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                e.font.draw(batch, e.text, e.x, e.y);
                if(e.clipped)
                {
                    batch.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                }
                batch.end();
                shapeRenderer.begin();
            }
            /*
            // Draw text bg
            if(e.entityType.equals("textbg"))
            {
                if(!e.initialized)
                {
                    
                }
                shapeRenderer.set(ShapeType.Filled);
                shapeRenderer.setColor(e.color[0], e.color[1], e.color[2], e.color[3]);
                if(scrollText)
                    e.y += fheight;
                if(e.clipped)
                {
                    ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), e.clipBounds, scissor);
                    scissorsPushed = ScissorStack.pushScissors(scissor);
                }
                shapeRenderer.rect(e.x, e.y, e.w, e.h);
                if(e.clipped)
                {
                    shapeRenderer.flush();
                    if(scissorsPushed)
                        ScissorStack.popScissors();
                    scissorsPushed = false;
                    
                }
            }*/
        }
        
        scrollText = false;
        shapeRenderer.end();
        
    }
    
    @Override
    public void resize(int newWidth, int newHeight) 
    {    
        glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        height = Gdx.graphics.getHeight();
        width = Gdx.graphics.getWidth();
        size = String.valueOf(width) + ", " + String.valueOf(height);
    }
    
    
     /**
     * Helper function for colorPhrase().
     * @param n1
     * @param n2
     * @param hue
     * @return 
     */
    private static float rgbval(float n1, float n2, float hue)
    {
        if (hue > 360)
            hue -= 360;
        else if (hue < 0)
            hue += 360;

        if (hue < 60)
            return n1 + (n2 - n1) * hue / 60.0f;
        else if (hue < 180)
            return n2;
        else if (hue < 240)
            return n1 + (n2 - n1) * (240 - hue) / 60.0f;
        else
            return n1;
   }
    
    public void setContext(Unicon2DContext context)
    {
        coupledContext = context;
        fgColor = context.fgColor;
        bgColor = context.bgColor;
        fgString = context.fgString;
        bgString = context.bgString;
        rgbmode = context.rgbmode;
        reverse = context.reverse;
        drawop = context.drawop;
        gamma = context.gamma;
        font = context.font;
        fheight = context.fheight;
        fwidth = context.fwidth;
        ascent = context.ascent;
        descent = context.descent;
        leading = context.leading;
        linewidth = context.linewidth;
        linestyle = context.linestyle;
        fillstyle = context.fillstyle;
        pattern = context.pattern;
        clipx = context.clipx;
        clipy = context.clipy;
        cliph = context.cliph;
        clipw = context.clipw;
        dx = context.dx;
        dy = context.dy;
        setReverse(String.valueOf(reverse));
        
    }
    
    /** Changes the font, size, and attributes of drawn text.
     *  
     * @param fontSize 
     */
    private void setFont(String fontType, int fontSize, boolean bold, boolean italic)
    {
        currfType = fontType;
        currfSize = fontSize;
        currfBold = bold;
        currfItalic = italic;
        
        fontParam.size = fontSize;
        fontParam.color = new Color(fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        if(fontType.equals("serif"))
        {   
            if(bold && italic)
                fRendFlag = 4;
                //font = serifBIGen.generateFont(fontParam);
            else if(bold && !italic)
                fRendFlag = 2;
                //font = serifBGen.generateFont(fontParam);
            else if(italic && !bold)
                fRendFlag = 3;
                //font = serifIGen.generateFont(fontParam);
            else
                fRendFlag = 1;
                //font = serifGen.generateFont(fontParam);   
        }
        if(fontType.equals("sans"))
        {
            if(bold && italic)
                fRendFlag = 8;
                //font = sansBIGen.generateFont(fontParam);
            else if(bold && !italic)
                fRendFlag = 6;
                //font = sansBGen.generateFont(fontParam);
            else if(italic && !bold)
                fRendFlag = 7;
                //font = sansBIGen.generateFont(fontParam);
            else
                fRendFlag = 5;
                //font = sansGen.generateFont(fontParam);     
        }
        if(fontType.equals("typewriter"))
        {
            if(bold && italic)
                fRendFlag = 12;
                //font = typewBIGen.generateFont(fontParam);
            else if(bold && !italic)
                fRendFlag = 10;
                //font = typewBGen.generateFont(fontParam);
            else if(italic && !bold)
                fRendFlag = 11;
                //font = typewBIGen.generateFont(fontParam);
            else
                fRendFlag = 9;
                //font = typewGen.generateFont(fontParam);     
        }
        if(fontType.equals("mono"))
        {
            if(bold && italic)
                fRendFlag = 15;
                //font = monoBIGen.generateFont(fontParam);
            else if(bold && !italic)
                fRendFlag = 14;
                //font = monoBGen.generateFont(fontParam);
            else if(italic && !bold)
                fRendFlag = 14;
                //font = monoBIGen.generateFont(fontParam);
            else
                fRendFlag = 13;
                //font = monoGen.generateFont(fontParam);     
        }
        layout.setText(font, "W");
        fheight = font.getLineHeight();
        fwidth  = layout.width;
        ascent = font.getAscent();
        descent = font.getDescent();
        leading = font.getLineHeight();
        updateContext();                
    }
    
    
    /**
     * Sets a new height (in pixels) for the opened window.
     * @param newHeight 
     */
    private void setHeight(int newHeight)
    {
        boolean shiftCurs;
        //if no text has been printed yet, shift cursor to top left corner
        shiftCurs = cursorPos.y == Gdx.graphics.getHeight()/2;
        
        Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), newHeight);
        //fixes opengl viewport, thie line is necessary when using lwjgl3
        glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(shiftCurs)
            cursorPos.y = Gdx.graphics.getHeight()/2;
        height = newHeight;
        size = String.valueOf(width) + ", " + String.valueOf(height);
    }
    
    public void setIcon(String file)
    {
        //this.addIcon()
    }
    
    
    /**
     * Reverses Fg and Bg colors given a change in reverse setting.
     * Returns the resultant state of reverse given an argument "on" or "off".
     * Will return false given unrecognized argument.
     * @param value
     * @return 
     */
    private boolean setReverse(String value)
    {
        float temp[] = new float[4];
        if(reverse)
        {
            if(value.equals("on"))
                return true;
            else if(value.equals("off"))
            {
                for(int i = 0; i<4; i++)
                {
                    temp[i] = fgColor[i];
                    fgColor[i] = bgColor[i];
                    bgColor[i] = temp[i];
                    setFont(currfType, currfSize, currfBold, currfItalic);
                }
                return false;
            }
            else return false;
        }
        else if(!reverse)
        {
            if(value.equals("off"))
                return false; 
            else if(value.equals("on"))
            {
                for(int i = 0; i<4; i++)
                {
                    temp[i] = fgColor[i];
                    fgColor[i] = bgColor[i];
                    bgColor[i] = temp[i];
                    setFont(currfType, currfSize, currfBold, currfItalic);
                }
                return true;
            }
            else return false;
        }
        return false;
    }
    
    
    private void setRgbmode(String rgbsetting)
    {
        if(rgbsetting.equals("48"))
            rgbmode = rgbsetting;
        else if(rgbsetting.equals("24"))
            rgbmode = rgbsetting;
        else if(rgbsetting.equals("auto"))
            rgbmode = rgbsetting;
        else if(rgbsetting.equals("normalized")||rgbsetting.equals("normal")||rgbsetting.equals("norm"))
            rgbmode = rgbsetting;
        else ;  //typo or unrecognized value, do nothing
    }
    
    /**
     * Sets a new width (in pixels) for the opened window.
     * @param newWidth 
     */
    private void setWidth(int newWidth)
    {
        boolean shiftCurs;
        //if no text has been printed yet, shift cursor to top left corner
        shiftCurs = cursorPos.x == 0-Gdx.graphics.getWidth()/2;
        
        Gdx.graphics.setWindowedMode(newWidth, Gdx.graphics.getHeight());
        //fixes opengl viewport, thie line is necessary when using lwjgl3
        glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(shiftCurs)
            cursorPos.x = 0-Gdx.graphics.getWidth()/2;
        width = newWidth;
    }

    
    public int TextWidth(String string)
    {
        layout.setText(font, string);
        return (int) layout.width;
    }
    
    
    @Override
    public boolean touchDown (int x, int y, int pointer, int button) 
    {
        UniconEvent clickEvent = new UniconEvent();
        clickEvent.keycode = button;
        clickEvent.x = x;
        clickEvent.y = y;
        clickEvent.pointer = pointer;
        clickEvent.timeStamp = TimeUtils.nanoTime();
        
        
        eventQueue.addLast(clickEvent);
        return true;
    }
    
    private void updateContext()
    {
        coupledContext.fgColor = fgColor;
        coupledContext.bgColor = bgColor;
        coupledContext.fgString = fgString;
        coupledContext.bgString = bgString;
        coupledContext.rgbmode = rgbmode;
        coupledContext.reverse = reverse;
        coupledContext.drawop = drawop;
        coupledContext.gamma = gamma;
        coupledContext.font = font;
        coupledContext.fheight = fheight;
        coupledContext.fwidth = fwidth;
        coupledContext.ascent = ascent;
        coupledContext.descent = descent;
        coupledContext.leading = leading;
        coupledContext.linewidth = linewidth;
        coupledContext.linestyle = linestyle;
        coupledContext.fillstyle = fillstyle;
        coupledContext.pattern = pattern;
        coupledContext.clipx = clipx;
        coupledContext.clipy = clipy;
        coupledContext.cliph = cliph;
        coupledContext.clipw = clipw;
        coupledContext.dx = dx;
        coupledContext.dy = dy;
        
    }
    
    /**
     * Sets a window attribute to a given value.
     * Takes input of form "attrib=value"
     * @param attribute
     * @return
     */
    public String WAttrib(String attribute)
    {
        String[] attribAr;
        String attrib, newVal;
        
        
        if(attribute.contains("="))
        {
            attribAr = attribute.split("=");
            attrib = attribAr[0].trim().toLowerCase();
            newVal = attribAr[1].trim().toLowerCase();
            
            if(attrib.equals("label"))
                rename(newVal);
            else if(attrib.equals("fg"))
                Fg(newVal);
            else if(attrib.equals("bg"))
                Bg(newVal);
            else if(attrib.equals("height"))
                setHeight(Integer.valueOf(newVal));
            else if(attrib.equals("width"))
                setWidth(Integer.valueOf(newVal));
            else if(attrib.equals("font"))
                Font(newVal);
            else if(attrib.equals("dx")){
                dx = Integer.valueOf(newVal);
                cursorPos.x += dx;
            }
            else if(attrib.equals("dy")){
                dy = Integer.valueOf(newVal);
                cursorPos.y -= dy + font.getLineHeight()*0.2;
            }
            else if(attrib.equals("reverse"))
                reverse = setReverse(newVal);
            else if(attrib.equals("lines"))
                setHeight(Integer.valueOf(newVal)*(int)fheight);
            else if(attrib.equals("cols"))
                setWidth(Integer.valueOf(newVal)*(int)fwidth);
            else if(attrib.equals("iconimage"))
                setIcon(newVal);
            else if(attrib.equals("clipx"))
                clipBounds.x = Integer.valueOf(newVal);
            else if(attrib.equals("clipy"))
                clipBounds.y = Integer.valueOf(newVal);
            else if(attrib.equals("clipw"))
                clipBounds.width = Integer.valueOf(newVal);
            else if(attrib.equals("cliph"))
                clipBounds.height = Integer.valueOf(newVal);
            else if(attrib.equals("rgbmode"))
                setRgbmode(newVal);
            else if(attrib.equals("size"))
            {
                String wh[] = newVal.split(",");
                if(Integer.valueOf(wh[0].trim()) < 10000 &&
                   Integer.valueOf(wh[0].trim()) > 0 &&
                   Integer.valueOf(wh[1].trim()) < 10000 &&
                   Integer.valueOf(wh[1].trim()) > 0)
                {
                    setWidth(Integer.valueOf(wh[0].trim()));
                    setHeight(Integer.valueOf(wh[1].trim()));
                }
            }
            else ;//System.out.println("Error:" + attrib + "is not a recognized attribute"); 
            updateContext();
        }
        else
            attrib = attribute.trim().toLowerCase();
            
        if(attrib.equals("label"))
            return windowLabel;
        else if(attrib.equals("fg"))
            return Fg();
        else if(attrib.equals("bg"))
            return Bg();
        else if(attrib.equals("height"))
            return String.valueOf(getHeight());
        else if(attrib.equals("width"))
            return String.valueOf(getWidth());
        else if(attrib.equals("font"))
            return Font();
        else if(attrib.equals("fheight"))
            return String.valueOf(font.getLineHeight());
         else if(attrib.equals("fwidth"))
             return String.valueOf(fwidth);
        else if(attrib.equals("ascent"))
            return String.valueOf(font.getAscent());
        else if(attrib.equals("dx"))
            return String.valueOf(dx);
        else if(attrib.equals("dy"))
            return String.valueOf(dy); 
        else if(attrib.equals("lines"))
            return String.valueOf(Gdx.graphics.getHeight()/fheight);
        else if(attrib.equals("cols"))
            return String.valueOf(Gdx.graphics.getWidth()/fwidth);
        else if(attrib.equals("clipx"))
            return String.valueOf(clipBounds.x);
        else if(attrib.equals("clipy"))
            return String.valueOf(clipBounds.y);
        else if(attrib.equals("clipw"))
            return String.valueOf(clipBounds.width);
        else if(attrib.equals("cliph"))
            return String.valueOf(clipBounds.height);
        else if(attrib.equals("rgbmode"))
            return rgbmode;
        else if(attrib.equals("size"))
            return size;
        else if(attrib.equals("display"))
            return currentMonitor.name;
        else if(attrib.equals("displayheight"))
            return String.valueOf(currentMonitor.virtualY);
        else if(attrib.equals("displaywidth"))
            return String.valueOf(currentMonitor.virtualX);

        
        
        else return "failed";
    }
    
    /**
     * Sets window attributes to a given values.
     * Takes input of form "attrib=value"
     * @param attributes
     */
    public void WAttrib(String... attributes)
    {
        for(String attrib: attributes)
        {
            WAttrib(attrib);                        //not recursion
        }
    }
    
    /**
     * Forces a flush (which is effectively a no-op) and delays application for
     * given milliseconds.
     * @param ms
     */
    public void WDelay(int ms)
    {
        flush = true;
        delayTime = ms;
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(Unicon2DCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Waits for a q or Q, then exits the program.
     */
    public void WDone()
    {
        while(true)
            for(UniconEvent e : eventQueue)
            {
                if(e.keycode == Q)
                {
                    System.exit(0);
                    //Gdx.app.exit();
                }
            }
    }
    
    /**
     * Takes in input.
     * Note: Unfinished
     */
    public void WRead()
    {
        takeInput = true;
    }
    
    /**
     * Outputs text to the screen.
     * @param string 
     */
    public void write(String string)
    {
        RenderedEntity text = new RenderedEntity("text", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        text.text = string;
        text.fontType = fRendFlag;
        text.x = (int) cursorPos.x;
        text.clipped = clipping;
        text.clipBounds = clipBounds;
        text.initialized = false;
        
        RenderedEntity textBg = new RenderedEntity("textbg", bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
        textBg.x = (int) cursorPos.x;
        
        if(cursorPos.y > -Gdx.graphics.getHeight()/2)
        {
            text.y = (int) cursorPos.y;
            textBg.y = (int) (cursorPos.y - font.getLineHeight()*0.9);
            cursorPos.y -= font.getLineHeight();
            cursorPos.x = -Gdx.graphics.getWidth()/2;
        }
        else
        {
            fheight = font.getLineHeight();                     //font may not be updated correctly here, could cause issues
            cursorPos.y = -Gdx.graphics.getHeight()/2;
            text.y = (int) cursorPos.y;
            textBg.y = (int) (cursorPos.y - font.getLineHeight()*0.8);
            scrollText = true;
        }
        
        text.textBg = textBg;
        waitList.add(text);
    }
    
    public void write(Unicon2DContext context, String string)
    {
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        write(string);
        setContext(original);
    }
    
    /**
     * Draws given image file to location x, y.
     * Clips rendering by w, h.
     * @param file
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public boolean ReadImage(String file, int x, int y, int w, int h)
    {
        RenderedEntity drawnImage = new RenderedEntity("image", fgColor[0], fgColor[1], fgColor[2], fgColor[3]);
        drawnImage.x = x;
        drawnImage.y = y;
        drawnImage.clipped = clipping;
        drawnImage.clipBounds = clipBounds;
        drawnImage.fixOrigin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), dx, dy);
        
        
        Texture texture = new Texture(Gdx.files.internal(file));
        Image img = new Image(texture);
        img.setX(drawnImage.x);
        img.setY(drawnImage.y-h);
        img.setSize(w, h);
        img.setScaling(Scaling.stretch);
        drawnImage.image = img;
        waitList.add(drawnImage);
        
        return true;     
    }
    
    public boolean ReadImage(Unicon2DContext context, String file, int x, int y, int w, int h)
    {   
        Unicon2DContext original = coupledContext.Clone();
        setContext(context);
        boolean success = ReadImage(file, x, y, w, h);
        setContext(original);
        return success;
    }

    /**
     * If a q or Q is found in the event queue, the program will close.
     * @return
     */
    public boolean WQuit()
    {
        UniconEvent quit;
        for(int i = 0; i < eventQueue.size; i++)
        {
            quit = eventQueue.removeFirst();
            //System.out.println("Event found, event keycode: " + String.valueOf(quit.keycode));
            if(quit.keycode == Q)
            {
                Gdx.app.exit();
            }
        }
        return false;
    }
            
    
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }
    
}
