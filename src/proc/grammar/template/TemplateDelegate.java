package proc.grammar.template;

public class TemplateDelegate extends TemplateConditional
{

    Object delegateName;
    
    public TemplateDelegate(Template parent, Object delegate)
    {
        super(parent);
        this.delegateName = delegate;
    }
    
    public TemplateDelegate delegate(Object name)
    {
        return parent.delegate(name);
    }
    
    public Template str(String s)
    {
        return parent.str(s);
    }
    
    public TemplateParam param(int idx)
    {
        return parent.param(idx);
    }
    
    @Override
    public TemplateDelegate ifEquals(String propertyName, Object expected)
    {
        return (TemplateDelegate) super.ifEquals(propertyName, expected);
    }

    @Override
    public TemplateDelegate ifExists(String propertyName)
    {
        return (TemplateDelegate) super.ifExists(propertyName);
    }
    
    @Override
    public String toString()
    {
        return "Delegate("+parent.name+"->"+delegateName+")";
    }
}
