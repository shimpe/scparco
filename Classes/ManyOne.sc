/*
[general]
title = "ManyOne"
summary = "a specialized Parser that matches one or more repeated occurrences of another Parser"
categories = "Parsing Tools"
related = "Classes/Parser, Classes/Many"
description = '''
Many takes a Parser and produces a new Parser that matches one or more of the given Parser.
The parse result of a Many parser is a list of parse results of the child parser.
'''
*/
ManyOne : Parser {
	/*
	[classmethod.new]
	description = '''creates a new ManyOne parser'''
	[classmethod.new.args]
	parser = "a Parser of which one or more need to be matched"
	[classmethod.new.returns]
	what = "ManyOne"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = '''initializes a ManyOne parser'''
	[method.init.args]
	parser = "a Parser of which one or more need to be matched. The parse result of a succeeded ManyOne parser is a list of parse results of the given Parser. For a failed ManyOne parser it's nil."
	[method.init.returns]
	what = "ManyOne"
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
					outputState = nextState.updateError("ManyOne: unable to match any parser at index" + nextState.index);
				} {
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
	var between = ParserFactory.makeBetween(StrParser("("), StrParser(")"));
	var intp = ParserFactory.makeIntegerParser;
	var int_between_brackets = between.(intp);
	var p = ManyOne(int_between_brackets);
	var result = p.run("(2309)(4545)(3409)"); // expected result: [2309, 4545, 3409]
	result.prettyprint;
	)
	'''
	*/
}