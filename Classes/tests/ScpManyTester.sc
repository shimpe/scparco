/*
[general]
title = "ScpManyTester"
summary = "unit tests for ScpMany and ScpManyOne"
categories = "Parsing Tools"
related = "Classes/ScpMany, Classes/ScpManyOne"
description = '''unit tests for ScpMany and ScpManyOne'''
*/
ScpManyTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_many_pass {
		var fullText = "hello world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = ScpStrParser("good morning Vietnam!");
		var p2 = ScpStrParser("hello world!");
		var c = ScpChoice([p1, p2]);
		var m = ScpMany(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		result.prettyprint;
	}

	test_many_pass_emptymatch {
		var fullText = "bye world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = ScpStrParser("good morning Vietnam!");
		var p2 = ScpStrParser("hello world!");
		var c = ScpChoice([p1, p2]);
		var m = ScpMany(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		result.prettyprint;
	}

	test_manyone_pass {
		var fullText = "hello world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = ScpStrParser("good morning Vietnam!");
		var p2 = ScpStrParser("hello world!");
		var c = ScpChoice([p1, p2]);
		var m = ScpManyOne(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		result.prettyprint;
	}


	test_manyone_fail_emptymatch {
		var fullText = "bye world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = ScpStrParser("good morning Vietnam!");
		var p2 = ScpStrParser("hello world!");
		var c = ScpChoice([p1, p2]);
		var m = ScpManyOne(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		result.prettyprint;
	}

    init {
    }
}