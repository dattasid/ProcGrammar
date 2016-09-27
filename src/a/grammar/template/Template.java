package a.grammar.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import a.grammar.Context;
import a.grammar.Grammar;
import a.grammar.common.FlexObject;
import a.grammar.template.TemplateParam.GenderType;
import a.grammar.utils.CVC;
import a.grammar.utils.Plural;

public class Template
{
    Grammar parent;
    Object name;
    String property;
    
    String templStr;
//    int maxParams = -1;
//    public static final int PARAM_IDX_MAX = 1;
    
    List<TemplateParam> params = new ArrayList<>();
    
    private BadTemplateParam badParam = new BadTemplateParam(this);
    
    List<TemplateDelegate> delegates = new ArrayList<>();
    
     static enum HandleUnique {
        NORMAL, UNIQUE, UNIQUE_COUNTED
        ;
    }
    
    public Template(Grammar grammar, Object name)
    {
        this.parent = grammar;
        this.name = name;
    }

//    public Template property(String property)
//    {
//        this.property = property;
//        return this;
//    }
    
    public Template str(String templStr)
    {
        this.templStr = templStr;
        return this;
    }
    
    public TemplateParam param(int idx)
    {
        if (templStr == null)
        {
            System.out.println("ERROR! Template str() must be called before param().");
            return badParam;
        }
        
        if (idx == 0)
        {
            System.out.println("ERROR! Cannot override parameter idx 0.");
            return badParam;
        }
        
        if (!templStr.contains("#"+idx))
        {
            System.out.println("ERROR! Parameter #"+idx+" not found in template string \""+templStr+"\".");
            return badParam;
        }
        
        TemplateParam p = new TemplateParam(idx, this);
        params.add(p);
        
        return p;
    }

    public TemplateDelegate delegate(Object templateName)
    {
        TemplateDelegate td = new TemplateDelegate(this, templateName);
        delegates.add(td);
        return td;
    }
    
    public String toString(Context ctx, Object obj)// TODO parameterize context
    {
        if (!delegates.isEmpty())
        {
            for (TemplateDelegate dd : delegates)
            {
                if (dd.check(obj))
                {
                    Template td = parent.template(dd.delegateName);
                    if (td == null)
                    {
                        System.out.println("ERROR! Delegate template "+dd.delegateName+" not found from template "+name);
                    }
                    
                    return td.toString(ctx, obj);
                }
            }
        }
        
        String s = templStr;
        
        if (s == null)
        {
            System.out.println("ERROR! In template "+name+". No template string set up, and no delegates.");
            return "";
        }
        
        if (!(obj instanceof FlexObject))
        {
            if (s.contains("#0"))
            {
                if (obj instanceof List)
                {
                    String itemsStr = list2Str((List) obj, null, HandleUnique.NORMAL, false, null);
                    s = s.replaceAll("#0", itemsStr);
                }
                else
                    s = s.replaceAll("#0", sani(obj));
            }
            if (!params.isEmpty())
                System.out.println("Warning: Could not apply parameters to plain object in template "+name+" obj "+obj);
            return s;
        }
        
        FlexObject root = (FlexObject) obj;

        if (s.contains("#0"))
        {
            List<Object> items = root.getDeepProperty("/");
            String itemsStr = list2Str(items, null, HandleUnique.NORMAL, false, null);
            s = s.replaceAll("#0", itemsStr);
        }
        
        for (TemplateParam param : params)
        {
            if (s.contains("#"+param.idx))
            {
                if (param.isConditionSet() && !param.check(obj))
                {
                    s = s.replaceAll("#"+param.idx, "");
                    continue;
                }
                List<Object> items;
                
                if (param.property != null)
                {
                    items = root.getDeepProperty(param.property);
                }
                else if (param.runTimeValue != null)
                {
                    Object oo = param.runTimeValue.act(ctx);
                    if (oo instanceof List)
                        items = (List<Object>) oo;
                    else
                        items = wrapList(oo);
                }
                else
                {
                    System.out.println("ERROR! Template "+name+" did not have a property or runtimevalue, ignoring!");
                    continue;
                }
                
//                System.out.println("Items "+propName+"="+items);
                String itemsStr = "";
                
                if (param.templName != null && !items.isEmpty())
                {
                    Template t = parent.template(param.templName);
                    if (t == null)
                    {
                        System.out.println("ERROR! In template "+name+" param "+param.idx+", target template "+param.templName+" was not found.");
                        continue;
                    }
                    
                    if (param.multi)
                    {
                        itemsStr = t.toString(ctx, items);
                    }
                    else
                    {
                        List<String> strs = new ArrayList<>();
                        for (Object item : items)
                        {
                            String s2 = t.toString(ctx, item);
                            strs.add(s2);
                        }
                        
                        itemsStr = list2Str(strs, param.delimiter, param.handleUnique, param.plural, param.gender);
                    }
                }
                else
                {
                    itemsStr = list2Str(items, param.delimiter, param.handleUnique, param.plural,
                            param.gender);
                }
                
                if (param.capFirst)
                    itemsStr = CVC.capFirst(itemsStr);
                
                if (param.spaceBefore)
                    itemsStr = " "+itemsStr;
                
                if (param.spaceAfter)
                    itemsStr = itemsStr+" ";
                
                s = s.replaceAll("#"+param.idx, itemsStr);
            }
        }
        
        return s;
        
    }

