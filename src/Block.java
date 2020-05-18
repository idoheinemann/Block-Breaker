/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;

/**
 *
 * @author HEINEMANN
 * 
 */
public class Block {
    public static final Color[] COLOR_SET = {Color.GREEN,Color.BLUE,Color.RED,Color.CYAN,new Color(171,0,255)};
    private int x,y,width,height,layer;
    Block(int x, int y, int w, int h, int layer){
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        this.layer = layer;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getLayer(){
        return layer;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setWidth(int w){
        width = w;
    }
    public void removeLayer(){
        layer--;
    }
    public void move(int sx){
        x+=sx;
    }
    public Rectangle getRect(){
        return new Rectangle(x,y,width-1,height-1);
    }
    public void render(Graphics2D ctx,Color c){
        ctx.setColor(c);
        ctx.fillRect(x,y,width-1,height-1);
        ctx.setColor(Color.WHITE);
        ctx.drawRect(x,y,width-1,height-1);
    }
    public boolean crash(Ball b){
        int disx = Math.abs((int)b.getX()+b.getRadius()-x-(width/2));
        int disy = Math.abs((int)b.getY()+b.getRadius()-y-(height/2));
        if(disx > b.getRadius()+(width/2) || disy > b.getRadius()+(height/2))return false;
        if(disx <= width/2 || disy <= height/2)return true;
        return (Math.pow(disx-(width/2.0),2)+Math.pow(disy-(height/2.0),2)<=Math.pow(b.getRadius(), 2));
    }
}
