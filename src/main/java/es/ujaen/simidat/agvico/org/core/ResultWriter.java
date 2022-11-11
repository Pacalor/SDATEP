/* 
 * The MIT License
 *
 * Copyright 2018 Ángel Miguel García Vico.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.ujaen.simidat.agvico.org.core;

import com.yahoo.labs.samoa.instances.InstancesHeader;
import es.ujaen.simidat.agvico.moa.subgroupdiscovery.StreamMOEAEFEP;
import es.ujaen.simidat.agvico.moa.subgroupdiscovery.genetic.Individual;
import es.ujaen.simidat.agvico.moa.subgroupdiscovery.qualitymeasures.QualityMeasure;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Class to store the results and rules of a given population of individuals
 *
 * @author Ángel Miguel García Vico (agvico@ujaen.es)
 * @since JDK 8
 * @version 1.0
 */
public final class ResultWriter {

    /**
     * The path where the objectives values for each individual is stored
     */
    private final String pathTra;

    /**
     * The path where the test quality measures are stored (detailed file)
     */
    private final String pathTst;

    /**
     * The path where the test quality measures are stored (summary file)
     */
    private final String pathTstSummary;

    /**
     * The path where the rules are stored
     */
    private final String pathRules;

    /**
     * The population to get the results
     */
    private ArrayList<Individual> population;

    /**
     * The instance where the variables are obtained
     */
    private InstancesHeader inst;

    /**
     * The formatter of the numbers
     */
    private final DecimalFormat sixDecimals;

    /**
     * The symbols to use in the formatter
     */
    private final DecimalFormatSymbols symbols;

    /**
     * It determines if it is the first time to write the header or not
     */
    private boolean firstTime;

    private ArrayList<String> testFile;
    
    /**
     * Default constructor, it sets the path where the files are stored.
     *
     *
     * @param tra The path for training QMs files
     * @param tst The path fot test QMs files
     * @param tstSummary The path for the summary of the test QMs file
     * @param rules The path for the rules file
     * @param population The population of individuals
     * @param header The InstancesHeader of the given problem
     * @param overwrite If previous files exists, overwrite it?
     */
    public ResultWriter(String tra, String tst, String tstSummary, String rules, ArrayList<Individual> population, InstancesHeader header, boolean overwrite) {
        this.pathRules = rules;
        this.pathTra = tra;
        this.pathTst = tst;
        this.pathTstSummary = tstSummary;
        this.population = population;
        this.inst = header;
        symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setDecimalSeparator('.');
        symbols.setNaN("NaN");
        symbols.setInfinity("INFINITY");
        sixDecimals = new DecimalFormat("0.000000", symbols);
        if (this.population != null) {
            this.population.sort((x, y) -> Integer.compare(x.getClas(), y.getClas()));
        }
        firstTime = true;

        if (overwrite) {
            File[] a = {new File(tra), new File(tst), new File(tstSummary), new File(rules)};
            for (File f : a) {
                if (f.exists()) {
                    //f.delete();
                }
            }
        }
        testFile=new ArrayList<>();
    }

    /**
     * It only writes the results of the rules
     */
    public void writeRules() {
        //System.out.println("ESCRIBO REGLAS");
        String content = "*************************************************\n";
        content += "Timestamp " + StreamMOEAEFEP.getTimestamp() + ":\n";
        for (int i = 0; i < population.size(); i++) {
            content += "Rule " + i + " (ID: "+ population.get(i).hashCode() + ")\n";
            content += population.get(i).toString(inst) + "\n";
            
            
            //////////////TEST EMPTY RULES
            if(population.get(i).isEmpty()){
                System.out.println("HAY UNA REGLA VACÍA");
                System.out.println(population.get(i).getClas()+"-clase");
                System.out.println(population.get(i).getDiversityMeasure()+"-diversityMeasure");
                System.out.println(population.get(i).getIndivEvaluated()+"-IndiviEvaluated");
                System.out.println(population.get(i).getMedidas()+"-medidas");
                System.out.println(population.get(i).getNumVars()+"-variables");
                System.out.println(population.get(i).getNEval()+"-nEval");
                //añadir más
            }
                
            ///////////
                
        }
        testFile.add(pathRules);
        testFile.add(content);
        //Files.addToFile(pathRules, content);
    }

    /**
     * It only writes the results of the objectives
     */
    public void writeTrainingMeasures() {
        String content = "";
        if (firstTime) {

            // Write the header (the consequent first, and next, the objective quality measures, finaly, the diversity measure)
            content += "Timestamp\tRule\tID\tConsequent";
            for (QualityMeasure q : (ArrayList<QualityMeasure>) population.get(0).getObjs()) {
                content += "\t" + q.getShortName();
            }
            content += "\t" + population.get(0).getDiversityMeasure().getShortName() + "(Diversity)";
            content += "\n";
        }
        
        // Now, for each individual, writes the training measures
        for (int i = 0; i < population.size(); i++) {
            content += sixDecimals.format(StreamMOEAEFEP.getTimestamp()) + "\t" + i + "\t" + population.get(i).hashCode() + "\t" + inst.outputAttribute(0).value(population.get(i).getClas()) + "\t";
            for (QualityMeasure q : (ArrayList<QualityMeasure>) population.get(i).getObjs()) {
                content += sixDecimals.format(q.getValue()) + "\t";
            }
            content += sixDecimals.format(population.get(i).getDiversityMeasure().getValue()) + "\n";
        }
        testFile.add(pathTra);
        testFile.add(content);
        //Files.addToFile(pathTra, content);
    }

