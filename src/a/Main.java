package a;

import static a.grammar.rule.Rule.*;
import static a.Main.RuleName.*;
import static a.Main.Gender.*;
import static a.Main.Occupation.*;

import java.util.Arrays;
import java.util.Random;

import a.grammar.Grammar;
import a.grammar.common.Action;
import a.grammar.rule.Rule;
import a.grammar.utils.CVC;


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
		
		GLOW_TYPE, BEAUTIFUL, HEAVENLY, GLOOMY, EVIL,
		
		FANCY_RING_SETTING, RING_SETTING_TYPES, FANCY_RING_SETTING_GEMS,
		CENTERPIECE, HALO_DECO, HALO_GEMSTONES, HALO_GEMSTONE_SHAPE, HALO_SHAPE,
		HALO_DECO_MAYBE, HALO_COUNT, FILIGREE_MILGRAIN, FILIGREE_MILGRAIN2,
		ENGRAVING, ENGRAVING_SUBJECT,
		
		SHAPE, GEOM_SHAPE, ORGANIC_SHAPE, QUALITY,
		
		SIMPLE_COUNTS, COUNT_EXACT_ONE_TO_FIVE, COUNT_EXACT_SIX_TO_TEN, COUNT_EXACT_TEN_TO_FIFTEEN,
		
		DEITY, INFLUENCE_SPHERES, HEAD_MAYBE, DEITY_NAME,
		
		BIG_ANIMALS, BIRDS, CREATURE
		
		
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
			.produces(GENDER, OCCUPATION, WEALTH, "name", ALL_JEWELLERIES);
		
		g.rule("name")
		    .pushNode("name")
		    .producesRunTimeName(
		            ctx->ctx.wealth>.5?CVC.getFancyName(ctx.rand):CVC.getCVC(ctx.rand, 5))// hack, producing leaf here
		    ;
		g.rule(GENDER)
		    .pushNode("gender")
			.producesRunTimeName(ctx->ctx.gender = MALE)
			.producesRunTimeName(ctx->ctx.gender = FEMALE);
