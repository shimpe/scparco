/*
[general]
title = "RegexParserTester"
summary = "unit tests for RegexParser"
categories = "Parsing Tools"
related = "Classes/RegexParser"
description = '''unit tests for RegexParser'''
*/
RegexParserTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_singlestr_pass {
		var lookFor = "[a-z]+";
		var fullText = "hello world!";
		var p = RegexParser.new(lookFor);
		var result = p.run(fullText);
		result.prettyprint;
		this.assertEquals(result.isError, false, message:"isError");
		this.assert("hello".compare(result.result) == 0,
			onFailure:{"hello !=" + result.result},
			message:"check if parse result is ok");
	}

    init {
    }
}