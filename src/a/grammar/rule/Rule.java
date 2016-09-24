package a.grammar.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import a.grammar.Context;
import a.grammar.Grammar;
import a.grammar.common.Action;
import a.grammar.common.Count;
import a.grammar.common.FlexObject;
import a.grammar.util.CalculateDouble;

public class Rule<T extends Context>
{
    public static final Object RULE_NONE = new String("NONE");
    
    public static final String RULENAME_INDICATOR = "RULENAME_INDICATOR";
    static final String LEAFPROP_INDICATOR = "LEAFPROP_INDICATOR";
    
    final Grammar<T> parent;
    
    public final Object name;
    List<Production<T>> productions = new ArrayList<>();
    
    boolean fireMany;
    Weight<T> defaultChance = FixedWeight.DefaultWeight;// When firing many
    // Some control of how many rules may fire.
    int fireManyMin = 0; // None may fire because all failed
    int fireManyMax = -1; // All of the rules may fire.
    
    Map<Production<T>, Count<T>> fireMultipleTimesMin = new HashMap<>(),
            fireMultipleTimesMax = new HashMap<>();
    
    String pushName = null;
    private List<Leaf> properties = new ArrayList<>();
    
    public Rule(Grammar<T> parent, Object name) {
        super();
        this.parent = parent;
        this.name = name;
    }

    public Production<T> produces(Object... ruleNames)
    {
        SequenceProduction<T> sp = new SequenceProduction<T>(this, ruleNames);
        productions.add(sp);
        return sp;
    }
    
    public Rule<T> producesOneOf(Object... ruleNames)
    {
        for (Object rn : ruleNames)
        {
            SequenceProduction<T> sp = new SequenceProduction<T>(this, rn);
            productions.add(sp);
        }
        return this;
    }
    
    public Production<T> produces(Action<T> act)
    {
        SequenceProduction<T> p = new SequenceProduction<T>(this);
        ActionStep<T> s = new ActionStep<T>(p, act);
        p.then(s);
        productions.add(p);
        return p;
    }
    
    public Rule<T> pushNode(String nodeName)
    {
        if (pushName != null)
            System.err.println("Multiple push names for same rule not supported.");
        this.pushName = nodeName;
        return this;
    }
    
    public Rule<T> pushLeaf(Object obj)
    {
        return pushLeaf(LEAFPROP_INDICATOR, obj);
    }

    public Rule<T> pushLeaf(String propertyname, Object obj)
    {
        properties.add(new Leaf(propertyname, obj));
        return this;
    }
    
