package a.grammar.common;

import a.grammar.Context;
import a.grammar.rule.Production;
import a.grammar.util.CalculateInt;

public class ActionCount<T extends Context> extends Count<T>
{
    CalculateInt<T> calculator;
    public ActionCount(Production<T> parent, CalculateInt<T> act) {
        super(parent);
        this.calculator = act;
    }
    
    
    @Override
    public int getCount(T ctx)
    {
        return calculator.apply(ctx);
        
    }
}