/*
[general]
title = "SequenceOf"
summary = "a Parser that tries to match multiple Parsers and stops when the first one succeeds"
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
SequenceOf takes a list of Parsers, and tries to match them one by one (in the order in which they were supplied). All Parsers in a SequenceOf parser must succeed for the SequenceOf to succeed.

The parse result of a SequenceOf is a list of parse results of each of the parsers that have run.

Note: an easy mistake to make (and with sometimes puzzling consequences) is to forget that the parsers to be supplied to SequenceOf must be in a list.
'''
*/
SequenceOf : Parser {
	/*
	[classmethod.new]
	description = '''makes a new SequenceOf parser'''
	[classmethod.new.args]
	parsers = '''a list of Parser'''
	*/
	*new {
		| parsers |
		^super.new.init(parsers);
	}

	/*
	[method.init]
	description = '''initializes a new SequenceOf parser'''
	[classmethod.init.args]
	parsers = '''a list of Parser'''
	[classmethod.init.returns]
	a Parser that parses a Sequence of user supplied parsers
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
					outputState = nextState.updateResult(results);
				} {
					outputState = parserStateIn.updateError("Couldn't match sequence at index" + nextState.index);
				};
				outputState;
			};
		}
	}
}
