/*
[general]
title = "LookAheadTester"
summary = "unit tests for LookAhead"
categories = "Parsing Tools"
related = "Classes/LookAhead"
description = '''unit tests for LookAhead'''
*/
LookAheadTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_lookahead {
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
		this.assert(
			result.result == [
				( 'type': 'exclamation', 'value': "!hello" ),
				( 'type': 'question', 'value': "?why" ),
				( 'type': 'exclamation', 'value': "!yay" ),
				( 'type': 'question', 'value': "?ohnoes" ) ];
		);
	}

	test_negative_lookahead {
		var str = "Blah!Blah";
		var p1 = SequenceOf([StrParser("Blah"), NegativeLookAhead(StrParser("!")), StrParser("!Blah")]);
		var p2 = SequenceOf([StrParser("Blah"), NegativeLookAhead(StrParser("?")),StrParser("!Blah")]);
		var state1, state2;

		state1 = p1.run("Blah!Blah");
		this.assertEquals(state1.isError, true, "state1_isError");

		state2 = p2.run("Blah!Blah");
		this.assertEquals(state2.isError, false, "state2_isError");
		this.assert(state2.result[0].compare("Blah") == 0, "state2_result0");
		this.assert(state2.result[1].compare("!Blah") == 0, "state2_result1");
	}

	init {
    }
}