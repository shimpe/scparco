ParserState {
	var <>targetString;
    var <>index;
    var <>result;
	var <>isError;
    var <>errorMsg;

	*new {
		| targetString = "", index=0, result=nil, isError=false, errorMsg="" |
		^super.new.init(targetString, index, result, isError, errorMsg);
	}

	init {
		| targetString, index, result, isError, errorMsg |
		this.targetString = targetString;
		this.index = index;
		this.result = result;
		this.isError = isError;
		this.errorMsg = errorMsg;
	}

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

	printOn {
		this.prettyprint();
	}

	updateState {
		| newindex, newresult |
		^ParserState(this.targetString, newindex, newresult, this.isError, this.errorMsg);
	}

	updateResult {
		| newresult |
		^ParserState(this.targetString, this.index, newresult, false, this.errorMsg);
	}

	updateError {
		| errorMsg |
		^ParserState(this.targetString, this.index, this.result, true, errorMsg);
	}
}