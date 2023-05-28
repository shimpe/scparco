SequenceOf : Parser {
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
				var keepGoing = true;
				parsers.do({
					| parser, idx |
					if (keepGoing) {
						nextState = parser.parserStateTransformer.(nextState);
					};
					if (nextState.isError == true)
					{
						keepGoing = false;
					};
					if (keepGoing) {
						results = results.add(nextState.result);
					}
				});
				if (keepGoing) {
					outputState = nextState.updateResult(results);
				} {
					outputState = nextState.updateError("Couldn't match sequence at index" + nextState.index);
				};
				outputState;
			};
		}
	}
}