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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 *
 * @author Scott
 */
public class RenderedEntity 
{
    // General attributes
    float[] color;
    String entityType;
    int x, y;
    boolean filled;
    
    // Circle attributes
    int r, theta, alpha;
    
    // Line attributes
    int x2, y2;
    
    // Polygon attributes
    float[] vertices;
    PolygonSprite polySprite;
    
    // Rectangle attributes
    int h, w;
    
    // Text attributes
    int tHeight, fontSize;
    String text;
    BitmapFont font;
    int fontType;
    RenderedEntity textBg;
    
    //Image attributes
    Image image;
    
    // Clipping attributes
    boolean clipped;
    Rectangle clipBounds;
    
    // CopyArea attribtues
    TextureRegion textureRegion;
    Texture texture;
    Pixmap pixmap;
    boolean initialized;
    Unicon2DWindow window;
    
    
    public RenderedEntity(String type, float r, float g, float b, float a)
    {
        entityType = type;
        color = new float[4];
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
        x = 0;
        y = 0;
        vertices = new float[0];
        h = 0;
        w = 0;
        image = new Image();
        initialized = false;
        //font = new BitmapFont();
    }
    
    /**
     * Sets the origin for rendering entity to the bottom left corner of window
     *  from center of window
     * 
     * @param windowWidth
     * @param windowHeight
     * @param dx
     * @param dy
     */
    public void fixOrigin(int windowWidth, int windowHeight, int dx, int dy)
    {        
        y = windowHeight - y -  dy;           //
        y2 = windowHeight - y2 -  dy;        //Flip to top left origin from bottom left
        x = x + dx;
        x2 = x2 + dx;
        h = -h;                             
        
        
        x -= (float) windowWidth/2;
        y -= (float) windowHeight/2;
        x2 -= (float) windowWidth/2;
        y2 -= (float) windowHeight/2;
        
        for(int i = 0; i < vertices.length; i++)
        {
            if((i%2)==0)
            {
                vertices[i] = vertices[i]+dx;
                vertices[i] -= (float) windowWidth/2;
            }
            else
            {
                vertices[i] = windowHeight - vertices[i] - dy;
                vertices[i] -= (float) windowHeight/2;
            }
        }

    }
    
    /**
     * Sets the origin for rendering entities to the top left corner from the 
     *  bottom left corner
     * @param windowWidth
     * @param windowHeight 
     */
    public void OrigBotLtoTopL(int windowWidth, int windowHeight)
    {
        y = windowHeight - y;           //
        y2 = windowHeight - y2;         //Flip to top left origin from bottom left
        h = -h;                         //
    }
    
    public void dispose()
    {
        font.dispose();
    }
}
