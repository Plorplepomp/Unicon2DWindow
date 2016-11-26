/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unicon.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Queue;

/**
 *
 * @author Scott
 */
public class Unicon2DWindow implements Screen
{
    private boolean initialized;
    private Unicon2DContext context;
    private Unicon2DCanvas canvas;
    
    public Unicon2DWindow(String... attributes)
    {
        initialized = false;
        canvas = new Unicon2DCanvas();
        context = new Unicon2DContext();
        canvas.setContext(context);
        canvas.open(attributes);
        initialized = true;
    }
    
    // This constructor can be used for couple
    public Unicon2DWindow(Unicon2DCanvas coupleCanvas, Unicon2DContext coupleContext, String... attributes)
    {
        initialized = false;
        canvas = coupleCanvas;
        context = coupleContext;
        canvas.open(attributes);
        canvas.setContext(context);
        //super.setScreen(canvas);
        initialized = true;
    }  
    
    public boolean isInitialized()
    {
        System.out.println("isInitialized called");
        return initialized;
    }
    
    public void addEntity(RenderedEntity entity){
        canvas.addEntity(entity);
    }
    
    public void Alert(){
        canvas.Alert();
    }
    
    public String Bg(){
        return canvas.Bg();
    }
    
    public void Bg(String colorPhrase){
        canvas.Bg(colorPhrase);
    }
    
    public void Bg(float r, float g, float b, float a){
        canvas.Bg(r,g,b,a);
    }
    
    public Unicon2DContext Clone(String... attributes){
        return canvas.Clone(attributes);
    }

    public void CenterString(int x, int y, String string){
        canvas.CenterString(x,y,string);
    }
    
    public void CenterString(Unicon2DContext context, int x, int y, String string){
        canvas.CenterString(context, x, y, string);
    }
    
    public void Clip(int x, int y, int w, int h){
        canvas.Clip(x,y,w,h);
    }
    
    public void Clip(){
        canvas.Clip();
    }
    
    public String ColorValue(String colorPhrase){
        return canvas.ColorValue(colorPhrase);
    }

    public void CopyArea(int x1, int y1, int w, int h, int x2, int y2){
        canvas.CopyArea(x1, y1, w, h, x2, y2);
    }
    
    public void CopyArea(Unicon2DWindow window, int x1, int y1, int w, int h, int x2, int y2){
        canvas.CopyArea(window, x1, y1, w, h, x2, y2);
    }
    
    public void drawArc(int x, int y, int w, int h){
        canvas.drawArc(x, y, w, h);
    }

    public void drawArc(Unicon2DContext context, int x, int y, int w, int h){
        canvas.drawArc(context, x, y, w, h);
    }
    
    public void drawCircle(int x, int y, int r, int theta, int alpha){
        canvas.drawCircle(x, y, r, theta, alpha);
    }
    
    public void drawCircle(Unicon2DContext context, int x, int y, int r, int theta, int alpha){
        canvas.drawCircle(context, x, y, r, theta, alpha);
    }
    
    public void drawLine(int x1, int y1, int x2, int y2){
        canvas.drawLine(x1, y1, x2, y2);
    }
    
    public void drawLine(Unicon2DContext context, int x1, int y1, int x2, int y2){
        canvas.drawLine(context, x1, y1, x2, y2);
    }

    public void drawPoint(int x, int y){
        canvas.drawPoint(x, y);
    }
    
    public void drawPoint(Unicon2DContext context, int x, int y){
        canvas.drawPoint(context, x, y);
    }
    
    public void drawPolygon(float... vertices){
        canvas.drawPolygon(vertices);
    }
    
    public void drawPolygon(Unicon2DContext context, float... vertices){
        canvas.drawPolygon(context, vertices);
    }
    
    public void drawRectangle(int x, int y, int w, int h){
        canvas.drawRectangle(x, y, w, h);
    }
    
    public void drawRectangle(Unicon2DContext context, int x, int y, int w, int h){
        canvas.drawRectangle(context, x, y, w, h);
    }
    
