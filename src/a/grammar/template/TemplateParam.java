package a.grammar.template;

import a.grammar.Context;
import a.grammar.common.ActionProduceRulename;
import a.grammar.template.Template.HandleUnique;

public class TemplateParam extends TemplateConditional
{

    int idx;
    
    String property;
    ActionProduceRulename<Context> runTimeValue;// TODO parameter
    
    Object templName;
    String delimiter;
    boolean multi;
    
    HandleUnique handleUnique = HandleUnique.NORMAL;
    boolean plural;
    
    static enum GenderType{
        SUBJECTIVE, OBJECTIVE, POSSESSIVE
    }
    
    GenderType gender;

    boolean capFirst = false;
    boolean spaceBefore = false, spaceAfter = false;
    
    public TemplateParam(int idx, Template parent)
    {
        super(parent);
        this.idx = idx;
        this.parent = parent;
    }
    
    public TemplateParam property(String property)
    {
        this.property = property;
        return this;
    }
    
    public TemplateParam runtimeValue(ActionProduceRulename<Context> runTimeValue)
    {
        this.runTimeValue = runTimeValue;
        return this;
    }
    
    public TemplateParam unique()
    {
        this.handleUnique = HandleUnique.UNIQUE;
        return this;
    }
    
    public TemplateParam param(int idx)
    {
        return parent.param(idx);
    }
    
    public TemplateParam useTemplate(Object name)
    {
        templName = name;
        return this;
    }

    public TemplateParam delimiter(String delim)
    {
        delimiter = delim;
        return this;
    }
    
    public TemplateParam multi()
    {
        multi = true;
        return this;
    }
    
    public TemplateParam uniqueCounted()
    {
        handleUnique = HandleUnique.UNIQUE_COUNTED;
        return this;
    }

    public TemplateParam plural()
    {
        plural = true;
        return this;
    }
    
    public TemplateParam genderSubjective()
    {
        gender = GenderType.SUBJECTIVE;
        return this;
    }
    
    public TemplateParam genderObjective()
    {
        gender = GenderType.OBJECTIVE;
        return this;
    }
    
    public TemplateParam genderPossessive()
    {
        gender = GenderType.POSSESSIVE;
        return this;
    }
    
    @Override
    public TemplateParam ifExists(String propertyName)
    {
        return (TemplateParam) super.ifExists(propertyName);
    }
    
    @Override
    public TemplateParam ifEquals(String propertyName, Object expected)
    {
        return (TemplateParam) super.ifEquals(propertyName, expected);
    }
    
    @Override
    public String toString()
    {
        return "Param("+parent.name+"/"+property+")";
    }

    public TemplateParam capFirst()
    {
        capFirst = true;
        return this;
    }

    public TemplateParam spaceBefore()
    {
        this.spaceBefore = true;
        return this;
    }

    public TemplateParam spaceAfter()
    {
        this.spaceAfter = true;
        return this;
    }
}
