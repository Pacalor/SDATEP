# SDATEP
Smartband Drift Analysis Thought Emerging Pattern 
AI algotrithm to Analisys data from Smartbands, efficient and lightweight enought to be used inside a android application



# This proyect is a fork from [FEPDS: A Proposal for the Extraction of Fuzzy Emerging Patterns in Data Streams](https://github.com/SIMIDAT/FEPDS)

In this repository the source code of the FEPDS algorithm is presented. Please cite this work as:

*FEPDS: A Proposal for the Extraction of Fuzzy Emerging Patterns in Data Streams, García-Vico, A.M., Carmona C. J., González P., Seker H., and del Jesus M. J. , IEEE Transactions on Fuzzy Systems, p.1-12, (In Press)*

## Compile

Maven is employed in this project for a better management of dependencies. Assuming maven is already installed on your system, for compiling the program just execute the following commands in a terminal:

```
mvn clean
mvn package
```
The final generated .jar file is under the /target directory.

## Data format

Data must follow the ARFF dataset format (https://www.cs.waikato.ac.nz/~ml/weka/arff.html).

## Parameters

The algorithm uses several parameters for its execution. All of them must be provided by means of a parameters file. An example of a fully functional parameters file is provided in this repository (param.txt). In addition, an example of a parameters file and brief details of the parameters are presented below:

```
# THIS IS A PARAMETER FILE FOR THE STREAM-MOEA ALGORITHM
#This a comment. Comments starts with a '#' sign.
algorithm = stream-moea

# The input data, it must be a stream stored in an arff file.
inputData = taxis.arff

# The paths of the results files, separated by whitespaces, in this order: training QMs, test QMs for each rule, test QMs summary and rules.
outputData = taxis_tra_qua.txt    taxis_tst_qua.txt     taxis_quaSumm.txt     taxis_rules.txt

# The number of collected instances before starting the genetic algorithm.
period = 2500

# Parameters of the genetic algorithm
seed = 1
RulesRepresentation = dnf
nLabels = 7
nGen = 70
popLength = 50
crossProb = 0.8
mutProb = 0.1


# Use this to set the evaluator: "byObjectives" uses presence and objective values in previous timestamps. Other value use only presence in previous steps.
# Use: "byDiversity" to apply the evaluator based in the application of the decay factor on the diversity measure
Evaluator = byDiversity

# The size of the sliding window to be used by the evaluator.
SlidingWindowSize = 5

# The objectives to be used in the genetic algorithm.
# They must match with the name of the class that represent the quality measure
Obj1 = WRAccNorm
Obj2 = SuppDiff
Obj3 = NULL

# The diversity measure to be used. Useful for process like Token Competition.
diversity = WRAccNorm

# The different filter applied at the end of the evolutionary process. It will be applied in 
# order, i.e., the first one is applied over the result, then the second is applied over the result extra-dnf-7-2500cted from the first one and so on.
filter = TokenCompetition
```

## Execution

The JVM must be installed on your system. For running the algorithm, two arguments must be passed to the algorithm:

1. The path of the parameters file.
2. A number indicating the column of the class variable. A value of -1 means the last column.

For running the algorithm using the parameters file *params.txt* with the class variable as the last one, just type the following command:

```
java -jar FEPDS-1.0.jar params.txt -1
```

## Study

To execute the experimental carried out in the paper, please check the release and download it. Then, decompress the study.tar.xz file and execute the _studyReplication.sh_ script.

