/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author andrei
 */
public class MatchingTask
{
    public static File file;
    public static void doTask() throws IOException
    {
        file=Lab1Helper.getImageFile();
        file=Lab1Helper.getMaskFile();
        
    }

}
