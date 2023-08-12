/*
[general]
title = "ScpLongestChoiceTester"
summary = "unit tests for ScpLongestChoice"
categories = "Parsing Tools"
related = "Classes/ScpLongestChoice, Classes/ScpChoice"
description = '''unit tests for ScpLongestChoice'''
*/
ScpLongestChoiceTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_choice_vs_longestchoice {
		var lc = ScpLongestChoice([
			ScpParserFactory.makeIntegerParser.map({|x| (\type : \int, \value : x) }),
			ScpParserFactory.makeFloatParser.map({|x| (\type: \float, \value: x)})
		]);
		var c = ScpChoice([
			ScpParserFactory.makeIntegerParser.map({|x| (\type : \int, \value : x) }),
			ScpParserFactory.makeFloatParser.map({|x| (\type: \float, \value: x)})
		]);
		var result1_longestchoice = lc.run("3.14"); // expected result: (\type: \float, \value : 3.14)
		var result2_longestchoice = lc.run("3"); // expected result: (\type: \int, \value: 3)
		var result3_choice = c.run("3.14"); // expected result: (\type: \int, \value: 3)
		var result4_choice = c.run("3"); // expected result: (\type: \int, \value: 3)
		this.assertEquals(result1_longestchoice.result, (\type:\float,\value:3.14));
		this.assertEquals(result2_longestchoice.result, (\type:\int,\value:3));
		this.assertEquals(result3_choice.result,(\type:\int,\value:3));
		this.assertEquals(result4_choice.result,(\type:\int,\value:3));
	}

	init {
    }
}
