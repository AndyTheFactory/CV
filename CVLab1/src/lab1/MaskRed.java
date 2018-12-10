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
public class MaskRed {
    double[][] mask;
    long rlen,blen,glen;
    public MaskRed(File maskFile) throws IOException
    {
        this(ImageIO.read(maskFile));
    }
    public MaskRed(BufferedImage maskImage){
        rlen=0;
        blen=0;
        glen=0;
        mask=new double[maskImage.getWidth()][maskImage.getHeight()];
        for(int i=0;i<mask.length;i++)
            for(int j=0;j<mask[i].length;j++){
                mask[i][j]=maskImage.getRGB(i, j);
                int bmask=((int)mask[i][j]) & 0xff,
                    gmask=((int)mask[i][j]>>8) & 0xff,
                    rmask=((int)mask[i][j]>>16) & 0xff;
               blen+=bmask*bmask; 
               rlen+=rmask*rmask; 
               glen+=gmask*gmask; 
            }
        blen=(long)Math.sqrt(blen);
        glen=(long)Math.sqrt(glen);
        rlen=(long)Math.sqrt(rlen);
        
    }
    public double applyMask(BufferedImage img,int x,int y){
        if(x>=img.getWidth() || y>=img.getHeight())
            return 0;
        
        boolean withAlphachan=img.getAlphaRaster()!=null;        
        int rwidth=(x+mask.length>img.getWidth())?(img.getWidth()-x):mask.length,
            rheight=(y+mask[0].length>img.getHeight())?(img.getHeight()-y):mask[0].length;
        
        byte[] pixels=((DataBufferByte) img.getData(new Rectangle(x,y,rwidth,rheight)).getDataBuffer()).getData();

        long resb=0,resg=0,resr=0;
        long rslen=0,bslen=0,gslen=0;
        for(int i=0,mx=0,my=0;i<pixels.length;mx++){
            if(mx>=mask.length){
                mx=0;
                my++;
            }
            int alpha=withAlphachan?(pixels[i++] & 0xff):0;
            int blue=(pixels[i++] & 0xff),
                green=(pixels[i++] & 0xff),
                red=(pixels[i++] & 0xff);
            int bmask=((int)mask[mx][my]) & 0xff,
                gmask=((int)mask[mx][my]>>8) & 0xff,
                rmask=((int)mask[mx][my]>>16) & 0xff;
            
            resb+=(blue*bmask);
            resg+=(green*gmask);
            resr+=(red*rmask);
            
            rslen+=red*red;
            bslen+=blue*blue;
            gslen+=green*green;
            
        }
        
        rslen=(long)Math.sqrt(rslen);
        bslen=(long)Math.sqrt(bslen);
        gslen=(long)Math.sqrt(gslen);

        resb=resb/(bslen*blen);
        resg=resg/(gslen*glen);
        resr=resr/(rslen*rlen);
        
        return (resb & 0xff)+(resg & 0xff)<<8+(resr & 0xff)<<16;
    }
    public double applyMask2(BufferedImage img,int x,int y){
        if(x>=img.getWidth() || y>=img.getHeight())
            return 0;
        
        boolean withAlphachan=img.getAlphaRaster()!=null;        
        int rwidth=(x+mask.length>img.getWidth())?(img.getWidth()-x):mask.length,
            rheight=(y+mask[0].length>img.getHeight())?(img.getHeight()-y):mask[0].length;
        
        byte[] pixels=((DataBufferByte) img.getData(new Rectangle(x,y,rwidth,rheight)).getDataBuffer()).getData();

        long resb=0,resg=0,resr=0;
        
        for(int i=0,mx=0,my=0;i<pixels.length;mx++){
            if(mx>=mask.length){
                mx=0;
                my++;
            }
            int alpha=withAlphachan?(pixels[i++] & 0xff):0;
            int blue=(pixels[i++] & 0xff),
                green=(pixels[i++] & 0xff),
                red=(pixels[i++] & 0xff);
            int bmask=((int)mask[mx][my]) & 0xff,
                gmask=((int)mask[mx][my]>>8) & 0xff,
                rmask=((int)mask[mx][my]>>16) & 0xff;
            
            resb+=Math.abs(blue-bmask);
            resg+=Math.abs(green-gmask);
            resr+=Math.abs(red-rmask);
            
            
        }
        resb=255- resb/pixels.length;
        resg=255- resg/pixels.length;
        resr=255- resr/pixels.length;
        
        return (resb & 0xff)+(resg & 0xff)<<8+(resr & 0xff)<<16;
    }
}
