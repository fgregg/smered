import java.util.*;
import cc.mallet.types.Dirichlet;
import cc.mallet.util.Randoms;
import cc.mallet.util.Maths;

// model the P(x) as a bunch of draws from various multinomials (one per field)
// model the distortion P(y|x) as y = z, with some probability, else y is uniform,
// done on a per-field basis.
//TODO: Rob: what is going on here. The above comment doens't match the manuscript and where is the Beta draw below coming from (where is the conditional coming from -- this is not in the paper). 

public class IndependentFieldsModel {

  // probability model P(y).
  // This is what the manuscript calls \theta, i.e., multinomial distribution
  // for each field
  protected double[][]  p_y_;
  // distortion model P(x|y).
  // This is what the manuscript calls \beta, i.e., probability that a given
  // field in a given record is distorted
  protected double[]    p_xy_;

  protected Random rand_;

  protected int index_;

  protected Vector<FieldDescriptor> schema_;

  public IndependentFieldsModel(Vector<FieldDescriptor> schema, int index) {
    schema_ = schema;
    rand_ = new Random(System.currentTimeMillis());
    p_y_ = new double[schema.size()][];
    p_xy_ = new double[schema.size()];
    index_ = index;
  }

  public IndependentFieldsModel(Vector<FieldDescriptor> schema) {
    schema_ = schema;
    rand_ = new Random(System.currentTimeMillis());
    p_y_ = new double[schema.size()][];
    p_xy_ = new double[schema.size()];
    index_ = 0;
  }
	
	//made a change as log jointPosterior isn't correct

	// really log jointPosterior (eqn 1)
	//first sum is trying to do second line of (eqn 1)
	//p_y_ stores the theta's 
	//ind.getIndividualRecord() are the y's
    //we ignore NA's in the dataset if we find that they are present
    public double logLikelihood(Individual ind) {
	double p = 0.0;
	//index i here is really over all fields (l)
	for(int i = 0; i < schema_.size(); i++) {
	    int fieldValue = ind.getIndividualRecord().getField(i).getValue();
	    if (fieldValue != -6) {
		p += Math.log(p_y_[i][ind.getIndividualRecord().getField(i).getValue()]);
	    } 
	}
	//first and third line (eqn 1)
	//pxy is beta 
	//x could not be equal to y and then dirac delta should be 0.
	for(LinkedRecord lr : ind.getLinkedRecords()) {
	    for(int i = 0; i < schema_.size(); i++) {
		if(lr.getDistortion(i)) {
		    int fieldValue = lr.getRecord().getField(i).getValue();
		    //-6 represents a NA
		    if (fieldValue != -6) {
			p += Math.log(p_xy_[i]);
			p += Math.log(p_y_[i][lr.getRecord().getField(i).getValue()]);
		    }
		    //if it's not distorted you can't have an NA however the latent could be NA and merged with a non-NA
		    //also, NA's could be distorted
		} else {
		    if(lr.getRecord().getField(i).getValue() != ind.getIndividualRecord().getField(i).getValue()) {p += Math.log(0);}
		    p +=Math.log(1.0-p_xy_[i]);
		}
	    }
	}
	return p;
    }

  public void sampleDistortion(Record indiv, LinkedRecord rec) {
    for(int f = 0; f < schema_.size(); f++) {
      int rf = rec.getRecord().getField(f).getValue();
      if(indiv.getField(f).getValue() != rf) {
        rec.setDistortion(f,true);
      } else {
        double num = p_xy_[f] * p_y_[f][rf];
        num = num / (num + 1.0 - p_xy_[f]);
        rec.setDistortion(f, rand_.nextDouble() < num);
      }
    }
  }

