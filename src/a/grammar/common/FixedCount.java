package a.grammar.common;

import a.grammar.Context;
import a.grammar.rule.Production;

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