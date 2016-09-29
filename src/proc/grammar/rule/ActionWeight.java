package proc.grammar.rule;

import proc.grammar.Context;
import proc.grammar.common.intf.CalculateDouble;

public class ActionWeight<T extends Context> extends Weight<T>
{
    CalculateDouble<T> calculator;
    public ActionWeight(Production<T> parent, CalculateDouble<T> act) {
        super(parent);
        this.calculator = act;
    }
    
    
    @Override
    public double getWeight(T ctx) 
    {
        return calculator.apply(ctx);
        
    }
}