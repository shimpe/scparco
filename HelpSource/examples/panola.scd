(
var noteParser = RegexParser("[aAbBcCdDeEfFgG]").map({|result| (\type: \notename, \value: result.toLower) });
var restParser = RegexParser("[rR]").map({|result| (\type: \rest) });
var noteModifier = Optional(Choice([
	StrParser("--").map({|result| (\type : \notemodifier, \value: \doubleflat) }),
	StrParser("-").map({|result| (\type: \notemodifier, \value: \flat) }),
	StrParser("#").map({|result| (\type: \notemodifier, \value: \sharp) }),
	StrParser("x").map({|result| (\type: \notemodifier, \value: \doublesharp) })
])).map({|result| result ? (\type: \notemodifier, \value: \natural) }); // map missing modifier to \natural sign
var octaveParser = Optional(
	RegexParser("\\d\\d?").map({|result| (\type: \octave, \value: result.asInteger )})
).map({|result| result ? (\type: \octave, \value: \previous) }); // map missing octave to \previous

var durationParser = Optional(SequenceOf([
	StrParser("_"),
	ParserFactory.makeFloatParser.map({|result| (\type: \duration, \value: result)}),
	Many(StrParser(".")).map({|result| (\type: \durdots, \value: result.size)}),
	Optional(SequenceOf([StrParser("*"), ParserFactory.makeIntegerParser]).map({|result| (\type: \durmultiplier, \value: result[1])})),
	Optional(SequenceOf([StrParser("/"), ParserFactory.makeIntegerParser]).map({|result| (\type: \durdivider, \value: result[1])}))
])).map({
	|result|
	if (result.isNil) {
		(\dur : \previous, \durmultiplier: \previous, \durdivider: \previous, \durdots: \previous);
	} {
		var dur = ( \dur : result[1][\value], \durdots : result[2][\value]);
		// treat divider and multiplier as one: specifying only one of the two affects the other one
		if (result[3].isNil && result[4].isNil) {
			dur[\durmultiplier] = \previous;
			dur[\durdivider] = \previous;
		} {
			if (result[3].isNil) {
				// only dividider specified ->reset multplier to 1
				dur[\durmultiplier] = 1;
				dur[\durdivider] = result[4][\value];
			} {
				if (result[4].isNil) {
					// only multiplier specified -> reset divider to 1
					dur[\durmultiplier] = result[3][\value];
					dur[\durdivider] = 1;
				} {
					// everything specified
					dur[\durmultiplier] = result[3][\value];
					dur[\durdivider] = result[4][\value];
				}
			};
		};
		dur;
	};
});

var propertynameParser = RegexParser("@[a-zA-z][a-zA-Z0-9]*").map({|result| (\type: \propertyname, \value: result.drop(1))});
var propertiesParser = Many(
	Choice([
		SequenceOf([
			propertynameParser,
			StrParser("{"),
			ParserFactory.makeFloatParser,
			StrParser("}")
		]).map({|result| (\propertyname: result[0][\value], \type: \animatedproperty, \value: result[2])}),
		SequenceOf([
			propertynameParser,
			StrParser("["),
			ParserFactory.makeFloatParser,
			StrParser("]")
		]).map({|result| (\propertyname: result[0][\value], \type: \staticproperty, \value: result[2])}),
		SequenceOf([
			propertynameParser,
			StrParser("^"),
			ParserFactory.makeFloatParser,
			StrParser("^")
		]).map({|result| (\propertyname: result[0][\value], \type: \oneshotproperty, \value: result[2])}),
]));

var noteAndMod = Choice([
	SequenceOf([noteParser, noteModifier]),
	restParser
]);
var noteAndModAndOct = Choice([
	SequenceOf([noteParser, noteModifier, octaveParser]).map({
		|result|
		(\type: \note,
			\notename: result[0][\value],
			\notemodifier: result[1][\value],
			\octave: result[2][\value])
	}),
	restParser
]);

var noteAndModAndOctAndDur = SequenceOf([
	noteAndModAndOct,
	durationParser
]).map({|result| (\pitch : result[0], \duration: result[1] ) });

var noteAndModAndOctAndDurAndProp = SequenceOf([
	noteAndModAndOctAndDur,
	propertiesParser]).map({|result| (\type: \singlenote, \info : ( \note : result[0], \props : result[1] ) ); });

var betweenChordBrackets = ParserFactory.makeBetween(
	SequenceOf([StrParser("<"), ParserFactory.makeWs]),
	StrParser(">"));

var chordParser = betweenChordBrackets.(
	ManyOne(
		SequenceOf([
			noteAndModAndOctAndDurAndProp,
			ParserFactory.makeWs
		]).map({|result| result[0] }); // remove whitespace from result
)).map({|result| (\type: \chord, \notes : result) });

var notelistParser = ManyOne(Choice([
	SequenceOf([chordParser, ParserFactory.makeWs]).map({|result| result[0]}), // eat whitespace
	SequenceOf([noteAndModAndOctAndDurAndProp, ParserFactory.makeWs]).map({|result| result[0] }) // eat whitespace
]));

var betweenRepeatBrackets = ParserFactory.makeBetween(
	SequenceOf([StrParser("("), ParserFactory.makeWs]),
	StrParser(")");
);

var mixedNotelist = ParserFactory.forwardRef(Thunk({
	ManyOne(Choice([repeatedNotelist, notelistParser])).map({|result| result.flatten(1); });
}));

var repeatedNotelist = SequenceOf([
	betweenRepeatBrackets.(mixedNotelist),
	ParserFactory.makeWs,
	StrParser("*"),
	ParserFactory.makeWs,
	ParserFactory.makeIntegerParser,
	ParserFactory.makeWs
]).map({
	// unroll the loop already - not sure if this is a good idea (memory consumption!)
	// but it's easier to evaluate later on
	|result|
	var parseRes = [];
	var repeat = result[4];
	repeat.do({
		parseRes = parseRes.addAll(result[0]);
	});
	parseRes.flatten(1);
});


// now do some checks that it actually works
var check = {
	| result, expected |
	var ok = true;
	var props = [\type, \value, \propertyname, \notename, \notemodifier, \octave, \dur, \durdots, \durmultiplier, \durdivider];
	props.do({
		| prop |
		if (expected[prop].isNil.not) {
			if (expected[prop].isString) {
				if (result[prop].compare(expected[prop]) != 0)
				{
					("" ++ prop + "mismatch: expected[\\" ++ prop ++ "] == " + expected[prop] + ", but result[\\" ++ prop ++ "] == " + result[prop]).postln;
					ok = false;
				}
			} {
				if (result[prop] != expected[prop]) {
					("" ++ prop + "mismatch: expected[\\" ++ prop ++ "] == " + expected[prop] + ", but result[\\" ++ prop ++ "] == " + result[prop]).postln;
					ok = false;
				};
			}
		};
	});
	if (ok) {
		"ok".postln;
	};
};

var checkemptylist = {
	| what |
	if (what != []) {
		("error! expected empty list, but got" + what).postln;
	}{
		"ok".postln;
	};
};

var checkeq = {
	| result, expected |
	if (result != expected) {
		("result " + result + "!= expected" + expected).postln;
	} {
		"ok".postln;
	};
};


var text = ();
var result = ();


text[\1] = "a#4_4";
result[\11] = noteParser.run(text[\1]);
check.(result[\11].result, (\type: \notename, \value: "a" ));

text[\2] = "#";
result[\21] = noteModifier.run(text[\2]);
check.(result[\21].result, (\type: \notemodifier, \value: \sharp));

result[\12] = noteAndMod.run(text[\1]);
check.(result[\12].result[0], (\type: \notename, \value: "a"));
check.(result[\12].result[1], (\type: \notemodifier, \value: \sharp));

text[\3] = "r_4";
result[\31] = noteAndMod.run(text[\3]);
check.(result[\31].result, (\type: \rest));

result[\13] = noteAndModAndOct.run(text[\1]);
check.(result[\13].result, (\type: \note, \notename: "a", \notemodifier: \sharp, \octave: 4));

text[\4] = "c--_4";
result[\41] = noteAndModAndOct.run(text[\4]);
check.(result[\41].result, (\type: \note, \notename: "c", \notemodifier: \doubleflat, \octave: \previous));

text[\5] = "f-_4*8/3";
result[\51] = noteAndModAndOctAndDur.run(text[\5]);
check.(result[\51].result[\pitch], (\type: \note, \octave: \previous, \notename: "f", \notemodifier: \flat));
check.(result[\51].result[\duration], (\dur: 4.0, \durdots: 0, \durmultiplier: 8, \durdivider: 3));

text[\6] = "r_2./3";
result[\61] = noteAndModAndOctAndDur.run(text[\6]);
check.(result[\61].result[\pitch], (\type: \rest));
check.(result[\61].result[\duration], (\dur: 2.0, \durdots: 1, \durmultiplier: 1, \durdivider: 3));

text[\7] = "f5_4@prop[34.4]@vol{5}@boem^1^";
result[\71] = noteAndModAndOctAndDurAndProp.run(text[\7]);
check.(result[\71].result[\info][\note][\pitch], ( 'octave': 5, 'type': 'note', 'notename': "f", 'notemodifier': 'natural' ));
check.(result[\71].result[\info][\note][\duration], ( 'durmultiplier': 'previous', 'durdots': 0, 'dur': 4.0, 'durdivider': 'previous' ));
check.(result[\71].result[\info][\props][0],  ( 'value': 34.4, 'propertyname': "prop", 'type': 'staticproperty' ));
check.(result[\71].result[\info][\props][1],  ( 'value': 5.0, 'propertyname': "vol", 'type': 'animatedproperty' ));
check.(result[\71].result[\info][\props][2],  ( 'value': 1.0, 'propertyname': "boem", 'type': 'oneshotproperty' ));

text[\8] = "bx2_4..*8";
result[\81] = noteAndModAndOctAndDurAndProp.run(text[\8]);
check.(result[\81].result[\info][\note][\pitch], ( 'octave': 2, 'type': 'note', 'notename': "b", 'notemodifier': 'doublesharp' ));
check.(result[\81].result[\info][\note][\duration], ( 'durmultiplier': 8, 'durdots': 2, 'dur': 4.0, 'durdivider': 1 ));
checkemptylist.(result[\81].result[\info][\props]);

text[\82] = "e";
result[\82] = noteAndModAndOctAndDurAndProp.run(text[\82]);
check.(result[\82].result[\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "e", 'notemodifier': 'natural' ));
check.(result[\82].result[\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
checkemptylist.(result[\82].result[\info][\props]);

text[\9] = "< a4_3@ped^127^ c5 e>";
result[\91] = chordParser.run(text[\9]);
if (result[\91].result[\type] != \chord) {"error: expected chord!".postln; } { "ok".postln; };
check.(result[\91].result[\notes][0][\info][\note][\pitch], ( 'octave': 4, 'type': 'note', 'notename': "a", 'notemodifier': 'natural' ));
check.(result[\91].result[\notes][0][\info][\note][\duration], ( 'durmultiplier': 'previous', 'durdots': 0, 'dur': 3.0, 'durdivider': 'previous' ));
check.(result[\91].result[\notes][0][\info][\props][0], ( 'value': 127.0, 'propertyname': "ped", 'type': \oneshotproperty ));
check.(result[\91].result[\notes][1][\info][\note][\pitch], ( 'octave': 5, 'type': 'note', 'notename': "c", 'notemodifier': 'natural' ));
check.(result[\91].result[\notes][1][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
checkemptylist.(result[\91].result[\notes][1][\info][\props]);
check.(result[\91].result[\notes][2][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "e", 'notemodifier': 'natural' ));
check.(result[\91].result[\notes][2][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ) );
checkemptylist.(result[\91].result[\notes][2][\info][\props]);


text[\10] = "a4 b-3_2@vol{123} <e2_4.*2/3 g b> d5";
result[\10] = notelistParser.run(text[\10]);
checkeq.(result[\10].result[0][\type], \singlenote);
check.(result[\10].result[0][\info][\note][\pitch], ( 'octave': 4, 'type': \note, 'notename': "a", 'notemodifier': \natural ));
check.(result[\10].result[0][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
checkemptylist.(result[\10].result[0][\info][\props]);
checkeq.(result[\10].result[1][\type], \singlenote);
check.(result[\10].result[1][\info][\note][\pitch], ( 'octave': 3, 'type': 'note', 'notename': "b", 'notemodifier': 'flat' ));
check.(result[\10].result[1][\info][\note][\duration], ( 'durmultiplier': 'previous', 'durdots': 0, 'dur': 2.0, 'durdivider': 'previous' ));
check.(result[\10].result[1][\info][\props][0], ( 'value': 123.0, 'propertyname': "vol", 'type': 'animatedproperty' ));
checkeq.(result[\10].result[2][\type], \chord);
check.(result[\10].result[2][\notes][0][\info][\note][\pitch], ( 'octave': 2, 'type': 'note', 'notename': "e", 'notemodifier': 'natural' ));
check.(result[\10].result[2][\notes][0][\info][\note][\duration], ( 'durmultiplier': 2, 'durdots': 1, 'dur': 4.0, 'durdivider': 3 ));
check.(result[\10].result[2][\notes][1][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "g", 'notemodifier': 'natural' ));
check.(result[\10].result[2][\notes][1][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ) );
check.(result[\10].result[2][\notes][2][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "b", 'notemodifier': 'natural' ));
check.(result[\10].result[2][\notes][2][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ) );
checkeq.(result[\10].result[3][\type], \singlenote);
check.(result[\10].result[3][\info][\note][\pitch], ( 'octave': 5, 'type': 'note', 'notename': "d", 'notemodifier': 'natural' ));
check.(result[\10].result[3][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
checkemptylist.(result[\10].result[3][\info][\props]);

text[\11] = "(a4 b-3_2@vol{123})*3";
result[\11] = repeatedNotelist.run(text[\11]);
checkeq.(result[\11].result.size, 6);

text[\12] = "e (a (b c#)*2 )*3 d-";
result[\12] = mixedNotelist.run(text[\12]);
checkeq.(result[\12].result.size, 17);
check.(result[\12].result[0][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "e", 'notemodifier': 'natural' ));
check.(result[\12].result[0][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[1][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "a", 'notemodifier': 'natural' ));
check.(result[\12].result[1][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[2][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "b", 'notemodifier': 'natural' ));
check.(result[\12].result[2][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[3][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "c", 'notemodifier': 'sharp' ));
check.(result[\12].result[3][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[4][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "b", 'notemodifier': 'natural' ));
check.(result[\12].result[4][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[5][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "c", 'notemodifier': 'sharp' ));
check.(result[\12].result[5][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[6][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "a", 'notemodifier': 'natural' ));
check.(result[\12].result[6][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[7][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "b", 'notemodifier': 'natural' ));
check.(result[\12].result[7][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[8][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "c", 'notemodifier': 'sharp' ));
check.(result[\12].result[8][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[9][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "b", 'notemodifier': 'natural' ));
check.(result[\12].result[9][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[10][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "c", 'notemodifier': 'sharp' ));
check.(result[\12].result[10][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[11][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "a", 'notemodifier': 'natural' ));
check.(result[\12].result[11][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[12][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "b", 'notemodifier': 'natural' ));
check.(result[\12].result[12][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[13][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "c", 'notemodifier': 'sharp' ));
check.(result[\12].result[13][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[14][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "b", 'notemodifier': 'natural' ));
check.(result[\12].result[14][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[15][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "c", 'notemodifier': 'sharp' ));
check.(result[\12].result[15][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));
check.(result[\12].result[16][\info][\note][\pitch], ( 'octave': 'previous', 'type': 'note', 'notename': "d", 'notemodifier': 'flat' ));
check.(result[\12].result[16][\info][\note][\duration], ( 'dur': 'previous', 'durmultiplier': 'previous', 'durdivider': 'previous', 'durdots': 'previous' ));

)