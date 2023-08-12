(

var example = """
rows {
(Slider:s1      | TextField:t1 | StaticText:l1  | StaticText:l2)
(StaticText:l3  | TextField:t2 ||               | Knob:k1      )
(StaticText:l4  | TextField:t3 |                | StaticText:l5)
(Button:b1      ||             | Button:b2      ||             )
}
specs {
s1 : (orientation:\\horizontal)
t1 : (value: text1)
t2 : (value: textfield2!!)
l1 : (string: yo yo yo label1)
l2 : (string: label2)
l3 : (string: label3)
l4 : (string: label4)
l5 : (string: label5)
}
""";
var keyval = ScpSequenceOf([
	ScpRegexParser("[a-z][a-z0-9]*"),
	ScpParserFactory.makeWs,
	ScpStrParser(":"),
	ScpParserFactory.makeWs,
	ScpRegexParser("[^(,\))][^(,\))]*"), // everything except comma and closing bracket
	ScpParserFactory.makeWs
]).map({|result| (\key : result[0], \value: result[4]) });
var betweenCommas = ScpParserFactory.makeSepBy(ScpSequenceOf([
	ScpStrParser(","),
	ScpParserFactory.makeWs])).(keyval);
var spec_contents = ScpParserFactory.makeBetween(
	ScpSequenceOf([ScpStrParser("("), ScpParserFactory.makeWs]),
	ScpSequenceOf([ScpStrParser(")"), ScpParserFactory.makeWs])).(betweenCommas);
var spec = ScpSequenceOf([
	ScpParserFactory.makeIdentifierParser,
	ScpParserFactory.makeWs,
	ScpStrParser(":"),
	ScpParserFactory.makeWs,
	spec_contents,
	ScpParserFactory.makeWs
]).map({ |result| (\element: result[0], \elementspecs: result[4]) });
var spec_section = ScpSequenceOf([
	ScpStrParser("specs"),
	ScpParserFactory.makeWs,
	ScpStrParser("{"),
	ScpParserFactory.makeWs,
	ScpMany(spec),
	ScpStrParser("}")
]).map({ |result| (\allspecs : result[4]) });
var rowelement = ScpSequenceOf([
	ScpParserFactory.makeWs,
	ScpRegexParser("[A-Z][_a-zA-Z0-9]*"),
	ScpParserFactory.makeWs,
	ScpStrParser(":"),
	ScpParserFactory.makeWs,
	ScpParserFactory.makeIdentifierParser,
	ScpParserFactory.makeWs
]).map({ | result | (\uielement : result[1], \name : result[5]) });
var row_contents = ScpChoice([rowelement, ScpParserFactory.makeWsOne.map({ |result| nil })]);
var separatedByPipes = ScpSequenceOf([
	row_contents,
	ScpParserFactory.makeWs,
	ScpMany(ScpSequenceOf([
		ScpChoice([
			ScpStrParser("||").map({|result| (\sep : \extendcol)}),
			ScpStrParser("|").map({|result| (\sep : \newcol)})]),
	    row_contents,
		ScpParserFactory.makeWs]).map({|result| [result[0], result[1]] }))
]).map({|result| [result[0], result[2] ].flat; });
var row = ScpParserFactory.makeBetween(
	ScpSequenceOf([ScpStrParser("("), ScpParserFactory.makeWs]),
	ScpSequenceOf([ScpStrParser(")"), ScpParserFactory.makeWs])).(separatedByPipes);
var row_section = ScpSequenceOf([
	ScpStrParser("rows"),
	ScpParserFactory.makeWs,
	ScpStrParser("{"),
	ScpParserFactory.makeWs,
	ScpMany(row),
	ScpParserFactory.makeWs,
	ScpStrParser("}"),
	ScpParserFactory.makeWs
]).map({ |result| (\allrows : result[4]) });
var guilang = ScpSequenceOf([
	ScpParserFactory.makeWs,
	row_section,
	ScpParserFactory.makeWs,
	spec_section
]).map({ |result| (\rows : result[1][\allrows], \specs: result[3][\allspecs]) });


/*
// parse result should look like this:
(
'rows': [
    [
        ( 'name': "s1", 'uielement': "Slider" ),
        ( 'sep': 'newcol' ),
        ( 'name': "t1", 'uielement': "TextField" ),
        ( 'sep': 'newcol' ),
        ( 'name': "l1", 'uielement': "StaticText" ),
        ( 'sep': 'newcol' ),
        ( 'name': "l2", 'uielement': "StaticText" )
    ],
    [
        ( 'name': "l3", 'uielement': "StaticText" ),
        ( 'sep': 'newcol' ),
        ( 'name': "t2", 'uielement': "TextField" ),
        ( 'sep': 'extendcol' ),
        nil,
        ( 'sep': 'newcol' ),
        ( 'name': "k1", 'uielement': "Knob" )
    ],
    [
        ( 'name': "l4", 'uielement': "StaticText" ),
        ( 'sep': 'newcol' ),
        ( 'name': "t3", 'uielement': "TextField" ),
        ( 'sep': 'newcol' ),
        nil,
        ( 'sep': 'newcol' ),
        ( 'name': "l5", 'uielement': "StaticText" )
    ],
    [
        ( 'name': "b1", 'uielement': "Button" ),
        ( 'sep': 'extendcol' ),
        nil,
        ( 'sep': 'newcol' ),
        ( 'name': "b2", 'uielement': "Button" ),
        ( 'sep': 'extendcol' ),
        nil
    ]
],
'specs': [
    (
        'elementspecs': [ ( 'value': "\\horizontal", 'key': "orientation" ) ],
        'element': "s1" ),
    (
        'elementspecs': [ ( 'value': "text1", 'key': "value" ) ],
        'element': "t1" ),
    (
        'elementspecs': [ ( 'value': "textfield2!!", 'key': "value" ) ],
        'element': "t2" ),
    (
        'elementspecs': [ ( 'value': "yo yo yo label1", 'key': "string" ) ],
        'element': "l1" ),
    (
        'elementspecs': [ ( 'value': "label2", 'key': "string" ) ],
        'element': "l2" ),
    (
        'elementspecs': [ ( 'value': "label3", 'key': "string" ) ],
        'element': "l3" ),
    (
        'elementspecs': [ ( 'value': "label4", 'key': "string" ) ],
        'element': "l4" ),
    (
        'elementspecs': [ ( 'value': "label5", 'key': "string" ) ],
        'element': "l5" )
]
)
*/


var get_specs = {
	| parse_tree, elementname |
	var returnvalue = nil;
	parse_tree.result[\specs].do({
		| spec |
		if (spec[\element].compare(elementname) == 0) {
			returnvalue = spec[\elementspecs];
		}
	});
	returnvalue;
};

var destill_columns = {
	| parse_tree |
	var stack = ();
	parse_tree.result[\rows].do({
		| row, idx |
		var stretch_so_far = 1;
		var extendcolactive = false;
		var r = idx.asSymbol;
		stack[r] = [];
		row.do({
			| el |
			if (el.isNil) {
				if (extendcolactive.not) {
					stack[r] = stack[r].add((
						\name: nil,
						\uielement: nil,
						\columns:1));
				};
			} {
				if (el[\name].notNil) {
					// a ui element
					stretch_so_far = 1;
					stack[r] = stack[r].add((
						\name : el[\name],
						\uielement : el[\uielement],
						\columns: 1));
					extendcolactive = false;
				} {
					if (el[\sep].notNil) {
						if (el[\sep] == \newcol) {
							stretch_so_far = 1;
							extendcolactive = false;
						} {
							if (el[\sep] == \extendcol) {
								stretch_so_far = stretch_so_far + 1;
								stack[r][stack[r].size-1][\columns] = stretch_so_far;
								extendcolactive = true;
							};
						};
					};
				};
			};
		});
	});
	stack;
};

var make_layout = {
	| parse_tree, cols, ui |
	var rows = [];
	cols.size.do({
		| idx |
		var colkey = idx.asSymbol;
		var row = cols[colkey].collect({
			| el |
			if (el[\name].isNil) {
				nil;
			} {
				var ctrl = (el[\uielement] ++ ".new()").interpret;
				var specs = get_specs.(parse_tree, el[\name]);
				var nameSym = el[\name].asSymbol;
				if (specs.notNil) {
					specs.do({
						|spec|
						var msg = spec[\value];
						if (msg.beginsWith("\\")) {
							msg = msg.drop(1).asSymbol;
						};
						ctrl.performList((spec[\key]++"_").asSymbol, [msg]);
					});
				};
				ctrl.action = ui[\callbacks][nameSym][\ui_to_model];
				ctrl.onClose_{ui[\controllers][nameSym].remove};
				ui[\uielements][nameSym] = ctrl;
				[ctrl, columns: el[\columns].asInteger]
			};
		});
		rows = rows.add(row);
	});
	ui[\layout] = GridLayout.rows(*rows);
	ui[\layout]
};
var make_models = {
	| cols, ui |
	cols.size.do({
		| idx |
		var colkey = idx.asSymbol;
		cols[colkey].do({
			|el|
			if (el[\name].notNil) {
				var nameSym = el[\name].asSymbol;
				ui[\models][nameSym] = Ref(el[\value] ?? 0);
			};
		});
	});
	ui[\models];
};

var make_callbacks = {
	| cols, ui |
	cols.size.do({
		| idx |
		var colkey = idx.asSymbol;
		var row = cols[colkey].collect({
			| el |
			if (el[\name].isNil.not) {
				var nameSym = el[\name].asSymbol;
				ui[\callbacks][nameSym] = (
					\model_to_ui : {
						|modelvalue|
						//("setting model" + el[\name] + "from" + ui[\uielements][nameSym].value + "to " + modelvalue.value).postln;
						ui[\callbacks][nameSym][\on_change_from_model].value(ui[\uielements][nameSym].value, modelvalue.value);
						ui[\uielements][nameSym].value_(modelvalue.value).changed(\value);
					},
					\ui_to_model : {
						| uielement |
						//("updating model" + el[\name] + "from" + ui[\models][nameSym].value + "to " + uielement.value).postln;
						ui[\callbacks][nameSym][\on_change_from_ui].value(ui[\models][nameSym].value, uielement.value);
						ui[\models][nameSym].value_(uielement.value);
					},
					\on_change_from_ui : {
						| oldvalue, newvalue |
						// to be filled in by user
					},
					\on_change_from_model : {
						| oldvalue, newvalue |
						// to be filled in by user
					}
				);
			};
		});
	});
	ui[\callbacks];
};

var make_controllers = {
	| cols, ui |
	cols.size.do({
		| idx |
		var colkey = idx.asSymbol;
		cols[colkey].do({
			| el |
			if (el[\name].notNil) {
				var nameSym = el[\name].asSymbol;
				ui[\controllers][nameSym] = SimpleController(ui[\models][nameSym]);
				ui[\controllers][nameSym].put(\value, ui[\callbacks][nameSym][\model_to_ui]);
			};
		});
	});
	ui[\controllers]
};

var create_ui = {
	| spec |
	var parse_tree = guilang.run(spec);
	var cols;
	var ui = (
		\models : (
			\setvalue : {
				|self, uistruct, uielementname, val|
				uistruct[\callbacks][uielementname][\model_to_ui].value(val);
			}
		),
		\callbacks: (),
		\uielements: (),
		\controllers: (),
		\layout : nil
	);
	if (parse_tree.isError) {
		"Error parsing your guilang specification!".postln;
		parse_tree.prettyprint;
	} {
		parse_tree.result.postcs;
	};
	cols = destill_columns.(parse_tree);
	make_callbacks.(cols, ui);
	make_layout.(parse_tree, cols, ui);
	make_models.(cols, ui);
	make_controllers.(cols, ui);
	ui;
};

var ui  = create_ui.(example);
var w = Window().layout_(ui[\layout]).front;

CmdPeriod.doOnce({
	w.close;
});

fork {
	// check if you can implement a callback (drag slider 1 to test)
	ui[\callbacks][\s1][\on_change_from_ui] = {
		| oldvalue, newvalue|
		("**** USER CALLBACK UI CHANGE!!! Changed s1 from " + oldvalue + "to" + newvalue).postln;
	};

	ui[\callbacks][\s1][\on_change_from_model] = {
		| oldvalue, newvalue|
		("**** USER CALLBACK MODEL CHANGE!!! Changed s1 from " + oldvalue + "to" + newvalue).postln;
	};

	// check if the model changes are observed by the ui
	10.do({
		| i |
		defer { ui[\models].setvalue(ui, \s1, 1.0.rand) };
		1.wait;
	});
};
)