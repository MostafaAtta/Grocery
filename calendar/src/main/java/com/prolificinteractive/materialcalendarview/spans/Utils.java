package com.prolificinteractive.materialcalendarview.spans;

/**
 * Created by rohitg on 31-Oct-17.
 */

public class Utils {

    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }

        String number1 = new String(chars);
        return number1.replace("٫", ".");
    }
}
