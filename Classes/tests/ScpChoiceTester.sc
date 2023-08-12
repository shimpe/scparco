/*
[general]
title = "ScpChoiceTester"
summary = "unit tests for ScpChoice"
categories = "Parsing Tools"
related = "Classes/ScpChoice"
description = '''unit tests for ScpChoice'''
*/
ScpChoiceTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_choice_pass {
		var fullText = "hello world!goodbye world!";
		var p1 = ScpStrParser("good morning Vietnam!");
		var p2 = ScpStrParser("hello world!");
		var c = ScpChoice([p1, p2]);
		var result = c.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		this.assert("hello world!".compare(result.result) == 0,
			onFailure:{"hello world! !=" + result.result},
			message:"check if parse result is ok");
	}

	test_choice_fail {
		var fullText = "hello world!goodbye world!";
		var p1 = ScpStrParser("good morning Vietnam!");
		var p2 = ScpStrParser("bye world!");
		var c = ScpChoice([p1, p2]);
		var result = c.run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		result.prettyprint;
	}

    init {
    }
}