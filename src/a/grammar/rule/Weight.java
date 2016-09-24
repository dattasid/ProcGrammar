package a.grammar.rule;

import a.grammar.Context;

public abstract class Weight<T extends Context> {
    Production<T> parent;
    public Weight(Production<T> parent) {
        this.parent = parent;
    }
    
    public abstract double getWeight(T ctx);
}