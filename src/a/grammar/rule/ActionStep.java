package a.grammar.rule;

import a.grammar.Context;
import a.grammar.common.Action;

public class ActionStep<T extends Context> extends Step<T>
{
    Action<T> act;
    public ActionStep(Production<T> parent, Action<T> act) {
    	super(parent);
        this.act = act;
    }
    
    
    @Override
    public void fire(T ctx) 
    {
        act.act(ctx);
    }
}