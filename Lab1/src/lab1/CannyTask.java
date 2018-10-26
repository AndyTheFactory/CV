/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import javax.imageio.ImageIO;

/**
 *
 * @author andrei
 */
public class CannyTask {
    public static final int UPPER_THRESHOLD=60;
    public static final int LOWER_THRESHOLD=30;
    public static File file;
    public static void doTask() throws IOException
    {
        file=Lab1Helper.getImageFile();

        GaussianFilter g;
        
        g=new GaussianFilter(3, 1);
        
        System.out.println("Applying Gaussian...");
        BufferedImage image=ImageIO.read(file);              
        BufferedImage image_out=g.applyFilter(image);
        File outfile=new File(Lab1Helper.getNewFileName(file,"gaussian"));
        ImageIO.write(image_out,"jpg",  outfile);
        
        System.out.println(String.format("Gaussian done - %s",outfile.getAbsoluteFile()));
        
        Gradient grad=new Gradient(image_out.getWidth(), image_out.getHeight());
        grad.doGradient(image_out);
    }
    
}

class Gradient{
    public static final int STRONG_EDGE=1;
    public static final int WEAK_EDGE=2;
    public static final int NO_EDGE=0;
    
    public static final int EDGE_THRESHOLD1=100;
    public static final int EDGE_THRESHOLD2=200;
    
    int[][] direction;
    double[][] gradient;
    public Gradient(int width,int height){
        direction = new int[width][height];
        gradient= new double[width][height];
        
    }
    public void doGradient(BufferedImage img){
        int[][] gradientX, gradientY;
        SobelFilterX gX=new SobelFilterX();
        SobelFilterY gY=new SobelFilterY();
        boolean hasAlpha=img.getAlphaRaster()!=null;
        //do grayscale
        for(int i=0;i<img.getWidth();i++)
            for (int j=0;j<img.getHeight();j++)
            {
                int p = img.getRGB(i,j);
          
                int a = hasAlpha?((p>>24)&0xff):0;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                int avg=(r+g+b)/3;
                
                p =  (a<<24) | (avg<<16) | (avg<<8) | avg;
                img.setRGB(i, j, p);
            }
        gradientX=gX.applyFilter(img, ImageFilter.CHANNEL_RED);
        gradientY=gY.applyFilter(img, ImageFilter.CHANNEL_RED);

        BufferedImage gradientImgX=gX.applyFilter(img);
        BufferedImage gradientImgY=gY.applyFilter(img);
        
        Lab1Helper.writeImageFile(img, CannyTask.file, "gray");
        Lab1Helper.writeImageFile(gradientImgX, CannyTask.file, "gradientX");
        Lab1Helper.writeImageFile(gradientImgY, CannyTask.file, "gradientY");

        
        for(int i=0;i<img.getWidth();i++)
            for (int j=0;j<img.getHeight();j++)
            {
                int GradX=gradientX[i][j];
                int GradY=gradientY[i][j];
                gradient[i][j]=Math.sqrt(
                    GradX*GradX+GradY*GradY
                );
                
                double dirAngle=(Math.atan2(GradX,GradY)/Math.PI)*180;
                direction[i][j]=angleToDir(dirAngle);
                
                
            }
        BufferedImage edge1=findEdges();
        
        Lab1Helper.writeImageFile(edge1, CannyTask.file, "edge_raw");
        
        BufferedImage edge2=new BufferedImage(edge1.getWidth(), edge1.getHeight(), edge1.getType());
        edge2.setData(edge1.getData());
        
        suppressNonMaxEdgess(edge1);

        int[][] edges1=findWeakEdges();

        Lab1Helper.writeImageFile(edge1, CannyTask.file, "edge1");

        suppressNonMaxGradient();
        int[][] edges2=findWeakEdges();

        int w=gradient.length;
        int h=gradient[0].length;
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
                if(gradient[i][j]==0)
                    edge2.setRGB(i,j,0);

        Lab1Helper.writeImageFile(edge2, CannyTask.file, "edge2");
        
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
                if(edges1[i][j]==Gradient.STRONG_EDGE)
                    edge2.setRGB(i,j,Color.WHITE.getRGB());
                else
                    edge2.setRGB(i,j,0);
        
        Lab1Helper.writeImageFile(edge2, CannyTask.file, "edge1_connected");
        
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
                if(edges2[i][j]==Gradient.STRONG_EDGE)
                    edge2.setRGB(i,j,Color.WHITE.getRGB());
                else
                    edge2.setRGB(i,j,0);
        
        Lab1Helper.writeImageFile(edge2, CannyTask.file, "edge2_connected");
    }
    
