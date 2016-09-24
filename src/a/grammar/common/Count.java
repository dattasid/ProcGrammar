package a.grammar.common;

import a.grammar.Context;
import a.grammar.rule.Production;

public abstract class Count<T extends Context> {
    Production<T> parent;
    public Count(Production<T> parent) {
        this.parent = parent;
    }
    
    public abstract int getCount(T ctx);
}