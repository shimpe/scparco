/*
[general]
title = "ScpMany"
summary = "a specialized ScpParser that matches zero or more repeated occurrences of another ScpParser"
categories = "Parsing Tools"
related = "Classes/ScpParser, Classes/ScpManyOne"
description = '''
ScpMany takes a ScpParser and produces a new ScpParser that matches zero or more of the given ScpParser.
The parse result of a ScpMany parser is a list of parse results of the child parser.
'''
*/
ScpMany : ScpParser {
	/*
	[classmethod.new]
	description = '''creates a new ScpMany parser'''
	[classmethod.new.args]
	parser = "a ScpParser of which zero or more need to be matched"
	[classmethod.new.returns]
	what = "ScpMany"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = '''initializes a ScpMany parser'''
	[method.init.args]
	parser = "a ScpParser of which zero or more need to be matched. The parse result of a ScpMany parser is a list of parse results of the given ScpParser. This can be an empty list."
	[method.init.returns]
	what = "ScpMany"
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
				this.logStartTrace(parserStateIn, "ScpMany");
				while ({done.not}) {
					var testState = parser.parserStateTransformer.(nextState);
					if (testState.isError.not)
					{
						this.logEndTrace(parserStateIn, "ScpMany", true);
						results = results.add(testState.result);
						nextState = testState;
					} {
						this.logEndTrace(parserStateIn, "ScpMany", true);
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
	var separator = ScpStrParser("|");
	var floatp = ScpParserFactory.makeFloatParser;
	var float_separated_by_separator = ScpSequenceOf(
	    separator,
	    ScpMany(ScpSequenceOf(
	        floatp,
	        separator)));
	var result = ScpMany(ScpSequenceOf([floatp, separator])).run("3.1415|-89.23|0.12|"); // expected result [[3.1415, "|"], [ -89.23, "|"], [0.12, "|"]]
	result.result.postcs;
	)
	'''
	other = '''
	// maybe you are interested in the values only, then it's easier to use the SepBy family of parsers
	(
	var sepBy = ScpParserFactory.makeSepBy(ScpStrParser("|"));
	var separated_floats_parser = sepBy.(ScpParserFactory.makeFloatParser);
	var result = separated_floats_parser.run("3.1415|-89.23|0.12|"); // expected result: [3.1415, -89.23, 0.12]
	result.result.postcs;
	)
	'''
	*/
}