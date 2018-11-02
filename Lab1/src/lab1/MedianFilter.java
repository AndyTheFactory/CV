/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author andrei
 */
public class MedianFilter extends ImageFilter
{   
    public MedianFilter(int radius)
    {
        this.radius=radius;
    }
    private int getMedianValue(ArrayList<Integer> arr){
        
        if (arr.size()<=0) return 0;
        
        Collections.sort(arr);
        
        
        if(arr.size() % 2==0){
            int medianPos=arr.size()/2;
            return (int)((arr.get(medianPos)+arr.get(medianPos+1))/2);
        }else{
            int medianPos=(arr.size()-1)/2;
            return arr.get(medianPos);
        }
        
        
    }
    @Override
    public int applyFilter(BufferedImage img,int x, int y){
        byte[] pixels;
        ArrayList<Integer> rarr=new ArrayList<>(),garr=new ArrayList<>(),barr=new ArrayList<>();
        int res=0;
        Rectangle r=new Rectangle(x-radius+1, y-radius+1, 2*radius-1, 2*radius-1);
        boolean withAlphachan=img.getAlphaRaster()!=null;

        
        if(r.x<0 || r.y<0 || r.getMaxX()>=img.getWidth() || r.getMaxY()>img.getHeight()){
            //Edge stuff
            int deltax=(int)(r.x<0?r.x:img.getWidth()-r.getMaxX());
            deltax=(deltax>0)?0:deltax;
            int deltay=(int)(r.y<0?r.y:img.getHeight()-r.getMaxY());
            deltay=(deltay>0)?0:deltay;
            
            r.setBounds(
                    Integer.max(r.x, 0),
                    Integer.max(r.y, 0),
                    r.width+deltax,
                    r.height+deltay
            );
            
        }
        pixels=((DataBufferByte) img.getData(r).getDataBuffer()).getData();
        for(int px=0;px<pixels.length;)
        {
                int alpha=withAlphachan?(pixels[px++] & 0xff):0;
                
                int blue=(pixels[px++] & 0xff),
                    green=(pixels[px++] & 0xff),
                    red=(pixels[px++] & 0xff);
                rarr.add(red);
                garr.add(green);
                barr.add(blue);
        }
        
        res+=(int)(getMedianValue(barr));
        res+=(int)(getMedianValue(garr))<<8;
        res+=(int)(getMedianValue(rarr))<<16;
        
        
        return res;
        
    }
    @Override
    public int applyFilter(BufferedImage img,int x, int y,int channel){
        int res=applyFilter(img, x, y);
        switch(channel){
            default:
            case CHANNEL_BLUE:
                res&=0xff;
                break;
            case CHANNEL_GREEN:
                res=res>>8 &0xff;
                break;
            case CHANNEL_RED:
                res=res>>16 &0xff;
                break;
                
        }
        return res;
    }
}
