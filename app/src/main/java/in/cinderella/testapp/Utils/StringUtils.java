package in.cinderella.testapp.Utils;

public class StringUtils {
    public static String extractFirstName(String name){
        String firstName= "";
        if(name.split("\\w+").length>1){
            firstName = name.substring(0, name.lastIndexOf(' '));
        }
        else{
            firstName = name;
        }
        return firstName;
    }
}
