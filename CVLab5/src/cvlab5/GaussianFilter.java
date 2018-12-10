/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvlab5;

/**
 *
 * @author andrei
 */
public class GaussianFilter extends ImageFilter{
    double sigma;
    
    /**
     *
     * @param radius Needs to be 
     * @param sigma 
     */
    public GaussianFilter(int radius,double sigma) {
        int i,j;
        data=new double[radius*2-1][radius*2-1];
        this.sigma=sigma;
        this.radius=radius;
        
        for(i=0;i<radius;i++){
            for(j=0;j<radius;j++){
                double v=Math.exp(-(i*i+j*j)/(2*sigma*sigma));
                v/=2*Math.PI*sigma*sigma;
                this.data[radius+i-1][radius+j-1]=v;
                this.data[radius-i-1][radius+j-1]=v;
                this.data[radius+i-1][radius-j-1]=v;
                this.data[radius-i-1][radius-j-1]=v;

            }        
        }
    }
}
