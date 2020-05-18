/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * 
 * @author HEINEMANN
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable{
    private static final int BW = 30;
    private static final int BH = 15;
    private byte dir;
    private long frames;
    private long time;
    private boolean ingame;
    private Thread loop;
    private final Graphics2D ctx;
    private final BufferedImage image;
    private ArrayList<Ball> balls;
    private int score;
    private byte level;
    private boolean inlevel;
    private final ArrayList<Bonus> bonuses;
    private final ArrayList<Ball> bullets;
    private final ArrayList<Ball> bombs;
    private Block[] guns = {new Block(0,0,5,20,1),new Block(0,0,5,20,1)};
    private long guntimer;
    private long guntimeout;
    private double bulletspeed;
    private double bombspeed;
    private boolean gunvisible;
    private int bulletsize;
    private boolean pause;
    private double t;
    private Level[] levelarr = {
        new Level(){
            @Override
            public void update(){
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10,BW,BH,2));
                }
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10+BH*2,BW,BH,1));
                }
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10+BH*4,BW,BH,0));
                }
            }
        },
        new Level(){
            @Override
            public void update(){
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10,BW,BH,2));
                }
                for(int i=0;i!=6;i++){
                    blocks.add(new Block(5,10+BH+BH*i,BW,BH,2));
                    blocks.add(new Block(5+BW*15,10+BH+BH*i,BW,BH,2));
                }
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10+BH*7,BW,BH,2));
                }
            }
        },
        new Level(){
            @Override
            public void update(){
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10,BW,BH,4));
                }
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10+BH*2,BW,BH,3));
                }
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10+BH*4,BW,BH,2));
                }
            }
        },
        new Level(){
            
            @Override
            public void update(){
                for(int i=0;i!=5;i++){
                    for(int j=0;j!=16;j++){
                        blocks.add(new Block(j*BW+5,10+BH*i,BW,BH,4-i));
                    }
                }
            }
        },
        new Level(){
            @Override
            public void update(){
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10,BW,BH,4));
                }
                for(int i=0;i!=6;i++){
                    blocks.add(new Block(5,10+BH+BH*i,BW,BH,4));
                    blocks.add(new Block(5+BW*15,10+BH+BH*i,BW,BH,4));
                }
                for(int i=0;i!=16;i++){
                    blocks.add(new Block(i*BW+5,10+BH*7,BW,BH,4));
                }
            }
        }
    };
    private byte lives;
    private Block racket;
    private ArrayList<Block> blocks;
    GamePanel(){
        setPreferredSize(new Dimension(500,500));
        setSize(500,500);
        setLayout(null);
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();
        setVisible(true);
        addKeyListener(new KeyListener(){
            @Override
            public void keyReleased(KeyEvent e){
                int kc = e.getKeyCode();
                if(kc == KeyEvent.VK_LEFT){dir = 0;}
                if(kc == KeyEvent.VK_RIGHT){dir = 0;}
                if(kc == KeyEvent.VK_SPACE && !(inlevel)){
                    balls.get(0).setAngle(2.5*Math.PI);
                    inlevel = true;
                }
                if(kc == KeyEvent.VK_S){
                    pause=!pause;
                }
            }
            @Override
            public void keyPressed(KeyEvent e){
                int kc = e.getKeyCode();
                if(kc == KeyEvent.VK_LEFT){dir = (byte)(t*-1);}
                if(kc == KeyEvent.VK_RIGHT){dir = (byte)t;}
            }
            @Override
            public void keyTyped(KeyEvent e){}
        });
        addMouseMotionListener(new MouseMotionListener() {
    		@Override
    		public void mouseDragged(MouseEvent e) {
    			int evx = e.getX();
    			racket.setX(evx+(racket.getWidth()/2)>500?evx-(racket.getWidth()/2)+(500-evx-(racket.getWidth()/2)):evx-(racket.getWidth()/2)<0?evx-(racket.getWidth()/2)-(evx-(racket.getWidth()/2)):evx-(racket.getWidth()/2));
    		}
    		@Override
    		public void mouseMoved(MouseEvent e) {}
        });
        addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!ingame) {
					ingame = true;
					balls.get(0).setAngle(2.5*Math.PI);
				}
				if(!inlevel) {
					inlevel=true;
					balls.get(0).setAngle(2.5*Math.PI);
				}
				if(new Rectangle(470,18,30,30).intersects(new Rectangle(e.getX(),e.getY(),3,3))) pause = !pause;
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
        });
        image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        ctx = image.createGraphics();
        balls = new ArrayList<Ball>();
        balls.add(new Ball(200.0,200.0,6,0,Color.WHITE));
        lives = 4;
        level = -1;
        bonuses = new ArrayList<Bonus>();
        racket = new Block(200,425,100,17,10);
        blocks = new ArrayList<Block>();
        bullets = new ArrayList<Ball>();
        bombs = new ArrayList<Ball>();
        bulletspeed = 3;
        bombspeed = 2.5;
        bulletsize = 2;
        for(Block g:guns){
            g.setY(racket.getY()-g.getHeight()+1);
        }
    }
    @Override
    public void addNotify(){
        super.addNotify();
        loop = new Thread(this);
        loop.start();
    }
    public void updateLevel(){
    	//render
        ctx.clearRect(0, 0, 500, 500);
        balls.get(0).render(ctx);
        racket.render(ctx, Color.WHITE);
        for(int i=0;i!=blocks.size();i++){
            blocks.get(i).render(ctx,Block.COLOR_SET[blocks.get(i).getLayer()]);
        }
        for(int i=0;i!=bonuses.size();i++){
            bonuses.get(i).render(ctx);
        }
        Graphics gr = getGraphics();
        gr.setFont(new Font("baloonist",1,30));
        gr.drawImage(image,0,0,null);
        gr.setColor(Color.WHITE);
        gr.drawString("Press <Space> to start game",25,200);
        gr.dispose();
        while(!inlevel){
            try{
                Thread.sleep(1);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        while(inlevel){
            if(pause){
            	render();
                ctx.setColor(Color.WHITE);
                for(int i=0;i!=balls.size();i++){
                    int sx = (int)(balls.get(i).getX()+balls.get(i).getRadius()+(balls.get(i).getRadius()+5)*Math.cos(balls.get(i).getAngle()));
                    int sy = (int)(balls.get(i).getY()+balls.get(i).getRadius()+(balls.get(i).getRadius()+5)*Math.sin(balls.get(i).getAngle()));
                    ctx.drawLine(sx,sy,(int)(sx+(20*Math.cos(balls.get(i).getAngle()))),(int)(sy+(20*Math.sin(balls.get(i).getAngle()))));
                    ctx.drawLine((int)(sx+(20*Math.cos(balls.get(i).getAngle()))),(int)(sy+(20*Math.sin(balls.get(i).getAngle()))),(int)(sx+(20*Math.cos(balls.get(i).getAngle()))+(8*Math.cos(balls.get(i).getAngle()-(0.75*Math.PI)))),(int)(sy+(20*Math.sin(balls.get(i).getAngle()))+(8*Math.sin(balls.get(i).getAngle()-(0.75*Math.PI)))));
                    ctx.drawLine((int)(sx+(20*Math.cos(balls.get(i).getAngle()))),(int)(sy+(20*Math.sin(balls.get(i).getAngle()))),(int)(sx+(20*Math.cos(balls.get(i).getAngle()))+(8*Math.cos(balls.get(i).getAngle()+(0.75*Math.PI)))),(int)(sy+(20*Math.sin(balls.get(i).getAngle()))+(8*Math.sin(balls.get(i).getAngle()+(0.75*Math.PI)))));
                }
                Graphics g1 = getGraphics();
                g1.drawImage(image, 0, 0, null);
                g1.dispose();
            }
            while(pause){
                try{
                    Thread.sleep(1);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(int r=0;r<balls.size();r++){
                if(balls.get(r).getX() < 0){
                    balls.get(r).setAngle(Math.PI-balls.get(r).getAngle());
                }
                if(balls.get(r).getX()+balls.get(r).getRadius()*2 > 500){
                    balls.get(r).setAngle(Math.PI-balls.get(r).getAngle());
                }
                if(balls.get(r).getY() < 0){
                    balls.get(r).setAngle(1/2*Math.PI-balls.get(r).getAngle());
                }
                if(balls.get(r).getY()+balls.get(r).getRadius()*2 > 500){
                    if(balls.size()==1){
                        System.out.println("Lives:"+lives);
                        lives--;
                        if(lives==-1){ingame=false;return;}
                        inlevel = false;
                        racket.setX(230);
                        t=2;
                        bullets.clear();
                        bombs.clear();
                        bonuses.clear();
                        gunvisible=false;
                        guntimeout = 0;
                        racket.setWidth(100);
                        balls.get(0).setX(racket.getX()+(racket.getWidth()/2)-(balls.get(0).getRadius()*2));
                        balls.get(0).setY(racket.getY()+(balls.get(0).getRadius()*2));
                        render();
                        while(!inlevel){
                            try{
                                Thread.sleep(1);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    else{
                        balls.remove(r);
                        r--;
                    }
                    continue;
                    //ball.setAngle(Math.PI-ball.getAngle());
                }
                balls.get(r).move(t*Math.cos(balls.get(r).getAngle()),t*Math.sin(balls.get(r).getAngle()));
                for(int i=0;i!=blocks.size();i++){
                    if(blocks.get(i).crash(balls.get(r))){
                        blocks.get(i).removeLayer();
                        if(inRange((balls.get(r).getX())+(balls.get(r).getRadius()*2),(double)blocks.get(i).getX(),4)){
                             balls.get(r).setAngle(Math.PI-balls.get(r).getAngle());
                        }
                        else if(inRange((balls.get(r).getX()),(double)blocks.get(i).getX()+blocks.get(i).getWidth(),4)){
                            balls.get(r).setAngle(Math.PI-balls.get(r).getAngle());
                        }
                        else if(inRange((balls.get(r).getY())+(balls.get(r).getRadius()*2),(double)blocks.get(i).getY(),4)){
                           balls.get(r).setAngle(1/2*Math.PI-balls.get(r).getAngle());
                        }
                        else if(inRange(balls.get(r).getY(),(double)blocks.get(i).getY()+blocks.get(i).getHeight(),4)){
                            balls.get(r).setAngle(1/2*Math.PI-balls.get(r).getAngle());
                        }
                        newBonus(i);
                        if(blocks.get(i).getLayer()==-1){
                            blocks.remove(i);
                            i--;
                            score++;
                        }
                        score++;
                    }
                }
                if(racket.crash(balls.get(r))){
                    double point = (balls.get(r).getX()+balls.get(r).getRadius()-racket.getX())/racket.getWidth();
                    point = (point<=0?0.05:point>=1?0.95:point);
                    balls.get(r).setAngle(Math.PI*point+Math.PI);
                }
            }
            out:for(int r=0;r!=bombs.size();r++){
                bombs.get(r).move(bombspeed*Math.cos(bombs.get(r).getAngle()),bombspeed*Math.sin(bombs.get(r).getAngle()));
                for(int i=0;i!=blocks.size();i++){
                    if(blocks.get(i).crash(bombs.get(r))){
                        while(blocks.get(i).getLayer()>-1){
                            blocks.get(i).removeLayer();
                            score++;
                            newBonus(i);
                        }
                        score++;
                        blocks.remove(i);
                        i--;
                        for(double p=2*Math.PI;p>0;p-=0.25*Math.PI){
                            bullets.add(new Ball(bombs.get(r).getX()+bombs.get(r).getRadius(),bombs.get(r).getY()+bombs.get(r).getRadius(),bulletsize,p,Color.GRAY));
                        }
                        bombs.remove(r);
                        r--;
                        continue out;
                    }
                }
                if(bombs.get(r).getY()<0){
                    for(double p=2*Math.PI;p>0;p-=0.25*Math.PI){
                        bullets.add(new Ball(bombs.get(r).getX()+bombs.get(r).getRadius(),bombs.get(r).getY()+bombs.get(r).getRadius(),bulletsize,p,Color.GRAY));
                    }
                    bombs.remove(r);
                    r--;
                }
            }
            out:for(int r=0;r<bullets.size();r++){
                bullets.get(r).move(bulletspeed*Math.cos(bullets.get(r).getAngle()), bulletspeed*Math.sin(bullets.get(r).getAngle()));
                for(int i=0;i!=blocks.size();i++){
                    if(blocks.get(i).crash(bullets.get(r))){
                        blocks.get(i).removeLayer();
                        newBonus(i);
                        score++;
                        if(blocks.get(i).getLayer()==-1){
                            blocks.remove(i);
                            i--;
                            score++;
                        }
                        bullets.remove(r);
                        r--;
                        continue out;
                    }
                }
                if(bullets.get(r).getY()<0||bullets.get(r).getY()>500||bullets.get(r).getX()<0||bullets.get(r).getX()>500){
                    bullets.remove(r);
                    r--;
                }
            }
            if((dir>0&&racket.getX()+racket.getWidth()<=500)||(dir<0&&racket.getX()>=0)){
                racket.move(dir);
            }
            if(gunvisible){
                if(guntimeout<=frames)gunvisible=false;
                guns[0].setX(racket.getX());
                guns[1].setX(racket.getX()+racket.getWidth()-guns[1].getWidth());
                if(frames%guntimer==0){
                    for(Block g:guns){
                        bullets.add(new Ball(g.getX(),g.getY()-(0.25*g.getWidth()),bulletsize,1.5*Math.PI,Color.GRAY));
                    }
                }
            }
            if(frames%500==0){
                t*=1.01;
            }
            if(blocks.isEmpty()){
                inlevel=false;
                score+=20;
            }
            for(int i=0;i!=bonuses.size();i++){
                if(racket.crash(bonuses.get(i))){
                    bonuses.get(i).action();
                    bonuses.remove(i);
                    i--;
                }
                else if(inRange((int)bonuses.get(i).getY(),0,3)){
                    bonuses.remove(i);
                    i--;
                }
                else{
                    bonuses.get(i).drop();
                }
            }
            render();
            //Loop
            frames++;
            try{
                Thread.sleep(time);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        if(ingame)setUpLevel();
    }
    public void render() {
    	//Rendering
        ctx.clearRect(0, 0, 500, 500);
        for(int r=0;r<balls.size();r++){
            balls.get(r).render(ctx);
        }
        racket.render(ctx, Color.WHITE);
        if(gunvisible){
            for(Block g:guns){
                g.render(ctx,Color.WHITE);
            }
        }
        for(int i=0;i<blocks.size();i++){
            blocks.get(i).render(ctx,Block.COLOR_SET[blocks.get(i).getLayer()]);
        }
        for(int i=0;i!=bombs.size();i++){
            bombs.get(i).render(ctx);
        }
        for(int i=0;i!=bullets.size();i++){
            bullets.get(i).render(ctx);
        }
        for(int i=0;i!=bonuses.size();i++){
            bonuses.get(i).render(ctx);
        }
        ctx.setFont(new Font("baloonist",1,20));
        ctx.setColor(Color.WHITE);
        for(int i=0;i!=lives;i++){
            new Ball((double)(balls.get(0).getRadius()*2*i+10+6*i),450.0,balls.get(0).getRadius(),0.0,Color.WHITE).render(ctx);
        }
        ctx.drawString("Score: "+score,10,30);
        ctx.drawString(pause?"►":"❚❚",480,30);
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
	}
	public boolean inRange(double x,double y,double r){
        return (x>=y-r&&x<=y+r);
    }
    public void newBonus(int i){
        int ran = (int)(Math.random()*200);
        if(ran>198){
            bonuses.add(new Life((double)(blocks.get(i).getX()+(blocks.get(i).getWidth()/2)),(double)(blocks.get(i).getY()+blocks.get(i).getHeight()),0,(byte)(Math.random()*3+1)));
        }
        else if(ran>179){
            bonuses.add(new Coin((double)(blocks.get(i).getX()+(blocks.get(i).getWidth()/2)),(double)(blocks.get(i).getY()+blocks.get(i).getHeight()),0,(byte)(Math.random()*3+1)));
        }
        else if(ran>169){
            bonuses.add(new Split((double)(blocks.get(i).getX()+(blocks.get(i).getWidth()/2)),(double)(blocks.get(i).getY()+blocks.get(i).getHeight()),0,(byte)(Math.random()*3+1)));
        }
        else if(ran>159){
            bonuses.add(new Gun((double)(blocks.get(i).getX()+(blocks.get(i).getWidth()/2)),(double)(blocks.get(i).getY()+blocks.get(i).getHeight()),0,(byte)(Math.random()*3+1)));
        }
        else if(ran>149){
            bonuses.add(new Bomb((double)(blocks.get(i).getX()+(blocks.get(i).getWidth()/2)),(double)(blocks.get(i).getY()+blocks.get(i).getHeight()),0,(byte)(Math.random()*3+1)));
        }
        else if(ran>139){
            bonuses.add(new Large((double)(blocks.get(i).getX()+(blocks.get(i).getWidth()/2)),(double)(blocks.get(i).getY()+blocks.get(i).getHeight()),0,(byte)(Math.random()*3+1)));
        }
        else if(ran>129){
            bonuses.add(new Small((double)(blocks.get(i).getX()+(blocks.get(i).getWidth()/2)),(double)(blocks.get(i).getY()+blocks.get(i).getHeight()),0,(byte)(Math.random()*3+1)));
        }
    }
    public void setUpLevel(){
        level++;
        blocks.clear();
        bonuses.clear();
        balls.clear();
        bullets.clear();
        bombs.clear();
        gunvisible=false;
        guntimeout = 0;
        balls.add(new Ball(200.0,200.0,6,0,Color.WHITE));
        dir = 0;
        time = 5;
        t = 2;
        frames = 0;
        racket.setX(230);
        racket.setWidth(100);
        balls.get(0).setX(racket.getX()+(racket.getWidth()/2)-(balls.get(0).getRadius()*2));
        balls.get(0).setY(racket.getY()-(balls.get(0).getRadius()*2));
        if(level<levelarr.length){
            levelarr[level].update();
            updateLevel();
        }
        ingame=false;
    }
    @Override
    public void run(){
        ingame = true;
        while(ingame){
            setUpLevel();
        }
        if(level<4)System.out.println("lost");
        File highs = new File("blockHighScore.dat");
        if(!highs.exists()){
            try{
                highs.createNewFile();
            }
            catch(IOException e){}
        }
        FileReader reader;
        BufferedReader breader = null;
        String high = null;
        try {
            reader = new FileReader(highs);
            breader = new BufferedReader(reader);
            high = breader.readLine();
            breader.close();
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        if(high==null)high="0";
        if(score>Integer.parseInt(high)){
            high = String.valueOf(score);
            FileWriter writer;
            BufferedWriter bwriter;
            try{
                writer = new FileWriter(highs);
                bwriter = new BufferedWriter(writer);
                bwriter.write(String.valueOf(score).toCharArray());
                bwriter.close();
            }
            catch(IOException e){}
        }
        Graphics g = getGraphics();
        g.setFont(new Font("baloonist",1,30));
        ctx.clearRect(0,0,500,500);
        g.drawImage(image,0,0,null);
        g.setColor(Color.WHITE);
        g.drawString("You "+(lives<=0?"Lost":"Won"),25,100);
        g.drawString("Your Score Was: "+score,25,200);
        g.drawString("Highest score: "+high,25,300);
        g.dispose();
    }
    private class Split extends Bonus{
        Split(double x,double y, double a, byte s){
            super(x,y,a,Color.GREEN,s);
        }
        @Override
        public void action(){
            balls.add(new Ball(balls.get(0).getX(),balls.get(0).getY(),balls.get(0).getRadius(),(balls.get(0).getAngle()-(0.25*Math.PI))%Math.PI==0?(balls.get(0).getAngle()-(0.25*Math.PI))+0.01:(balls.get(0).getAngle()-(0.25*Math.PI)),Color.WHITE));
            balls.add(new Ball(balls.get(0).getX(),balls.get(0).getY(),balls.get(0).getRadius(),(balls.get(0).getAngle()+(0.25*Math.PI))%Math.PI==0?(balls.get(0).getAngle()+(0.25*Math.PI))-0.01:(balls.get(0).getAngle()+(0.25*Math.PI)),Color.WHITE));
        }
    }
    private class Large extends Bonus{
        Large(double x,double y, double a, byte s){
            super(x,y,a,Color.BLUE,s);
        }
        @Override
        public void action(){
            racket.setX(racket.getX()-5);
            racket.setWidth(racket.getWidth()+10);
        }
    }
    private class Small extends Bonus{
        Small(double x,double y, double a, byte s){
            super(x,y,a,Color.RED,s);
        }
        @Override
        public void action(){
            if(racket.getWidth()>20){
                racket.setX(racket.getX()+5);
                racket.setWidth(racket.getWidth()-10);
            }
        }
    }
    private class Coin extends Bonus{
        Coin(double x,double y, double a, byte s){
            super(x,y,a,Color.YELLOW,s);
        }
        @Override
        public void action(){
            score += 10;
        }
    }
    private class Life extends Bonus{
        Life(double x,double y, double a, byte s){
            super(x,y,a,Color.PINK,s);
        }
        @Override
        public void action(){
            lives++;
        }
    }
    private class Gun extends Bonus{
        Gun(double x,double y, double a, byte s){
            super(x,y,a,Color.LIGHT_GRAY,s);
        }
        @Override
        public void action(){
            gunvisible = true;
            guntimer = 1000/time;
            guntimeout = guntimer*10+frames;
        }
    }
    private class Bomb extends Bonus{
        Bomb(double x,double y, double a, byte s){
            super(x,y,a,Color.decode("#CC6600"),s);
        }
        @Override
        public void action(){
            bombs.add(new Ball((double)(racket.getX()+(0.5*racket.getWidth())-14),racket.getY()-13,7,1.5*Math.PI,Color.ORANGE));
        }
    }
}
