import java.util.Vector;

public class Record {
  protected Field[] fields_;
  
  public Record(Field[] fields) {
    // copy the array
    fields_ = new Field[fields.length];
    for(int i = 0; i < fields.length; i++) {
      fields_[i] = new Field(fields[i]);
    }
  }

  public Record(Record r) {
    // copy the array
    fields_ = new Field[r.fields_.length];
    for(int i = 0; i < r.fields_.length; i++) {
      fields_[i] = new Field(r.fields_[i]);
    }
  }

  public int numFields() {
    return fields_.length;
  }

  public Field getField(int i) {
    return fields_[i];
  }

  public String toString() {
    String s = fields_[0].toString();
    for(int i = 1; i < fields_.length; i++) {
      s += ", " + fields_[i].toString();
    }
    return s;
  }
  
}
