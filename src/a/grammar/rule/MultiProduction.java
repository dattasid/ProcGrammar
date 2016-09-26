package a.grammar.rule;

import java.util.List;
import java.util.ArrayList;

import a.grammar.Context;

public class MultiProduction<T extends Context> extends ArrayList<Production<T>>
{
    public MultiProduction()
    {
    }    
    public MultiProduction(List<Production<T>> prods)
    {
        addAll(prods);
    }
    
    public MultiProduction<T> addProd(Production<T> p)
    {
        add(p);
        return this;
    }
    
    public MultiProduction<T> then(Object... ruleNames)
    {
        for (Production<T> p : this)
        {
            p.then(ruleNames);
        }
        return this;
    }
}
