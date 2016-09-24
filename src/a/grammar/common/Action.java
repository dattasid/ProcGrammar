package a.grammar.common;

import a.grammar.Context;

public interface Action<T extends Context> {
    public void act(T ctx);
}

