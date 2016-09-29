package proc.grammar.rule;

import proc.grammar.Context;

public abstract class Step<T extends Context>
{
	public final Production<T> parent;
	public Step(Production<T> parent) {
		this.parent = parent;
	}
    public abstract void fire(T ctx); 
}