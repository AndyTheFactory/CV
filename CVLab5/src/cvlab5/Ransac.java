/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvlab5;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *
 * @author andrei
 */
public class Ransac {
    int iterations;
    int threshold;
    double inliers;
    ArrayList<Line2D> lines;
    int SAMPLEWIDTH;
    static int ANGLE_TOLERANCE=10;
    static int DISTANCE_TOLERANCE=15;
    
    
    public Ransac(int iterations, int threshold, double inliers,int sampleWidth)
    {
        this.inliers=inliers;
        this.threshold=threshold;
        this.iterations=iterations;
        this.lines=new ArrayList<>();
        this.SAMPLEWIDTH=sampleWidth;
    }
    
    public void doRansac(BufferedImage img)
    {
        lines.clear();
        for (int i=0;i<img.getWidth()/SAMPLEWIDTH;i++)
            for (int j=0;j<img.getHeight()/SAMPLEWIDTH;j++)
                Ransac(i*SAMPLEWIDTH,j*SAMPLEWIDTH,img);
    }
    private void addToLines(ArrayList<Point> fitted){
        Point minP=null,maxP=null;
        for(Point p:fitted){
            if (minP==null || minP.x>p.x) minP=p;
            if (maxP==null || maxP.x<p.x) maxP=p;
        }
        lines.add(new Line2D.Double(minP, maxP));
    }
    private double costFunc(Line2D line, Point p){
        double m=(line.getY2()-line.getY1())/(line.getX1()==line.getX2()?0.00001:(line.getX2()-line.getX1())),
                b=line.getY1();
        return 
                Math.min(
                    Math.pow(
                        p.y-m*p.x-line.getY1()
                    ,2),
                    Math.pow(
                        p.x-(m==0?0.0001:(1/m))*p.y-line.getX1()
                    ,2)
                );
    }
    public void Ransac(int x,int y, BufferedImage img){
        ArrayList<Point> candidates=new ArrayList<>();
        for(int i=x;i<Math.min(x+SAMPLEWIDTH, img.getWidth()-1);i++)
            for(int j=y;j<Math.min(y+SAMPLEWIDTH, img.getHeight()-1);j++)
                if (img.getRGB(i,j)==Color.WHITE.getRGB())
                    candidates.add(new Point(i, j));
        if (candidates.size()<=2)
            return;
        int iter;
        double bestfit=999999999;
        Point p1,p2;
        ArrayList<Point> bestmodel=new ArrayList<>();
        do {
            iter=0;
            bestmodel.clear();
            while (iter<iterations){
                ArrayList<Point> inlier=new ArrayList<>();
                p1=candidates.get((int)(Math.random()*candidates.size()));
                candidates.remove(p1);
                p2=candidates.get((int)(Math.random()*candidates.size()));
                candidates.remove(p2);
                Line2D model=new Line2D.Double(p1, p2);
                double cost=0;
                for(Point p:candidates){
                    double dist=model.ptLineDist(p);
                    if (dist<threshold){
                        inlier.add(p);
                        cost+=costFunc(model, p);
                        //cost+=dist/candidates.size();
                    }
                }
                if (inlier.size()>inliers){
                    //model fits
                    if (cost<bestfit){
                        bestfit=cost;
                        bestmodel.clear();
                        bestmodel.addAll(inlier);
                        bestmodel.add(p1);
                        bestmodel.add(p2);
                    }
                }
                candidates.add(p1);
                candidates.add(p2);
                iter++;
            }

            if (bestmodel.size()>0){
                candidates.removeAll(bestmodel);
                addToLines(bestmodel);

            }
        } while(bestmodel.size()>0 && candidates.size()>1);
    }
    public void drawLines(BufferedImage img){
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.red);
        g2d.setPaint(Color.red);
        BasicStroke bs = new BasicStroke(4);
        g2d.setStroke(bs);    
        
        for(Line2D line:lines){
            g2d.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
        }
        
    }
    
    public ArrayList<Rectangle> findRectangles()
    {
        ArrayList<Rectangle> res=new ArrayList<>();
        for (Line2D line:lines){
            ArrayList<Line2D> candidati=new ArrayList<Line2D>(lines);
            candidati.remove(line);
            
            Line2D muchie1=findNextMuchie(line, candidati);
            if (muchie1==null) continue;
            
            Line2D muchie2=findNextMuchie(line, candidati);
            if (muchie2==null) continue;
            
            Line2D muchie3=findNextMuchie(line, candidati);
            if (muchie3==null) continue;
            
            Line2D muchie4=findNextMuchie(line, candidati);
            if (muchie4==null) continue;
            double x1=Math.min(
                    Math.min(
                        Math.min(muchie1.getX1(), muchie1.getX2()),
                        Math.min(muchie2.getX1(), muchie2.getX2())
                    ),
                    Math.min(
                        Math.min(muchie3.getX1(), muchie3.getX2()),
                        Math.min(muchie4.getX1(), muchie4.getX2())
                    )
            );
            double x2=Math.max(
                    Math.max(
                        Math.max(muchie1.getX1(), muchie1.getX2()),
                        Math.max(muchie2.getX1(), muchie2.getX2())
                    ),
                    Math.max(
                        Math.max(muchie3.getX1(), muchie3.getX2()),
                        Math.max(muchie4.getX1(), muchie4.getX2())
                    )
            );
            double y1=Math.min(
                    Math.min(
                        Math.min(muchie1.getY1(), muchie1.getY2()),
                        Math.min(muchie2.getY1(), muchie2.getY2())
                    ),
                    Math.min(
                        Math.min(muchie3.getY1(), muchie3.getY2()),
                        Math.min(muchie4.getY1(), muchie4.getY2())
                    )
            );
            double y2=Math.max(
                    Math.max(
                        Math.max(muchie1.getY1(), muchie1.getY2()),
                        Math.max(muchie2.getY1(), muchie2.getY2())
                    ),
                    Math.max(
                        Math.max(muchie3.getY1(), muchie3.getY2()),
                        Math.max(muchie4.getY1(), muchie4.getY2())
                    )
            );
            
            res.add(new Rectangle((int)x1,(int)y2,(int)(x2-x1),(int)(y2-y1)));
            
            
        }
        return res;
    }
    private Line2D findNextMuchie(Line2D fromline, ArrayList<Line2D> candidati)
    {
        for(Line2D line:lines){
            if ((fromline.getP2().distance(line.getP1())<=DISTANCE_TOLERANCE) ||
                (fromline.getP2().distance(line.getP2())<=DISTANCE_TOLERANCE)){
                double angle=getAngle(fromline, line);
                if (Math.abs(90-angle)<=ANGLE_TOLERANCE)
                    return line;
                
            }
        }
        return null;
    }
    
    static public double getAngle(Line2D line1, Line2D line2){
        double angle1 = Math.atan2(line1.getY1() - line1.getY2(),
                                   line1.getX1() - line1.getX2());
        double angle2 = Math.atan2(line2.getY1() - line2.getY2(),
                                   line2.getX1() - line2.getX2());
        double angle = Math.abs(Math.toDegrees(angle1-angle2));
        
        if (angle>180) angle-=180;
                
        return angle;
    }
    
}
