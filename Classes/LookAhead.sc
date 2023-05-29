/*
[general]
title = "LookAhead"
summary = "a specialized Parser that tries to match a Parser and succeeds if the matching succeeds. It does not advance the ParserState index."
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
LookAhead is a parser to check if something specific follows, but if it succeeds, it doesn't alter the parsing state. This can be used to decide how to proceed parsing based on what text is coming.
'''
*/
LookAhead : Parser {
	/*
	[classmethod.new]
	description = "new creates a new LookAhead Parser"
	[classmethod.new.args]
	parser = "a parser that needs to match the next text for LookAhead to succeed"
	[classmethod.new.returns]
	what = "LookAhead"
	*/
	*new {
		| parser |
		^super.new.init(parser);
	}

	/*
	[method.init]
	description = "init initializes the LookAhead parser with a suitable parserStateTransformer function that tries to match the given parser but doesn't advance parser state"
	[method.init.args]
	parser = "a parser"
	[method.init.returns]
	what = "LookAhead"
	*/
	init {
		| parser |
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError == true) {
				parserStateIn;
			} {
				var nextState = parserStateIn.deepCopy(); // do not alter original parser state
				var testState = parser.parserStateTransformer.(nextState);
				if (testState.isError) {
					nextState.updateError("failed to match LookAhead at index" + nextState.index);
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
	var exclLetter = RegexParser("[!a-zA-Z]+"); // must include "!" because LookAhead won't consume the !
	var quesLetter = RegexParser("[?a-zA-Z]+"); // must include "?" because LookAhead won't consume the ?
	var c = Many(
		Choice([
			SequenceOf([
				LookAhead(StrParser("!")),
				exclLetter.map({|value| (\type: \exclamation, \value : value)})]).map({|result| result[1]}), // map removes the result of lookahead
			SequenceOf([
				LookAhead(StrParser("?")),
				quesLetter.map({|value| (\type: \question, \value : value)})]).map({|result| result[1]}),
	])); // map removes the result of lookahead
	var result = c.run("!hello?why!yay?ohnoes");
	result.result.postcs;
	)
	(
	// same thing without LookAhead
	var exclLetter = RegexParser("[!][a-zA-Z]+");
	var quesLetter = RegexParser("[?][a-zA-Z]+");
	var c = Many(Choice([
				exclLetter.map({|value| (\type: \exclamation, \value : value)}),
				quesLetter.map({|value| (\type: \question, \value: value)})]));
	var result = c.run("!hello?why!yay?ohnoes");
	result.result.postcs;
	)
	'''
	*/
}