//		    .produces(MALE)
//		    .produces(FEMALE)
		    ;
		
		 g.rule(OCCUPATION)
		    .pushNode("occupation")
			.producesRunTimeName(ctx->ctx.occu=RULER)/*.weight(.1)*/
			.producesRunTimeName(ctx->ctx.occu=NOBLE)/*.weight(.2)*/
			
			.producesRunTimeName(ctx->ctx.occu=MERCHANT)/*.weight(.1)*/
			
			.producesRunTimeName(ctx->ctx.occu=MAGICIAN)/*.weight(.1)*/
			
			// .produces(ctx->ctx.occu=WARRIOR)
			
			.producesRunTimeName(ctx->ctx.occu=FARMER)
			.producesRunTimeName(ctx->ctx.occu=LUMBERJACK)
			.producesRunTimeName(ctx->ctx.occu=SAILOR)
			.producesRunTimeName(ctx->ctx.occu=FISHERMAN)
			;
		
		 g.rule(WEALTH).producesAction(ctx->{
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
		    .producesAction(ctx->ctx.addJewelry(RING)).then(MATERIAL, RING_SETTING_TYPES, FX );
		 
		 g.rule(RING_SETTING_TYPES)
		     .produces(RULE_NONE)
//		     .produces(RING_SETTING)
		     .produces(FANCY_RING_SETTING).weight(ctx->.2+ctx.wealth*5)
		     ;
		 
		 g.rule(FANCY_RING_SETTING)
		     .pushNode("fancy")
		     .produces(FANCY_RING_SETTING_GEMS)
		     //produces FANCY_RING_MOTIF
		     ;
		 
		 
		 g.rule(FANCY_RING_SETTING_GEMS)
		     .produces(CENTERPIECE, HALO_DECO_MAYBE, FILIGREE_MILGRAIN2)
		     ;
		 
		 g.rule(FILIGREE_MILGRAIN2).pushNode("fil_stuff").produces(FILIGREE_MILGRAIN, QUALITY);
		 
		 g.rule(HALO_DECO_MAYBE)
		     .produces(RULE_NONE)
		     .produces(HALO_DECO)
		     ;
		 
		 g.rule(CENTERPIECE)
		     .pushNode("centerpiece")
		     .produces(GEMSTONE)
		     // produces CLUSTER
		     ;
		 
		 g.rule(HALO_DECO)
		     .pushNode("halo")
		     .produces(HALO_GEMSTONES, HALO_GEMSTONE_SHAPE, HALO_SHAPE, HALO_COUNT)
		     ;
		 
		 g.rule(HALO_COUNT)
		     .pushNode("count")
		     .produces(SIMPLE_COUNTS)
		     .produces(COUNT_EXACT_SIX_TO_TEN)
		     .produces(COUNT_EXACT_TEN_TO_FIFTEEN)
		     ;
		 
		 g.rule(HALO_GEMSTONES)
		     .pushNode("gem")
		     .produces(GEMSTONE).fireMultipleTimes(ctx->1, ctx->3);
		 
		 g.rule(HALO_GEMSTONE_SHAPE)
		     .pushNode("gem_shape")
		     .produces(RULE_NONE)
		     .produces(SHAPE)
		     ;
		 
		 g.rule(HALO_SHAPE)
		     .pushNode("shape")
		     .produces(RULE_NONE)
             .produces(GEOM_SHAPE)
             ;
		 
		 g.rule(FILIGREE_MILGRAIN)
		     .pushNode("filigree")
		     .produces(RULE_NONE).weight(2)
		     .producesOneOf("filigree", "milgrain", "filigree and milgrain")/*.then(QUALITY)*/
		     ;
		 
		 g.rule(SHAPE)
		     .producesOneOf(ORGANIC_SHAPE, GEOM_SHAPE);
		 
		 g.rule(QUALITY)
		     .pushNode("quality")
		     .produces(RULE_NONE)
             .producesOneOf("fine", "artistic", "delicate");
		 
		 g.rule(ORGANIC_SHAPE).producesOneOf("petal", "leaf", "teardrop");
		 
		 g.rule(GEOM_SHAPE).producesOneOf("oval", "round", "square", "rectangular");
		 
		 g.rule(SIMPLE_COUNTS).producesOneOf("a few", "many", "countless", "a handful of");
		 g.rule(COUNT_EXACT_ONE_TO_FIVE).producesOneOf("one", "two", "three", "four", "five");
		 g.rule(COUNT_EXACT_SIX_TO_TEN).producesOneOf("six", "seven", "eight", "nine", "ten");
		 g.rule(COUNT_EXACT_TEN_TO_FIFTEEN)
		     .producesOneOf("eleven", "twelve", "thirteen", "fourteen", "fifteen");
		 
		 g.rule(NECKLACE)
		    .pushNode("jewellery")
		    .pushLeaf("type", NECKLACE)
		    .producesAction(ctx->ctx.addJewelry(NECKLACE)).then(CHAIN, PENDANT, FX);
		
		 g.rule(CROWN)
		    .pushNode("jewellery")
		    .pushLeaf("type", CROWN)
		    .producesAction(ctx->ctx.addJewelry(CROWN));
		
		 g.rule(CHAIN).pushNode("chain")
		           .produces(METAL_CHAIN).weight(ctx->.2+ctx.wealth*.7)
		           .produces(STRUNG_BEADS).weight(ctx->.5+(1-ctx.wealth)*.5);
		
		 g.rule(METAL_CHAIN).produces(MATERIAL);
		
		 g.rule(STRUNG_BEADS).pushNode("beads")
		                  .produces(GEMSTONE).fireMultipleTimes(ctx->1, ctx->1+ctx.wealth*2).weight(ctx->.2+ctx.wealth*3)
		                  .produces(COMMON_BEADS).weight(ctx->(1.-ctx.wealth));
		
		 g.rule(COMMON_BEADS).producesOneOf(WOOD, STONE, ANTLER, QUILL, SHELL);
		
		 g.rule(PENDANT).pushNode("pendant")
		             .produces(RULE_NONE)
		             .produces(RING_SETTING).weight(ctx->.2+ctx.wealth)
		             .produces(ENGRAVING);
		
		 g.rule(MATERIAL)
		        .pushNode("material")
		        .produces(PRECIOUS_METAL).weight(ctx->.1+ctx.wealth*.9)
	            .produces(CHEAP_METAL).weight(ctx->.1+(1-ctx.wealth)*.9)
		;
		
		g.rule(PRECIOUS_METAL).produces(GOLD).produces(SILVER);
		g.rule(CHEAP_METAL).produces(BRONZE).produces(COPPER);
		
		g.rule(RING_SETTING).pushNode("setting")
		// TODO SYMBOL on the ring instead of gem
		                  .produces(GEMSTONE).fireMultipleTimes(ctx->1, ctx->1+ctx.wealth*2)
		;
		
		g.rule(GEMSTONE)
	                  .produces(PRECIOUS_G).weight(ctx->.4+ctx.wealth*.2)
		              .produces(SEMI_PRECIOUS_G).weight(ctx->.4+(1-ctx.wealth)*.2)
		;
		
		g.rule(PRECIOUS_G).producesOneOf(DIAMOND, RUBY, SAPPHIRE, EMARLD);
		g.rule(SEMI_PRECIOUS_G).producesOneOf(AMETHYST, TURQUOISE, TOPAZ, PERIDOT, JADE, GARNET, ROSE_QUARTZ, OPAL,
		        PEARL, BLACK_ONYX, AQUAMARINE, AMBER, JET);
		
		g.rule(ENGRAVING).pushNode("engraving")
		    .produces(MATERIAL, ENGRAVING_SUBJECT);
		
		g.rule(ENGRAVING_SUBJECT).pushNode("subject")
		    .produces("engraving_creature")
		    .produces(DEITY)
		;
		
		g.rule("engraving_creature").pushNode("creature").produces(CREATURE);
		
		g.rule(FX)
		    .produces(RULE_NONE)
		    .produces(GLOW).condition(ctx->ctx.occu==MAGICIAN);
		
		g.rule(GLOW).pushNode("glow").produces(COLOR, GLOW_TYPE);
		
		g.rule(COLOR).pushNode("color").producesOneOf(RED, BLUE, WHITE, BLACK);
		
		g.rule(GLOW_TYPE).pushNode("type").producesOneOf("faint", "barely perceiptible", BEAUTIFUL, HEAVENLY, GLOOMY, EVIL);

		
		g.rule(INFLUENCE_SPHERES).pushNode("sphere")
		    .producesOneOf("harvest", "home", "war", "courage", "river", "sea",
                           "mountain", "earth", "love", "passion", "wine", "happiness",
                           "mischief")
		;
		
		g.rule(BIG_ANIMALS).producesOneOf("tiger", "lion", "jackal", "wolf", "bison", "ox", "cow", "elephant", "seal",
		                                   "leopard", "horse", "cheetah", "bear")
		;
		
		g.rule(BIRDS).producesOneOf("hawk", "eagle", "heron", "falcon", "peacock", "dove", "pigeon", 
		        "stork", "parrot", "cockatiel", "emu", "swallow")
		;
		
		g.rule(CREATURE).producesOneOf(BIG_ANIMALS, BIRDS);
		
		g.rule(DEITY).pushNode("deity")
		    .produces(DEITY_NAME, HEAD_MAYBE, INFLUENCE_SPHERES)// TODO: blessed creature should be generated here, but cannot with the features right now.
		;
		
		g.rule(DEITY_NAME)
          .pushNode("name")
          .producesRunTimeName(
              ctx->CVC.getCVC(ctx.rand, 5))// hack, producing leaf here
           ;
		
		g.rule(HEAD_MAYBE).pushNode("head")
	                .produces(RULE_NONE)/*.weight(2)*/
		            .produces(CREATURE)
		            ;
		
		// TODO: use Orthogonal concerns to specify what is expensive, what is cheap, what is more likely what is less.
		
		g.template(PERSON).str("#5 is a #4. #3 is wearing #1.\n#2")
		        .param(1).property("/jewellery/type").uniqueCounted().plural()
		        .param(2).property("/jewellery").useTemplate(JEWELRY).delimiter("\n")
		        .param(3).runtimeValue(ctx->((MyContext)ctx).gender).genderSubjective().capFirst()
		        .param(4).runtimeValue(ctx->((MyContext)ctx).occu)
		        .param(5).property("name").capFirst()
		        ;
		g.template(JEWELRY).delegate(RING).ifEquals("type", RING)
		                   .delegate(NECKLACE).ifEquals("type", NECKLACE)
		                   .delegate(CROWN).ifEquals("type", CROWN)
		                   .str("Some jewelry $0.");
		
		g.template(RING).str("The ring is made of #1.#2#4#3").param(1).property("material")
		                        .param(2).property("").ifExists("setting")
		                            .useTemplate(RING_SETTING)
		                        .param(3).property("glow").useTemplate(GLOW)
		                        .param(4).property("fancy")
                                    .useTemplate(FANCY_RING_SETTING);
		
		g.template(FANCY_RING_SETTING).str(" The centerpiece of the ring is a #1.#2#3")
		        .param(1).property("centerpiece")
		        .param(2).property("halo").useTemplate(HALO_DECO)
		        .param(3).property("fil_stuff").ifExists("fil_stuff/filigree")
		                .useTemplate(FILIGREE_MILGRAIN)
		        ;
		
		g.template(HALO_DECO).str(" It is accented by #3 #2#1#4.")
		    .param(1).property("gem").unique().plural()
		    .param(2).property("gem_shape").useTemplate(HALO_GEMSTONE_SHAPE)
		    .param(3).property("count")
		    .param(4).property("shape").useTemplate(HALO_SHAPE);
		
		g.template(HALO_SHAPE).str(" in a #0 shape");
		
		g.template(FILIGREE_MILGRAIN).str(" The piece has #1#2 detail.")
		    .param(1).property("quality").spaceAfter()
		    .param(2).property("filigree")
//		    .param(3).property("filigree_shape")
		    ;
		
		g.template(HALO_GEMSTONE_SHAPE).str("#0 shaped ");
		
		g.template(RING_SETTING).str(" It is set with #1.").param(1).property("setting").uniqueCounted().plural();
		
		g.template(GLOW).str(" It has a #1 #2 glow.")
		                        .param(1).property("type")
		                        .param(2).property("color");
		
		g.template(NECKLACE).str("The necklace is #1#2.#3")
		            .param(1).property("chain/material").useTemplate(CHAIN)
		            .param(2).property("chain").ifExists("chain/beads").useTemplate(STRUNG_BEADS)
		            .param(3).property("pendant").useTemplate(PENDANT);
		
		g.template(CHAIN).str("a chain of #0");
		
		g.template(STRUNG_BEADS).str("a strand of #1 beads").param(1).property("beads").unique();
		
		g.template(PENDANT).str(" The #3pendant is #1#2.")
		    .param(1).property("").ifExists("setting").useTemplate("pendant_setting")
		    .param(2).property("engraving").useTemplate(ENGRAVING)
		    .param(3).property("engraving/material").spaceAfter()
		    ;
		
		g.template("pendant_setting").str("set with #1")
		    .param(1).property("setting").unique().plural()
		;
		
		g.template(ENGRAVING).str("engraved with the image of #1#2, ")
		    .param(1).property("subject").ifExists("subject/deity").useTemplate(DEITY)
		    .param(2).property("subject/creature").uniqueCounted(); // provides a/an
		
		g.template(DEITY).str("#3 #1god of #2")
		    .param(1).property("deity/head").useTemplate("god_head")
		    .param(2).property("deity/sphere")
		    .param(3).property("deity/name").capFirst()
		    ;
		
		g.template("god_head").str("#0-headed ");
		
		g.template(CROWN).str(""/*"A crown."*/);
		
		//---------------------------------------------------------------------------------------
		
		
	      g.sanityCheck(PERSON);
	        
        for (int i = 0; i < 10; i++)
        {
            context = new MyContext();
            g.newContext(context, System.nanoTime());
            
            g.rule(PERSON).fire(context);
            
    //      System.out.println(ctx);
            System.out.println(context.root);
            
            System.out.println(g.toString(context, PERSON));
            System.out.println("\n");
        }
	        


	}
	
	@SafeVarargs
	public static <T> boolean oneOf(T find, T... ts)
	{
		return Arrays.asList(ts).contains(find);
	}
	
}
