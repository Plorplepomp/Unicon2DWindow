/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unicon.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;

/**
 *
 * @author Scott
 */
public class Unicon2DContext 
{
    public float       fgColor[], 
                       bgColor[];
    public String      fgString,
                       bgString,
                       rgbmode;
    public boolean     reverse, 
                       drawop;
    public float       gamma;
    
    public BitmapFont  font;
    public float       fheight,fwidth,    
                       ascent, descent,
                       leading;
    
    public float         linewidth;
    public String      linestyle,
                      fillstyle,
                      pattern;
    
    public float       clipx, clipy,
                       clipw, cliph;
    int                 dx, dy;
    
    // Other declarations
    String currfType;
    int currfSize;
    boolean currfBold, currfItalic;
    GlyphLayout layout;
    
    int fontFlag;
    
    Rectangle scissor, clipBounds;
    boolean clipping;
    
    
    FreeTypeFontGenerator serifGen, serifBGen,  serifIGen,  serifBIGen,
                          sansGen,  sansBGen,   sansIGen,   sansBIGen,
                          typewGen, typewBGen,  typewIGen,  typewBIGen,
                          monoGen,  monoBGen,   monoIGen,   monoBIGen;
    FreeTypeFontParameter fontParam;
    
    
    public Unicon2DContext(String...attributes)
    {
        
        fgColor = new float[]{0,0,0,1};
        fgString = "black";
        
        bgColor = new float[]{1,1,1,1};
        bgString = "white";
        
        rgbmode = "auto";
        
        reverse = false;
        drawop = false;
        
        gamma = 1;
        
        typewGen   = new FreeTypeFontGenerator(Gdx.files.internal("CourierCode/CourierCode-Roman.ttf"));
        fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = 16;
        fontParam.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789`~!@#$%^&*()_+-=[]\\;',./{}|:\">?";
        //font = typewGen.generateFont(fontParam);
        currfType = "serif";
        currfSize = fontParam.size;
        currfBold = currfItalic = false;
        
        //fheight = font.getLineHeight();
        
        layout = new GlyphLayout();
        //layout.setText(font, "W");
        //fwidth = layout.width;
        
        //ascent = font.getAscent();
        
        //descent = font.getDescent();
        
        //leading = font.getLineHeight();
        
        linewidth = 1;
        
        linestyle = "solid";
        
        fillstyle = "solid";
        
        pattern = "black";
        
        clipx = clipy = 0;
        
        cliph = clipw = 0;
        
        dx = dy = 0;
        
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
        
        for (String s: attributes)
        {
            setAttrib(s);
        }
        
    }
    
    public Unicon2DContext Clone(String...attributes)
    {
        Unicon2DContext copy = new Unicon2DContext();
        copy.fgColor = fgColor;
        copy.fgString = fgString;
        copy.bgColor = bgColor;
        copy.bgString = bgString;
        copy.rgbmode = rgbmode;
        copy.reverse = reverse;
        copy.drawop = drawop;
        copy.gamma = gamma;
        copy.font = font;
        copy.fheight = fheight;
        copy.fwidth = fwidth;
        copy.ascent = ascent;
        copy.descent = descent;
        copy.leading = leading;
        copy.linewidth = linewidth;
        copy.linestyle = linestyle;
        copy.fillstyle = fillstyle;
        copy.pattern = pattern;
        copy.clipx = clipx;
        copy.clipy = clipy;
        copy.cliph = cliph;
        copy.clipw = clipw;
        copy.dx = dx;
        copy.dy = dy;
        
        for (String s: attributes)
        {
            copy.setAttrib(s);
        }
        
        return copy;
    }
    
    private void setAttrib(String attribute)
    {
        String[] attribAr;
        String attrib, newVal;

        attribAr = attribute.split("=");
        attrib = attribAr[0].trim().toLowerCase();
        newVal = attribAr[1].trim().toLowerCase();
            
        if(attrib.equals("fg"))
            fgColor = Unicon2DCanvas.colorPhrase(newVal);
        else if(attrib.equals("bg"))
            bgColor = Unicon2DCanvas.colorPhrase(newVal);
        //else if((attrib.equals("font")))
       //     Font(newVal);
        else if((attrib.equals("dx")))
            dx = Integer.valueOf(newVal);
        else if((attrib.equals("dy")))
            dy = Integer.valueOf(newVal);
        else if((attrib.equals("reverse")))
            setReverse(newVal);
        else if((attrib.equals("clipx")))
            clipx = Integer.valueOf(newVal);
        else if((attrib.equals("clipy")))
            clipy = Integer.valueOf(newVal);
        else if((attrib.equals("clipw")))
            clipw = Integer.valueOf(newVal);
        else if((attrib.equals("cliph")))
            cliph = Integer.valueOf(newVal);
        else if((attrib.equals("rgbmode")))
            setRgbmode(newVal);
    }
    
    private boolean Font(String fontParams)            
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
                font = serifBIGen.generateFont(fontParam);
            else if(bold && !italic)
                font = serifBGen.generateFont(fontParam);
            else if(italic && !bold)
                font = serifBIGen.generateFont(fontParam);
            else    
                font = serifGen.generateFont(fontParam);   
        }
        if(fontType.equals("sans"))
        {
            if(bold && italic)
                font = sansBIGen.generateFont(fontParam);
            else if(bold && !italic)
                font = sansBGen.generateFont(fontParam);
            else if(italic && !bold)
                font = sansBIGen.generateFont(fontParam);
            else    
                font = sansGen.generateFont(fontParam);     
        }
        if(fontType.equals("typewriter"))
        {
            if(bold && italic)
                font = typewBIGen.generateFont(fontParam);
            else if(bold && !italic)
                font = typewBGen.generateFont(fontParam);
            else if(italic && !bold)
                font = typewBIGen.generateFont(fontParam);
            else    
                font = typewGen.generateFont(fontParam);     
        }
        if(fontType.equals("mono"))
        {
            if(bold && italic)
                font = monoBIGen.generateFont(fontParam);
            else if(bold && !italic)
                font = monoBGen.generateFont(fontParam);
            else if(italic && !bold)
                font = monoBIGen.generateFont(fontParam);
            else    
                font = monoGen.generateFont(fontParam);     
        }
        layout.setText(font, "W");
        fheight = font.getLineHeight();
        fwidth  = layout.width;       
    }
   
     
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
    
    public void dispose(){
    //font.dispose();
    }

}

