package com.kyper.yarn;


import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * A collection of callable functions
 */
public class Library {

	private ObjectMap<String, FunctionInfo> functions = new ObjectMap<String, Library.FunctionInfo>();

	/**
	 * Returns a function - throws an exception if the function doesnt exist.Use
	 * FunctionExists to check for a functions existance.
	 */
	public FunctionInfo getFunction(String name) {
		if (functions.containsKey(name))
			return functions.get(name);
		throw new IllegalStateException(name + " is not a valid function");
	}

	/**
	 * loads functions from another lib. if the other lib contains a function with
	 * the same name as ours, ours will be replaced
	 */
	public void importLibrary(Library otherLib) {
		for (Entry<String, FunctionInfo> entry : otherLib.functions) {
			functions.put(entry.key, entry.value);
		}
	}

	public void registerFunction(FunctionInfo function) {
		functions.put(function.name, function);
	}

	public void registerFunction(String name,int paramCount,ReturningFunc implementation) {
		FunctionInfo info = new FunctionInfo(name, paramCount, implementation);
		registerFunction(info);
	}

	public void registerFunction(String name,int paramCount,Function implementation) {
		FunctionInfo info = new FunctionInfo(name, paramCount, implementation);
		registerFunction(info);
	}

	public boolean functionExists(String name) {
		return functions.containsKey(name);
	}

	public void deregisterFunction(String name) {
		if(functions.containsKey(name))
			functions.remove(name);
	}

	public static interface ReturningFunc {
		public Object invoke(Value... params);
	}

	public static interface Function {
		public void invoke(Value... params);
	}

	protected static class FunctionInfo {
		//the name of the function as it exists in the script
		private String name;
		//the number of parameters this function requores.
		//-1 = the function will accept any number of params
		private int    paramCount;

		//the actual implementation of the function
		//comes in two flavours: a returning one, and a non returning one.
		//doing this means that you dont have to add a return null
		//to the end of a function if it doesnt return values
		private Function      function;
		private ReturningFunc returningFunc;

		//TODO: support for typed parameters
		//TODO: support for return type
		protected FunctionInfo(String name, int paramCount, Function implementation) {
			this.name = name;
			this.paramCount = paramCount;
			this.function = implementation;
			this.returningFunc = null;
		}

		protected FunctionInfo(String name, int paramCount, ReturningFunc implementation) {
			this.name = name;
			this.paramCount = paramCount;
			this.returningFunc = implementation;
			this.function = null;
		}

		protected FunctionInfo(String name, int paramCount) {
			this.name = name;
			this.paramCount = paramCount;
			this.returningFunc = null;
			this.function = null;
		}

		public Function getFunction() {
			return function;
		}

		public ReturningFunc getReturningFunction() {
			return returningFunc;
		}

		public String getName() {
			return name;
		}

		public int getParamCount() {
			return paramCount;
		}

		//does this function return a value?
		public boolean returnsValue() {
			return returningFunc != null;
		}

		public Value invoke(Value... params) {
			return invokeWithArray(params);
		}

		public Value invokeWithArray(Value[] params) {
			int length = 0;
			if (params != null)
				length = params.length;

			if (isParamCountCorrect(length)) {
				if (returnsValue()) {
					return new Value(returningFunc.invoke(params));
				} else {
					function.invoke(params);
					return Value.NULL;
				}
			} else {
				String error = StringUtils.format(
								"Incorrect number of parameters for function %1$s (expected %2$s, got %3$s", this.name,
								this.paramCount, params.length);
				throw new IllegalStateException(error);
			}

		}

		protected boolean isParamCountCorrect(int paramCount) {
			return this.paramCount == paramCount || this.paramCount == -1;
		}
	}
}
