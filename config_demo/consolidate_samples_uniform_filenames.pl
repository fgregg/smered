#!/usr/bin/perl

use List::Util qw[min max];  
 # for min() and max() functions
  # see http://timmurphy.org/2012/02/01/min-and-max-functions-in-perl/
#write to command line to run
#./consolidate_samples.pl linkage_iter 10 5000 l posterior_link 3 20485 17466 19126
### must add +1 to n1, n2, n3 because initilization is at 0 (how rob is currently coding

# take in name-base for all files $base and iteration skip (thinning) size $thin
# maximum iteration number $M, name base for variables $lambda,
# output file name $out, number of rows
# for variables $nrow, number of columns for each variable will be in an array
# @ncol 

  # For the files used on 2013-02-24, $nrow=3, @ncol=(20485,17466,19126)

# process arguments from command line
# Die angrily if the number of arguments is off
if (@ARGV < 7) {
    die("i've, made a stupid error!\nUsage: namebase thin max_iteration variable_name output nrow ncol1 ncol2 ... ncolk \n");
}
# Otherwise, assume they come in the right order
$base = shift(@ARGV); # Remove the first element from the array and return it
$thin = shift(@ARGV);
$M = shift(@ARGV);
$lambda = shift(@ARGV);
$out = shift(@ARGV);
$nrow = shift(@ARGV);
@ncol = ();  # initialize empty array for storing columns per row
while (@ARGV > 0) {
    push(@ncol, shift(@ARGV));
}


# Create a 2D array to store (iterations)x(variables)
@all_iterations = ();  # Initializes to empty array
   # Because Perl handles 2D arrays funnily (1D array of pointers to 1D arrays),
   # special magic will be needed to actually build this array, see below

# Iterate over all files
for ($i=0; $i <= $M; $i+=$thin) {
    $row_index = $i/$thin;  # For refering to row numbers in the matrix of
       # iterations
  # generate file name automatically
    # numbers are padded with leading zeroes so numerical and lexical order
    # match
    # The number of digits to print out is determined by $M, the maximum
    # iteration number
   # Build a formatting string for handling the input files
    $number_digits = length("$M");  # Should interpolate $M into a character
      # string so length will give number of characters
    $format = "%0" . $number_digits . "d";
       # The format says "print out a decimal number, padded with leading
       # zeroes to length $number_digits"
    $padded_i = sprintf($format, $i);
  # form file name by concatenating the parts; assume it ends ".csv"
  $filename = $base . "_" . $padded_i . ".csv";
  # open the current file or die trying
  open(IN,'<',$filename) || die "Could not open $filename for reading: $!\n";
    print "Processing file $filename, hooray!\n";
  # iterate over lines of the file
  while(<IN>) {
      # read in the latest line
      # remove trailing garbage
      chomp;  # chomp acts by default on $_ if no other arg is given
      $the_line = $_;   #$_ is predfined global, $the_line is my variable
      # split the line at commas into an array
      @the_values = split(',',$the_line);
      # remove "-1" values in the line, since they indicate nulls
         # grep will return the values in the array which is its 2nd argument
         # which evaluate to TRUE in its first argument; we're making that
         # first argument the logical "does not match '-1' ", so we should
         # get all the valid entries in @the_values
      @valid_values = grep(!/-1/, @the_values);
      # Even valid values may have trailing ^M characters due to filesystem
      # differences on what counts as a newline; delete those
      for ($j=0; $j < scalar(@valid_values); $j++) {
	  $valid_values[$j] =~ s/\cM//g;
      }
         # Translation: for each valid value, substitute, in place of the
         # control character ^M, the empty character /blah/; do so globally
      # concatenate the purged values to the end of the current vector, which
      # is accessed through the array of array references
        # see first answer at
        # http://stackoverflow.com/questions/317310/how-can-i-create-multidimensional-arrays-in-perl
      # This effectively adds on to row number $row_index
      #second arg gets pushed onto first
      push @{ $all_iterations[$row_index] }, @valid_values;
  }
  # Close file or die
  close(IN) || die "Could not close $filename: $!\n";
}


### TODO: Change the Java to output stuff in a more sensible order, and
### then restore the code for generating headers that follows, marked off
### with "****" in the comments
# ****
# # Generate header names
#   # Need a name base for the header, and to know how many rows and columns there
#   # were in the matrix, so we say things like "lambda1.1" or "z231.42" as
#   # desired
# # Create a vector to store variable names
# @variable_names = ();
#  # Loop over rows of the variable matrix
# for ($r=1; $r <= $nrow; $r++) {
#     # Loop over columns of the variable matrix
#     for ($c=1; $c <= $ncol; $c++) {
#         # Create the corresponding variable name and push it on to the end of
#         # the vector of variables
# 	push(@variable_names, join('.', $lambda, $r, $c));
#     }
# }
# ****

# Generate header names, working around current (2013-02-24) Java code
  # storage order is current: (file 1 column 1, file 2 column 1, file 3
  # column 1), (file 1 column 2, file 2 column 2, file 3 column 2), etc.
  # Need: a name base for the header; to know how many files, or rows of
  # the variable matrix in the model; to know how many columns for each file/row
# Initialize vector of variable names
@variable_names = ();
# Determine the maximum of the column numbers
$nmax = max(@ncol);  # max() is from a package loaded at the head of the script
# iterate over column numbers, up to the max
for ($c=1; $c <= $nmax; $c++) {
  # iterate over row numbers (source files)
    for ($r=1; $r <= $nrow; $r++) {
        # Check if this is a valid row/column combination
        if ($c <= $ncol[$r-1]) {
          # If valid:
          # Create the corresponding variable name and push it on to the end
          # of the vector of variable names
  	  push(@variable_names, join('.', $lambda, $r, $c));
	}
        # Do nothing if the row/column combination is invalid
    }
}


# TODO: Sanity-check that length of @variable_names matches the number of
  # columns of @all_iterations
  # For now, we laugh in the face of user error
# TODO: output a file of variable names in a format friendly for the CODA
  # package in R (needs variable names plus someting else, to be checked)

# Do the output!
# Open the output file or die trying
open(OUT,'>',$out) || die "Couldn't open file $out for writing: $!\n";

# Write out a header line of all variable names, comma-separated
$variable_names = join(',',@variable_names); 
print OUT "$variable_names\n";

# Now iterate over the rows of @all_iterations
  # Rather than trying to figure out from Perl's documentation how to get
  # the size of an array, we'll use the number of rows it should have
$max_row = $M/$thin;
for ($j=0; $j<=$max_row; $j++) {
  # We are on row $j of @all_iterations, so join all its values with
  # commas and print them out 
  # Partly inspired by
    # http://perldoc.perl.org/perllol.html#Slices
  # plus a great deal of wishful thinking
    @the_row = @{ $all_iterations[$j] };
  print OUT join(',',@the_row);
  print OUT "\n";
}
# We're done with this file, so close it
close(OUT) || die "Couldn't close $out: $!\n";
