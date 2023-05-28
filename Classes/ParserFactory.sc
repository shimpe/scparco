ParserFactory {
	*new {
		^super.new.init();
	}

	init {
	}

	*makeBetween {
		| leftParser, rightParser |
		^{
			| contentParser |
			var newP = SequenceOf([
				leftParser,
				contentParser,
				rightParser])
			.map({|results| results[1]});
			newP;
		}
	}

	*makeIntegerParser {
		^RegexParser("[+-]?[0-9]+").map({|txt| txt.asInteger }); // alternative that does conversion for you
		// ^RegexParser("[+-]?[0-9]+");
	}

	*makeFloatParser {
		^RegexParser("[+-]?([0-9]*[.])?[0-9]+").map({|txt| txt.asFloat }); // alternative that does conversion for you
		//^RegexParser("[+-]?([0-9]*[.])?[0-9]+");
	}

	*makeSepBy {
		| separatorParser |
		^{
			| valueParser |
			var newP = Parser();
			newP.parserStateTransformer = {
				| parserState |
				var results = [];
				var nextState = parserState;
				var keepTrying = true;
				while ({keepTrying}) {
					var thingWeWantState = valueParser.parserStateTransformer.(nextState);
					if (thingWeWantState.isError) {
						keepTrying = false;
					} {
						var separatorState;
						results = results.add(thingWeWantState.result);
						nextState = thingWeWantState;
						separatorState = separatorParser.parserStateTransformer.(nextState);
						if (separatorState.isError) {
							keepTrying = false;
						} {
							nextState = separatorState;
						}
					}
				};

				nextState = nextState.updateResult(results);
			};
			newP;
		}
	}

	*makeSepByOne {
		| separatorParser |
		^{
			| valueParser |
			var newP = Parser();
			newP.parserStateTransformer = {
				| parserState |
				var results = [];
				var nextState = parserState;
				var keepTrying = true;
				while ({keepTrying}) {
					var thingWeWantState = valueParser.parserStateTransformer.(nextState);
					if (thingWeWantState.isError) {
						keepTrying = false;
					} {
						var separatorState;
						results = results.add(thingWeWantState.result);
						nextState = thingWeWantState;
						separatorState = separatorParser.parserStateTransformer.(nextState);
						if (separatorState.isError) {
							keepTrying = false;
						} {
							nextState = separatorState;
						}
					}
				};

				results.postln;
				if (results.size == 0) {
					nextState = nextState.updateError("SepByOne: Unable to capture any results at index" + nextState.index);
				} {
					nextState = nextState.updateResult(results);
				};
				nextState;
			};
			newP;
		}
	}

	*forwardRef {
		| parserThunk |
		var newP = Parser();
		newP.parserStateTransformer = {
			| parserState |
			// only now (while parsing is already ongoing) the real parser is instantiated and run
			// since by now, all parsers referred to in the thunk have been initialized
			var parser = parserThunk.value;
			var nextState = parser.parserStateTransformer.(parserState);
			nextState;
		};
		^newP;
	}

}