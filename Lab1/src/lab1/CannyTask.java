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
public class CannyTask {
    public static void doTask() throws IOException
    {
        File file=Lab1Helper.getImageFile();
        
        GaussianFilter g;
        
        g=new GaussianFilter(3, 1);
        
        System.out.println("Applying Gaussian...");
        BufferedImage image=ImageIO.read(file);              
        BufferedImage image_out=g.applyFilter(image);
        File outfile=new File(Lab1Helper.getNewFileName(file,"gaussian"));
        ImageIO.write(image_out,"jpg",  outfile);
        
        System.out.println(String.format("Gaussian done - %s",outfile.getAbsoluteFile()));
        
        
    }
    
}

class Gradient{
    int[][] direction;
    float[][] gradient;
    public Gradient(int width,int height){
        direction = new int[width][height];
        gradient= new float[width][height];
        
    }
    public void doGradient(BufferedImage img){
        
    }
}
