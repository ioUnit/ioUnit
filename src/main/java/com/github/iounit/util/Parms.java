package com.github.iounit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parms {
    public static String deParam(final String trim) {
        final Pattern p = Pattern.compile("(.*?)\\$[{]([^}:]*)(?:[:]([^}]*))?}([^$]*)");
        final Matcher m = p.matcher(trim);
        final StringBuilder b = new StringBuilder();
        while(m.find()){
            b.append(m.group(1));
            if(System.getProperty(m.group(2))!=null){
                b.append(System.getProperty(m.group(2)));
            }else if(System.getenv(m.group(2))!=null){
                b.append(System.getenv(m.group(2)));
            }else if(m.group(3)!=null){
                b.append(m.group(3));
            }
            else{
                System.err.println(m.group(2)+ " env variable not defined");
            }
            b.append(m.group(4));
        }
        if(b.length()==0){
            b.append(trim);
        }
        return b.toString();
    }
}
