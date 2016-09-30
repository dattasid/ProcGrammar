# ProcGrammar
Rule based Context sensitive Grammar for Procedural generation

Please have a look at src/sid/example/jewelry/Main.java for the only example currently available. The below code snippets may not work, best place for a working example will be  src/sid/example/jewelry/Main.java.

## Introduction

Many text generation systems have a rule based system. for example:

```
start: $boy meets $girl.
boy: John, Charlie
girl: Gina, Charlotte
```

And it will generate sentences like "Charlie meets Gina" etc.

The system in this project attempts do something more complex. Usualy you cannot look back at the data generated and make decisions. This project helps you do that.

This is not a DSL, the rules must be Java code.

Defined rules first produce an object. A separate set of rules then take that object produce text out of it. The produced object might be interpreted differently by other modules instead, for example to draw an image.

## Rule Examples

### Basic Examples

Rule names can be strings or Enums (actually can be any object but that is not recommended.)

```java
Grammar g = new Grammar(new Context());
```

```java
g.rule("A")
  .produces("B")
  .produces("C");
```
Rule A produces B or C with 50-50 chance.

```java
g.rule("A")
  .produces("B").weight(2)
  .produces("C");
```
Rule A produces B or C with 66-33 chance. Weights can be any value, all weights are normalized into probabilities. A weight of 0 makes sure that rule will not be picked.

```java
g.rule("object")
  .produces("color", "shape");
```
Rule 'object' calls rules 'color', then calls rule 'shape'. Both rules will be called in order.
This means rule 'object' decides the color and shape of the object.

```java
g.rule("object")
  .produces("color").then("shape");
```
Same as above.

Please note none of the abocve generate any results, how to generate actual objects will be shown in a following section.

### Actally generating objects

The final result of firing the rules is a JSON-like object. It has a set of properties and every property has a set of values, which may themselves have properties and values.
```java
Context ctx = new Context();
Grammar g = new Grammar();

// ... create rules here

g.rule("chair").fire(ctx); // creates object 

System.out.println(ctx.root); // Print generated object

```

```java
g.rule("chair").pushNode("chair") 
// Creates a new object, under property. All rules generated by this rule will now append values to that new object
  .produces("color").then("shape");
g.rule("color").pushNode("color").producesOneOf("red", "blue");
g.rule("shape").pushNode("shape").producesOneOf("square", "round");
```
Produces a chair which is red or blue, and square or round. Final object is:
```javascrpt
{chair={color="red", shape="round"}}
```

Note: As a convenience feature, any rules that do not produce anything return themselves as values. The rule "red" becomes a value.

### Deciding weights and taking arbitrary actions runtime

```java
static class MyContext extends Context
{
  Color color;
  int price;
}
MyContext ctx = new MyContext();
Grammar<MyContext> g = new Grammar<MyContext>(ctx);

g.rule("chair")
  .produces("color", "price");
g.rule("color")
  .producesAction(ctx->ctx.color=RED)
  .producesAction(ctx->ctx.color=BLUE);
g.rule("price").producesAction(ctx->ctx.price=(ctx.color==RED)?2:1);
```
In this example, first we decide the chairs color and store it in the contex. Then we use the context to deicide the price of the chair, red chairs cost $2 while all others are $1.

Simiarly weights can be decided based on context data.
```java
g.rule("a").produces("b").weight(ctx->ctx.value);
```

Note: A consistent object system is TBD. The current object is given by ```ctx.nodes.peek()```, but there are no good ways to access whats in it. It is easy to access fields added by extending the Context class, but the user must take of creating his hierarchy. Using  the pushNode/PushLeaf based object system to make decisions and decide values is TBD.

## Template examples

```
g.template("chair").str("A chair, #1 in color and #2 in shape.")
      .param(1).property("color")
      .param(1).property("shape")
```
Converts the ```{chair={color="red", shape="round"}}``` to string "A chair, red in color and round in shape."
Converts the ```{chair={color=["red", "blue], shape="round"}}``` to string "A chair, red and blue in color and round in shape."

List are converted to comma delimited strings.

```
g.template("boy").str("The boy has #1.")
      .param(1).property("boy/coin").uniqueCount().plural();
```
Converts the ```{boy={coin=["penny", "penny", "quarter"]}}``` to string "The boy has two pennies and a quarter."
The system tries to guess plural of English objects.

```
g.template("boy").str("The boy has #1.#2")
      .param(1).property("boy/item/type").uniqueCount().plural()
      .param(2).property("boy/item").useTemplate("item");
      
g.template("item").str("The #1 is #2.")
      .param(1).property("type")
      .param(2).property("quality");
```

Converts the ```{boy={item=[{type="comb", quality="broken"}, {type="wallet", quality="leather"}]}}``` to string "The boy has a comb and a wallet. The comb is broken. The wallet is leather."
Please note how we can refer to the same object multiple times and talk about it differently.

Note: ```#0``` refers to the current object and can be used to debug.