    /**
     * It writes the full version of the results test quality measures, i.e.,
     * the whole set of measures for each individual on each timestamp, in
     * addition to the summary
     */
    public void writeTestFullResults() {
        // this array stores the sum of the quality measures for the average
        ArrayList<Double> averages = new ArrayList<>();
        double numVars = 0.0;
        
        for (QualityMeasure q : (ArrayList<QualityMeasure>) population.get(0).getMedidas()) {
            averages.add(0.0);
        }

        // First, write the headers
        String content = "";
        if (firstTime) {
            content = "Timestamp\tRule\tClass\tID\tNumRules\tNumVars";

            // now, append each test quality measure
            for (int j = 0; j < population.get(0).getMedidas().size(); j++) {
                QualityMeasure q = (QualityMeasure) population.get(0).getMedidas().get(j);
                content += "\t" + q.getShortName();
            }
            content += "\n";
        }

        // now write the test results for each individual
        for (int i = 0; i < population.size(); i++) {
            content += sixDecimals.format(StreamMOEAEFEP.getTimestamp()) + "\t"
                    + i + "\t" 
                    + inst.outputAttribute(0).value(population.get(i).getClas()) + "\t" + population.get(i).hashCode() + "\t"
                    + "------\t"
                    + sixDecimals.format(population.get(i).getNumVars()) + "\t";
            numVars += population.get(i).getNumVars();

            for (int j = 0; j < population.get(i).getMedidas().size(); j++) {
                QualityMeasure q = (QualityMeasure) population.get(i).getMedidas().get(j);
                content += sixDecimals.format(q.getValue()) + "\t";
                try{
                averages.set(j, averages.get(j) + q.getValue());
                } catch (IndexOutOfBoundsException ex){
                    System.err.println("PopSize: " + population.size());
                    System.err.println("Medidas: " + population.get(0).getMeasures());
                    for(int k = 0; k < population.size(); k++){
                        System.err.println("Empty?: " + population.get(k).isEmpty());
                        System.err.println("Diversity: " + population.get(k).getDiversityMeasure());
                    }
                    System.err.println("averages: " + averages.size());
                    System.err.println("j: " + j);
                    System.err.println("i: " + i);
                    System.exit(-1);
                }
            }
            content += "\n";
        }

        numVars /= (double) population.size();
        // finally, write the average results
        content += "------\t------\t------\t------\t" + sixDecimals.format(population.size()) + "\t" + sixDecimals.format(numVars) + "\t";
        for (Double d : averages) {
            content += sixDecimals.format(d / (double) population.size()) + "\t";
        }
        content += "\n";
        testFile.add(pathTst);
        testFile.add(content);
        //Files.addToFile(pathTst, content);
    }

    /**
     * It writes the summary results of the test quality measures, i.e., it only
     * writes the line with the average results.
     *
     * @param time_ms The execution time in milliseconds.
     */
    public void writeTestSummaryResults(long time_ms, long memoryMB) {
        // this array stores the sum of the quality measures for the average
        ArrayList<Double> averages = new ArrayList<>();
        double numVars = 0.0;
        for (QualityMeasure q : (ArrayList<QualityMeasure>) population.get(0).getMedidas()) {
            averages.add(0.0);
        }

        // First, write the headers
        String content = "";
        if (firstTime) {
            content = "Timestamp\tRule\tClass\tNumRules\tNumVars";

            // now, append each test quality measure
            for (int j = 0; j < population.get(0).getMedidas().size(); j++) {
                QualityMeasure q = (QualityMeasure) population.get(0).getMedidas().get(j);
                content += "\t" + q.getShortName();
            }
            content += "\tExecTime_ms\tMemory_mb\n";
        }

        // Now, average the results of the test measures
        for (int i = 0; i < population.size(); i++) {
            numVars += population.get(i).getNumVars();
            for (int j = 0; j < population.get(i).getMedidas().size(); j++) {
                QualityMeasure q = (QualityMeasure) population.get(i).getMedidas().get(j);
                averages.set(j, averages.get(j) + q.getValue());
            }
        }

        numVars /= (double) population.size();
        // finally, write the average results
        content += sixDecimals.format(StreamMOEAEFEP.getTimestamp()) + "\t------\t------\t" + sixDecimals.format(population.size()) + "\t" + sixDecimals.format(numVars) + "\t";
        for (Double d : averages) {
            content += sixDecimals.format(d / (double) population.size()) + "\t";
        }
        content += sixDecimals.format(time_ms) + "\t" + sixDecimals.format(memoryMB / (1024*1024)) + "\n";
        
        testFile.add(pathTstSummary);
        testFile.add(content);
        //Files.addToFile(pathTstSummary, content);
    }

    /**
     * It writes the results of the individuals in the files
     */
    public void writeResults(long time_ms, long memoryMB) {
        writeRules();
        writeTrainingMeasures();
        writeTestFullResults();
        writeTestSummaryResults(time_ms, memoryMB);
        firstTime = false;
    }

    /**
     * @return the population
     */
    public ArrayList<Individual> getPopulation() {
        return population;
    }

    /**
     * @param population the population to set
     */
    public void setPopulation(ArrayList<Individual> population) {
        this.population = population;
        if (this.population != null) {
            this.population.sort((x, y) -> Integer.compare(x.getClas(), y.getClas()));
        }
    }

    /**
     * @param inst the inst to set
     */
    public void setInstancesHeader(InstancesHeader header) {
        this.inst = header;
    }
    
    public ArrayList<String> getDatas(){
        return testFile;
    
    } 

}
