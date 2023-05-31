/*
[general]
title = "FailParser"
summary = "a Parser that always fails with a given error msg."
categories = "Parsing Tools"
related = "Classes/RegexParser"
description = '''
FailParser always fails and can be used to disguise something as a Parser that failed with a given error msg.
'''
*/
FailParser : Parser {
	/*
	[classmethod.new]
	description = '''creates a new FailParser'''
	[classmethod.new.args]
	errorMsg = "the errorMsg with which to fail"
	[classmethod.new.returns]
	what = "a new FailParser"
	*/
	*new {
		| errorMsg |
		^super.new.init(errorMsg);
	}

	/*
	[method.init]
	description = '''initializes a new FailParser'''
	[method.init.args]
	errorMsg = "the errorMsg with which to fail"
	[method.init.returns]
	what = "an initialized FailParser"
	*/
	init {
		| errorMsg |

		this.parserStateTransformer = {
			| parserStateIn |
			parserStateIn.updateError(errorMsg);
		};
	}

	/*
	[examples]
	what = '''
	(
      var t = "123$56%%";
	  var fail = FailParser("What were you thinking?!");
      var result = fail.run(t);
	  result.isError.postcs; // expect true
	  result.errorMsg.postcs; // expect "What were you thinking?"
	)
	'''
	*/
}
