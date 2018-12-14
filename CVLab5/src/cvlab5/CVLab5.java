/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvlab5;

import static cvlab5.CannyTask.file;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author andrei
 */
public class CVLab5 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException{
        // TODO code application logic here
        
        //CannyTask.doTask();                
        
        
        Ransac rans=new Ransac(30, 5, 0.03);
        
        File file=new File("d:\\Work\\Java\\CV\\CVLab5\\images\\Testimg1_edge1.jpg");
        
        BufferedImage image=ImageIO.read(file);              
        rans.doRansac(image);
        rans.drawLines(image);

        Lab5Helper.writeImageFile(image, file, "rans1");
        
        

      
    }
    
}
