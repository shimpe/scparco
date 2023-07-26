/*
[general]
title = "NegativeLookAhead"
summary = "a specialized Parser that tries to match a Parser and fails if the matching succeeds. It does not advance the ParserState index."
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
NegativeLookAhead is a parser to check if something specific does not follow. If it succeeds, it doesn't alter the parsing state. This can be used to decide how to proceed parsing based on what text is coming.
'''
*/
NegativeLookAhead : Parser {
	/*
	[classmethod.new]
	description = "new creates a new NegativeLookAhead Parser"
	[classmethod.new.args]
	parser = "a parser that needs to match the next text for NegativeLookAhead to fail"
	[classmethod.new.returns]
	what = "NegativeLookAhead"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = "init initializes the NegativeLookAhead parser with a suitable parserStateTransformer function that tries to match the given parser but doesn't advance parser state"
	[method.init.args]
	parser = "a parser"
	[method.init.returns]
	what = "NegativeLookAhead"
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
				this.logStartTrace(parserStateIn, "NegativeLookAhead");
				testState = parser.parserStateTransformer.(nextState);
				if (testState.isError) {
					this.logEndTrace(parserStateIn, "NegativeLookAhead parser didn't match (so: success)", true);
				}{
					nextState = nextState.updateError("NegativeLookAhead parser matched (so: failure) at index" + nextState.index);
					this.logEndTrace(nextState, "NegativeLookAhead parser matched (so: failure)", false);
					nextState.print;
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
	var p1 = SequenceOf([StrParser("Blah"), NegativeLookAhead(StrParser("?")), StrParser("!Blah")]);
	var p2 = SequenceOf([StrParser("Blah"), NegativeLookAhead(StrParser("!")), StrParser("!Blah")]);
	var s1 = p1.run(str);
	var s2 = p2.run(str);
	s1.isError.debug("p1 error"); // should be false
	s2.isError.debug("p2 error"); // should be true
    )
	'''
	*/
}
