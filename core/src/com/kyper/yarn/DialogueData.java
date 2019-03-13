package com.kyper.yarn;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.kyper.yarn.Dialogue.VariableStorage;
import com.kyper.yarn.Value.Type;

/**
 * A data table that stores Values
 *
 */
public class DialogueData implements VariableStorage {

	private static final String NAME = "$USERDATA_NAME";

	private ObjectMap<String, Value> variables;
	private static Json MYFRIEND;

	private String name;

	public DialogueData(String name) {
		this.name = name;
		variables = new ObjectMap<String, Value>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void put(String name, Object object) {
		setValue(name, new Value(object));
	}
	
	public boolean contains(String name) {
		if(variables.containsKey(name))
			return true;
		return false;
	}
	
	public Value remove(String name) {
		if(contains(name)) {
			return variables.remove(name);
		}
		else return null;
	}

	public String getString(String name) {
		if (variables.containsKey(name)) {
			Value val = variables.get(name);
			return val.getType() == Type.STRING?val.getStringValue():val.asString();
		}
		return Value.NULL.asString();
	}

	public float getFloat(String name) {
		if(variables.containsKey(name)) {
			Value val = variables.get(name);
			return val.getType() == Type.NUMBER?val.getNumberValue():val.asNumber();
		}
		return Value.NULL.asNumber();
	}

	public int getInt(String name) {
		return (int) getFloat(name);
	}

	public boolean getBoolean(String name) {
		if (variables.containsKey(name)) {
			Value val = variables.get(name);
			return val.getType() == Type.BOOL?val.getBoolValue():val.asBool();
		}
		return Value.NULL.asBool();
	}

	public void setFloat(String name, float value) {
		put(name, value);
	}

	public void setInt(String name, int value) {
		put(name, value);
	}

	public void setBoolean(String name, boolean value) {
		put(name, value);
	}

	public void setString(String name, String value) {
		put(name, value);
	}

	@SuppressWarnings("unchecked")
	public boolean loadFromJson(String json) {
		if (MYFRIEND == null)
			MYFRIEND = new Json();
		ObjectMap<String, Value> vv = MYFRIEND.fromJson(ObjectMap.class, json);
		if (vv != null)
			setName(vv.remove(NAME).asString());
		variables = vv != null ? vv : variables;
		return vv != null;

	}

	public String toJson() {
		put(NAME, getName());
		if (MYFRIEND == null)
			MYFRIEND = new Json();
		return MYFRIEND.toJson(variables);
	}

	public void clear() {
		variables.clear();
	}

	@Override
	public void setValue(String name, Value value) {
		variables.put(name, value);
	}

	@Override
	public Value getValue(String name) {
		Value value = Value.NULL;
		if (variables.containsKey(name)) {
		   value =  variables.get(name);
		}
		return value;
	}
}
