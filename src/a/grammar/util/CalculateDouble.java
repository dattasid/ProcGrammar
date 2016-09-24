package a.grammar.util;

import java.util.function.Function;

import a.grammar.Context;
//TODO!! calculators get a immutable context.
public interface CalculateDouble<T extends Context> extends Function<T, Double> {
}