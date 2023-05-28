Parser {
	var <>parserStateTransformer;

	*new {
		^super.new.parserInit;
	}

	parserInit {
		this.parserStateTransformer = nil;
	}

	run {
		| targetString |
		var initialState = ParserState(
			targetString: targetString,
			index: 0,
			result: nil,
			isError: false,
			errorMsg: "");
		^this.parserStateTransformer.(initialState);
	}

	// map allows an arbitrary transformation on a parser result to be specified
	// fn is the function that maps the parse result to the desired result
	map {
		| fn |
		var newP = Parser();
		newP.parserStateTransformer = {
			| parserState |
			var nextState = this.parserStateTransformer.(parserState);
			if (nextState.isError) {
				nextState;
			} {
				nextState.updateResult(fn.(nextState.result));
			}
		};
		^newP;
	}

	// tldr; * fn takes parse result and returns a parser
	//       * chain returns new state after running parser selected by fn on current state
	// chain allows dynamic selection of a next parser based on what was just parsed
	// fn is the function that selects the next parser based on the parse result
	// chain takes a function that takes a parse result,
	// and uses it to select a next parser,
	// and runs that next parser on the state so far to return the next state
	chain {
		| fn |
		var newP = Parser();
		newP.parserStateTransformer = {
			| parserState |
			var nextState;
			nextState = this.parserStateTransformer.(parserState);
			if (nextState.isError)
			{
				nextState;
			} {
				var nextParser = fn.(nextState.result);
				nextParser.parserStateTransformer.(nextState);
			};
		};
		^newP;
	}

	errorMap {
		| fn |
		var newP = Parser();
		newP.parserStateTransformer = {
			| parserState |
			var nextState = this.parserStateTransformer.(parserState);
			if (nextState.isError.not) {
				nextState;
			} {
				nextState.updateError(fn.(nextState.errorMsg, nextState.index));
			}
		};
		^newP;
	}
}