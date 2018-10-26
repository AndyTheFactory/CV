/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 *
 * @author andrei
 */
public class Lab1Helper {
    public static File getImageFile()
    {
        String filepath=Paths.get(".\\images").toAbsolutePath().normalize().toString();
        File[] files=new File(filepath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpg")&&(!name.contains("_"));
                }
            }
        );
        System.out.println("Pick file: ");
        int i=0;
        for(File file:files){            
            System.out.println(String.format("%d. %s", ++i,file.getName()));
        }
        
        System.out.print("> ");
        
        i=new Scanner(System.in).nextInt();

        return files[i-1];
    }
    public static File getMaskFile()
    {
        String filepath=Paths.get(".\\images").toAbsolutePath().normalize().toString();
        File[] files=new File(filepath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpg")&&(name.contains("_filter"));
                }
            }
        );
        System.out.println("Pick file: ");
        int i=0;
        for(File file:files){            
            System.out.println(String.format("%d. %s", ++i,file.getName()));
        }
        
        System.out.print("> ");
        
        i=new Scanner(System.in).nextInt();

        return files[i-1];
    }
    public static String getNewFileName(File file, String suffix)
    {
        String newfilename=file.getAbsolutePath();
        int i=newfilename.lastIndexOf(".");
        return newfilename.substring(0, i)+"_"+suffix+newfilename.substring(i);
    }
    public static String getNewFileName(String fileName, String suffix)
    {
        int i=fileName.lastIndexOf(".");
        return fileName.substring(0, i)+"_"+suffix+fileName.substring(i);
    }
    public static void writeImageFile(BufferedImage img,String fileName,String suffix)
    {
        try{
            ImageIO.write(img,"jpg", new File(Lab1Helper.getNewFileName(fileName,suffix)));
        }catch(IOException E){
            System.out.println("Hauuuuu! nu pot scrie "+Lab1Helper.getNewFileName(fileName,suffix));
        }
    }
    public static void writeImageFile(BufferedImage img,File file,String suffix)
    {
        try{
            ImageIO.write(img,"jpg", new File(Lab1Helper.getNewFileName(file,suffix)));
        }catch(IOException E){
            System.out.println("Hauuuuu! nu pot scrie "+Lab1Helper.getNewFileName(file,suffix));
        }
    }
            
}
