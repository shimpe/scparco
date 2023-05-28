/*
[general]
title = "ParserState"
summary = "holds the internal state of a parser"
categories = "Parser Tools"
related = "Classes/Parser"
description = '''
ParserState uses the internal state of a parser. It has methods to update that state and to pretty-print it.
'''
*/
ParserState {
	/*
	[method.targetString]
	description='''the string to be parsed'''
	[method.targetString.returns]
	what = "a string"
	*/
	var <>targetString;
	/*
	[method.index]
	description='''the position in the string where we are during parsing'''
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
	[classmethod.new]
	description = "creates a new ParserState"
	[classmethod.new.args]
	targetString = "string that is being parsed"
	index = "current position in the string as parsing is proceeding"
	result = "result of parsing the string, will be a string by default, but can be transformed by specifying a map on the parser"
	isError = "boolean to indicate if parsing failed"
	errorMsg = "string with some explanation about what error occurred. Error msgs can be made contextual by specifying a n erorrMap on the parser"
	[classmethod.new.returns]
	what = "a new ParserState"
	*/
	*new {
		| targetString = "", index=0, result=nil, isError=false, errorMsg="" |
		^super.new.init(targetString, index, result, isError, errorMsg);
	}
	/*
	[method.init]
	description = "initializes a new ParserState"
	[method.init.args]
	targetString = "string that is being parsed"
	index = "current position in the string as parsing is proceeding"
	result = "result of parsing the string, will be a string by default, but can be transformed by specifying a map on the parser"
	isError = "boolean to indicate if parsing failed"
	errorMsg = "string with some explanation about what error occurred. Error msgs can be made contextual by specifying a n erorrMap on the parser"
	[method.init.returns]
	what = "an initialized ParserState"
	*/
	init {
		| targetString, index, result, isError, errorMsg |
		this.targetString = targetString;
		this.index = index;
		this.result = result;
		this.isError = isError;
		this.errorMsg = errorMsg;
	}

	/*
	[method.prettyprint]
	description = "prints a readable representation of the ParserState to the post window; in case of errors it will just show the error msg"
	*/
	prettyprint {
		if (this.isError == true) {
			this.errorMsg.postln;
		} {
			"*************".postln;
			"*ParserState*".postln;
			"*************".postln;
			("targetString: " + this.targetString).postln;
			("index: " + this.index).postln;
			("result: " + this.result).postln;
			"*************".postln;
		}
	}

	/*
	[method.updateState]
	description = "creates a completely new ParserState out of the current one with a new result and updated index"
	[method.updateState.args]
	newindex = "updated index"
	newresult = "updated result"
	[method.updateState.returns]
	what = "ParserState"
	*/
	updateState {
		| newindex, newresult |
		^ParserState(this.targetString, newindex, newresult, this.isError, this.errorMsg);
	}

	/*
	[method.updateResult]
	description = '''creates a completely new ParserState out of the current one with a new result
	updateResult will also reset isError to false since we assume having a result means that parsing succeeded
	'''
	[method.updateResult.args]
	newresult = "updated result"
	[method.updateResult.returns]
	what = "ParserState"
	*/
	updateResult {
		| newresult |
		^ParserState(this.targetString, this.index, newresult, false, this.errorMsg);
	}

	/*
	[method.updateError]
	description = '''creates a completely new ParserState out of the current one to represents an error
	'''
	[method.updateError.args]
	errorMsg = "error message to be shown to the user"
	[method.updateError.returns]
	what = "ParserState"
	*/
	updateError {
		| errorMsg |
		^ParserState(this.targetString, this.index, this.result, true, errorMsg);
	}
}