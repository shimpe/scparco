/*
[general]
title = "ScpOptional"
summary = "a specialized ScpParser that can match zero or one ScpParser"
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpOptional is a ScpParser that matches zero or one ScpParser. If it fails, it returns a result of nil.
'''
*/
ScpOptional : ScpParser {
	/*
	[classmethod.new]
	description = "new creates a new ScpOptional ScpParser"
	[classmethod.new.args]
	parser = "a ScpParser"
	[classmethod.new.returns]
	what = "ScpOptional"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = "init initializes the ScpOptional parser with a suitable parserStateTransformer function that tries to match but succeeds even if the match fails. A failed ScpOptional returns a parse result of nil."
	[method.init.args]
	parser = "a ScpParser"
	[method.init.returns]
	what = "ScpOptional"
	*/
	init {
		| parser |
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var nextState = parserStateIn.deepCopy();
				this.logStartTrace(parserStateIn, "ScpOptional");
				nextState = parser.parserStateTransformer.(nextState);
				if (nextState.isError)
				{
					this.logEndTrace(parserStateIn, "ScpOptional", false);
					nextState = nextState.updateResult(nil);
				} {
					this.logEndTrace(parserStateIn, "ScpOptional", true);
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
	var s2 = ScpOptional(ScpStrParser("bye!"));
	var text = "hello!hello!";
	var seq = ScpSequenceOf([s1,s2,s1]);
	var result = seq.run(text); // [ "hello!", nil, "hello!" ]
	result.result.postcs;
	)
	'''
	*/
}