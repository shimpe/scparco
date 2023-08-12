/*
[general]
title = "ScpLookAheadTester"
summary = "unit tests for ScpLookAhead"
categories = "Parsing Tools"
related = "Classes/ScpLookAhead"
description = '''unit tests for ScpLookAhead'''
*/
ScpLookAheadTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_lookahead {
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
		var p1 = ScpSequenceOf([ScpStrParser("Blah"), ScpNegativeLookAhead(ScpStrParser("!")), ScpStrParser("!Blah")]).map({|result| [result[0], result[2]]});
		var p2 = ScpSequenceOf([ScpStrParser("Blah"), ScpNegativeLookAhead(ScpStrParser("?")), ScpStrParser("!Blah")]).map({|result| [result[0], result[2]]});
		var state1, state2;

		state1 = p1.run("Blah!Blah");
		this.assertEquals(state1.isError, true, "state1_isError");

		state2 = p2.run("Blah!Blah", trace:true);
		state2.result.postcs;
		this.assertEquals(state2.isError, false, "state2_isError");
		this.assert(state2.result[0].compare("Blah") == 0, "state2_result0");
		this.assert(state2.result[1].compare("!Blah") == 0, "state2_result1");
	}

	init {
    }
}