    public static int angleToDir(double Angle){		
        
          
        if ( ( (Angle < 22.5) && (Angle > -22.5) ) || (Angle > 157.5) || (Angle < -157.5) )
		return 0;
        if ( ( (Angle > 22.5) && (Angle < 67.5) ) || ( (Angle < -112.5) && (Angle > -157.5) ) )
                return 45;
        if ( ( (Angle > 67.5) && (Angle < 112.5) ) || ( (Angle < -67.5) && (Angle > -112.5) ) )
                return 90;
        if ( ( (Angle > 112.5) && (Angle < 157.5) ) || ( (Angle < -22.5) && (Angle > -67.5) ) )
               return 135;
				
        return 0;
        
    }
    private BufferedImage findEdges()
    {
        int w=gradient.length;
        int h=gradient[0].length;
        
        BufferedImage edger = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
            {
                if(gradient[i][j]>CannyTask.UPPER_THRESHOLD){
                    switch(direction[i][j]){
                        case 0:
                        case 45:
                        case 90:
                        case 135:
                            followEdge(i,j,direction[i][j],edger);
                            break;
                        default:
                            edger.setRGB(i, j, 0);                            
                    }
                }else{
                    edger.setRGB(i, j, 0);
                }
            }
        return edger;
    }
    private void followEdge(int x, int y, int dir, BufferedImage edger)    
    {
        int deltax=(dir==0)?0:1;
        int deltay=(dir==90)?0:1*((int)Math.signum(134-dir)); // -1 for 135
        
        int nX=x+deltax,nY=y+deltay;
        boolean boundsOk=(nX>=0)&&(nX<gradient.length)&&
                    (nY>0)&&(nY<gradient[0].length);
        while (boundsOk && direction[nX][nY]==dir && gradient[nX][nY]>CannyTask.LOWER_THRESHOLD){
            edger.setRGB(nX, nY, Color.WHITE.getRGB());
            nX+=deltax;
            nY+=deltay;
            boundsOk=(nX>=0)&&(nX<gradient.length)&&
                        (nY>0)&&(nY<gradient[0].length);
            
        }
    }
     private void suppressNonMaxEdgess(BufferedImage edger)
    {
        int w=gradient.length;
        int h=gradient[0].length;
        
        
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
            {
                if(edger.getRGB(i, j)==Color.WHITE.getRGB()){
                    switch(direction[i][j]){
                        case 0:
                        case 45:
                        case 90:
                        case 135:
                            supressNonMax(i,j,direction[i][j],edger);
                            break;
                        default:
                    }
                }
            }
    }
    private void supressNonMax(int x, int y, int dir, BufferedImage edger)
    {
        int deltax=(dir==90)?0:1;
        int deltay=(dir==0)?0:(dir==45?-1:1); // -1 for 45
        class Pixel{
            int x,y;
            double gradient;
            public Pixel(int x,int y,double gradient){
                this.x=x;this.y=y;this.gradient=gradient;
            }
        }
        ArrayList<Pixel> pxlist=new ArrayList<>();
        int nX=x+deltax,nY=y+deltay;
        boolean boundsOk=(nX>=0)&&(nX<gradient.length)&&
                    (nY>0)&&(nY<gradient[0].length);
        
         while (boundsOk && direction[nX][nY]==dir && edger.getRGB(nX, nY)==Color.WHITE.getRGB()){
            pxlist.add(new Pixel(nX,nY,gradient[nX][nY]));
            nX+=deltax;
            nY+=deltay;
            boundsOk=(nX>=0)&&(nX<gradient.length)&&
                        (nY>0)&&(nY<gradient[0].length);
            
        }
        deltax*=-1;
        deltay*=-1;
        nX=x+deltax;
        nY=y+deltay;
        boundsOk=(nX>=0)&&(nX<gradient.length)&&
                    (nY>0)&&(nY<gradient[0].length);

        while (boundsOk && direction[nX][nY]==dir && edger.getRGB(nX, nY)==Color.WHITE.getRGB()){
            pxlist.add(new Pixel(nX,nY,gradient[nX][nY]));
            nX+=deltax;
            nY+=deltay;
            boundsOk=(nX>=0)&&(nX<gradient.length)&&
                        (nY>0)&&(nY<gradient[0].length);
            
        }
        double maxgrad=0;
        for(Pixel px:pxlist){
            maxgrad=px.gradient>maxgrad?px.gradient:maxgrad;
        }
        for(Pixel px:pxlist){
            if (maxgrad>=px.gradient){
                edger.setRGB(px.x, px.y, 0);
            }
        }
        
    }
    private void suppressNonMaxGradient()
    {
        int w=gradient.length;
        int h=gradient[0].length;
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
            {
                double g1,g2;
                if(gradient[i][j]!=0){
                    int deltax=(direction[i][j]==0)?0:1;
                    int deltay=(direction[i][j]==90)?0:1*((int)Math.signum(134-direction[i][j])); // -1 for 135
                    int nX=i+deltax,nY=j+deltay;
                    boolean boundsOk=(nX>=0)&&(nX<w)&&(nY>0)&&(nY<h);
                    g1=boundsOk?gradient[nX][nY]:0;
                    
                    nX=i-deltax;
                    nY=j-deltay;
                    boundsOk=(nX>=0)&&(nX<w)&&(nY>0)&&(nY<h);
                    g2=boundsOk?gradient[nX][nY]:0;
                    
                    if (g1>gradient[i][j] || g2>gradient[i][j]){
                        gradient[i][j]=0;//suppress non maxima
                    }
                }
            }
    }
    private int[][] findWeakEdges(){
        int w=gradient.length;
        int h=gradient[0].length;
        int deltas[][]={{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0}};
        int[][] edgy=new int[w][h];
        class Pixel{
            int x,y;
            public Pixel(int x,int y){this.x=x;this.y=y;}
        }
        ArrayDeque<Pixel> q=new ArrayDeque<>();
        
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
            {
                if (gradient[i][j]>EDGE_THRESHOLD2){
                    q.add(new Pixel(i,j));
                    edgy[i][j]=STRONG_EDGE;
                }
                while (q.size()>0){
                    Pixel pp=q.pop();
                    for(int k=0;k<deltas.length;k++){
                        int nX=i+deltas[k][0],nY=j+deltas[k][1];
                        boolean boundsOk=(nX>=0)&&(nX<w)&&(nY>0)&&(nY<h);
                        if (boundsOk && gradient[nX][nY]>EDGE_THRESHOLD1 && gradient[nX][nY]<=EDGE_THRESHOLD2 && edgy[nX][nY]!=STRONG_EDGE){
                            edgy[nX][nY]=STRONG_EDGE;
                            q.push(new Pixel(nX,nY));
                        }
                       
                    }
                }
                
            }
        return edgy;
    }
}
