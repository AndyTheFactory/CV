/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
/**
 *
 * @author Dell
 */
public class Mask {
    double[][] mask;
    public Mask(BufferedImage maskImage){
        mask=new double[maskImage.getWidth()][maskImage.getHeight()];
        for(int i=0;i<mask.length;i++)
            for(int j=0;j<mask[i].length;j++)
                mask[i][j]=maskImage.getRGB(i, j);
    }
    public double applyMask(BufferedImage img,int x,int y){
        if(x>=img.getWidth() || y>=img.getHeight())
            return 0;
        
        byte[] pixels=((DataBufferByte) img.getDatane(new Rectangle(x,y,mask.length,mask[0].length)).getDataBuffer()).getData();
       
        double[][] imgrect=new deouble[mask.length][mask[0].length];
        
        for(i=0;pixels.length)
        
    }
}
