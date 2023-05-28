ManyOne : Parser {
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
					nextState = parser.parserStateTransformer.(nextState);
					if (nextState.isError.not)
					{
						results = results.add(nextState.result);
					} {
						done = true;
					};
				};

				if (results.size == 0) {
					outputState = nextState.updateError("ManyOne: unable to match any parser at index" + nextState.index);
				} {
					outputState = nextState.updateResult(results);
				};
				outputState;
			};
		}
	}
}