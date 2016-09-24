package a.grammar.common;

import a.grammar.Context;

public interface ActionProduceRulename<T extends Context> {
	// return rulename
    public Object act(T ctx);
}

