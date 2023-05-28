/*
[general]
title = "ParserFactory"
summary = "a factory for commonly used parsers, made from smaller library provided parsers"
categories = "Parser Tools"
related = "Classes/Parser"
description = '''
ParserFactory is a util that can create some parsers and families of parsers for common use cases. These parsers are built as combinations of more primitive library provided parsers. ParserFactory only implements classmethods, meaning you can call them using the syntax ParserFactory.makeIntegerParser (i.e. there's no need to create a ParserFactory instance first).
'''
*/
ParserFactory {
	/*
	[classmethod.new]
	description = "creates a new ParserFactory"
	[classmethod.new.returns]
	what = "a new ParserFactory"
	*/
	*new {
		^super.new.init();
	}

	/*
	[method.init]
	description = "initializes the parser factory (but in reality it does nothing :))"
	[method.init.returns]
	what = "ParserFactory"
	*/
	init {
	}

	/*
	[classmethod.makeBetween]
	description = '''creates a parser factory that can extract something between two other parsers.
	So, given a string and 3 parsers [LeftParser, ContentParser, RightParser], it will return only what is found by the ContentParser. Think of a value between brackets, where you are only interested in what is between the brackets (see example code).

	The result of a call to makeBetween is not a complete parser yet. It is a function that expects the ContentParser as argument. This allows to prepare a "between parser", where the LeftParser and RightParser are specified when makeBetween is called, and the ContentParser is specified at the moment of finalizing the parser. To finalize the parser, call the function return by makeBetween with a ContentParser as argument.'''

	[classmethod.makeBetween.args]
	leftParser = "a Parser that matches whatever should be left of the content you are interested in"
	rightParser = "a Parser that matches whatever should be right of the content you are interested in"

	[classmethod.makeBetween.returns]
	what = '''a function that, when called with a ContentParser as argument, will produce a new parser that matches first LeftParser, then ContentParser, then RightParser and keeps only the result of the ContentParser.'''
	*/
	*makeBetween {
		| leftParser, rightParser |
		^{
			| contentParser |
			var newP = SequenceOf([
				leftParser,
				contentParser,
				rightParser])
			.map({|results| results[1]});
			// map keeps only the result of the ContentParser and throws away the results of LeftParser and RightParser
			newP;
		}
	}

	/*
	[classmethod.makeIntegerParser]
	description = "class to parse an integer number and convert it from string to Integer"
	[classmethod.makeIntegerParser.returns]
	what = "Parser that can parse a (signed) integer number"
	*/
	*makeIntegerParser {
		^RegexParser("[+-]?[0-9]+").map({|txt| txt.asInteger });
	}

	/*
	[classmethod.makeFloatParser]
	description = "class to parse a float number and convert it from string to Float"
	[classmethod.makeFloatParser.returns]
	what = "Parser that can parse a (signed) float number"
	*/
	*makeFloatParser {
		^RegexParser("[+-]?([0-9]*[.])?[0-9]+").map({|txt| txt.asFloat });
	}

	/*
	[classmethod.makeWs]
	description = "class to match zero or more whitespace characters"
	[classmethod.makeWs.returns]
	what = '''Parser that parses multiple whitespace characters [\t\r\n\f]'''
	*/
	*makeWs {
		^RegexParser("\\s*");
	}

	/*
	[classmethod.makeWsOne]
	description = "class to match one or more whitespace characters"
	[classmethod.makeWsOne.returns]
	what = '''Parser that parses multiple whitespace characters [\t\r\n\f]'''
	*/
	*makeWsOne {
		^RegexParser("\\s+");
	}

	/*
	[classmethod.makeSepBy]
	description = "generates a function that expects a ValueParser and produces a parser that matches zero or more separated values (see example code)."
	[classmethod.makeSepBy.args]
	separatorParser = "a Parser that matches whatever separates the values we are interested in"
	[classmethod.makeSepBy.returns]
	what = "a function that expects a ValueParser as argument and produces a Parser that matches zero or more separated values"
	*/
	*makeSepBy {
		| separatorParser |
		^{
			| valueParser |
			var newP = Parser();
			newP.parserStateTransformer = {
				| parserState |
				var results = [];
				var nextState = parserState;
				var keepTrying = true;
				while ({keepTrying}) {
					var thingWeWantState = valueParser.parserStateTransformer.(nextState);
					if (thingWeWantState.isError) {
						keepTrying = false;
					} {
						var separatorState;
						results = results.add(thingWeWantState.result);
						nextState = thingWeWantState;
						separatorState = separatorParser.parserStateTransformer.(nextState);
						if (separatorState.isError) {
							keepTrying = false;
						} {
							nextState = separatorState;
						}
					}
				};

				nextState = nextState.updateResult(results);
			};
			newP;
		}
	}

	/*
	[classmethod.makeSepByOne]
	description = "generates a function that expects a ValueParser and produces a parser that matches one or more separated values (see example code)."
	[classmethod.makeSepByOne.args]
	separatorParser = "a Parser that matches whatever separates the values we are interested in"
	[classmethod.makeSepByOne.returns]
	what = "a function that expects a ValueParser as argument and produces a Parser that matches one or more separated values"
	*/
	*makeSepByOne {
		| separatorParser |
		^{
			| valueParser |
			var newP = Parser();
			newP.parserStateTransformer = {
				| parserState |
				var results = [];
				var nextState = parserState;
				var keepTrying = true;
				while ({keepTrying}) {
					var thingWeWantState = valueParser.parserStateTransformer.(nextState);
					if (thingWeWantState.isError) {
						keepTrying = false;
					} {
						var separatorState;
						results = results.add(thingWeWantState.result);
						nextState = thingWeWantState;
						separatorState = separatorParser.parserStateTransformer.(nextState);
						if (separatorState.isError) {
							keepTrying = false;
						} {
							nextState = separatorState;
						}
					}
				};

				results.postln;
				if (results.size == 0) {
					nextState = nextState.updateError("SepByOne: Unable to capture any results at index" + nextState.index);
				} {
					nextState = nextState.updateResult(results);
				};
				nextState;
			};
			newP;
		}
	}

	/*
	[classmethod.forwardRef]
	description = "forwardRef is used in recursive parsers, since without forward reference it's impossible to use parsers that have not been defined yet leading to a chicken-and-egg problem (in a recursive parser, parser A refers to parser B, but parser B refers to parser A again)."
	[classmethod.forwardRef.args]
	parserThunk = "a Thunk (= unevaluated function) that, when evaluated, will produce a parser. The Thunk can contain parsers that have not yet been defined in the code (see example code)."
	[classmethod.forwardRef.returns]
	what = "a parser wrapper that will evaluate to an actual parser only when it is being used during parsing. At that moment in time, all parsers it refers to must have been instantiated and initialized. This allows for making parser A depend on parser B, where parser B depends on parser A (circular dependencies, which often occur when defining recursive parsers)."
	*/
	*forwardRef {
		| parserThunk |
		var newP = Parser();
		newP.parserStateTransformer = {
			| parserState |
			// only now (while parsing is already ongoing) the real parser is instantiated and run
			// since by now, all parsers referred to in the thunk have been initialized
			var parser = parserThunk.value;
			var nextState = parser.parserStateTransformer.(parserState);
			nextState;
		};
		^newP;
	}

	/*
	[examples]
	what = '''
	(
	var betweenBrackets = ParserFactory.makeBetween(StrParser("("), StrParser(")"));
	var lettersBetweenBrackets = betweenBrackets.(RegexParser("[A-Za-z]+"));
	var result = lettersBetweenBrackets.run("(foobarsnafu)"); // result should be "foobarsnafu"
	result.result.postcs;
	)
	(
	var commasep = ParserFactory.makeSepBy(StrParser(","));
	var csv_ints = commasep.(ParserFactory.makeIntegerParser);
	var result = csv_ints.run("45,78,-93,12"); // result should be [45, 78, -93, 12]
	result.result.postcs;
    )
    (
	// both separators and values can be more complex parsers as well
	var commacolonsep = ParserFactory.makeSepBy(Choice([StrParser(","), StrParser(";")]));
	var csv_numbers = commacolonsep.(ParserFactory.makeFloatParser);
	var text = "45,-89.23;56.7,67;90.09";
	var result = csv_numbers.run(text); // result should be [ 45.0, -89.23, 56.7, 67.0, 90.09 ]
	result.result.postcs;
    )
	(
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
		arrayparser])
	}));
	var arrayparser = betweenSquareBrackets.(commaSeparated.(nested_array_parser));
	var result = arrayparser.run("[1,[2,3,[4,5],6],7,[8],9]"); // expected result: [ 1, [ 2, 3, [ 4, 5 ], 6 ], 7, [ 8 ], 9 ]
	result.result.postcs;
	)
	'''
	*/

}