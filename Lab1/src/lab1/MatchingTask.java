/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author andrei
 */
public class MatchingTask
{
    public static File file;
    public static File maskfile;
    public static int THRESHOLD1=160;
    
    public static void doTask() throws IOException
    {
        file=Lab1Helper.getImageFile();
        maskfile=Lab1Helper.getMaskFile();
        
        
        BufferedImage imgmask=maskImage(ImageIO.read(file),500);       
        Lab1Helper.writeImageFile(imgmask, file, "mask1");
        
        MedianFilter median=new MedianFilter(5);
       
        BufferedImage imgmask_median=median.applyFilter(imgmask);
        Lab1Helper.writeImageFile(imgmask_median, file, "mask2");
        
        //imgmask=maskImage(ImageIO.read(file));       
        //Lab1Helper.writeImageFile(imgmask, file, "mask3");
        
        //imgmask_median=median.applyFilter(imgmask);
        //Lab1Helper.writeImageFile(imgmask_median, file, "mask4");
        

    }
    private static BufferedImage maskImage(BufferedImage src,int resize) throws IOException
    {
        int w=src.getWidth(),h=src.getHeight();
        Mask mask=new Mask(maskfile);

        BufferedImage res=new BufferedImage(w, h, src.getType());
        
        if (w>resize || h>resize){
            int ws=(w>h)?resize:(resize*w/h);
            int hs=(w>h)?(resize*h/w):resize;
            
            Image tmp=src.getScaledInstance(ws, hs, Image.SCALE_FAST);
            BufferedImage scaleimg = new BufferedImage(ws, hs , BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaleimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            BufferedImage tmpmask =ImageIO.read(maskfile);
            
            int maskw=ws*tmpmask.getWidth()/w,maskh=hs*tmpmask.getHeight()/h;
            tmp=tmpmask.getScaledInstance(maskw,maskh, Image.SCALE_FAST);
            
            BufferedImage scalemask = new BufferedImage(maskw, maskh , BufferedImage.TYPE_INT_ARGB);
            
            g2d = scalemask.createGraphics();
            g2d.drawImage(tmpmask, 0, 0, null);
            g2d.dispose();

            Mask mask2=new Mask(scalemask);
            
            for(int i=0;i<ws;i++)
                for(int j=0;j<hs;j++){
                    int msk=(int)mask.applyMask(src, i, j);
                    int blue=msk & 0xff, green=msk>>8 & 0xff, red=msk>>16 & 0xff;
                    if (blue>THRESHOLD1 && red>THRESHOLD1 && blue >THRESHOLD1 ){
                        Rectangle r=new Rectangle(
                            (int)i*w/ws,(int)j*h/hs,(int)w/ws,(int)h/hs
                        );
                        for(int x=r.x;x<r.x+r.width;x++)
                            for(int y=r.y;y<r.y+r.height;y++)
                                if (x<src.getWidth() && y<src.getHeight())
                                    res.setRGB(x, y,(int)mask.applyMask(src, x, y));
                    
                    }
                }
            
        }
        
        return res;        
    }
    private static BufferedImage maskImage(BufferedImage src) throws IOException
    {
        int w=src.getWidth(),h=src.getHeight();
        Mask mask=new Mask(maskfile);

        BufferedImage res=new BufferedImage(w, h, src.getType());
        
        for(int x=0;x<w;x++)
            for(int y=0;y<h;y++)
            {
                res.setRGB(x, y,(int)mask.applyMask(src, x, y));
            }

       
        return res;        
    }
    
    
}
