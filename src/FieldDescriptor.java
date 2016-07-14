import java.util.HashMap;
import java.util.Vector;

public class FieldDescriptor {
  // Name of the field.
  protected String                  name_;
  // Mapping of observed field values to integer indices.
  protected HashMap<String,Integer> val_idx_map_;
  // Reverse mapping.
  protected Vector<String>          idx_val_map_;         

  public FieldDescriptor(String name) {
    name_ = name;
    val_idx_map_ = new HashMap<String,Integer>();
    idx_val_map_ = new Vector<String>();
  }

  public Field createField(String val) {
    int ind = -1;
    if(val_idx_map_.containsKey(val)) {
      ind = val_idx_map_.get(val);
    } else {
      ind = idx_val_map_.size();
      idx_val_map_.add(val);
      val_idx_map_.put(val, ind);
    }
    return new Field(this, ind);
  }
  
  public String getName() {
    return name_;
  }

  public String getValueString(int val) {
    return idx_val_map_.elementAt(val);
  }

  public int numValues() {
    return idx_val_map_.size();
  }
}
