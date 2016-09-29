package proc.grammar.common;

import proc.grammar.Context;
import proc.grammar.common.intf.CalculateInt;
import proc.grammar.rule.Production;

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