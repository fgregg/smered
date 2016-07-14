import java.util.HashMap;
import java.util.Vector;

public class RecordBlocking {

  Vector<Integer> blocking_idx_;
  Vector<FieldDescriptor> schema_;
  public RecordBlocking(Vector<FieldDescriptor> schema,
                        String[] blocking_fields) {
    schema_ = schema;
    blocking_idx_ = new Vector<Integer>();
    for(int j = 0; j < blocking_fields.length; j++) {
      boolean found = false;
      for(int i = 0; i < schema.size(); i++) {
        if(blocking_fields[j].equals(schema.elementAt(i).getName())) {
          blocking_idx_.add(i);
          found = true;
          break;
        }
      }
      if(!found) {
        System.err.println("Unable to block with non-existant field " +
          blocking_fields[j]);
      }
    }
  }

  public Vector<Vector<int[]>> constructBlocking(
    Vector<Vector<Record>> records) {
    // just block by sex, birthyear.
    HashMap<String,Vector<int[]>> blocking = 
      new HashMap<String,Vector<int[]>>();
    // iterate over files and block
    for(int i = 0; i < records.size(); i++) {
      Vector<Record> file = records.elementAt(i);
      for(int j = 0; j < file.size(); j++) {
        Record rec = file.elementAt(j);
        // construct blocking code.
        String code = "";
        for(int f : blocking_idx_) {
          code = code + rec.getField(f).getValueString() + "___";
        }
        if(blocking.containsKey(code)) {
          blocking.get(code).add(new int[]{i, j});
        } else {
          Vector<int[]> block = new Vector<int[]>();
          block.add(new int[]{i, j});
          blocking.put(code, block);
        } 
      }
    }
    // extract blocking to vectors.
    int minsz = 1000000;
    int maxsz = 0;
    Vector<Vector<int[]>> blockingvec = new Vector<Vector<int[]>>();
    for(Vector<int[]> block : blocking.values()) {
      blockingvec.add(block);
      minsz = Math.min(minsz, block.size());
      maxsz = Math.max(maxsz, block.size());
    } 
    // CHANGING printing of success message from STDERR to STDOUT
    // Done 2013-03-12
    System.out.println("Divided into " + blockingvec.size() + " blocks");
    System.out.println("Max size: " + maxsz);
    System.out.println("Min size: " + minsz);
    return blockingvec;
  }
}
