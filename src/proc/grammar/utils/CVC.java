package proc.grammar.utils;

import java.util.Random;

public class CVC
{

    public static final String vowels = "aeiou";
    public static final String cons = "bcdfghjklmnpqrstvwxyz";
    
    public static String getCVC(Random rand, int len)
    {
        boolean v = rand.nextBoolean();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
        {
            if (v)
                sb.append(vowels.charAt(rand.nextInt(vowels.length())));
            else
                sb.append(cons.charAt(rand.nextInt(cons.length())));
            v = !v;
        }
        return sb.toString();

    }
            
    public static String getFancyName(Random rand)
    {
        int choice = rand.nextInt(2); 
        switch(choice)
        {
        case 0:
        return capFirst(getCVC(rand, 4+rand.nextInt(3)))+" "
                    +capFirst(getCVC(rand, 4+rand.nextInt(5)));
        case 1:
            return capFirst(getCVC(rand, 4+rand.nextInt(3)))+" "
                        +getCVC(rand, 1)+"'"
                        +capFirst(getCVC(rand, 4+rand.nextInt(5)));
        default:
            return "TODO";    
        }
    }
    
    public static String capFirst(String s)
    {
        return s.substring(0, 1).toUpperCase()+s.substring(1);
    }


}
