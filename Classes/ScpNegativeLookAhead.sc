/*
[general]
title = "ScpNegativeLookAhead"
summary = "a specialized ScpParser that tries to match a ScpParser and fails if the matching succeeds. It does not advance the ScpParserState index."
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpNegativeLookAhead is a parser to check if something specific does not follow. If it succeeds, it doesn't alter the parsing state. This can be used to decide how to proceed parsing based on what text is coming.
'''
*/
ScpNegativeLookAhead : ScpParser {
	/*
	[classmethod.new]
	description = "new creates a new ScpNegativeLookAhead ScpParser"
	[classmethod.new.args]
	parser = "a parser that needs to match the next text for ScpNegativeLookAhead to fail"
	[classmethod.new.returns]
	what = "ScpNegativeLookAhead"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = "init initializes the ScpNegativeLookAhead parser with a suitable parserStateTransformer function that tries to match the given parser but doesn't advance parser state"
	[method.init.args]
	parser = "a parser"
	[method.init.returns]
	what = "ScpNegativeLookAhead"
	*/
	init {
		| parser |
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var nextState = parserStateIn.deepCopy(); // do not alter original parser state
				var testState;
				this.logStartTrace(parserStateIn, "ScpNegativeLookAhead");
				testState = parser.parserStateTransformer.(nextState);
				if (testState.isError) {
					this.logEndTrace(parserStateIn, "ScpNegativeLookAhead parser didn't match (so: success)", true);
				}{
					nextState = nextState.updateError("ScpNegativeLookAhead parser matched (so: failure) at index" + nextState.index);
					this.logEndTrace(nextState, "ScpNegativeLookAhead parser matched (so: failure)", false);
				};
				nextState;
			};
		}
	}

	/*
	[examples]
	what = '''
    (
	// just a contrived example
	var str = "Blah!Blah";
	var p1 = ScpSequenceOf([ScpStrParser("Blah"), ScpNegativeLookAhead(ScpStrParser("?")), ScpStrParser("!Blah")]);
	var p2 = ScpSequenceOf([ScpStrParser("Blah"), ScpNegativeLookAhead(ScpStrParser("!")), ScpStrParser("!Blah")]);
	var s1 = p1.run(str);
	var s2 = p2.run(str);
	s1.isError.debug("p1 error"); // should be false
	s2.isError.debug("p2 error"); // should be true
    )
	'''
	*/
}
