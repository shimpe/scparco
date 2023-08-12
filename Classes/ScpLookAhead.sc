/*
[general]
title = "ScpLookAhead"
summary = "a specialized ScpParser that tries to match a ScpParser and succeeds if the matching succeeds. It does not advance the ParserState index."
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpLookAhead is a parser to check if something specific follows, but if it succeeds, it doesn't alter the parsing state. This can be used to decide how to proceed parsing based on what text is coming.
'''
*/
ScpLookAhead : ScpParser {
	/*
	[classmethod.new]
	description = "new creates a new ScpLookAhead ScpParser"
	[classmethod.new.args]
	parser = "a parser that needs to match the next text for ScpLookAhead to succeed"
	[classmethod.new.returns]
	what = "ScpLookAhead"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = "init initializes the ScpLookAhead parser with a suitable parserStateTransformer function that tries to match the given parser but doesn't advance parser state"
	[method.init.args]
	parser = "a parser"
	[method.init.returns]
	what = "ScpLookAhead"
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
				this.logStartTrace(parserStateIn, "ScpLookAhead");
				testState = parser.parserStateTransformer.(nextState);
				if (testState.isError) {
					nextState = nextState.updateError("failed to match ScpLookAhead at index" + nextState.index);
					this.logEndTrace(nextState, "ScpLookAhead", false);
				}{
					this.logEndTrace(parserStateIn, "ScpLookAhead", true);
				};
				nextState;
			};
		}
	}

	/*
	[examples]
	what = '''
	(
	// note: you can much better make this parser without lookahead (see below); just a contrived example
	var exclLetter = ScpRegexParser("[!a-zA-Z]+"); // must include "!" because ScpLookAhead won't consume the !
	var quesLetter = ScpRegexParser("[?a-zA-Z]+"); // must include "?" because ScpLookAhead won't consume the ?
	var c = ScpMany(
		ScpChoice([
			ScpSequenceOf([
				ScpLookAhead(ScpStrParser("!")),
				exclLetter.map({|value| (\type: \exclamation, \value : value)})]).map({|result| result[1]}), // map removes the result of lookahead
			ScpSequenceOf([
				ScpLookAhead(ScpStrParser("?")),
				quesLetter.map({|value| (\type: \question, \value : value)})]).map({|result| result[1]}),
	])); // map removes the result of lookahead
	var result = c.run("!hello?why!yay?ohnoes");
	result.result.postcs;
	)
	(
	// same thing without ScpLookAhead
	var exclLetter = ScpRegexParser("[!][a-zA-Z]+");
	var quesLetter = ScpRegexParser("[?][a-zA-Z]+");
	var c = ScpMany(ScpChoice([
				exclLetter.map({|value| (\type: \exclamation, \value : value)}),
				quesLetter.map({|value| (\type: \question, \value: value)})]));
	var result = c.run("!hello?why!yay?ohnoes");
	result.result.postcs;
	)
	'''
	*/
}
