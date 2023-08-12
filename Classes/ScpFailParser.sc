/*
[general]
title = "ScpFailParser"
summary = "a ScpParser that always fails with a given error msg."
categories = "Parsing Tools"
related = "Classes/ScpRegexParser"
description = '''
ScpFailParser always fails and can be used to disguise something as a ScpParser that failed with a given error msg.
'''
*/
ScpFailParser : ScpParser {
	/*
	[classmethod.new]
	description = '''creates a new ScpFailParser'''
	[classmethod.new.args]
	errorMsg = "the errorMsg with which to fail"
	[classmethod.new.returns]
	what = "a new ScpFailParser"
	*/
	*new {
		| errorMsg |
		^super.new.init(errorMsg);
	}

	/*
	[method.init]
	description = '''initializes a new ScpFailParser'''
	[method.init.args]
	errorMsg = "the errorMsg with which to fail"
	[method.init.returns]
	what = "an initialized ScpFailParser"
	*/
	init {
		| errorMsg |

		this.parserStateTransformer = {
			| parserStateIn |
			var newState;
			this.logStartTrace(parserStateIn, "ScpFailParser");
			newState = parserStateIn.updateError(errorMsg);
			this.logEndTrace(parserStateIn, "ScpFailParser", false);
			newState;
		};
	}

	/*
	[examples]
	what = '''
	(
      var t = "123$56%%";
	  var fail = ScpFailParser("What were you thinking?!");
      var result = fail.run(t);
	  result.isError.postcs; // expect true
	  result.errorMsg.postcs; // expect "What were you thinking?"
	)
	'''
	*/
}
