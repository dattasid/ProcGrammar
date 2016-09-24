package a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import a.Main.Gender;
import a.Main.Occupation;
import a.Main.RuleName;
import a.grammar.Context;

public class MyContext extends Context 
{
    Gender gender;
    Occupation occu;

    List<Jewelry> jewelery = new ArrayList<>();

    public int wealth;

    
    public MyContext()
    {
        super();

    }

    public MyContext addJewelry(RuleName jwlRN)
    {
        Jewelry j = new Jewelry();
        j.type = RNtoStr(jwlRN);
        jewelery.add(j);
        return this;
    }

    public String toString()
    {
        // return "Name is "+"TODO!!";

        return up1lowall(gender.pronoun1 + " is wearing " + list2stringwCount(jewelery)) + ".\n\n"
                + up1lowall(gender.pronoun1 + " is a " + occu + ".");
    }

    private static String RNtoStr(RuleName rn)
    {
        return rn.toString().toLowerCase().replaceAll("_", " ");
    }

    private String list2stringwCount(List<Jewelry> list)
    {
        Map<String, Integer> things = new HashMap<>();
        for (Jewelry s : list)
        {
            Integer i = things.get(s.type);
            if (i == null)
                i = 1;
            else
                i++;
            things.put(s.type, i);
        }

        int l = things.size();

        if (l == 0)
            return "nothing";

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Entry<String, Integer> e : things.entrySet())
        {
            if (i > 0)
                if (i == l - 1)
                    sb.append(" and ");
                else
                    sb.append(", ");

            Integer ct = e.getValue();
            sb.append(ct == 1 ? getArticle(e.getKey()) : counts[ct]).append(" ").append(e.getKey())
                    .append(ct > 1 ? "s" : "");
            i++;
        }

        return sb.toString();
    }

    // private String addCount(String in, int count)
    // {
    // in = in.trim().toLowerCase();
    // return counts[count]+" "+in;
    // }

    static final String counts[] = new String[] { "", "one", "two", "three", "four", "five", "six", "seven", "eight",
            "nine", "ten" };
    static final String counth[] = new String[] { "", "first", "second", "third", "fourth", "fifth", "sixth", "seventh",
            "eighth", "nineth", "tenth" };
    static final String startsWithAn = "aeiou";

    private String getArticle(String in)
    {
        in = in.trim().toLowerCase();
        if (startsWithAn.contains(in.substring(0, 1)))
            return "an";
        return "a";
    }

    private String up1lowall(String in)
    {
        return in.substring(0, 1).toUpperCase() + in.substring(1).toLowerCase();
    }
}