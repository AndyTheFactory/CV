/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 *
 * @author andrei
 */
public abstract class ImageFilter {
    double[][] data;
    int radius;
    static final int CHANNEL_BLUE=1;
    static final int CHANNEL_RED=2;
    static final int CHANNEL_GREEN=3;
    
    
    public float applyFilter(float[][] imgdata){
        int i,j;
        float res=0;
        for(i=0;i<data.length;i++){
            for(j=0;j<data[i].length;j++){
                res+=imgdata[i][j]*data[i][j];
            }
        }
        return res;
    }
    public int applyFilter(BufferedImage img,int x, int y){
        byte[] pixels;
        int res=0;
        Rectangle r=new Rectangle(x-radius+1, y-radius+1, 2*radius-1, 2*radius-1);
        boolean withAlphachan=img.getAlphaRaster()!=null;

        
        if(r.x<0 || r.y<0 || r.getMaxX()>=img.getWidth() || r.getMaxY()>img.getHeight()){
            //Edge stuff
            int deltax=(int)(r.x<0?r.x:img.getWidth()-r.getMaxX());
            deltax=(deltax>0)?0:deltax;
            int deltay=(int)(r.y<0?r.y:img.getHeight()-r.getMaxY());
            deltay=(deltay>0)?0:deltay;
            
            Rectangle r2=new Rectangle(
                    Integer.max(r.x, 0),
                    Integer.max(r.y, 0),
                    r.width+deltax,
                    r.height+deltay
            );
            pixels=((DataBufferByte) img.getData(r2).getDataBuffer()).getData();
            for(int i=0,x1=r.x;i<data.length;i++,x1++)
                for(int j=0,y1=r.y;j<data[i].length;j++,y1++){
                    int px;
                    if(x1>=0 && x1<=img.getWidth()){
                        px=x1-r2.x;
                    }else{
                        px=(int)(x1<0?-x1:(2*img.getWidth()-r.getMaxX()-r2.x));
                    }
                    if(y1>=0 && y1<=img.getWidth()){
                        px+=(y1-r2.y)*r2.width;
                    }else{
                        px+=(y1<0?-y1:(2*img.getHeight()-r.getMaxY()-r2.y))*r2.width;
                    }
                    int alpha=withAlphachan?(pixels[px++] & 0xff):0;
                    int blue=(pixels[px++] & 0xff),
                        green=(pixels[px++] & 0xff),
                        red=(pixels[px++] & 0xff);
                    res+=alpha<<24;
                    res+=(int)(blue*data[i][j]);
                    res+=((int)(green*data[i][j]))<<8;
                    res+=((int)(red*data[i][j]))<<16;
                }
            
        }else{
            pixels=((DataBufferByte) img.getData(r).getDataBuffer()).getData();
            for(int i=0,px=0;i<data.length;i++)
                for(int j=0;j<data[i].length;j++){
                    int alpha=withAlphachan?(pixels[px++] & 0xff):0;
                    int blue=(pixels[px++] & 0xff),
                        green=(pixels[px++] & 0xff),
                        red=(pixels[px++] & 0xff);
                    res+=alpha<<24;
                    res+=(int)(blue*data[i][j]);
                    res+=((int)(green*data[i][j]))<<8;
                    res+=((int)(red*data[i][j]))<<16;
                }
        }
        return res;
    }
    public int applyFilter(BufferedImage img,int x, int y,int channel){
        byte[] pixels;
        int res=0;
        Rectangle r=new Rectangle(x-radius+1, y-radius+1, 2*radius-1, 2*radius-1);
        boolean withAlphachan=img.getAlphaRaster()!=null;

        
        if(r.x<0 || r.y<0 || r.getMaxX()>=img.getWidth() || r.getMaxY()>img.getHeight()){
            //Edge stuff
            int deltax=(int)(r.x<0?r.x:img.getWidth()-r.getMaxX());
            deltax=(deltax>0)?0:deltax;
            int deltay=(int)(r.y<0?r.y:img.getHeight()-r.getMaxY());
            deltay=(deltay>0)?0:deltay;
            
            Rectangle r2=new Rectangle(
                    Integer.max(r.x, 0),
                    Integer.max(r.y, 0),
                    r.width+deltax,
                    r.height+deltay
            );
            pixels=((DataBufferByte) img.getData(r2).getDataBuffer()).getData();
            for(int i=0,x1=r.x;i<data.length;i++,x1++)
                for(int j=0,y1=r.y;j<data[i].length;j++,y1++){
                    int px;
                    if(x1>=0 && x1<=img.getWidth()){
                        px=x1-r2.x;
                    }else{
                        px=(int)(x1<0?-x1:(2*img.getWidth()-r.getMaxX()-r2.x));
                    }
                    if(y1>=0 && y1<=img.getWidth()){
                        px+=(y1-r2.y)*r2.width;
                    }else{
                        px+=(y1<0?-y1:(2*img.getHeight()-r.getMaxY()-r2.y))*r2.width;
                    }
                    int alpha=withAlphachan?(pixels[px++] & 0xff):0;
                    int blue=(pixels[px++] & 0xff),
                        green=(pixels[px++] & 0xff),
                        red=(pixels[px++] & 0xff);
                    if (channel==CHANNEL_BLUE)
                        res+=(int)(blue*data[i][j]);
                    if (channel==CHANNEL_GREEN)
                        res+=((int)(green*data[i][j]));
                    if (channel==CHANNEL_RED)
                        res+=((int)(red*data[i][j]));
                }
            
        }else{
            pixels=((DataBufferByte) img.getData(r).getDataBuffer()).getData();
            for(int i=0,px=0;i<data.length;i++)
                for(int j=0;j<data[i].length;j++){
                    int alpha=withAlphachan?(pixels[px++] & 0xff):0;
                    int blue=(pixels[px++] & 0xff),
                        green=(pixels[px++] & 0xff),
                        red=(pixels[px++] & 0xff);
                    if (channel==CHANNEL_BLUE)
                        res+=(int)(blue*data[i][j]);
                    if (channel==CHANNEL_GREEN)
                        res+=((int)(green*data[i][j]));
                    if (channel==CHANNEL_RED)
                        res+=((int)(red*data[i][j]));
                }
        }
        return res;
    }
    public int[][] applyFilter(BufferedImage img,int channel)
    {
        int[][] res=new int[img.getWidth()][img.getHeight()];
        for(int x=0;x<img.getWidth();x++)
            for(int y=0;y<img.getHeight();y++){
                int ppval=applyFilter(img, x, y,channel);
                res[x][y]=ppval;
            }
        
        return res;
    }
    public BufferedImage applyFilter(BufferedImage img) throws ArrayIndexOutOfBoundsException
    {
        int x,y;
        
        BufferedImage outImage=new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        for(x=0;x<img.getWidth();x++)
            for(y=0;y<img.getHeight();y++){
                outImage.setRGB(x, y, 
                        applyFilter(img, x, y)
                        );
            }
        return outImage;
    }
    
    @Override
    public String toString()
    {
        int i,j;
        StringBuilder sb=new StringBuilder();
        
        for(i=0;i<data.length;i++){
            for(j=0;j<data[i].length;j++){
                sb.append(String.format("%.3f ", data[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
}
