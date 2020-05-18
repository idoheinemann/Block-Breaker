
import java.awt.Color;
import java.awt.Graphics2D;

public class Ball{
        private int radius;
        private double angle,x,y;
        private Color color;
        Ball(double x,double y,int radius,double angle,Color c){
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.angle = angle;
            color = c;
        }
        public void setX(double nx){
            x = nx;
        }
        public void setY(double nx){
            y = nx;
        }
        public void setRadius(int nx){
            radius = nx;
        }
        public void setAngle(double nx){
            angle = nx;
        }
        public Color getColor(){
            return color;
        }
        public void render(Graphics2D ctx){
            ctx.setColor(color);
            ctx.fillOval((int)x, (int)y, radius*2, radius*2);
            ctx.setColor(Color.WHITE);
            ctx.drawOval((int)x, (int)y, radius*2, radius*2);
        }
        public double getX(){
            return x;
        }
        public double getY(){
            return y;
        }
        public double getAngle(){
            return angle;
        }
        public int getRadius(){
            return radius;
        }
        public void move(double nx, double ny){
            x+=nx;
            y+=ny;
        }
    }