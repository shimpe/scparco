Many : Parser {
	*new {
		| parser |
		^super.new.init(parser);
	}

	init {
		| parser |
		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var nextState = parserStateIn;
			var results = [];
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var done = false;
				var nextState = parserStateIn;
				while ({done.not}) {
					var testState = parser.parserStateTransformer.(nextState);
					if (testState.isError.not)
					{
						results = results.add(testState.result);
						nextState = testState;
					} {
						done = true;
					};
				};
				outputState = nextState.updateResult(results);
				outputState;
			};
		}
	}
}