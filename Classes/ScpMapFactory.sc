/*
[general]
title = "ScpMapFactory"
summary = "a factory for commonly used map functions"
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpMapFactory is a util that can create commonly used map functions.
'''
*/
ScpMapFactory {
	/*
	[classmethod.new]
	description = "creates a new ScpMapFactory"
	[classmethod.new.returns]
	what = "a new ScpMapFactory"
	*/
	*new {
		^super.new.init();
	}

	/*
	[method.init]
	description = "initializes the parser factory (but in reality it does nothing :))"
	[method.init.returns]
	what = "ScpMapFactory"
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
	[classmethod.log.args]
	msg = "msg (will be printed to post window)"
	[classmethod.log.returns]
	what = '''a function that, when called with a parse result, returns that same result after printing something to the post window'''
	*/
	*log {
		| msg |
		^{ |result| msg.postln; result }
	}

	/*
	[classmethod.keyvalue]
	description = '''creates a map function taking parameters keyarg and valuearg, that wraps a parse result in an event with keys (\key : keyarg, \value: valuearg)'''
	[classmethod.keyvalue.args]
	key = "what to store under the \key key"
	value = "what to store under the \value key"
	[classmethod.keyvalue.returns]
	what = '''a function that, when called with a parse result, wraps it in an event with keys (\key: keyarg, \value: valuearg)'''
	*/
	*keyvalue {
		| key |
		^{ | result | (\key : key, \value : result) }
	}

	/*
	[examples]
	what = '''
	(
	var stream = Int8Array[0xF0];
	var binparser = ScpParserFactory.makeBinaryUIntParser(8).map(ScpMapFactory.tag(\startOfSysex));
	var result = binparser.run(stream);
	result.result.postcs;
	)
	'''
	*/
}
