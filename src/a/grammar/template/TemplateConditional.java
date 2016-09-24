package a.grammar.template;

import java.util.List;

import a.grammar.common.FlexObject;


public class TemplateConditional
{
    Template parent;
    
    String conditionPropertyName;
    Object expected;
    boolean isExistsCheck;
    
    public TemplateConditional(Template parent)
    {
        super();
        this.parent = parent;
    }

    public TemplateConditional ifExists(String propertyName)
    {
        if (this.conditionPropertyName != null)
        {
            System.err.println("Deletegate for "+parent.name+", condition property was already set!");
        }
        this.conditionPropertyName = propertyName;
        isExistsCheck = true;
        return this;
    }
    
    public TemplateConditional ifEquals(String propertyName, Object expected)
    {
        if (this.conditionPropertyName != null)
        {
            System.err.println("Deletegate for "+parent.name+", condition property was already set!");
        }
        this.conditionPropertyName = propertyName;
        this.expected = expected;
        isExistsCheck = false;
        return this;
    }
    
    public boolean isConditionSet()
    {
        return conditionPropertyName != null;
    }
    
    public boolean check(Object obj)
    {
        if (conditionPropertyName == null)
        {
            System.err.println("Deletegate for "+parent.name+", condition not set up!");
            return false;
        }
        if (obj == null)
            return false;
        
        if (!(obj instanceof FlexObject))
        {
            if (!"".equals(conditionPropertyName) && !"/".equals(conditionPropertyName))
            {
                System.err.println("Warning: Could not match plain object against check in "+toString());
            }
            
            if (isExistsCheck)
                return true;
            else
                return obj == expected;
        }
        
        FlexObject fo = (FlexObject) obj;
        
        List<Object> val = fo.getDeepProperty(conditionPropertyName);
        
//        System.out.println("------- In check prop "+val+" compare "+expected);
        
        if (val == null || val.isEmpty())
            return false;
        if (isExistsCheck)
        {
            return true;
        }
        else
        {
            if (val.contains(expected))
                return true;
        }
        
        return false;
//        if (object instanceof List)
//        {
//            List l = (List) object;
//            
//            
//        }
    }
}
