---
output: html_document
---

SMERED:  Split and MErge REcord linkage and De-duplication toolbox for Java

Website: https://bitbucket.org/resteorts/smered/

Author:  Rebecca C. Steorts (http://www2.stat.duke.edu/~rcs46/, beka AT stat dot duke dot edu)

This toolbox provides code for running the SMERED algorithm for record linkage as illustrated in the 
papers below. 

This software is released under the BSD 3-clause license. Please see the LICENSE file for details.

*Update 2016:* This repo is not under active development, but I'm still happy to handle support requests.

## Repo Contents

The repository has the following primary content:


* README/

contains one example dataset (publically available data scraped from the NLTCS and used in both our AISTATS and JASA paper)
See the README for how to run posterior inference on this data. 

      
## Academic Citation

If you find this toolbox useful, please cite one of our papers:


#### AISTATS 2014 conference paper

> This shorter conference paper 
 presents a coherent reference for understanding the SMERED algorithm 
as well as split-merge MCMC version 
for record linkage applied to real data as well as simulated data. 
 
* "SMERED: A Bayesian Approach to Graphical Record Linkage and De-duplication."
Rebecca C. Steorts, Rob Hall, Stephen Fienberg, JMLR W&CP, 29(1), 922-930.
[[paper]](http://arxiv.org/pdf/1403.0211v1.pdf)


#### JASA 2016 journal paper

> Our JASA, Theory and Methods 2016 paper extended the conference paper, where we demonatrated our method's effectiveness on a second application, beating this method both in speed and accuracy. Comparisons are made by errors rates, confusion matrices, posterior matching sets and linkage probabilities, etc. Futhermore, we make connections to other literature. Specially, we prove that such methods as (eg. Sadinle (2014, 2016), Tancredi and Liseo (2011), among others) which use as data structure known as the coference matrix are, in fact, as special case of our linkage structure. Finally, we illustrate that a uniform prior on such data structures is highly informative and should not be used in practice. 

* "A Bayesian Approach to Graphical
Record Linkage and De-duplication". 
Rebecca C. Steorts, Rob Hall, Stephen E. Fienberg. (2016), Journal of the American Statistical Association, forthcoming, [[paper]](http://arxiv.org/pdf/1312.4645v4.pdff)



## Acknowledgements

This code is was originally written by both Rob Hall and Rebecca Steorts. Most functions have been rewritten for speed, readability, and extensibility, but Rob deserves much credit for the very first version of the algorithm SMERED. 



I also thank 

* Josh Tokle for help with creating the toolbox and helping to create to its improve imrovements. 
* Brunero Liseo, Andrea Tancredi, Willie Neiswanger, Matt Barnes for providing feedback regarding earlier versions of the routines in this package.