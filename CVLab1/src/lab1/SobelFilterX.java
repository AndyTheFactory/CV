/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

/**
 *
 * @author andrei
 */
public class SobelFilterX  extends ImageFilter{
    public SobelFilterX(){
        data=new double[][]{
            {-1,0,1},
            {-2,0,2},
            {-1,0,1}
            };
        radius=2;
    }
}
