package a.grammar.rule;

import a.grammar.Context;

public class FixedWeight extends Weight
{
    final double w;
    static final FixedWeight DefaultWeight = new FixedWeight(null, 1);
    
    public FixedWeight(Production parent, double w) {
        super(parent);
        this.w = w;
    }

    @Override
    public double getWeight(Context ctx) {
        return w;
    }
}