(
// simple calculator
// first the parser
var numberParser = ParserFactory.makeFloatParser.map({|x| (\type : \number, \value : x) });
var operatorParser = Choice([StrParser("+"), StrParser("-"), StrParser("*"), StrParser("/")]);
var betweenBrackets = ParserFactory.makeBetween(StrParser("("), StrParser(")"));
var expression = ParserFactory.forwardRef(Thunk({
	Choice([
		numberParser,
		operationParser
	])
}));
var operationParser = betweenBrackets.(SequenceOf([
	operatorParser,
	ParserFactory.makeWs,
	expression,
	ParserFactory.makeWs,
	expression
])).map({ |results| (\type: \operation, \value : (\op : results[0], \a : results[2], \b : results[4]) ) });

// then an evaluator to perform the actual calculations based on the parsing result
var evaluate = {
	| node |
	if (node[\type] == \number) {
		node[\value];
	} {
		if (node[\type] == \operation) {
			if (node[\value][\op].compare("+") == 0) {
				thisFunction.(node[\value][\a]) + thisFunction.(node[\value][\b]); //recursion
			} {
				if (node[\value][\op].compare("-") == 0) {
					thisFunction.(node[\value][\a]) - thisFunction.(node[\value][\b]);
				} {
					if (node[\value][\op].compare("*") == 0) {
						thisFunction.(node[\value][\a]) * thisFunction.(node[\value][\b]);
					} {
						if (node[\value][\op].compare("/") == 0) {
							thisFunction.(node[\value][\a]) / thisFunction.(node[\value][\b]);
						}
					}
				}
			}
		}
	};
};

var interpreter = {
	| program |
	// first parse the program, then evaluate it
	var parseResult = expression.run(program);
	if (parseResult.isError) {
		"Invalid program".postln;
		nil;
	} {
		evaluate.(parseResult.result);
	}
};

// try it out
var program = "(+ (* 10 2) (-       (/50 3) 2))";
var result = interpreter.(program);
result.debug("result of the calculation");
)