package a;

import static a.grammar.rule.Rule.*;
import static a.Main.RuleName.*;
import static a.Main.Gender.*;
import static a.Main.Occupation.*;

import java.util.Arrays;
import java.util.Random;

import a.grammar.Grammar;
import a.grammar.rule.Rule;


public class Main {

	public static enum RuleName
	{
		PERSON, GENDER, OCCUPATION, WEALTH, ALL_JEWELLERIES,
		RING, NECKLACE, CROWN,
		
		MATERIAL, RING_SETTING,
		
		CHAIN, METAL_CHAIN, STRUNG_BEADS,
		
		PENDANT,
		
		PRECIOUS_METAL, CHEAP_METAL,
		
		GOLD, SILVER, 
		
		COPPER, BRONZE,
		
		GEMSTONE, PRECIOUS_G, SEMI_PRECIOUS_G,
		
		DIAMOND, RUBY, SAPPHIRE, EMARLD,
		
		AMETHYST, TURQUOISE, TOPAZ, PERIDOT, JADE, GARNET, ROSE_QUARTZ, OPAL,
		PEARL, BLACK_ONYX, AQUAMARINE, AMBER, JET,
		
		COMMON_BEADS, WOOD, STONE, ANTLER, QUILL, SHELL,
		
		FX, GLOW,
		
		COLOR, RED, BLUE, WHITE, BLACK,
		
		GLOW_TYPE, BEAUTIFUL, HEAVENLY, GLOOMY, EVIL
	}

	public static final String JEWELRY = "jewelry";
	
	static enum Gender { MALE("he", "him"), FEMALE("she", "her");
		public final String pronoun1, pronoun2;

		private Gender(String pronoun1, String pronoun2) {
			this.pronoun1 = pronoun1;
			this.pronoun2 = pronoun2;
		}
		
	}
	
	static enum Occupation {
		RULER, NOBLE, MERCHANT, MAGICIAN, FARMER, LUMBERJACK, SAILOR, FISHERMAN
	}
	
	// TODO!! calculators get a immutable context.
	

