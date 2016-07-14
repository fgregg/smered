import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

public class RecordLoader {
  // For each file, a vector of records.
  protected Vector<Vector<Record>> record_;
  // The corresponding keys, if available in the file.
  protected Vector<Vector<String>> key_;   
  // Whether or not the key is present in the file.
  protected boolean has_key_;
  // The field descriptors
  protected Vector<FieldDescriptor> field_desc_;
  // Total number of records loaded.
  protected int total_records_;

  public RecordLoader(String schema_fp, String[] files) {
    loadSchema(schema_fp);
    record_ = new Vector<Vector<Record>>();
    key_ = new Vector<Vector<String>>();
    total_records_ = 0;
    for(String fp : files) {
      loadFile(fp);
    }
  }

  public int fieldIndex(String name) {
    for(int i = 0; i < field_desc_.size(); i++) {
      if(field_desc_.elementAt(i).getName().equals(name)) {
        return i;
      }
    }
    System.err.println("no field named " + name);
    return -1;
  }

  protected void loadSchema(String schema_fp) {
    field_desc_ = new Vector<FieldDescriptor>();
    String line = null;
    int linenum = 0;
    try {
      BufferedReader rdr = new BufferedReader(new FileReader(schema_fp));
      while((line=rdr.readLine()) != null) {
        linenum++;
        String[] part = line.split("\\s+");
        if(part.length != 2) {
          System.err.println("Error on schema line " + linenum);
          System.err.println(line);
          System.exit(0);
        }
        if(part[1].equalsIgnoreCase("KEY")) {
          if(linenum == 1) {
            has_key_ = true;
          } else {
            throw new Exception("Key must be the first field, if present");
          }
        } else if(part[1].equalsIgnoreCase("VAR")) {
          field_desc_.add(new FieldDescriptor(part[0]));
        } else {
          throw new Exception("Unsupported type: " + part[1] +
            " on line " + linenum);
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  protected void loadFile(String fp) {
    Vector<String> keys = new Vector<String>();
    Vector<Record> recs = new Vector<Record>();
    String line = null;
    int linenum = 0;
    int num_parts = field_desc_.size() + (has_key_ ? 1 : 0);
    try {
      BufferedReader rdr = new BufferedReader(new FileReader(fp));
      while((line=rdr.readLine()) != null) {
        linenum++;
        String[] parts = line.split("\\s+");
        if(parts.length != num_parts) {
          System.err.println("Skipping bad input line " + linenum + 
            " of " + fp);
          System.err.println(line);
          continue;
        }
        if(has_key_) {
          keys.add(parts[0]);
        }
        Field[] fields = new Field[field_desc_.size()];
        for(int i = 0; i < fields.length; i++) {
          fields[i] = field_desc_.elementAt(i).createField(
            parts[i + (has_key_ ? 1 : 0)]);
        }
        recs.add(new Record(fields));
      }
    } catch(Exception e) {
      System.err.println("Error on line " + linenum + " of " + fp);
      System.err.println(line);
      e.printStackTrace();
    }
    record_.add(recs);
    key_.add(keys);
    // CHANGING printing of success message from STDERR to STDOUT
    // Done: 2013-03-13
    System.out.println("loaded " + recs.size() + " from file " + fp);
    total_records_ += recs.size();
  }

  public Vector<Vector<Record>> getRecords() {
    return record_;
  }

  public Vector<Vector<String>> getKeys() {
    return key_;
  }

  public Vector<FieldDescriptor> getSchema() {
    return field_desc_;
  }

  public int numFiles() {
    return record_.size();
  }
  
  public int numRecords() {
    return total_records_;
  }
  
  // test case.
  public static void main(String[] argv) {
    String[] files = {"data/nltcs_82.txt",
                      "data/nltcs_89.txt",
                      "data/nltcs_94.txt"};
    String schema_fp = "data/nltcs_schema.txt";
    RecordLoader rl = new RecordLoader(schema_fp, files);
    Vector<Vector<Record>> recs = rl.getRecords();
    System.out.println(recs.elementAt(0).elementAt(0));
  }
}


