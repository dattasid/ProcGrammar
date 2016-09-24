package a.grammar.rule;

import a.grammar.Context;
import a.grammar.util.CalculateDouble;

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