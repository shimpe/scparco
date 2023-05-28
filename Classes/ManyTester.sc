/*
[general]
title = "ManyTester"
summary = "unit tests for Many and ManyOne"
categories = "Parsing Tools"
related = "Classes/Many, Classes/ManyOne"
description = '''unit tests for Many and ManyOne'''
*/
ManyTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_many_pass {
		var fullText = "hello world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = StrParser("good morning Vietnam!");
		var p2 = StrParser("hello world!");
		var c = Choice([p1, p2]);
		var m = Many(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		result.prettyprint;
	}

	test_many_pass_emptymatch {
		var fullText = "bye world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = StrParser("good morning Vietnam!");
		var p2 = StrParser("hello world!");
		var c = Choice([p1, p2]);
		var m = Many(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		result.prettyprint;
	}

	test_manyone_pass {
		var fullText = "hello world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = StrParser("good morning Vietnam!");
		var p2 = StrParser("hello world!");
		var c = Choice([p1, p2]);
		var m = ManyOne(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		result.prettyprint;
	}


	test_manyone_fail_emptymatch {
		var fullText = "bye world!hello world!good morning Vietnam!hello world!goodbye world!";
		var p1 = StrParser("good morning Vietnam!");
		var p2 = StrParser("hello world!");
		var c = Choice([p1, p2]);
		var m = ManyOne(c);
		var result = m.run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		result.prettyprint;
	}

    init {
    }
}