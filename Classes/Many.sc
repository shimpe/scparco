/*
[general]
title = "Many"
summary = "a specialized Parser that matches zero or more repeated occurrences of another Parser"
categories = "Parsing Tools"
related = "Classes/Parser, Classes/ManyOne"
description = '''
Many takes a Parser and produces a new Parser that matches zero or more of the given Parser.
The parse result of a Many parser is a list of parse results of the child parser.
'''
*/
Many : Parser {
	/*
	[classmethod.new]
	description = '''creates a new Many parser'''
	[classmethod.new.args]
	parser = "a Parser of which zero or more need to be matched"
	[classmethod.new.returns]
	what = "Many"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = '''initializes a Many parser'''
	[method.init.args]
	parser = "a Parser of which zero or more need to be matched. The parse result of a Many parser is a list of parse results of the given Parser. This can be an empty list."
	[method.init.returns]
	what = "Many"
	*/
	init {
		| parser |
		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var nextState = parserStateIn;
			var results = [];
			if (parserStateIn.isError == true) {
				outputState = parserStateIn;
			} {
				var done = false;
				var nextState = parserStateIn;
				while ({done.not}) {
					var testState = parser.parserStateTransformer.(nextState);
					if (testState.isError.not)
					{
						results = results.add(testState.result);
						nextState = testState;
					} {
						done = true;
					};
				};
				outputState = nextState.updateResult(results);
			};
			outputState;
		}
	}
	/*
	[examples]
	what = '''
	(
	var separator = StrParser("|");
	var floatp = ParserFactory.makeFloatParser;
	var float_separated_by_separator = SequenceOf(
	    separator,
	    Many(SequenceOf(
	        floatp,
	        separator)));
	var result = Many(SequenceOf([floatp, separator])).run("3.1415|-89.23|0.12|"); // expected result [[3.1415, "|"], [ -89.23, "|"], [0.12, "|"]]
	result.result.postcs;
	)
	'''
	other = '''
	// maybe you are interested in the values only, then it's easier to use the SepBy family of parsers
	(
	var sepBy = ParserFactory.makeSepBy(StrParser("|"));
	var separated_floats_parser = sepBy.(ParserFactory.makeFloatParser);
	var result = separated_floats_parser.run("3.1415|-89.23|0.12|"); // expected result: [3.1415, -89.23, 0.12]
	result.result.postcs;
	)
	'''
	*/
}