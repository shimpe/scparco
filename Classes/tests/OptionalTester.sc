/*
[general]
title = "OptionalTester"
summary = "unit tests for Optional"
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''unit tests for Optional'''
*/
OptionalTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_1 {
		var s1 = StrParser("hello!");
		var s2 = Optional(StrParser("bye!"));
		var text = "hello!hello!";
		var seq = SequenceOf([s1,s2,s1]);
		var result = seq.run(text); // [ "hello!", nil, "hello!" ]
		this.assertEquals(result.isError, false);
		this.assert(result.result[0].compare("hello!") == 0);
		this.assert(result.result[1] == nil);
		this.assert(result.result[2].compare("hello!") == 0);
	}

    init {
    }
}