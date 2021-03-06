package se.claremont.autotest.support;

import java.io.File;

/**
 * Static methods to transform strings
 *
 * Created by jordam on 2016-11-04.
 */
public class StringManagement {

    /**
     * Returns the same string, but beginning with a uppercase letter, and the rest of the characters in lowercase.
     *
     * @param instring The string to transform
     * @return Returns the string with initial uppercase letter, and trailing lowercase letters.
     */
    public static String firstUpperLetterTrailingLowerLetter(String instring){
        if(instring == null || instring.length() < 1) return "";
        String returnString;
        returnString = instring.substring(0,1).toUpperCase();
        if(instring.length() > 1){
            returnString += instring.substring(1).toLowerCase();
        }
        return returnString;

    }

    /**
     * Method naming should only consist of method name safe characters, and be formatted according to method naming conventions in java, and according to coding guidelines.
     *
     * @param instring The string to convert
     * @return Returns the converted string
     */
    public static String methodNameWithOnlySafeCharacters(String instring){
        if(instring == null || instring.length() < 1){
            return "";
        }
        instring = instring.trim();
        String returnString = "";

        for(String spaceDividedWord : instring.split(" ")){
            for(String dashDividedWord : spaceDividedWord.split("-")){
                for(String underscoreDividedWord : dashDividedWord.split("_")){
                    returnString += firstUpperLetterTrailingLowerLetter(underscoreDividedWord);
                }

            }
        }

        returnString = returnString.replaceAll("--", "-").
                replace(" ", "").
                replace(",", "").
                replace("–", "_").
                replace(".", "_").
                replace("%", "Proc").
                replace("&", "Ampersand").
                replace("$", "Dollar").
                replace("£", "Pound").
                replace("€", "Euro").
                replace("\\", "_").
                replace("\"", "").
                replace("'", "").
                replace("!", "").
                replace("?", "").
                replace("é", "e").
                replace("è", "e").
                replace("-", "_").
                replace("*", "_").
                replace("+", "Plus").
                replace("©", "Copyright").
                replace("å", "a").
                replace("ä", "a").
                replace("|", "_").
                replace("ö", "o").
                replace("Å", "A").
                replace("Ä", "A").
                replace("=", "").
                replace("@", "At").
                replace("/", "_").
                replace("(", "_").
                replace(")", "_").
                replace(";", "").
                replace("^", "_").
                replace(":", "").
                replace("__", "_").
                replace("Ö", "O");


        if(Character.isDigit(returnString.charAt(0))){
            returnString = "_" + returnString;
        } //Method names cannot start with digits
        returnString = returnString.substring(0,1).toLowerCase() + returnString.substring(1);
        return returnString;
    }

    /**
     * Since java enum elements are written in capital letters (considered being
     * constants) and enum values are parsed as string frequently this method
     * re-formats the enum values to initial capital letter and the rest of the
     * string in lower case - and underscore characters substituted with spaces.
     *
     * @param CAPITALIZED_STRING A string from an enum value
     * @return The modified string, in friendlier text format
     */
    public static String enumCapitalNameToFriendlyString(String CAPITALIZED_STRING){
        return CAPITALIZED_STRING.substring(0, 1).toUpperCase() +
                CAPITALIZED_STRING.substring(1).replace('_', ' ').toLowerCase();
    }

    /**
     * Converts a string to camelback notation (no spaces, initial uppercase
     * letter of each word), but with even the very first letter in uppercase.
     *
     * @param instring The string to convert
     * @return Returns a string with no spaces and uppercase initial letter in each word.
     */
    public static String stringToCapitalInitialCharacterForEachWordAndNoSpaces(String instring){
        StringBuilder stringBuilder = new StringBuilder();
        String[] words = instring.split(" ");
        for(String word : words){
            if(word.length() > 0){
                stringBuilder.append(word.trim().substring(0,1).toUpperCase());
                if(word.length() > 1){
                    stringBuilder.append(word.trim().substring(1).toLowerCase());
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Converts a file path string like 'C:\myFile.txt' to the 'file://C:/myFile.txt' format used on web
     *
     * @param filePath FilePath
     * @return Returns a web formatted file reference.
     */
    public static String filePathToHtmlSrc(String filePath){
        if(filePath == null) return null;
        if(!filePath.toLowerCase().startsWith("file://")) filePath = "file://" + filePath;
        return filePath.replace("\\", "/");
    }

    /**
     * Converts a file path string to the current OS preferred format.
     *
     * @param filePath The file path
     * @return The same file path, but converted.
     */
    public static String filePathToCurrentOsAdaptedFormat(String filePath){
        if(filePath == null) return null;
        if(filePath.startsWith("\\\\")) return filePath; //UNC format is universal
        if(filePath.toLowerCase().startsWith("file://")){
            filePath = filePath.substring(("file://").length());
        }
        return filePath.replace("\\", File.separator).replace("/", File.separator);
    }

    /**
     * Displaying HTML code on a HTML page requires a few character substitutions, performed by this method.
     *
     * @param htmlContent The HTML code that should be displayed as code on a HTML page.
     * @return Returns the converted string
     */
    public static String htmlContentToDisplayableHtmlCode(String htmlContent){
        return "<pre>" +
                SupportMethods.LF + SupportMethods.LF +
                htmlContent.replace("&", "&amp;").
                        replace("<", "&lt;").
                        replace(">", "&gt;") +
                SupportMethods.LF + SupportMethods.LF +
                "</pre>" +
                SupportMethods.LF;
    }
}
