package a.grammar.common;

import a.grammar.Context;
import a.grammar.rule.Production;
import a.grammar.rule.Weight;

public class FixedWeight extends Weight
{
    final double w;
    public static final FixedWeight DefaultWeight = new FixedWeight(null, 1);
    
    public FixedWeight(Production parent, double w) {
        super(parent);
        this.w = w;
    }

    @Override
    public double getWeight(Context ctx) {
        return w;
    }
}