//special version of Individual.java for handling dups (but it should work in either case)
//updated on 7.16.13

// TODO (long-term): The Vector class is deprecated, replace with ArrayList
// or similar (HashMap? HashSet?) as appropriate - consult authorities

import java.util.*;

// An individual is just a cluster of records from different files.
public class Individual {
  // The fields of this individual (using a record object for storage).
  protected Record                fields_;
  // The linked records.
  protected Vector<LinkedRecord>  records_;
    // DONE: position in the ArrayList of individuals should be obtained,
    // when needed, by using an ArrayList method, so index_ should go away
  // The position in the big vector of individuals.
    // protected int                   index_;
    // DONE: Previous and next individuals in the ArrayList should be
    // obtained with ArrayList methods, when needed, so prev_ and next_
    // should go away
  // position in a big linked list of individuals
    // protected Individual            prev_;
    // protected Individual            next_;

  protected int                   cluster_index_;

    // DONE: Constructor should not take index
  public Individual(Record fields) {
    records_ = new Vector<LinkedRecord>();
    fields_ = fields;
    cluster_index_ = 0;
  }

    // DONE: Constructor should not take index
  public Individual(Record fields, int cluster_index) {
    records_ = new Vector<LinkedRecord>();
    fields_ = fields;
    cluster_index_ = cluster_index;
  }

 // FLAG: Confusion Matrix Calculation
    // Constructor that builds an individual from a set of
    // LinkedRecords --- note that it has no previous or next Individual
    // set, so this will not alter the main linked list of Individuals
    // (though we could set a previous and next if we wanted to add
    // this Individual into the linked list)
    public Individual(Set<LinkedRecord> some_records) {
	records_ = new Vector<LinkedRecord>();
	for (LinkedRecord lr : some_records) {
	    records_.add(lr);
	}
	//	index_ = 0;
	//fields_ = some_records[1].getRecord();
	// ATTN: Pick some arbitrary member of the set some_records and
	// use its fields
	cluster_index_ = 0;
    }

  public int getClusterIndex() {
    return cluster_index_;
  }

  public void setClusterIndex(int i) {
    cluster_index_ = i;
  }

    // public Individual getNext() {
    // return next_;
    // }
  
    //  public void setNext(Individual next) {
    // next_ = next;
    // }

  // public Individual getPrev() {
  //   return prev_;
  // }
  
  // public void setPrev(Individual prev) {
  //   prev_ = prev;
  // }

  // public void setIndex(int ind){
  //   index_ = ind;
  // }

  // public int getIndex() {
  //   return index_;
  // }
  
  public void addRecord(LinkedRecord lr) {
      //System.out.printf("%s %d%n", this.toString(), numRecords()); // ATTN
    records_.add(lr);
  }
  
  public Record getIndividualRecord() {
    return fields_;
  }

  public void setIndividualRecord(Record fields) {
    fields_ = fields;
  }

  public Vector<LinkedRecord> getLinkedRecords() {
    return records_;
  }

  public int numRecords() {
    return records_.size();
  }

  public Vector<Record> getRecords() {
    Vector<Record> recs = new Vector<Record>();
    //System.out.printf("%s %d%n", this.toString(), numRecords()); // ATTN
    for(LinkedRecord lr : records_) {
      recs.add(lr.getRecord());
    }
    return recs;
  }

    // Calculate the linkage pattern of an individual
	// Linkage patterns are binary vectors (stored as ints), showing 0 if an individual
	// isn't linked to any records in a file, 1 if there are linked records
	// Modifications made to handle possibility of duplicates
	// Java compiler complaining about "unsafe or unchecked" operations (but not throwing
	// an error), possibly because of the files_seen.add() method
    //do increment if haven't encounter record from this file?
  public int getPattern() {
	  int pattern = 0;
	  //files_seen is an ArrayList initialized to capacity of size 10
	  ArrayList<Integer> files_seen = new ArrayList<Integer>();
	  boolean added_file;  // place-holder for return value of ArrayList's add() method
		// wouldn't be needed if that would just be of type void
	  int current_file = 0;
	  for(LinkedRecord lr : records_) {
		  current_file = lr.getFile();
		  //if files_seen.contains(current_file) is FALSE, then we update
		  //the pattern and count of file (this is true the first time)
		if (!files_seen.contains(current_file)) {
			//update pattern count, update the NUMBER of files_seen
			pattern += 1 << current_file;
			//java complaining with error that we aren't doing anything with files_seen.add
			//so stick it in the placeholder variable
			//added_file = 
			files_seen.add(current_file);
		}
    }
    return pattern;
  } 

	// Tests whether current individual can be merged with another individual
	// If we are presuming that all duplicates are removed from within files, the right test is
	// "do the individuals' linkage patterns not overlap at all?"
	// If the files are not already de-duped, then all individuals are mergable
  public boolean isMergeable(Individual ind, boolean allDeDup) {
	  if (allDeDup) {
		  return ((getPattern() & ind.getPattern()) == 0);
	  } else {
		  return(true);
	  }
  }

}
