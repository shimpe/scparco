/*
[general]
title = "ScpParserState"
summary = "holds the internal state of a parser"
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpParserState uses the internal state of a parser. It has methods to update that state and to pretty-print it.
'''
*/
ScpParserState {
	/*
	[method.target]
	description='''the string to be parsed (text parsers) or an Int8Array (binary parsers)'''
	[method.target.returns]
	what = "a string or an Int8Array"
	*/
	var <>target;
	/*
	[method.index]
	description='''the position in the string/stream where we are during parsing'''
	[method.index.returns]
	what = "an integer"
	*/
    var <>index;
	/*
	[method.result]
	description='''
	the result of parsing, e.g. in case of an IntegerParser this could be an integer.
	(Side-Note: results produced by parsers can be influenced by specifying a map operation on the parser,
	and can influence how parsing proceeds by specifying a chain operation on the parser.)
	'''
	[method.result.returns]
	what = "anything you want it to"
	*/
    var <>result;
	/*
	[method.isError]
	description='''a boolean to indicate if parsing failed'''
	[method.isError.returns]
	what = "a boolean"
	*/
	var <>isError;
	/*
	[method.errorMsg]
	description='''a string to give some explanation about what went wrong'''
	[method.errorMsg.returns]
	what = "a string"
	*/
    var <>errorMsg;

	/*
	[method.trace]
	description='''a boolean to indicate if the parsers log themselves for debugging purposes'''
	[method.trace.returns]
	what = "a boolean"
	*/
	var <>trace;

	/*
	[classmethod.new]
	description = "creates a new ScpParserState"
	[classmethod.new.args]
	target = "string/Int8Array that is being parsed"
	index = "current position in the string as parsing is proceeding"
	result = "result of parsing the string, will be a string by default, but can be transformed by specifying a map on the parser"
	isError = "boolean to indicate if parsing failed"
	errorMsg = "string with some explanation about what error occurred. Error msgs can be made contextual by specifying a n erorrMap on the parser"
	[classmethod.new.returns]
	what = "a new ScpParserState"
	*/
	*new {
		| target, index=0, result=nil, isError=false, errorMsg="", trace=false |
		^super.new.init(target, index, result, isError, errorMsg, trace);
	}
	/*
	[method.init]
	description = "initializes a new ScpParserState"
	[method.init.args]
	target = "string/Int8Array that is being parsed"
	index = "current position in the string as parsing is proceeding"
	result = "result of parsing the string, will be a string by default, but can be transformed by specifying a map on the parser"
	isError = "boolean to indicate if parsing failed"
	errorMsg = "string with some explanation about what error occurred. Error msgs can be made contextual by specifying a n erorrMap on the parser"
	[method.init.returns]
	what = "an initialized ScpParserState"
	*/
	init {
		| target, index, result, isError, errorMsg, trace |
		this.target = target;
		this.index = index;
		this.result = result;
		this.isError = isError;
		this.errorMsg = errorMsg;
		this.trace = trace;
	}

	/*
	[method.prettyprint]
	description = "prints a readable representation of the ScpParserState to the post window; in case of errors it will just show the error msg"
	*/
	prettyprint {
		if (this.isError == true) {
			this.errorMsg.postln;
		} {
			"*************".postln;
			"*ScpParserState*".postln;
			"*************".postln;
			("target: " + this.target).postln;
			("index: " + this.index).postln;
			("result: " + this.result).postln;
			"*************".postln;
		}
	}

	/*
	[method.print]
	description = "prints the contents of the ScpParserState to the post window"
	*/
	print {
		"*************".postln;
		"*ScpParserState*".postln;
		"*************".postln;
		("target: " + this.target).postln;
		("index: " + this.index).postln;
		("result: " + this.result).postln;
		("isError: " + this.isError).postln;
		("errorMsg: " + this.errorMsg).postln;
		("trace: " + this.trace).postln;
	}

	/*
	[method.updateState]
	description = "creates a completely new ScpParserState out of the current one with a new result and updated index"
	[method.updateState.args]
	newindex = "updated index"
	newresult = "updated result"
	[method.updateState.returns]
	what = "ScpParserState"
	*/
	updateState {
		| newindex, newresult |
		^ScpParserState(this.target, newindex, newresult, this.isError, this.errorMsg, this.trace);
	}

	/*
	[method.updateResult]
	description = '''creates a completely new ScpParserState out of the current one with a new result
	updateResult will also reset isError to false since we assume having a result means that parsing succeeded
	'''
	[method.updateResult.args]
	newresult = "updated result"
	[method.updateResult.returns]
	what = "ScpParserState"
	*/
	updateResult {
		| newresult |
		^ScpParserState(this.target, this.index, newresult, false, this.errorMsg, this.trace);
	}

	/*
	[method.updateError]
	description = '''creates a completely new ScpParserState out of the current one to represents an error
	'''
	[method.updateError.args]
	errorMsg = "error message to be shown to the user"
	[method.updateError.returns]
	what = "ScpParserState"
	*/
	updateError {
		| errorMsg |
		^ScpParserState(this.target, this.index, this.result, true, errorMsg, this.trace);
	}
}
