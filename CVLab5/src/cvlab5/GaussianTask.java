/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvlab5;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 *
 * @author andrei
 */
public class GaussianTask {
    static void doTask() throws IOException{
        File file=Lab5Helper.getImageFile();
        
        GaussianSettings gs=new GaussianSettings();
        GaussianFilter g;
        
        g=new GaussianFilter(gs.width, 1);
        
        BufferedImage image=ImageIO.read(file);              
        BufferedImage image_out=g.applyFilter(image);
        File outfile=new File(Lab5Helper.getNewFileName(file,"gaussian"));
        ImageIO.write(image_out,"jpg",  outfile);
        
        System.out.println(String.format("Done - %s",outfile.getAbsoluteFile()));
       
    }
}

class GaussianSettings{
    int width;
    double sigma;
    public GaussianSettings()
    {
        System.out.print("Enter the gaussian kernel radius: ");
        width=new Scanner(System.in).nextInt();

        System.out.print("Enter the gaussian sigma: ");
        sigma=new Scanner(System.in).nextFloat();
    }
}
