StrParser : Parser {
	*new {
		| str |
		^super.new.init(str);
	}

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
				currentTargetString = parserStateIn.targetString.drop(currentIndex);
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
}