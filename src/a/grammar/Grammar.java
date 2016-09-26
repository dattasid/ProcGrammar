package a.grammar;

import static a.grammar.rule.Rule.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import a.grammar.common.FlexObject;
import a.grammar.rule.MultiRule;
import a.grammar.rule.Production;
import a.grammar.rule.Rule;
import a.grammar.rule.RuleStep;
import a.grammar.rule.SequenceProduction;
import a.grammar.rule.Step;
import a.grammar.template.Template;
import a.grammar.utils.Plural;

public class Grammar<T extends Context>
{

    public final Plural plural = new Plural();
    Map<Object, Template> templates = new HashMap<>();
    
    Map<Object, Rule<T>> allRules = new HashMap<>();

//    T context;

    // TODO above should not be public, move them into Context
    // Fix: put these in a context object. The object itself will be hidden.
    
    public Grammar(T context)
    {
        this(System.currentTimeMillis(), context);
    }
    
    public void newContext(T ctx)
    {
        newContext(ctx, System.currentTimeMillis());
    }
    public void newContext(T ctx, long seed)
    {
//        context = ctx;
        if (ctx.rand == null)
            ctx.rand = new Random(seed);
        ctx.root = FlexObject.createRoot();
        
        ctx.objects.clear();
        ctx.nodes.clear();
        
        ctx.objects.push(ctx.root);
    }
    
    public static <T extends Context> Grammar<T> create(T ctx)
    {
        return new Grammar<T>(ctx);
    }
    
    public Grammar(long seed, T context)
    {
        newContext(context, seed);
//        this.context = context;
    }
    
    public Rule<T> rule(Object name)
    {
        Rule<T> rr = allRules.get(name);
        if (rr == null)
        {
            rr = new Rule<T>(this, name);
            allRules.put(name,  rr);
        }
        return rr;
    };
    
    public MultiRule<T> forAllRules(Object... names){
        Rule<T>[] rules = new Rule[names.length];
        for (int i = 0; i < names.length; i++)
        {
            Object name = names[i];
            rules[i] = rule(name);
        }
        
        return new MultiRule<T>(rules);
    }
    
    public Template template(Object name)
    {
        Template t = templates.get(name);
        if (t == null)
        {
            t = new Template(this, name);
            templates.put(name, t);
        }
        return t;
    }
    
    public String toString(T context, Object templateName)
    {
//        StringBuilder sb = new StringBuilder();
        
        if (!templates.containsKey(templateName))
        {
            System.err.println("Template "+templateName+" not found.");
            return "";
        }
        String s = template(templateName).toString(context, context.root);
        
        return s;
    }
    
    
    public void sanityCheck(Object root)
    {
        Set<Object> reached = new HashSet<>();
        // Every rule reachable, every rule does something.
        for (Rule<T> rule : allRules.values())
        {
            if (rule.name == RULE_NONE)
                continue;
            // rules not doing anything produce self.
//            if (rule.getProductions().isEmpty() && rule.getProperties().isEmpty())
//                System.err.println(rule.name+" does not have any productions.");
        }
        
        if (!rule(RULE_NONE).getProductions().isEmpty())
            System.err.println("NONE has productions, probably an error.");
        
        reached.add(RULE_NONE);// NONE never has productions!
        reachableRec(root, reached);
        
        for (Rule<T> rule : allRules.values())
        {
            if (!reached.contains(rule.name))
                System.err.println(rule.name+" is not reachable from "+root);
        }
        
    }
    
    
    public void reachableRec(Object root, Set<Object> reached)
    {
        if (reached.contains(root)) // already explored
            return;
        
      Rule<T> rule = rule(root);
      reached.add(root);
      
      for (Production<T> pp : rule.getProductions())
      {
          if (pp instanceof SequenceProduction)
          {
              SequenceProduction<T> sp = (SequenceProduction<T>) pp;
              for (Step step : sp.getSteps())
              {
                  if (step instanceof RuleStep)
                  {
                      RuleStep rst = (RuleStep) step;
                      reachableRec(rst.rule.name, reached);
                  }
              }
          }
      }
    }    
}
