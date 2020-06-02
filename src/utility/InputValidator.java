package utility;

import javafx.scene.control.TextField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class InputValidator {

    private final static String DOUBLE_PATTERN = "[0-9]+(\\.){0,1}[0-9]*";
    private final static String INTEGER_PATTERN = "\\d+";

    public static boolean isDouble(TextField textField){
        if (Pattern.matches(DOUBLE_PATTERN,textField.getText()) &&  Double.parseDouble(textField.getText()) > 0)
            return true;
        return false;
    }

    public static boolean isInteger(TextField textField){
        if (Pattern.matches(INTEGER_PATTERN,textField.getText()) &&  Integer.parseInt(textField.getText()) > 0)
            return true;
        return false;
    }

    public static boolean isDateValid(String textfield){
        SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
        sdfrmt.setLenient(false);
        try
        {
            Date javaDate = sdfrmt.parse(textfield);
        }
        // Date format is invalid
        catch (ParseException e)
        {
            System.out.println("Invalid date format Please try again !(DD/MM/YYYY)");
            return false;
        }
        return true;
    }

    public static boolean isDouble(String string){
        if (Pattern.matches(DOUBLE_PATTERN,string) &&  Double.parseDouble(string) > 0)
            return true;
        return false;
    }

    public static boolean isInteger(String string){
        if (Pattern.matches(INTEGER_PATTERN,string) &&  Integer.parseInt(string) > 0)
            return true;
        return false;
    }

}
