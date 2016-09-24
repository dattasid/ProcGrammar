package a.grammar.util;

import java.util.function.Function;

import a.grammar.Context;
//TODO!! calculators get a immutable context.
public interface CalculateBool<T extends Context> extends Function<T, Boolean>{
}