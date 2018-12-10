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
public class SobelFilterY extends ImageFilter{
    public SobelFilterY(){
        data=new double[][]{
            {1,2,1},
            {0,0,0},
            {-1,-2,-1}
            };
        radius=2;
        
    }
}
