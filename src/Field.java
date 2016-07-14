

public class Field {
  // Descriptor of this field.
  protected FieldDescriptor desc_;
  // The field value.
  protected int val_;
  
  public Field(FieldDescriptor desc, int val) {
    desc_ = desc;
    val_ = val;
  }

  public Field(Field f) {
    desc_ = f.desc_;
    val_ = f.val_;
  }

  public String toString() {
    return desc_.getName() + ": " + desc_.getValueString(val_);
  }

  public FieldDescriptor getFieldDescriptor() {
    return desc_;
  } 

  public int getValue() {
    return val_;
  }

  public void setValue(int val) {
    val_ = val;
  }

  public String getValueString() {
    return desc_.getValueString(val_);
  }
}