    // DONE: Modify so that this takes an ArrayList of Individuals, not
    // a single Individual
    // DONE: Changed ArrayList to LinkedList
    // TODO: Change LinkedList to HashSet
  public void sampleParameters(HashSet<Individual> allIndividuals) {
    // Initialize count vectors.
    // y_counts stores the sum of counts for both the true y values,
    // and the x values when the latter are distorted
    double[][] y_counts = new double[schema_.size()][];
    for(int i = 0; i < schema_.size(); i++) {
      y_counts[i] = new double[schema_.elementAt(i).numValues()];
    }
    // xy_counts stores the number of distorted records for each field
    double[] xy_counts = new double[schema_.size()];
    // total_xy stores the total number of records
    // TODO: does this need to be recomputed over and over?
    double total_xy = 0;
    Iterator<Individual> individualsIterator = allIndividuals.iterator();
    while(individualsIterator.hasNext()) {
      Individual ind = individualsIterator.next();
      if(ind.getClusterIndex() == index_ ) {
        Record r = ind.getIndividualRecord();
        for(int f = 0; f < schema_.size(); f++) {
          y_counts[f][r.getField(f).getValue()] += 1.0;
        }
        for(LinkedRecord lr : ind.getLinkedRecords()) {
          total_xy += 1.0;
          for(int f = 0; f < schema_.size(); f++) {
            if(lr.getDistortion(f)) {
              y_counts[f][lr.getRecord().getField(f).getValue()] += 1.0;
              xy_counts[f] += 1.0;
            }
          }
        }
      }
    }
    // Distortion model parameters.
    //Initial a1, a2 (Rob) Values below (0.01, 1.0) give a highly flat prior with a spike at 0!
    double a1 = 5;
    double a2 = 500;
    // Draw parameters from respective posteriors.
    Randoms rand = new Randoms((int)System.currentTimeMillis());
    for(int f = 0; f < schema_.size(); f++) {
	// Update multinomial distribution (\theta) for each field
	// Apparently takes Dirichlet prior parameter (\mu_{lm}) to be
	// 1.0; why?
	p_y_[f] = sampleDirichletPosterior(y_counts[f], 1.0);
	// Update distortion probability (\beta) for each field
	p_xy_[f] = rand.nextBeta(xy_counts[f]+a1, total_xy-xy_counts[f]+a2);
	// CHANGING printing of amount of distorting from STDERR to
	// STDOUT so that it can be logged and analyzed more easily
	// Prints out _number_ of distorted records for each field, not
	// fraction
	System.out.println("distort " + schema_.elementAt(f).getName() + ": " + xy_counts[f]);
    }
  }

  // sample from a dirichlet posterior with symmetric alphas.
  public double[] sampleDirichletPosterior(double[] counts, double alpha) {
    double[] param = new double[counts.length];
    for(int i = 0; i < param.length; i++) {
      param[i] = counts[i] + alpha;
    }
    Dirichlet dir = new Dirichlet(param);
    return dir.nextDistribution();
  }

  public double[] individualParameters(double[] theta, double p_flip,
                                       Vector<Integer> obs) {
    double[] param = new double[theta.length];
    double den = 0.0;
    for(int i = 0; i < theta.length; i++) {
      param[i] = theta[i];
    }
    for(int ob : obs) {
      for(int i = 0; i < param.length; i++) {
        if(ob == i) {
          param[i] *= (p_flip*theta[i] + (1.0-p_flip));
        } else {
          param[i] *= p_flip*theta[i];
        }
      }
    }
    for(int i = 0; i < param.length; i++) {
      den = den + param[i];
    }
    for(int i = 0; i < param.length; i++) {
      param[i] /= den;
    }
    return param;
  }

  public int sampleMultinomial(double[] param) {
    double r = rand_.nextDouble();
    for(int i = 0; i < param.length; i++) {
      r -= param[i];
      if(r <= 0.0) {
        return i;
      }
    }
    return -1;
  }

  public void setDistortionParam(int index, double param) {
    p_xy_[index] = param;
  }

  public double lChoose(int n, int p) {
    return Maths.logFactorial(n) - Maths.logFactorial(n-p) - Maths.logFactorial(p);
  }

}
