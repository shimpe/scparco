/*
[general]
title = "RegexParser"
summary = "a specialized Parser that can recognize a regex"
categories = "Parsing Tools"
related = "Classes/Parser, Classes/StrParser"
description = '''a RegexParser takes a regex and succeeds if it can match that regex. RegexParser can be used as the basis for many simple parsers like numbers or email adresses'''
*/
RegexParser : Parser {

	/*
	[classmethod.new]
	description = "creates a new RegexParser"
	[classmethod.new.args]
	regex = "regex to be recognized"
	[classmethod.new.returns]
	what = "RegexParser"
	*/
	*new {
		| regex |
		^super.new.init(regex);
	}

	/*
	[method.init]
	description = "initializes a RegexParser"
	[method.init.args]
	regex = "regex to be recognized"
	[method.init.returns]
	what = "RegexParser"
	*/
	init {
		| regex |

		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var currentIndex;
			var currentTarget;

			if (parserStateIn.isError == True) {
				parserStateIn;
			} {
				currentIndex = parserStateIn.index;
				currentTarget = parserStateIn.target.drop(currentIndex);
				if (currentTarget.size == 0) {
					outputState = parserStateIn.updateError(
						"regexParser: Error! Expected to match '" ++ regex ++ "' but found an unexpected end of input!");
				} {
					if (currentTarget.findRegexpAt(regex, 0).notNil) {
						var res = currentTarget.findRegexpAt(regex, 0);
						var match = res[0];
						outputState = parserStateIn.updateState(currentIndex + match.size, match);
					} {
						outputState = parserStateIn.updateError("regexParser: Error! at position" +
							currentIndex + "expected to match '" ++ regex ++ "' but found '" ++
							currentTarget.keep(regex.size.max(10)) ++ "[...]'");
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
	var p = RegexParser("[A-Za-z]+");
	var result = p.run("Hello world!"); // expect to see result "Hello" and index has shifted to position 5
	result.result.postcs;
	result.index.debug("index");
	)
	'''
	*/
}
