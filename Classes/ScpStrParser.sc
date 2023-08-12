/*
[general]
title = "ScpStrParser"
summary = "a ScpParser that can match a literal string"
categories = "Parsing Tools"
related = "Classes/ScpRegexParser"
description = '''
ScpStrParser can match a literal string in a text.
'''
*/
ScpStrParser : ScpParser {
	/*
	[classmethod.new]
	description = '''creates a new ScpStrParser'''
	[classmethod.new.args]
	str = "the string to match"
	[classmethod.new.returns]
	what = "a new ScpStrParser"
	*/
	*new {
		| str |
		^super.new.init(str);
	}

	/*
	[method.init]
	description = '''initializes a new ScpStrParser'''
	[method.init.args]
	str = "the string to match"
	[method.init.returns]
	what = "an initialized ScpStrParser"
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
				this.logStartTrace(parserStateIn, "ScpStrParser(\""++str++"\")");
				currentIndex = parserStateIn.index;
				currentTargetString = parserStateIn.target.drop(currentIndex);
				if (currentTargetString.size == 0) {
					outputState = parserStateIn.updateError(
						"ScpStrParser: Error! Expected to match '" ++ str ++ "' but found an unexpected end of input!");
				} {
					if (currentTargetString.beginsWith(str)) {
						this.logEndTrace(parserStateIn, "ScpStrParser(\""++str++"\")", true);
						outputState = parserStateIn.updateState(currentIndex + str.size, str);
					} {
						this.logEndTrace(parserStateIn, "ScpStrParser(\""++str++"\")", false);
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
	var sp = ScpStrParser("hello world!");
	var result = sp.run(text); // expected result: "hello world!"
	result.result.postcs;
	)
	'''
	*/
}
