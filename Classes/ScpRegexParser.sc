/*
[general]
title = "ScpRegexParser"
summary = "a specialized ScpParser that can recognize a regex"
categories = "Parsing Tools"
related = "Classes/ScpParser, Classes/ScpStrParser"
description = '''a ScpRegexParser takes a regex and succeeds if it can match that regex. ScpRegexParser can be used as the basis for many simple parsers like numbers or email adresses'''
*/
ScpRegexParser : ScpParser {

	/*
	[classmethod.new]
	description = "creates a new ScpRegexParser"
	[classmethod.new.args]
	regex = "regex to be recognized"
	[classmethod.new.returns]
	what = "ScpRegexParser"
	*/
	*new {
		| regex |
		^super.new.init(regex);
	}

	/*
	[method.init]
	description = "initializes a ScpRegexParser"
	[method.init.args]
	regex = "regex to be recognized"
	[method.init.returns]
	what = "ScpRegexParser"
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
				this.logStartTrace(parserStateIn, "ScpRegexParser(\"" ++ regex ++ "\")");

				currentIndex = parserStateIn.index;
				currentTarget = parserStateIn.target.drop(currentIndex);
				if (currentTarget.size == 0) {
					this.logEndTrace(parserStateIn, "ScpRegexParser(\"" ++ regex ++ "\")", false);
					outputState = parserStateIn.updateError(
						"regexParser: Error! Expected to match '" ++ regex ++ "' but found an unexpected end of input!");
				} {
					if (currentTarget.findRegexpAt(regex, 0).notNil) {
						var res = currentTarget.findRegexpAt(regex, 0);
						var match = res[0];
						this.logEndTrace(parserStateIn, "ScpRegexParser(\"" ++ regex ++ "\")", true);
						outputState = parserStateIn.updateState(currentIndex + match.size, match);
					} {
						this.logEndTrace(parserStateIn, "ScpRegexParser(\"" ++ regex ++ "\")", false);
						outputState = parserStateIn.updateError("regexParser: Error! at position" +
							currentIndex + "expected to match '" ++ regex ++ "' but found '" ++
							currentTarget.keep(regex.size.max(10)) ++ "[...]'");
					};
				};
				outputState;
			}
		};
	}

	/*
	[examples]
	what = '''
	(
	var p = ScpRegexParser("[A-Za-z]+");
	var result = p.run("Hello world!"); // expect to see result "Hello" and index has shifted to position 5
	result.result.postcs;
	result.index.debug("index");
	)
	'''
	*/
}
