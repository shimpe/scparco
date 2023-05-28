Choice : Parser {
	*new {
		| parsers |
		^super.new.init(parsers);
	}

	init {
		| parsers |
		this.parserStateTransformer = {
			| parserStateIn |
			var outputState;
			var nextState = parserStateIn;
			var results = [];
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var keepTrying = true;
				parsers.do({
					| parser, idx |
					if (keepTrying) {
						nextState = parser.parserStateTransformer.(parserStateIn);
					};
					if (nextState.isError.not)
					{
						keepTrying = false;
					};
				});
				if (nextState.isError) {
					nextState = nextState.updateError("Choice: unable to match with any parser at index" + parserStateIn.index);
				};
				nextState;
			};
		}
	}
}