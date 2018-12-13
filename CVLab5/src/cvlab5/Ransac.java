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
    static int SAMPLEWIDTH=100;
    
    public Ransac(int iterations, int threshold, double inliers)
    {
        this.inliers=inliers;
        this.threshold=threshold;
        this.iterations=iterations;
        lines=new ArrayList<>();
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
    public void Ransac(int x,int y, BufferedImage img){
        ArrayList<Point> candidates=new ArrayList<>();
        for(int i=x;i<Math.min(x+SAMPLEWIDTH, img.getWidth()-1);i++)
            for(int j=y;j<Math.min(y+SAMPLEWIDTH, img.getHeight()-1);j++)
                if (img.getRGB(i,j)==Color.WHITE.getRGB())
                    candidates.add(new Point(i, j));
        if (candidates.size()<=0)
            return;
        int iter=0;
        double bestfit=999999999;
        Point p1,p2;
        ArrayList<Point> bestmodel=new ArrayList<>();
        do {
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
                        cost+=dist;
                    }
                }
                if (inlier.size()>candidates.size()*inliers){
                    //model fits
                    cost=cost/inlier.size();
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
        } while(bestmodel.size()>0);
    }
    public void drawLines(BufferedImage img){
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.red);
        g2d.setPaint(Color.red);
        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);    
        
        for(Line2D line:lines){
            g2d.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
        }
        
    }
}
