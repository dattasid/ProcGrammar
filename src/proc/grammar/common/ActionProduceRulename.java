package proc.grammar.common;

import proc.grammar.Context;

public interface ActionProduceRulename<T extends Context> {
	// return rulename
    public Object act(T ctx);
}

