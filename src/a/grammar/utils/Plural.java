package a.grammar.utils;

import java.util.HashSet;
import java.util.Set;

public class Plural
{

    static final String vowels = "aeiou";
    static final Set<String> es2 = new HashSet<>();
    static final Set<String> es1 = new HashSet<>();
    static
    {
         es2.add("ch");
         es2.add("sh");
         
         es1.add("s");
         es1.add("x");
         es1.add("z");
    }
    // TODO user created special cases
    public String plural(String word1)
    {
        String word = word1;//.toLowerCase();
        if (word == null || word.length() == 0)
            return "";
        if (word.length() == 1)
            return word1+"s";
        
        int l = word.length();
        char c2 = word.charAt(l - 1);
        char c1 = word.charAt(l - 2);
        String end = ""+c1+""+c2;
        
//        System.out.println("END "+end);
        
        if (es1.contains(""+c2) || es2.contains(end)
                || (c2 == 'o' && !isVowel(c1)))
            return word1+"es";
        
        if (c2 == 'y')
        {
            if (isVowel(c1))
                return word1+"s";
            else
                return word1.substring(0, l-1)+"ies";
        }
        
        if (c2 == 'f')
            return word1.substring(0, l-1)+"ves";
        if (c2 == 'e' && c1 == 'f')
            return word1.substring(0, l-2)+"ves";
        
        return word1+"s";
    }
    
    public static boolean isVowel(char c)
    {
        if (vowels.indexOf(Character.toLowerCase(c)) < 0)
            return false;
        return true;
    }
    
    public static void main(String[] args)
    {
        String[] test = new String[] {
          "boy", "book", "box", "bus", "prize", "key", "baby", "country", "thief", "elfe", "kangaroo", "hero"      
        };
        
        Plural p = new Plural();
        for (String t : test)
            System.out.println(t+" "+p.plural(t));
    }
}
