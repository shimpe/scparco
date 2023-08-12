/*
[general]
title = "ScpSequenceOf"
summary = "a ScpParser that tries to match multiple Parsers and stops when the first one succeeds"
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpSequenceOf takes a list of Parsers, and tries to match them one by one (in the order in which they were supplied). All Parsers in a ScpSequenceOf parser must succeed for the ScpSequenceOf to succeed.

The parse result of a ScpSequenceOf is a list of parse results of each of the parsers that have run.

Note: an easy mistake to make (and with sometimes puzzling consequences) is to forget that the parsers to be supplied to ScpSequenceOf must be in a list.
'''
*/
ScpSequenceOf : ScpParser {
	/*
	[classmethod.new]
	description = '''makes a new ScpSequenceOf parser'''
	[classmethod.new.args]
	parsers = '''a list of ScpParser'''
	*/
	*new {
		| parsers |
		^super.new.init(parsers);
	}

	/*
	[method.init]
	description = '''initializes a new ScpSequenceOf parser'''
	[classmethod.init.args]
	parsers = '''a list of ScpParser'''
	[classmethod.init.returns]
	a ScpParser that parses a Sequence of user supplied parsers
	*/
	init {
		| parsers |
		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var results = [];
				var keepGoing = true;
				var nextState = parserStateIn;
				this.logStartTrace(parserStateIn, "ScpSequenceOf");
				parsers.do({
					| parser, idx |
					if (keepGoing) {
						nextState = parser.parserStateTransformer.(nextState);
					};
					if (nextState.isError == true)
					{
						keepGoing = false;
					} {
						results = results.add(nextState.result);
					}
				});
				if (keepGoing) {
					this.logEndTrace(parserStateIn, "ScpSequenceOf", true);
					outputState = nextState.updateResult(results);
				} {
					this.logEndTrace(parserStateIn, "ScpSequenceOf", false);
					outputState = parserStateIn.updateError("Couldn't match sequence at index" + nextState.index);
				};
				outputState;
			};
		}
	}
}
