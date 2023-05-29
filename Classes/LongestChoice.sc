/*
[general]
title = "LongestChoice"
summary = "a specialized Parser that can match multiple parsers and choose the one that consumes most text"
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
LongestChoice is a Parser that matches multiple subparsers. All parsers are tried and the one that consumes most text is chosen. If multiple of the most-consuming parsers consume the same amount of text, the first one in the list wins.

Note: an easy mistake to make (and with sometimes puzzling consequences) is to forget that the parsers to be supplied to LongestChoice must be in a list.
'''
*/
LongestChoice : Parser {
	/*
	[classmethod.new]
	description = "new creates a new LongestChoice Parser"
	[classmethod.new.args]
	parsers = "a list of Parsers"
	[classmethod.new.returns]
	what = "LongestChoice"
	*/
	*new {
		| parsers |
		^super.new.init(parsers);
	}

	/*
	[method.init]
	description = "init initializes the LongestChoice parser with a suitable parserStateTransformer function that can try all parsers and go with the one that succeeds and consumes most text"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "LongestChoice"
	*/
	init {
		| parsers |
		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var nextState = parserStateIn;
			var results = [];
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var bestNextState = parserStateIn;
				var oneSucceeded = false;
				parsers.do({
					| parser, idx |
					var testState = parser.parserStateTransformer.(parserStateIn);
					if (testState.isError.not)
					{
						oneSucceeded = true;
						if (testState.index > bestNextState.index)
						{
							bestNextState = testState;
						}
					};
				});
				if (oneSucceeded.not) {
					bestNextState = bestNextState.updateError("LongestChoice: unable to match with any parser at index" + parserStateIn.index);
				};
				bestNextState;
			};
		}
	}

	/*
	[examples]
	what = '''
	(
	var lc = LongestChoice([
	    ParserFactory.makeIntegerParser.map({|x| (\type : \int, \value : x) }),
	    ParserFactory.makeFloatParser.map({|x| (\type: \float, \value: x)})
	]);
	var c = Choice([
		ParserFactory.makeIntegerParser.map({|x| (\type : \int, \value : x) }),
	    ParserFactory.makeFloatParser.map({|x| (\type: \float, \value: x)})
	]);
	var result1_longestchoice = lc.run("3.14"); // expected result: (\type: \float, \value : 3.14)
	var result2_longestchoice = lc.run("3"); // expected result: (\type: \int, \value: 3)
	var result3_choice = c.run("3.14"); // expected result: (\type: \int, \value: 3)
	var result4_choice = c.run("3"); // expected result: (\type: \int, \value: 3)
	result1_longestchoice.result.debug("longest choice on float");
	result2_longestchoice.result.debug("longest choice on int");
	result3_choice.result.debug("choice on float");
	result4_choice.result.debug("choice on int");
	)
	'''
	*/
}