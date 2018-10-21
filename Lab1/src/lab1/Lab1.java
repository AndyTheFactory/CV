/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.util.Scanner;

/**
 * @author andrei
 */
public class Lab1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        System.out.println("1. Task - Gaussian Filter");
        System.out.println("2. Task - Edge detector");
        System.out.println("3. Task - Stop sign filter");
        
        System.out.println("> ");
        
        int i=new Scanner(System.in).nextInt();
        
        switch(i){
            case 1: GaussianTask.doTask();
                break;
            case 2: CannyTask.doTask();                
                break;
            case 3:
                System.out.println("not implemented");
                break;
        }
        
        System.out.println("Bye bye!");
    }
    
}
