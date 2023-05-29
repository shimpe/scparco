/*
[general]
title = "Optional"
summary = "a specialized Parser that can match zero or one Parser"
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
Optional is a Parser that matches zero or one Parser. If it fails, it returns a result of nil.
'''
*/
Optional : Parser {
	/*
	[classmethod.new]
	description = "new creates a new Optional Parser"
	[classmethod.new.args]
	parser = "a Parser"
	[classmethod.new.returns]
	what = "Optional"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = "init initializes the Optional parser with a suitable parserStateTransformer function that tries to match but succeeds even if the match fails. A failed Optional returns a parse result of nil."
	[method.init.args]
	parser = "a Parser"
	[method.init.returns]
	what = "Optional"
	*/
	init {
		| parser |
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var nextState = parserStateIn.deepCopy();
				nextState = parser.parserStateTransformer.(nextState);
				if (nextState.isError)
				{
					nextState = nextState.updateResult(nil);
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
	var s2 = Optional(StrParser("bye!"));
	var text = "hello!hello!";
	var seq = SequenceOf([s1,s2,s1]);
	var result = seq.run(text); // [ "hello!", nil, "hello!" ]
	result.result.postcs;
	)
	'''
	*/
}