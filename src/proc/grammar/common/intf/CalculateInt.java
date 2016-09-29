package proc.grammar.common.intf;

import java.util.function.Function;

import proc.grammar.Context;

//TODO!! calculators get a immutable context.
public interface CalculateInt<T extends Context> extends Function<T, Integer>{
}