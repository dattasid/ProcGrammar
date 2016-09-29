package proc.grammar.common;

import proc.grammar.Context;
import proc.grammar.rule.Production;

public abstract class Count<T extends Context> {
    Production<T> parent;
    public Count(Production<T> parent) {
        this.parent = parent;
    }
    
    public abstract int getCount(T ctx);
}