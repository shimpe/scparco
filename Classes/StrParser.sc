/*
[general]
title = "StrParser"
summary = "a Parser that can match a literal string"
categories = "Parsing Tools"
related = "Classes/RegexParser"
description = '''
StrParser can match a literal string in a text.
'''
*/
StrParser : Parser {
	/*
	[classmethod.new]
	description = '''creates a new StrParser'''
	[classmethod.new.args]
	str = "the string to match"
	[classmethod.new.returns]
	what = "a new StrParser"
	*/
	*new {
		| str |
		^super.new.init(str);
	}

	/*
	[method.init]
	description = '''initializes a new StrParser'''
	[method.init.args]
	str = "the string to match"
	[method.init.returns]
	what = "an initialized StrParser"
	*/
	init {
		| str |

		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var currentIndex;
			var currentTargetString;

			if (parserStateIn.isError == True) {
				parserStateIn;
			} {
				currentIndex = parserStateIn.index;
				currentTargetString = parserStateIn.target.drop(currentIndex);
				if (currentTargetString.size == 0) {
					outputState = parserStateIn.updateError(
						"strParser: Error! Expected to match '" ++ str ++ "' but found an unexpected end of input!");
				} {
					if (currentTargetString.beginsWith(str)) {
						outputState = parserStateIn.updateState(currentIndex + str.size, str);
					} {
						outputState = parserStateIn.updateError("strParser: Error! at position" +
							currentIndex + "expected to match '" ++ str ++ "' but found '" ++
							currentTargetString.keep(str.size) ++ "[...]'");
					};
				};
				//outputState.prettyprint;
				outputState;
			}
		};
	}

	/*
	[examples]
	what = '''
	(
	var text = "hello world!goodbye world!";
	var sp = StrParser("hello world!");
	var result = sp.run(text); // expected result: "hello world!"
	result.result.postcs;
	)
	'''
	*/
}
