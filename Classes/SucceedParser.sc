/*
[general]
title = "SucceedParser"
summary = "a Parser that always succeeds."
categories = "Parsing Tools"
related = "Classes/RegexParser"
description = '''
SucceedParser always succeeds and can be used to disguise something as a Parser that has thing as its result
'''
*/
SucceedParser : Parser {
	/*
	[classmethod.new]
	description = '''creates a new SucceedParser'''
	[classmethod.new.args]
	thing = "the thing to disguise as a Parser with thing as parse result"
	[classmethod.new.returns]
	what = "a new SucceedParser"
	*/
	*new {
		| thing |
		^super.new.init(thing);
	}

	/*
	[method.init]
	description = '''initializes a new SucceedParser'''
	[method.init.args]
	thing = "the thing to disguise as a Parser that has thing as parse result"
	[method.init.returns]
	what = "an initialized SucceedParser"
	*/
	init {
		| thing |

		this.parserStateTransformer = {
			| parserStateIn |
			parserStateIn.updateResult(thing);
		};
	}

	/*
	[examples]
	what = '''
	(
      var t = "123$56%%";
      var success = SucceedParser(t);
      var result = success.run(t);
	  result.isError.postcs; // expect false
      result.result.postcs; // expect "123$56%%"
	)
	'''
	*/
}
