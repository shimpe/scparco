/*
[general]
title = "ScpSucceedParser"
summary = "a ScpParser that always succeeds."
categories = "Parsing Tools"
related = "Classes/ScpRegexParser"
description = '''
ScpSucceedParser always succeeds and can be used to disguise something as a ScpParser that has thing as its result
'''
*/
ScpSucceedParser : ScpParser {
	/*
	[classmethod.new]
	description = '''creates a new ScpSucceedParser'''
	[classmethod.new.args]
	thing = "the thing to disguise as a ScpParser with thing as parse result"
	[classmethod.new.returns]
	what = "a new ScpSucceedParser"
	*/
	*new {
		| thing |
		^super.new.init(thing);
	}

	/*
	[method.init]
	description = '''initializes a new ScpSucceedParser'''
	[method.init.args]
	thing = "the thing to disguise as a ScpParser that has thing as parse result"
	[method.init.returns]
	what = "an initialized ScpSucceedParser"
	*/
	init {
		| thing |

		this.parserStateTransformer = {
			| parserStateIn |
			var newState;
			this.logStartTrace(parserStateIn, "ScpSucceedParser");
			newState = parserStateIn.updateResult(thing);
			this.logEndTrace(parserStateIn, "ScpSucceedParser", true);
			newState;
		};
	}

	/*
	[examples]
	what = '''
	(
      var t = "123$56%%";
      var success = ScpSucceedParser(t);
      var result = success.run(t);
	  result.isError.postcs; // expect false
      result.result.postcs; // expect "123$56%%"
	)
	'''
	*/
}
