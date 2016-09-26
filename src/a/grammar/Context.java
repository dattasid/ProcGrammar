package a.grammar;

import java.util.Random;
import java.util.Stack;

import a.grammar.common.FlexObject;
import a.grammar.rule.Rule;

public class Context
{
    public FlexObject root = FlexObject.createRoot();
    public Stack<Rule> curRule = new Stack<>();

    public Stack<String> nodes = new Stack<>();
    public Stack<FlexObject> objects = new Stack<>();
    
    public Random rand;

}
