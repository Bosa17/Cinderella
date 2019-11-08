package com.getcinderella.app.Utils;

public class StringUtils {
    public static String extractFirstName(String name){
        String firstName= "";
        if(name.split("\\w+").length>1){
            firstName = name.substring(0, name.indexOf(' '));
        }
        else{
            firstName = name;
        }
        return firstName;
    }

    public static String removeFirstChar(String str){
        return str.substring(1);
    }

    public static boolean isName(String name){
        return name.charAt(0)==('0');
    }

    public static String[] extractRoomIdandParticipID(String comboId){
        return comboId.split(" ");
    }
}
