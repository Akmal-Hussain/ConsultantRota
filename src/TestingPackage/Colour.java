/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestingPackage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dakhussain
 */
public final class Colour {
    private final static List<Integer> list = new ArrayList<>();

    Colour(){    
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);
            
    
}

    public static List<Integer> getList() {
        return list;
    }
    
}