    public void drawString(int x, int y, String string){
        canvas.drawString(x, y, string);
    }
    
    
    public void drawString(Unicon2DContext context, int x, int y, String string){
        canvas.drawString(context, x, y, string);
    }
    
    public void Enqueue(UniconEvent event, int x, int y, int keycode, long interval){
        canvas.Enqueue(event, x, y, keycode, interval);
    }
            
    public void EraseArea(int x, int y, int w, int h){
        canvas.EraseArea(x, y, w, h);
    }
    
    public void EraseArea(Unicon2DContext context, int x, int y, int w, int h){
        canvas.EraseArea(context, x, y, w, h);
    }
    
    public UniconEvent Event(){
        return canvas.Event();
    }
    
    public String Fg(){
        return canvas.Fg();
    }
    
    public void Fg(String colorPhrase){
        canvas.Fg(colorPhrase);
    }
    
    public void Fg(float r, float g, float b, float a){
        canvas.Fg(r, g, b, a);
    }
    
    public void fillArc(int x, int y, int w, int h){
        canvas.fillArc(x, y, w, h);
    }
    
    public void fillArc(Unicon2DContext context, int x, int y, int w, int h){
        canvas.fillArc(context, x, y, w, h);
    }
    
    public void fillCircle(int x, int y, int r, int alpha, int theta){
        canvas.fillCircle(x, y, r, alpha, theta);
    }
    
    public void fillCircle(Unicon2DContext context, int x, int y, int r, int alpha, int theta){
        canvas.fillCircle(context, x, y, r, alpha, theta);
    }
    
    public void fillPolygon(float... vertices){
        canvas.fillPolygon(vertices);
    }
    
    public void fillPolygon(Unicon2DContext context, float... vertices){
        canvas.fillPolygon(context, vertices);
    }
    
    public void fillRectangle(int x, int y, int w, int h){
        canvas.fillRectangle(x, y, w, h);
    }
    
    public void fillRectangle(Unicon2DContext context, int x, int y, int w, int h){
        canvas.fillRectangle(x, y, w, h);
    }
    
    public void Flush(){
        canvas.Flush();
    }
    
    public String Font(){
        return canvas.Font();
    }
    
    public boolean Font(String fontParams){
        return canvas.Font(fontParams);
    }
    
    public void GotoRC(int r, int c){
        canvas.GotoRC(r, c);
    }
    
    public void GotoXY(int x, int y){
        canvas.GotoXY(x, y);
    }
    
    public void LeftString(int x, int y, String string){
        canvas.LeftString(x, y, string);
    }
    
    public void LeftString(Unicon2DContext context, int x, int y, String string){
        canvas.LeftString(context, x, y, string);
    }
    
    public Queue Pending(){
        return canvas.Pending();
    }
    
    public void Raise()
    {
        Unicon2DWindowManager wManager = ((Unicon2DWindowManager) Gdx.app.getApplicationListener());
        if(wManager.getCurrentWindow() != this)
        {
            wManager.setScreen(this);
            wManager.updateCurrentWindow();
        }
    }
    
    public boolean ReadImage(String file, int x, int y, int w, int h){
        return canvas.ReadImage(file, x, y, w, h);
    }
    
    public boolean ReadImage(Unicon2DContext context, String file, int x, int y, int w, int h){
        return canvas.ReadImage(context, file, x, y, w, h);
    }
            
    public int TextWidth(String string){
        return canvas.TextWidth(string);
    }

    public String WAttrib(String attribute){
        return canvas.WAttrib(attribute);
    }
    
    public void WAttrib(String... attributes){
        canvas.WAttrib(attributes);
    }
    
    public void WClose(){
        canvas.WClose();
    }
    
    public void WDelay(int ms){
        canvas.WDelay(ms);
    }
    
    public void WDone(){
        canvas.WDone();
    }
    
    public void write(String string){
        canvas.write(string);
    }
    
    public void write(Unicon2DContext context, String string){
        canvas.write(context, string);
    }
    
    public boolean WQuit(){
        return canvas.WQuit();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) 
    {
        canvas.render(delta);
    }

    @Override
    public void resize(int width, int height) {
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
    public void dispose() {
        context.dispose();
        canvas.dispose();
    }







}
