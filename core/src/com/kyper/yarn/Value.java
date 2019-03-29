package com.kyper.yarn;

import com.kyper.yarn.Dialogue.YarnRuntimeException;

/** values to be used by yarn */
public class Value implements Comparable<Value> {

  public static final Value NULL = new Value();
  private static final String NULL_STRING  = "null";
  private static final String FALSE_STRING = "false";
  private static final String TRUE_STRING  = "true";
  private static final String NAN          = "NaN";
  private Type    type;
  private float   numberValue;
  private String  variableName;
  private String  stringValue;
  private boolean boolValue;

  public Value(){
    this(null);
  }

  public Value(Object value){
    //coppy existing value
    if (value instanceof Value) {
      Value otherValue = (Value) value;
      this.type = otherValue.type;
      switch (type) {
        case NUMBER:
          setNumberValue(otherValue.getNumberValue());
          break;
        case STRING:
          setStringValue(otherValue.getStringValue());
          break;
        case BOOL:
          setBoolValue(otherValue.getBoolValue());
          break;
        case VARNAME:
          setVarName(otherValue.getVarName());
          break;
        case NULL:
          break;
        default:
          throw new IllegalStateException("ArgumentOutOfBounds");
      }
      return;
    }
    if (value == null) {
      type = Type.NULL;
      return;
    }

    if (value instanceof String) {
      type = Type.STRING;
      setStringValue(value.toString());
      return;
    }

    if (value instanceof Integer || value instanceof Float || value instanceof Double) {
      type = Type.NUMBER;
      setNumberValue(Float.parseFloat(value.toString()));
      return;
    }
    if (value instanceof Boolean) {
      type = Type.BOOL;
      setBoolValue((Boolean) value);
      return;
    }

    String error = String.format("Attempted to create a Value using a %s; currently, " + "Values can only be numbers," +
																				 " strings, bools or null.",
                                 value.getClass().getSimpleName());
    throw new YarnRuntimeException(error);
  }

  public Type getType(){
    return type;
  }

  protected void setType(Type type){
    this.type = type;
  }

  public float getNumberValue(){
    return numberValue;
  }

  private void setNumberValue(float numberValue){
    this.numberValue = numberValue;
  }

  public String getVarName(){
    return variableName;
  }

  public void setVarName(String variableName){
    this.variableName = variableName;
  }

  public String getStringValue(){
    return stringValue;
  }

  private void setStringValue(String stringValue){
    this.stringValue = stringValue;
  }

  public boolean getBoolValue(){
    return boolValue;
  }

  private void setBoolValue(boolean boolValue){
    this.boolValue = boolValue;
  }

  public boolean asBool(){
    switch (type) {
      case NUMBER:
        return !Float.isNaN(numberValue) && numberValue != 0f;
      case STRING:
        return stringValue != null && !stringValue.isEmpty();
      case BOOL:
        return boolValue;
      case NULL:
        return false;
      default:
        throw new IllegalStateException("Cannot cast to bool from " + type.name());
    }
  }

  public float asNumber(){
    switch (type) {
      case NUMBER:
        return numberValue;
      case STRING:
        try {
          return Float.parseFloat(stringValue);
        } catch (NumberFormatException e) {
          return 0f;
        }
      case BOOL:
        return boolValue ? 1f : 0f;
      case NULL:
        return 0f;
      default:
        throw new IllegalStateException("Cannot cast to number from " + type.name());
    }
  }

  public String asString(){
    switch (type) {
      case NUMBER:
        if (Float.isNaN(numberValue)) return NAN;
        else Float.toString(numberValue);
      case STRING:
        return stringValue;
      case BOOL:
        return boolValue ? TRUE_STRING : FALSE_STRING;
      case NULL:
        return NULL_STRING;
      default:
        throw new IllegalStateException("Argument out of Range");
    }

  }

  private Object backingValue(){
    switch (type) {
      case NULL:
        return null;
      case STRING:
        return this.stringValue;
      case NUMBER:
        return this.numberValue;
      case BOOL:
        return this.boolValue;
      default:
        break;
    }

    throw new IllegalStateException(String.format("Cant get good backing type for %s", this.type));
  }

  @Override
  public int compareTo(Value o){
    if (o.type == this.type) {
      switch (type) {
        case NULL:
          return 0;
        case STRING:
          return this.stringValue.compareTo(o.getStringValue());
        case NUMBER:
          return Float.valueOf(numberValue).compareTo(o.getNumberValue());
        case BOOL:
          return Boolean.valueOf(boolValue).compareTo(o.getBoolValue());
        default:
          break;
      }
    }
    //try to compare as strings if all else fails
    return this.asString().compareTo(o.asString());
  }

  @Override
  public boolean equals(Object obj){
    if (obj == null || !(obj instanceof Value)) return false;
    Value other = (Value) obj;

    switch (type) {
      case NUMBER:
        return this.asNumber() == other.asNumber();
      case STRING:
        return this.asString().equals(other.asString());
      case BOOL:
        return this.asBool() == other.asBool();
      case NULL:
        return other.type == Type.NULL || other.asNumber() == 0 || other.asBool() == false;
      default:
        throw new IllegalStateException("ArgumentOutOfRange");
    }
  }