	public static void main(String[] args) {
		MyContext context = new MyContext();
	    Grammar<MyContext> g = Grammar.create(context);
	    
		g.rule(PERSON)
			.produces(GENDER, OCCUPATION, WEALTH, ALL_JEWELLERIES);
		
		g.rule(GENDER)
		    .pushNode("gender")
			.produces(ctx->ctx.gender = MALE)
			.produces(ctx->ctx.gender = FEMALE);
//		    .produces(MALE)
//		    .produces(FEMALE)
		    ;
		
		 g.rule(OCCUPATION)
		    .pushNode("occupation")
			.produces(ctx->ctx.occu=RULER)/*.weight(.1)*/
			.produces(ctx->ctx.occu=NOBLE)/*.weight(.2)*/
			
			.produces(ctx->ctx.occu=MERCHANT)/*.weight(.1)*/
			
			.produces(ctx->ctx.occu=MAGICIAN)/*.weight(.1)*/
			
			// .produces(ctx->ctx.occu=WARRIOR)
			
			.produces(ctx->ctx.occu=FARMER)
			.produces(ctx->ctx.occu=LUMBERJACK)
			.produces(ctx->ctx.occu=SAILOR)
			.produces(ctx->ctx.occu=FISHERMAN)
			;
		
		 g.rule(WEALTH).produces(ctx->{
			if (oneOf(ctx.occu, RULER, NOBLE, MERCHANT, MAGICIAN))
				ctx.wealth=1;
			else
				ctx.wealth=0;
			});
		
		 g.rule(ALL_JEWELLERIES)
		    .fireMany().fireAtLeast(1).fireAtMost(5).defaultFireChance(ctx->.5+ctx.wealth*.4)
			.produces(RING).fireMultipleTimes(ctx->1, ctx->1+ctx.wealth*3)
			.produces(NECKLACE)
			.produces(CROWN).condition(ctx->ctx.occu==RULER);
		
		 g.rule(RING)
		    .pushNode("jewellery")
		    .pushLeaf("type", RING)
		    .produces(ctx->ctx.addJewelry(RING)).then(MATERIAL,RING_SETTING, FX );
		 g.rule(NECKLACE)
		    .pushNode("jewellery")
		    .pushLeaf("type", NECKLACE)
		    .produces(ctx->ctx.addJewelry(NECKLACE)).then(CHAIN, PENDANT, FX);
		
		 g.rule(CROWN)
		    .pushNode("jewellery")
		    .pushLeaf("type", CROWN)
		    .produces(ctx->ctx.addJewelry(CROWN));
		
		 g.rule(CHAIN).pushNode("chain")
		           .produces(METAL_CHAIN).weight(ctx->.2+ctx.wealth*.7)
		           .produces(STRUNG_BEADS).weight(ctx->.5+(1-ctx.wealth)*.5);
		
		 g.rule(METAL_CHAIN).produces(MATERIAL);
		
		 g.rule(STRUNG_BEADS).pushNode("beads")
		                  .produces(GEMSTONE).fireMultipleTimes(ctx->1, ctx->1+ctx.wealth*2).weight(ctx->.2+ctx.wealth*3)
		                  .produces(COMMON_BEADS).weight(ctx->(1.-ctx.wealth));
		
		 g.rule(COMMON_BEADS).producesOneOf(WOOD, STONE, ANTLER, QUILL, SHELL);
		
		 g.rule(PENDANT).pushNode("pendant")
		             .produces(RING_SETTING)
		             .produces(COMMON_BEADS).weight(ctx->(1.-ctx.wealth)*2);
		
		 g.rule(MATERIAL)
		        .pushNode("material")
		        .produces(PRECIOUS_METAL).weight(ctx->.1+ctx.wealth*.9)
	            .produces(CHEAP_METAL).weight(ctx->.1+(1-ctx.wealth)*.9)
		;
		
		g.rule(PRECIOUS_METAL).produces(GOLD).produces(SILVER);
		g.rule(CHEAP_METAL).produces(BRONZE).produces(COPPER);
		
		g.rule(RING_SETTING).pushNode("setting")
		                   .produces(RULE_NONE)
		// TODO SYMBOL on the ring instead of gem
		                  .produces(GEMSTONE).fireMultipleTimes(ctx->1, ctx->1+ctx.wealth*2).weight(ctx->.2+ctx.wealth*5)
		;
		
		g.rule(GEMSTONE)
	                  .produces(PRECIOUS_G).weight(ctx->.4+ctx.wealth*.2)
		              .produces(SEMI_PRECIOUS_G).weight(ctx->.4+(1-ctx.wealth)*.2)
		;
		
		g.rule(PRECIOUS_G).producesOneOf(DIAMOND, RUBY, SAPPHIRE, EMARLD);
		g.rule(SEMI_PRECIOUS_G).producesOneOf(AMETHYST, TURQUOISE, TOPAZ, PERIDOT, JADE, GARNET, ROSE_QUARTZ, OPAL,
		        PEARL, BLACK_ONYX, AQUAMARINE, AMBER, JET);
		
		g.rule(FX)
		    .produces(RULE_NONE)
		    .produces(GLOW).condition(ctx->ctx.occu==MAGICIAN);
		
		g.rule(GLOW).pushNode("glow").produces(COLOR, GLOW_TYPE);
		
		g.rule(COLOR).pushNode("color").producesOneOf(RED, BLUE, WHITE, BLACK);
		
		g.rule(GLOW_TYPE).pushNode("type").producesOneOf(BEAUTIFUL, HEAVENLY, GLOOMY, EVIL);
		
		g.forAllRules(
		        GOLD, SILVER, 
		        
		        COPPER, BRONZE,
		        
		        DIAMOND, RUBY, SAPPHIRE, EMARLD,
		        
		        AMETHYST, TURQUOISE, TOPAZ, PERIDOT, JADE, GARNET, ROSE_QUARTZ, OPAL,
                PEARL, BLACK_ONYX, AQUAMARINE, AMBER, JET,
                
                WOOD, STONE, ANTLER, QUILL, SHELL, 
                
                RED, BLUE, WHITE, BLACK,
                
                BEAUTIFUL, HEAVENLY, GLOOMY, EVIL
		        ).pushLeaf(Rule.RULENAME_INDICATOR);
		
		// TODO: use Orthogonal concerns to specify what is expensive, what is cheap, what is more likely what is less.
		
		Random rand = new Random();
//		for (int i = 0; i < 10; i++)
//			rule(A).fire(new Context(rand));
		
		g.sanityCheck(PERSON);
		
		g.rule(PERSON).fire(context);
		
		System.out.println("\n\n");
		
//		System.out.println(ctx);
		System.out.println(context.root);
		
		
		g.template(PERSON).str("He/She is wearing #1.\n#2")
		        .param(1).property("/jewellery/type").uniqueCounted().plural()
		        .param(2).property("/jewellery").useTemplate(JEWELRY).delimiter("\n")
		        ;
		g.template(JEWELRY).delegate(RING).ifEquals("type", RING)
		                   .delegate(NECKLACE).ifEquals("type", NECKLACE)
		                   .delegate(CROWN).ifEquals("type", CROWN)
		                   .str("Some jewelry $0.");
		
		g.template(RING).str("The ring is made of #1.#2#3").param(1).property("material")
		                        .param(2).property("").ifExists("setting")
		                            .useTemplate(RING_SETTING)
		                        .param(3).property("glow").useTemplate(GLOW);
		
		g.template(RING_SETTING).str(" It is set with #1.").param(1).property("setting").uniqueCounted().plural();
		g.template(GLOW).str(" It has a #1 #2 glow.")
		                        .param(1).property("type")
		                        .param(2).property("color");
		
		g.template(NECKLACE).str("The necklace is #1#2.")
		            .param(1).property("chain/material").useTemplate(CHAIN)
		            .param(2).property("chain").ifExists("chain/beads").useTemplate(STRUNG_BEADS);
		
		g.template(CHAIN).str("a chain of #0");
		
		g.template(STRUNG_BEADS).str("strung with #1").param(1).property("beads").unique();
		
		g.template(CROWN).str("A crown.");
		
		System.out.println(g.toString(context.root, PERSON));
	}
	
	@SafeVarargs
	public static <T> boolean oneOf(T find, T... ts)
	{
		return Arrays.asList(ts).contains(find);
	}
	
}
