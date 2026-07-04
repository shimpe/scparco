/*
[general]
title = "ScpChoice"
summary = "a specialized ScpParser that can match one of multiple possibilities"
categories = "Parsing Tools"
related = "Classes/ScpParser, Classes/ScpLongestChoice"
description = '''
ScpChoice is a ScpParser that matches one of multiple subparsers. Parsers are tried in the order they are supplied,
and matching stops when the first one succeeded.

Note: an easy mistake to make (and with sometimes puzzling consequences) is to forget that the parsers to be supplied to ScpChoice must be in a list.
'''
*/
ScpChoice : ScpParser {
	/*
	[classmethod.new]
	description = "new creates a new ScpChoice ScpParser"
	[classmethod.new.args]
	parsers = "a list of Parsers"
	[classmethod.new.returns]
	what = "ScpChoice"
	*/
	*new {
		| parsers |
		^super.new.init(parsers);
	}

	/*
	[method.init]
	description = "init initializes the ScpChoice parser with a suitable parserStateTransformer function that can try successive parsers until one succeeds"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "ScpChoice"
	*/
	init {
		| parsers |
		this.parserStateTransformer = {
			| parserStateIn |
			var nextState = parserStateIn;
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var keepTrying = true;
				// track the alternative that advanced FURTHEST into the input:
				// its (specific) error is the most relevant one to report
				var furthestError = nil;
				this.logStartTrace(parserStateIn, "ScpChoice");
				parsers.do({
					| parser, idx |
					if (keepTrying) {
						var testState = parser.parserStateTransformer.(parserStateIn);
						if (testState.isError.not) {
							nextState = testState;
							keepTrying = false;
						} {
							if (furthestError.isNil or: { testState.index > furthestError.index }) {
								furthestError = testState;
							};
						};
					};
				});
				if (keepTrying) {
					// no alternative matched -> surface the furthest-advancing failure
					// (instead of a generic "unable to match" message)
					this.logEndTrace(parserStateIn, "ScpChoice", false);
					nextState = furthestError ?? {
						parserStateIn.updateError("ScpChoice: no parsers to match at index" + parserStateIn.index)
					};
				} {
					this.logEndTrace(parserStateIn, "ScpChoice", true);
				};
				nextState;
			};
		}
	}

	/*
	[examples]
	what = '''
	(
	var s1 = ScpStrParser("hello!");
	var s2 = ScpStrParser("bye!");
	var text = "hello!hello!bye!hello!";
	var seq = ScpMany(ScpChoice([s1, s2]));
	var result = seq.run(text); // [ "hello!", "hello!", "bye!", "hello!" ]
	result.result.postcs;
	)
	'''
	*/
}