/*
[general]
title = "ScpManyOne"
summary = "a specialized ScpParser that matches one or more repeated occurrences of another ScpParser"
categories = "Parsing Tools"
related = "Classes/ScpParser, Classes/ScpMany"
description = '''
ScpMany takes a ScpParser and produces a new ScpParser that matches one or more of the given ScpParser.
The parse result of a ScpMany parser is a list of parse results of the child parser.
'''
*/
ScpManyOne : ScpParser {
	/*
	[classmethod.new]
	description = '''creates a new ScpManyOne parser'''
	[classmethod.new.args]
	parser = "a ScpParser of which one or more need to be matched"
	[classmethod.new.returns]
	what = "ScpManyOne"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = '''initializes a ScpManyOne parser'''
	[method.init.args]
	parser = "a ScpParser of which one or more need to be matched. The parse result of a succeeded ScpManyOne parser is a list of parse results of the given ScpParser. For a failed ScpManyOne parser it's nil."
	[method.init.returns]
	what = "ScpManyOne"
	*/
	init {
		| parser |
		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var nextState = parserStateIn;
			var results = [];
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var done = false;
				var nextState = parserStateIn;
				this.logStartTrace(parserStateIn, "ScpManyOne");
				while ({done.not}) {
					nextState = parser.parserStateTransformer.(nextState);
					if (nextState.isError.not)
					{
						results = results.add(nextState.result);
					} {
						done = true;
					};
				};

				if (results.size == 0) {
					this.logEndTrace(parserStateIn, "ScpManyOne", false);
					outputState = nextState.updateError("ScpManyOne: unable to match any parser at index" + nextState.index);
				} {
					this.logEndTrace(parserStateIn, "ScpManyOne", true);
					outputState = nextState.updateResult(results);
				};
				outputState;
			};
		}
	}

	/*
	[examples]
	what = '''
	(
	var between = ScpParserFactory.makeBetween(ScpStrParser("("), ScpStrParser(")"));
	var intp = ScpParserFactory.makeIntegerParser;
	var int_between_brackets = between.(intp);
	var p = ScpManyOne(int_between_brackets);
	var result = p.run("(2309)(4545)(3409)"); // expected result: [2309, 4545, 3409]
	result.prettyprint;
	)
	'''
	*/
}