    private static class ProdChance<T extends Context>
    {
        public double successPerc;
        public Production<T> prod;
        public ProdChance(double successPerc, Production<T> prod) {
            super();
            this.successPerc = successPerc;
            this.prod = prod;
        }
        @Override
        public String toString() {
            return "PC[suc=" + successPerc + ", p=" + prod + "]";
        }
        
    }
    public void fire(T ctx)
    {
        int len = productions.size();
        
        if (len == 0 && properties.size() == 0)
        {
            if (name != RULE_NONE)
                System.err.println("Rule "+name+" did not have any production rules.");
            
            return;
        }

        parent.curRule.push(this);
//      System.out.println("--------------"+name+" "+pushName+" "+properties);

        if (pushName != null)
        {
            parent.nodes.push(pushName);
            FlexObject o = parent.objects.peek().addProperty(pushName);
            parent.objects.push(o);
        }
            
        if (properties.size() > 0)
        {
            for (Leaf lf : properties)
            {
                Object thisleaf = lf.property;
                if (thisleaf == RULENAME_INDICATOR)
                    thisleaf = name;
//                System.out.println("---------------- Adding "+lf.name+"/"+thisleaf+" to "+ctx.objects.peek());
                parent.objects.peek().addLeaf(lf.name, thisleaf);
            }
        }
        

        if (len > 0)
        {
            double[] pweights = new double[len];
            for (int i = 0; i < len; i++)
            {
                pweights[i] = productions.get(i).getWeight(ctx);
            }
            
            if (!fireMany)
            {
                int choice = choose(parent.rand, pweights);
                Production<T> pc = productions.get(choice);
                
//              System.out.println("Rule "+name+" -> production "+choice+" "+pc);
                
                // How many times do we fire this rule??
                int min = 1, max = 1;
                Count<T> c = fireMultipleTimesMin.get(pc);
                if (c != null) min = c.getCount(ctx);
                c = fireMultipleTimesMax.get(pc);
                if (c != null) max = c.getCount(ctx);
                
                int numRolls = min;
                if (max > min)
                    numRolls = min + parent.rand.nextInt(max - min);
//                System.out.println("MM "+min+" "+max+" -- "+numRolls);
                
                for (int i = 0; i < numRolls; i++)
                    pc.fire(ctx);
            }
            else
            {
                List<ProdChance<T>> sucList = new ArrayList<>();
                double defC = defaultChance.getWeight(ctx);
                for (int i = 0; i < len; i++)
                {
                    pweights[i] *= defC;
                    
                    if (pweights[i] > 1)
                        pweights[i] = 1;
                    if (pweights[i] < 0)
                        pweights[i] = 0;
                
                    Production<T> prod = productions.get(i);
                    // How many times do we fire this rule??
                    int min = 1, max = 1;
                    Count<T> c = fireMultipleTimesMin.get(prod);
                    if (c != null) min = c.getCount(ctx);
                    c = fireMultipleTimesMax.get(prod);
                    if (c != null) max = c.getCount(ctx);
                    
                    int numRolls = min;
                    if (max > min)
                        numRolls = min + parent.rand.nextInt(max - min);
//                  System.out.println("MM "+min+" "+max+" -- "+numRolls);
                    
                    for (int j = 0; j < numRolls; j++)
                    {
                        double roll = parent.rand.nextDouble();
                        
                        sucList.add(new ProdChance<T>(roll/pweights[i], prod));
                    }
                }
                
                Collections.sort(sucList, (a1, a2)->{
                    if (a1.successPerc < a2.successPerc) return -1;
                    if (a1.successPerc > a2.successPerc) return 1;
                    
                    return 0;
                });
                
//              System.out.println(sucList);
                
                int firedCount = 0;
                // All success productions are fired (successPerc < 1)
                // Even failed productions are fired in sorted order if less than MIN prods succeeded.
                // Even successful prods are not fired if they are past MAX in sorted order.
                // Must fail prods (successPerc = Inf) are never fired.
                // fireManyMax == -1 means fire all
                for (int i = 0; i < sucList.size(); i++)
                {
                    ProdChance<T> entry = sucList.get(i);
                    if ((fireManyMax > 0 && firedCount >= fireManyMax) // firing too many
                            || Double.isInfinite(entry.successPerc)) // found must NOT fire
                        break;
                    
                    if (firedCount >= fireManyMin && entry.successPerc > 1) // Fired MIN rules, found failing rule
                        break;
                    
                    firedCount++;
                    
//                  System.out.println("Rule "+name+" -> production "+entry.prod);
                    entry.prod.fire(ctx);
                }
                
            }
        }
        
        if (pushName != null)
        {
            if (parent.nodes.peek() != pushName)
                System.err.println("Top of the nodes stack is not expected! Expected "+pushName+" found "+parent.nodes.peek());
            else
            {
                parent.nodes.pop();
                FlexObject lastTop = parent.objects.pop();
                // pack
                FlexObject curTop = parent.objects.peek();
                List<Object> lst = lastTop.getProperty(LEAFPROP_INDICATOR);
                lastTop.removeAll(LEAFPROP_INDICATOR);
                if (lastTop.isEmpty())
                    curTop.removeProperty(pushName, lastTop);
                if (lst != null)
                {
                    for (Object prop : lst)
                    {
                        curTop.addLeaf(pushName, prop);
                    }
                }
            }
        }           
        if (parent.curRule.peek() != this)
            System.err.println("Top of the rule stack is not this! Expected "+name+" found "+parent.curRule.peek().name);
        else
            parent.curRule.pop();
    }       
    /**
     * Weights will now modify the chance.
     * Default chance is 50%
     * @return
     */
    public Rule<T> fireMany()
    {
        fireMany = true;
        return this;
    }
    
    private void fireManyOnly(String optionName)
    {
        if (fireMany) return;
        System.err.println("Warning: Option \""+optionName+"\" applies to multi fire with chance rules only.");
    }
    
    public Rule<T> defaultFireChance(double chance)
    {
        fireManyOnly("default fire chance");
        defaultChance = new FixedWeight(null, chance);
        return this;
    }
    
    public Rule<T> defaultFireChance(CalculateDouble<T> chance)
    {
        fireManyOnly("default fire chance");
        defaultChance = new ActionWeight<T>(null, chance);
        return this;
    }
    
    public Rule<T> fireAtLeast(int times)
    {
        fireManyOnly("fire at least");
        fireManyMin = times;
        return this;
    }
    
    public Rule<T> fireAtMost(int times)
    {
        fireManyOnly("fire at most");
        fireManyMax = times;
        return this;
    }
    
    void fireMultipleTimes(Production<T> production, Count<T> min, Count<T> max) {
//      fireManyOnly("fire rule multiple times");
        fireMultipleTimesMin.put(production, min);
        fireMultipleTimesMax.put(production, max);
    }
    
    @Override
    public String toString() {
        return name.toString();
    }
    

    
    public static int choose(Random rand, double[] weights)
    {
        double sum = 0;
        for (double d : weights)
        {
            sum += d;
        }
        
        double choice = rand.nextDouble() * sum;
                
        for (int i = 0; i < weights.length; i++)
        {
            if (choice < weights[i])
                return i;
            
            choice -= weights[i];
        }
        
        return weights.length - 1;
    }

    public List<Production<T>> getProductions()
    {
        return Collections.unmodifiableList(productions);
    }

    public List<Leaf> getProperties()
    {
        return Collections.unmodifiableList(properties);
    }
   
}