    boolean sanitize = true;
    static HashSet<String> male = new HashSet<String>(), female = new HashSet<String>();
    {
        male.add("male");male.add("m");
        female.add("female");female.add("f");
    }
    static String heStr[] = new String[] {"he", "him", "his"};
    static String sheStr[] = new String[] {"she", "her", "her"};
    private String list2Str(List<? extends Object> items, String delim,
            HandleUnique hdlUniq, boolean plural, GenderType gender)
    {
        if (items.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();

        List<String> items1 = new ArrayList<>();
        for (Object it : items)
        {
            if (gender != null)
            {
                String ss = it.toString().toLowerCase();
                if (male.contains(ss))
                    ss = heStr[gender.ordinal()];
                else if (female.contains(ss))
                    ss = sheStr[gender.ordinal()];
                // else ss stays same
                
                items1.add(ss);
            }
            else
            {
                String ss = sani(it);
                
                items1.add(ss);
            }
        }

        if (hdlUniq != HandleUnique.NORMAL)
        {
            Map<String, Integer> ct = new HashMap<>();
            for (String s : items1)
            {
                Integer i = ct.get(s);
                if (i == null)
                    i = 1;
                else
                    i++;
                ct.put(s, i);
            }
            
            items1 = new ArrayList<>();
            for (Entry<String, Integer> e : ct.entrySet())
            {
                String ss = e.getKey();
                if (plural && (e.getValue() > 1 ||
                            hdlUniq == HandleUnique.UNIQUE/*force plurals*/))
                    ss = parent.plural.plural(ss);
                
                if (hdlUniq == HandleUnique.UNIQUE_COUNTED)
                {
                    items1.add(getCount(e.getValue(), ss)+" "+ss);
                }
                else
                {
                    items1.add(ss);
                }
            }
        }
        items = items1;
        
        
        sb.append(items.get(0));
        
        for (int i = 1; i < items.size(); i++)
        {
            if (delim == null)
            {
                if (i == items.size() - 1)
                    sb.append(" and ");
                else
                    sb.append(", ");
            }
            else
                sb.append(delim);
            
            sb.append(items.get(i));
        }
        return sb.toString();
    }
    
    private static String getCount(int ct, String obj)
    {
        if (obj.startsWith("the "))
            return "";
        
        if (ct == 1)
        {
            if (Plural.isVowel(obj.charAt(0)))
                return "an";
            else
                return "a";
        }
        else if (ct < counts_len - 1)
            return counts[ct];
        else return counts[counts_len - 1];
    }
    
    static final String counts[] = new String[] { "", "one", "two", "three", "four", "five", "six", "seven", "eight",
            "nine", "ten", "many" };
    static final int counts_len = counts.length;
    
    private String sani(Object in)
    {
        if (!sanitize || !(in instanceof Enum))
            return in.toString();
        return in.toString().toLowerCase().replaceAll("_", " ");
    }
    
    private static List<Object> wrapList(Object o)
    {
        ArrayList<Object> l = new ArrayList<Object>(1);
        l.add(o);
        return l;
    }
}
