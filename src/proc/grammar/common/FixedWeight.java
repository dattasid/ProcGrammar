package proc.grammar.common;

import proc.grammar.Context;
import proc.grammar.rule.Production;
import proc.grammar.rule.Weight;

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