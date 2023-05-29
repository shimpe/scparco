/*
[general]
title = "Choice"
summary = "a specialized Parser that can match one of multiple possibilities"
categories = "Parsing Tools"
related = "Classes/Parser, Classes/LongestChoice"
description = '''
Choice is a Parser that matches one of multiple subparsers. Parsers are tried in the order they are supplied,
and matching stops when the first one succeeded.

Note: an easy mistake to make (and with sometimes puzzling consequences) is to forget that the parsers to be supplied to Choice must be in a list.
'''
*/
Choice : Parser {
	/*
	[classmethod.new]
	description = "new creates a new Choice Parser"
	[classmethod.new.args]
	parsers = "a list of Parsers"
	[classmethod.new.returns]
	what = "Choice"
	*/
	*new {
		| parsers |
		^super.new.init(parsers);
	}

	/*
	[method.init]
	description = "init initializes the Choice parser with a suitable parserStateTransformer function that can try successive parsers until one succeeds"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "Choice"
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
				var keepTrying = true;
				parsers.do({
					| parser, idx |
					if (keepTrying) {
						nextState = parser.parserStateTransformer.(parserStateIn);
					};
					if (nextState.isError.not)
					{
						keepTrying = false;
					};
				});
				if (nextState.isError) {
					nextState = nextState.updateError("Choice: unable to match with any parser at index" + parserStateIn.index);
				};
				nextState;
			};
		}
	}

	/*
	[examples]
	what = '''
	(
	var s1 = StrParser("hello!");
	var s2 = StrParser("bye!");
	var text = "hello!hello!bye!hello!";
	var seq = Many(Choice([s1, s2]));
	var result = seq.run(text); // [ "hello!", "hello!", "bye!", "hello!" ]
	result.result.postcs;
	)
	'''
	*/
}