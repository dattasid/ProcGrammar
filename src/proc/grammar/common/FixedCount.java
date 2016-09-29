package proc.grammar.common;

import proc.grammar.Context;
import proc.grammar.rule.Production;

public class FixedCount<T extends Context> extends Count<T>
{
    final int c;

    public FixedCount(Production<T> parent, int c) {
        super(parent);
        this.c = c;
    }

    @Override
    public int getCount(T ctx) {
        return c;
    }
}