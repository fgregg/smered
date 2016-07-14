// Remake of the MCMC_State using a big tree of java objects instead
// of a lot of matrices of various indices.

//edited on 7.27.13 to add compareTo function, needed for matches 
//function in MHSampler_master version.
import java.util.*;
import java.lang.Integer;

class LinkedRecord implements Comparable<LinkedRecord> {
    // The individual.
    protected Individual individual_;
    // The record.
    protected Record     record_;
    // Distortion pattern.
    protected boolean[]  distortion_;
    // What file the record is from.
    protected int        file_;
    // What position the record is in the file.
    protected int        index_;

    public LinkedRecord(Individual ind, Record rec, int file, int index) {
	individual_ = ind;
	record_ = rec;
	file_ = file;
	index_ = index;
	distortion_ = new boolean[record_.numFields()];
    }

    public void setDistortion(int field, boolean distortion) {
	distortion_[field] = distortion;
    }
  
    public boolean getDistortion(int field) {
	return distortion_[field];
    }

    public void setDistortion(boolean[] distortion) {
	distortion_ = distortion;
    }
  
    public boolean[] getDistortion() {
	return distortion_;
    }
  
    public Individual getIndividual() {
	return individual_;
    }
  
    public void setIndividual(Individual ind) {
	individual_ = ind;
    }

    public Record getRecord() {
	return record_;
    }

    public int getFile() {
	return file_;
    }

    public int getIndex() {
	return index_;
    }

    public int compareTo(LinkedRecord lr) {
	//want to compare two linked records which have assoc files
	//then compare the two linked records (file is like last name
	//index is like first name
	//see the following example 
	//http://docs.oracle.com/javase/tutorial/collections/interfaces/order.html
	//However, had to convert int to new Integer, see example at
	//http://www.java-examples.com/convert-java-int-integer-object-example
	Integer thisFile = new Integer(file_);
	Integer otherFile = new Integer(lr.getFile());
	Integer thisIndex = new Integer(index_);
	Integer otherIndex = new Integer(lr.getIndex());
	int fileCmp = thisFile.compareTo(otherFile);
	return (fileCmp != 0 ? fileCmp : thisIndex.compareTo(otherIndex));
    }

}

