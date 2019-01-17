/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvlab5;

import static cvlab5.Ransac.ANGLE_TOLERANCE;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
        
        
        Ransac rans=new Ransac(100, 5, 20,50);
        
        File file=new File("images\\Testimg2_edge2_connected.jpg");
        
        BufferedImage image=ImageIO.read(file);              
        rans.doRansac(image);

        File file2=new File("images\\Testimg2.jpg");
        BufferedImage image2=ImageIO.read(file2);              
        rans.drawLines(image2);

        
        Graphics2D g2d = image2.createGraphics();
        g2d.setColor(Color.yellow);
        g2d.setPaint(Color.yellow);
        BasicStroke bs = new BasicStroke(5);
        g2d.setStroke(bs);    
        
        for(Rectangle r:rans.findRectangles()){
            g2d.drawRect((int)r.getMinX(),(int)r.getMaxY(), (int)r.getWidth(), (int)r.getHeight());
        }

        Lab5Helper.writeImageFile(image2, file, "rans1");
        
        double angle=Ransac.getAngle(
                new Line2D.Double(3.68,19.74,15.02,4.3),
                new Line2D.Double(16.94,4.62,23.06,20.8)
        );
        System.out.println(angle);
                
                
      
    }
    
}
