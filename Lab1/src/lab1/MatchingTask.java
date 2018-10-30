/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author andrei
 */
public class MatchingTask
{
    public static File file;
    public static void doTask() throws IOException
    {
        file=Lab1Helper.getImageFile();
        file=Lab1Helper.getMaskFile();
        
    }
    private BufferedImage makeHeatMap(BufferedImage src,BufferedImage mask)
    {
        int w=src.getWidth(),h=src.getHeight();
        BufferedImage res=new BufferedImage(w, h, src.getType());
        
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++){
                res.setRGB(i, j, 0);
            }
        return res;
    }
}
