/*
[general]
title = "ChoiceTester"
summary = "unit tests for Choice"
categories = "Parsing Tools"
related = "Classes/Choice"
description = '''unit tests for Choice'''
*/
ChoiceTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_choice_pass {
		var fullText = "hello world!goodbye world!";
		var p1 = StrParser("good morning Vietnam!");
		var p2 = StrParser("hello world!");
		var c = Choice([p1, p2]);
		var result = c.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		this.assert("hello world!".compare(result.result) == 0,
			onFailure:{"hello world! !=" + result.result},
			message:"check if parse result is ok");
	}

	test_choice_fail {
		var fullText = "hello world!goodbye world!";
		var p1 = StrParser("good morning Vietnam!");
		var p2 = StrParser("bye world!");
		var c = Choice([p1, p2]);
		var result = c.run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		result.prettyprint;
	}

    init {
    }
}