  @Override
  public int hashCode(){
    Object backing = this.backingValue();

    //TODO: broken?
    if (backing != null) {
      return backing.hashCode();
    }

    return 0;
  }

  @Override
  public String toString(){
    return String.format("[Value: type=%1$s, AsNumber=%2$s, AsBool=%3$s, AsString=%4$s]",
                         type,
                         asNumber(),
                         asBool(),
                         asString());
  }

  /**
   * add this value and the parameter
   *
   * @param o
   *
   * @return
   */
  public Value add(Value o){
    Value a = this, b = o;

    // catches:
    // undefined + string
    // number + string
    // string + string
    // bool + string
    // null + string
    if (a.type == Type.STRING || b.type == Type.STRING) {
      // we're headed for string town!
      return new Value(a.asString() + b.asString());
    }

    // catches:
    // number + number
    // bool (=> 0 or 1) + number
    // null (=> 0) + number
    // bool (=> 0 or 1) + bool (=> 0 or 1)
    // null (=> 0) + null (=> 0)
    if ((a.type == Type.NUMBER || b.type == Type.NUMBER) || (a.type == Type.BOOL && b.type == Type.BOOL) || (a.type == Type.NULL && b.type == Type.NULL)) {
      return new Value(a.asNumber() + b.asNumber());
    }

    throw new IllegalArgumentException(String.format("Cannot add types %s and %s.", a.type, b.type));
  }

  /**
   * subtract paramert to this value
   *
   * @param o
   *
   * @return
   */
  public Value sub(Value o){
    Value a = this, b = o;
    if (a.type == Type.NUMBER && (b.type == Type.NUMBER || b.type == Type.NULL) || b.type == Type.NUMBER && (a.type == Type.NUMBER || a.type == Type.NULL)) {
      return new Value(a.asNumber() - b.asNumber());
    }

    throw new IllegalArgumentException(String.format("Cannot subtract types %s and %s.", a.type, b.type));
  }

  /**
   * multiply parameter with this value
   *
   * @param o
   *
   * @return
   */
  public Value mul(Value o){
    Value a = this, b = o;
    if (a.type == Type.NUMBER && (b.type == Type.NUMBER || b.type == Type.NULL) || b.type == Type.NUMBER && (a.type == Type.NUMBER || a.type == Type.NULL)) {
      return new Value(a.asNumber() * b.asNumber());
    }

    throw new IllegalArgumentException(String.format("Cannot multiply types %s and %s.", a.type, b.type));
  }

  /**
   * divide parameter with this value
   *
   * @param o
   *
   * @return
   */
  public Value div(Value o){
    Value a = this, b = o;
    if (a.type == Type.NUMBER && (b.type == Type.NUMBER || b.type == Type.NULL) || b.type == Type.NUMBER && (a.type == Type.NUMBER || a.type == Type.NULL)) {
      return new Value(a.asNumber() / b.asNumber());
    }

    throw new IllegalArgumentException(String.format("Cannot divide types %s and %s.", a.type, b.type));
  }

  /**
   * modulo
   *
   * @param o
   *
   * @return
   */
  public Value mod(Value o){
    Value a = this, b = o;
    if (a.type == Type.NUMBER && (b.type == Type.NUMBER || b.type == Type.NULL) || b.type == Type.NUMBER && (a.type == Type.NUMBER || a.type == Type.NULL)) {
      return new Value(a.asNumber() % b.asNumber());
    }
    throw new IllegalArgumentException(String.format("Cannot modulo types %s and %s.", a.type, b.type));
  }

  /**
   * get negative of this value
   *
   * @return
   */
  public Value negative(){
    Value a = this;
    if (a.type == Type.NUMBER) {
      return new Value(-a.getNumberValue());
    }
    if (a.type == Type.NULL && a.type == Type.STRING && (a.asString() == null || a.asString().trim() == "")) {
      return new Value(-0);
    }
    return new Value(Float.NaN);
  }

  /**
   * check if this is greater than parameter
   *
   * @param o
   *
   * @return
   */
  public boolean greaterThan(Value o){
    Value operand1 = this, operand2 = o;
    return operand1.compareTo(operand2) == 1;
  }

  /**
   * check if this is less than parameter
   *
   * @param o
   *
   * @return
   */
  public boolean lessThan(Value o){
    Value operand1 = this, operand2 = o;
    return operand1.compareTo(operand2) == -1;
  }

  /**
   * check if this is greater than or = to param
   *
   * @param o
   *
   * @return
   */
  public boolean greaterThanOrEqual(Value o){
    Value operand1 = this, operand2 = o;
    return operand1.compareTo(operand2) >= 0;
  }

  /**
   * check if this is less than or equal to param
   *
   * @param o
   *
   * @return
   */
  public boolean lessThanOrEqual(Value o){
    Value operand1 = this, operand2 = o;
    return operand1.compareTo(operand2) <= 0;
  }

  public enum Type {
    NUMBER, // a constant number
    STRING, // a string
    BOOL, // a boolean value
    VARNAME, // the name of a variable; will be expanded at runtime
    NULL, // the null value
  }

}
