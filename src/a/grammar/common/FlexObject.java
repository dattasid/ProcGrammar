package a.grammar.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

// Can insert any property
public class FlexObject
{
    Map<String, List<Object>> map = new HashMap<>();
    
    public FlexObject addProperty(String name)
    {
        FlexObject o = new FlexObject();
        List<Object> lst = map.get(name);
        if (lst == null)
        {
            lst = new ArrayList<>();
            map.put(name, lst);
        }
        lst.add(o);
        return o;
    }
    
    public void addLeaf(String name, Object o)
    {
        List<Object> lst = map.get(name);
        if (lst == null)
        {
            lst = new ArrayList<>();
            map.put(name, lst);
        }
        lst.add(o);
    }
    
    public List<Object> getProperty(String name)
    {
        return map.get(name);
    }
    
    public boolean removeProperty(String name, Object obj)
    {
        List<Object> l = map.get(name);
        if (l == null)
            return false;
        return l.remove(obj);
    }
    
    public boolean removeAll(String name)
    {
        return map.remove(name)!=null;
    }
    
    public boolean isEmpty()
    {
        return map.isEmpty();
    }
    
    public List<Object> getDeepProperty(String name)
    {
        List<Object> ret = new ArrayList<>();
        
        if (name.length() == 0 || name.equals("/"))
        {
            ret.add(this);
            return ret;
        }
        
        String[] parts = name.split("/");
        
        if (parts[0].length() == 0)
            parts = Arrays.copyOfRange(parts, 1, parts.length);
        
        getDeepPropertyRec(parts, ret);
        
        return ret;
    }
    
    private void  getDeepPropertyRec(String[] name, List<Object> out)
    {
//        if (name.length == 0)
//        {
//            out.add(this);
//            return;
//        }
        String propname = name[0];
        List<Object> ll1 = map.get(propname);
        if (ll1 == null || ll1.isEmpty())
            return;

        if (name.length == 1)
        {
           out.addAll(ll1);
           return;
        }
        
        String[] name1 = Arrays.copyOfRange(name, 1, name.length);
        for (Object obj : ll1)
        {
            if (obj instanceof FlexObject)
            {
                ((FlexObject)obj).getDeepPropertyRec(name1, out);
            }
            else
                out.add(obj);
        }
    }
    
    @Override
    public String toString()
    {
        
        return map.toString();
    }
    
    public static void main(String[] args)
    {
        FlexObject f1 = new FlexObject();
        FlexObject f2 = f1.addProperty("a");
        FlexObject f3 = f2.addProperty("b");
        FlexObject f4 = f2.addProperty("b");
        
        f3.addLeaf("leaf", "l");
        f4.addLeaf("leaf", "l2");
        
        System.out.println(f1);
        
//        System.out.println(f1.getDeepProperty("/a/b/leaf"));
        System.out.println(f1.getDeepProperty("/"));
    }
    
}
