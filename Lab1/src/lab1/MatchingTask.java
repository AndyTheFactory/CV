/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.awt.image.BufferedImage;
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
    public static Mask mask;
    
    public static void doTask() throws IOException
    {
        file=Lab1Helper.getImageFile();
        maskfile=Lab1Helper.getMaskFile();
        
        mask=new Mask(maskfile);
        
        BufferedImage imgmask=makeHeatMap(ImageIO.read(file));
        
        Lab1Helper.writeImageFile(imgmask, file, "_mask1");
    }
    private static BufferedImage makeHeatMap(BufferedImage src)
    {
        int w=src.getWidth(),h=src.getHeight();
        BufferedImage res=new BufferedImage(w, h, src.getType());
        
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++){
                res.setRGB(i, j,(int) mask.applyMask(src, i, j));
            }
        return res;
    }
}
