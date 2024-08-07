/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sc2.compiler;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author yangjiandong
 */
public class Util {
        
    public static String getBaseName(String name) {
        int pos = name.lastIndexOf(".");
        if (pos == -1) {
            return name;
        }
        String basename = name.substring(0, pos);
        return basename;
    }
    
    public static ArrayList<File> listFile(File file) {
        ArrayList<File> list = new ArrayList<File>();
        addFile(file, list, ".sc");
        return list;
    }
    
    private static void addFile(File file, ArrayList<File> sources, String extName) {
        if (!file.isDirectory()) {
            if (file.getName().endsWith(extName)) {
                sources.add(file);
            }
        }
        else {
            File[] list = file.listFiles();
            for (File file2 : list) {
                addFile(file2, sources, extName);
            }
        }
    }
}
