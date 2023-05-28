RegexParser : Parser {

	*new {
		| regex |
		^super.new.init(regex);
	}

	init {
		| regex |

		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var currentIndex;
			var currentTargetString;

			if (parserStateIn.isError == True) {
				parserStateIn;
			} {
				currentIndex = parserStateIn.index;
				currentTargetString = parserStateIn.targetString.drop(currentIndex);
				if (currentTargetString.size == 0) {
					outputState = parserStateIn.updateError(
						"regexParser: Error! Expected to match '" ++ regex ++ "' but found an unexpected end of input!");
				} {
					if (currentTargetString.findRegexpAt(regex, 0).notNil) {
						var res = currentTargetString.findRegexpAt(regex, 0);
						var match = res[0];
						outputState = parserStateIn.updateState(currentIndex + match.size, match);
					} {
						outputState = parserStateIn.updateError("regexParser: Error! at position" +
							currentIndex + "expected to match '" ++ regex ++ "' but found '" ++
							currentTargetString.keep(regex.size.max(10)) ++ "[...]'");
					};
				};
				//outputState.prettyprint;
				outputState;
			}
		};
	}
}