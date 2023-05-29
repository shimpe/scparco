/*
[general]
title = "CombinatorTester"
summary = "unit tests using some ParserFactory methods"
categories = "Parsing Tools"
related = "Classes/ParserFactory"
description = '''unit tests using some ParserFactory methods'''
*/
CombinatorTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_between {
		var fullText;
		var betweenBrackets;
		var result;
		fullText = "(hello)";
		betweenBrackets = ParserFactory.makeBetween(StrParser("("), StrParser(")"));
		result = betweenBrackets.(RegexParser("[a-z]+")).run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		this.assert(result.result.compare("hello") == 0,
			onFailure:{"hello !=" + result.result},
			message:"check if parse result is ok"
		);
		result.prettyprint;
	}

	test_chain {
		// use chain to select parsers based on what was parsed before
		// input has form "type:value" and the output must be transformed differently based on type
		// "string:hello" -> expected output: ( typename: 'string', value : 'hello' )
		// "number:42" -> expected output: ( typename: 'number', value: 42)
		// "diceroll:2d8" -> expected output: ( typename: 'diceroll', value: [2,8] }
		var stringParser = RegexParser("[A-Za-z]+").map({ |result| (\typename: 'string', \value : result); });
		var numberParser = RegexParser("[0-9]+").map({ |result| (\typename: 'number',  \value : result.asInteger); });
		var numParserResult = numberParser.run("42");
		var dicerollParser = SequenceOf([
			RegexParser("[0-9]+"),
			StrParser("d"),
			RegexParser("[0-9]+")
		]).map({ | result | (\typename : 'diceroll', \value : [ result[0].asInteger, result[2].asInteger ]) });

		var typevalueparser = SequenceOf([
				RegexParser("[A-Za-z]+"),
				StrParser(":")])
		.map({
			|results|
			results[0] })
		.chain({
			| thetype |
			if (thetype.compare("string") == 0) {
				stringParser
			} {
				if (thetype.compare("number") == 0) {
					numberParser;
				}{
					dicerollParser;
				}
			}
		});

		var fullText1 = "number:42";
		var fullText2 = "string:hello";
		var fullText3 = "diceroll:6d9";
		var result;

		// basic assertions on the number parser
		this.assertEquals(numParserResult.isError, false, message: "numParserResult.isError");
		this.assertEquals(numParserResult.result[\typename] == 'number', true, message: "typename is correct");
		this.assertEquals(numParserResult.result[\value] == 42, true, message: "value is correct");

		// now for the real stuff
		result = typevalueparser.run(fullText1);
		this.assertEquals(result.isError, false, message:"isError");
		//result.prettyprint;
		this.assert(result.result[\typename] == 'number',
			onFailure:{"failed to parse number"},
			message:"check if we can parse a number"
		);
		this.assert(result.result[\value] == 42,
			onFailure:{"failed to parse number"},
			message:"check if we can parse a number"
		);

		result = typevalueparser.run(fullText2);
		this.assertEquals(result.isError, false, message:"isError");
		//result.prettyprint;
		this.assert(result.result[\typename] == 'string',
			onFailure:{"failed to parse string"},
			message:"check if we can parse a string"
		);
		this.assert(result.result[\value].compare("hello") == 0,
			onFailure:{"failed to parse string"},
			message:"check if we can parse a string"
		);

		result = typevalueparser.run(fullText3);
		this.assertEquals(result.isError, false, message:"isError");
		//result.prettyprint;
		this.assert(result.result[\typename] == 'diceroll',
			onFailure:{"failed to parse diceroll"},
			message:"check if we can parse a diceroll"
		);
		this.assert(result.result[\value] == [6,9],
			onFailure:{"failed to parse diceroll"},
			message:"check if we can parse a diceroll"
		);
	}

	test_sepby {
		var p1 = ParserFactory.makeSepBy(StrParser(",")).(RegexParser("[A-Za-z]+"));
		var p2 = ParserFactory.makeSepByOne(StrParser(",")).(RegexParser("[A-Za-z]+"));
		var result;

		result = p1.run("hello,goodbye,why,what,yippee");
		this.assertEquals(result.isError, false, message: "isError p1 - A");
		//result.prettyprint;

		result = p1.run("hello,blah@einde");
		this.assertEquals(result.isError, false, message: "isError p1 - B");
		this.assert(result.result[0].compare("hello") == 0);
		this.assert(result.result[1].compare("blah") == 0);

		result = p1.run("hello@blah@einde");
		this.assertEquals(result.isError, false, message: "isError p1 - B");
		this.assert(result.result[0].compare("hello") == 0);

		result = p2.run("hello@blah@einde");
		this.assertEquals(result.isError, false, message: "isError p2");
		this.assert(result.result[0].compare("hello") == 0);

		result = p1.run("@empty");
		this.assertEquals(result.isError, false, message: "isError p1 - C");
		this.assertEquals(result.result, []);

		result = p2.run("@blah@einde");
		this.assertEquals(result.isError, true, message: "isError p2");
	}

	test_parsearray {
		var betweenSquareBrackets = ParserFactory.makeBetween(StrParser("["), StrParser("]"));
		var commaSeparated = ParserFactory.makeSepBy(StrParser(","));
		var flat_arrayparser = betweenSquareBrackets.(commaSeparated.(RegexParser("[0-9]+").map({|x| x.asInteger })));
		var result = flat_arrayparser.run("[1,2,3,4,5,6,7,8,9]");
		result.prettyprint;
		this.assertEquals(result.isError, false, message: "isError");
		this.assert(result.result == [1,2,3,4,5,6,7,8,9]);
	}

	test_parse_nested_array {
		var betweenSquareBrackets = ParserFactory.makeBetween(StrParser("["), StrParser("]"));
		var commaSeparated = ParserFactory.makeSepBy(StrParser(","));
		var digitparser = ParserFactory.makeIntegerParser;
		var nested_array_parser = ParserFactory.forwardRef(Thunk({
			// Thunk makes sure its inside is not evaluated yet
			// and forwardRef "disguises" (wraps) the Thunk as a Parser
			//
			// We need a forward ref here to get around a mutual dependency problem
			// namely: nested_array_element uses flat_arrayparser, but
			// flat_arrayparser uses nested_array_element... (= mutually recursive definition).
			Choice([
				digitparser,
				arrayparser
			])
		}));
		var arrayparser = betweenSquareBrackets.(commaSeparated.(nested_array_parser));
		var result = arrayparser.run("[1,[2,3,[4,5],6],7,[8],9]");
		this.assertEquals(result.isError, false);
		this.assertEquals(result.result, [ 1, [ 2, 3, [ 4, 5 ], 6 ], 7, [ 8 ], 9 ]);
	}

	init {

	}
}