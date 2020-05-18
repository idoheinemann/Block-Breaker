
import java.awt.Color;

public abstract class Bonus extends Ball{
    private int speed;
    Bonus(double x,double y, double a, Color c, byte s){
        super(x,y,10,a,c);
        speed = s;
    }
    public void drop(){
        setY(getY()+speed);
    }
    public abstract void action();
}
