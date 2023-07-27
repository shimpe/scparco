/*
[general]
title = "MapFactory"
summary = "a factory for commonly used map functions"
categories = "Parser Tools"
related = "Classes/Parser"
description = '''
MapFactory is a util that can create commonly used map functions.
'''
*/
MapFactory {
	/*
	[classmethod.new]
	description = "creates a new MapFactory"
	[classmethod.new.returns]
	what = "a new MapFactory"
	*/
	*new {
		^super.new.init();
	}

	/*
	[method.init]
	description = "initializes the parser factory (but in reality it does nothing :))"
	[method.init.returns]
	what = "MapFactory"
	*/
	init {
	}

	/*
	[classmethod.tag]
	description = '''creates a map function that wraps a parse result in an event with keys (\tagname : parseresult)'''
	[classmethod.tag.args]
	tagname = "tagname (will be transformed to a symbol)"
	[classmethod.tag.returns]
	what = '''a function that, when called with a parse result, wraps it in an event with keys (\tagname: parseresult) (see example code)'''
	*/
	*tag {
		| tagname |
		^{|result| (tagname.asSymbol : result)}
	}

	/*
	[classmethod.log]
	description = '''creates a map function doesn't modify the result, but logs a message to the post window = debugging tool'''
	[classmethod.msg.args]
	tagname = "msg (will be printed to post window)"
	[classmethod.msg.returns]
	what = '''a function that, when called with a parse result, returns that same result after printing something to the post window'''
	*/
	*log {
		| msg |
		^{ |result| msg.postln; result }
	}

	/*
	[examples]
	what = '''
	(
	var stream = Int8Array[0xF0];
	var binparser = ParserFactory.makeBinaryUIntParser(8).map(MapFactory.tag(\startOfSysex));
	var result = binparser.run(stream);
	result.result.postcs;
	)
	'''
	*/
}
