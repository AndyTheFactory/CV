/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author Dell
 */
public class Mask {
    double[][] mask;
    public Mask(File maskFile) throws IOException
    {
        this(ImageIO.read(maskFile));
    }
    public Mask(BufferedImage maskImage){
        mask=new double[maskImage.getWidth()][maskImage.getHeight()];
        for(int i=0;i<mask.length;i++)
            for(int j=0;j<mask[i].length;j++)
                mask[i][j]=maskImage.getRGB(i, j);
    }
    public double applyMask(BufferedImage img,int x,int y){
        if(x>=img.getWidth() || y>=img.getHeight())
            return 0;
        
        boolean withAlphachan=img.getAlphaRaster()!=null;        
        int rwidth=(x+mask.length>img.getWidth())?(img.getWidth()-x):mask.length,
            rheight=(y+mask[0].length>img.getHeight())?(img.getHeight()-y):mask[0].length;
        
        byte[] pixels=((DataBufferByte) img.getData(new Rectangle(x,y,rwidth,rheight)).getDataBuffer()).getData();

        int res=0;
        
        for(int i=0,mx=0,my=0;i<pixels.length;mx++){
            if(mx>=mask.length){
                mx=0;
                my++;
            }
            int alpha=withAlphachan?(pixels[i++] & 0xff):0;
            int blue=(pixels[i++] & 0xff),
                green=(pixels[i++] & 0xff),
                red=(pixels[i++] & 0xff);
            res+=alpha<<24;
            res+=(int)(blue*mask[mx][my]);
            res+=((int)(green*mask[mx][my]))<<8;
            res+=((int)(red*mask[mx][my]))<<16;
            
            
        }
        return res;
    }
}
