package proc.grammar.common;

import proc.grammar.Context;

public interface Action<T extends Context> {
    public void act(T ctx);
}

