package com.fermanis.aitetris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;


/*
 * A genetic algorithm to find combinations for AI values. This is an interface to the rest
 * of JTetris: they start by calling setAIValues() to let us set some values for the AI, then
 * they call sendScore() to give us what they got.
 */
public class GeneticAIAlgorithm {
    // *************************************************** //
    // ******** Genetic Algorithm Configuration ********** //
    // *************************************************** //

    // How many candidates are there in a generation?
    // Must be a multiple of 4.
    @Value("${genetic_algo.population}")
    private int population = 16;

    // How often do chromosomes mutate?
    @Value("${genetic_algo.mutation_rate}")
    private double mutation_rate = 0.05;

    // ****** Fitness Function ******* //
    // For Picking Winners - use Top scoring half of population?
    // If False - use random head-to-head pairings to determine winners.
    private boolean useTopHalf = true;

    /* *** Reproduction options:*****
     * if useCrossover is true, crossover is used to generate children
     * if useParentsAverage is true, an average of each parents' weights is used
     * if useTwinPrevention is true, an offspring cannot have the same weights as a sibling
     * if neither is true, a "coin-flip" is used per gene to pick which parent the gene comes from
     */
    boolean useCrossover = true;
    boolean useParentsAverage = false;
    boolean useTwinPrevention = true;


    // ********** Application Configuration *** //

    // If false, use the input values
    boolean useGeneticAI = true;

    // Which generation are we in?
    int generation = 1;

    // How many runs per evaluation?
    int currentRunTotal = 3;

    // Save the generation so it can be loaded on subsequent runs?
    boolean serializeGeneration = true;

    // Load the Generation from a previous run?
    boolean useLoadedGeneration = false;

    // Load a preset Starting Population?
    boolean usePreset = true;


    // *********** Constants ************* //

    private static final String generationFile = "classpath:generations/generation.ser";
    private static final String presetFile = "classpath:generations/presetGeneration.csv";

    // A chromosome is just an array of 7 doubles.
    double[][] chromosomes = new double[population][7];
    int[] scores = new int[population];

    // Set up Logger
    private static final Logger log = LoggerFactory.getLogger(GeneticAIAlgorithm.class);

    // Application hooks
    Random rnd;

    TetrisEngine tetris;
    int current = 0;
    int currentRun = 0;
    int[] currentScores = new int[currentRunTotal];
    double currentRunSum = 0.0;

    /**
     * Constructor
     *
     * @param tetris
     * @param useGenetic
     */
    public GeneticAIAlgorithm(TetrisEngine tetris, boolean useGenetic) {
        log.error(" |********************************************************|");
        log.error(" |************ Initializing Genetic Algorithm ************|");
        log.error(" |********************************************************|");
        log.info("Config Values: Pop:" + population);

        this.tetris = tetris;

        useGeneticAI = useGenetic;

        if (useLoadedGeneration) {
            chromosomes = loadGeneration();
        } else if (usePreset) {
            chromosomes = loadPreset();
        } else {
            // Randomize starting chromosomes with values between -5 and 5.
            rnd = new Random();
            for (int i = 0; i < population; i++) {
                for (int j = 0; j < 7; j++) {
                    chromosomes[i][j] = rnd.nextDouble() * 10 - 5;
                }
            }
        }

    }


    void newGeneration() {
        log.error("************* NEW GENERATION *****************");
        // Calculate average fitness
        int[] sortedScores = new int[population];
        ArrayList<Integer> scoreList = new ArrayList<Integer>();
        int scoreTotal = 0;
        for (int i = 0; i < scores.length; i++) {
            sortedScores[i] = scores[i];
            scoreTotal += scores[i];
        }
        double average = scoreTotal / population;
        double deviation = 0.0;
        for (int i = 0; i < scores.length; i++) {
            deviation += (scores[i] - average) * (scores[i] - average);
        }
        deviation = deviation / (population - 1);
        deviation = Math.sqrt(deviation);
        Arrays.sort(sortedScores);
        log.error("Generation " + generation +
                "; min = " + sortedScores[0] +
                "; med = " + sortedScores[population / 2] +
                "; max = " + sortedScores[population - 1] +
                "; avg = " + scoreTotal / population +
                "; stdDev = " + Double.toString(((double) Math.round(deviation * 100)) / 100));
        List<double[]> winners = new ArrayList<double[]>();

        if (useTopHalf) {
            for (int i = (population - 1); i > (population / 2) - 1; i--) {
                int nextTopScore = sortedScores[i];
                for (int j = 0; j < sortedScores.length; j++) {
                    if (scores[j] == nextTopScore) {
                        // Multiple chromosomes with same score - ignore ones already added
                        if (!scoreList.contains(j)) {
                            winners.add(chromosomes[j]);
                            scoreList.add(j);
                            log.error("Winner " + j + ": Score: " + nextTopScore + " " + printWeights(chromosomes[j], false));
                            break;
                        }
                    }
                }
            }

        } else {
            // Pair 1 with 2, 3 with 4, etc.
            for (int i = 0; i < population; i = i + 2) {

                // Pick the more fit of the two pairs
                int c1score = scores[i];
                int c2score = scores[i + 1];
                int winner = c1score > c2score ? i : i + 1;

                // Keep the winner, discard the loser.
                winners.add(chromosomes[winner]);
                log.error("Winner: " + printWeights(chromosomes[winner], true));
            }
        }


        int counter = 0;
        List<double[]> new_population = new ArrayList<double[]>();

        log.error("*************** Making Babies!! ******************");

        // Pair up two winners at a time
        for (int i = 0; i < winners.size(); i = i + 2) {
            double[] winner1 = winners.get(i);
            double[] winner2 = winners.get(i + 1);

            log.error("Parent #" + (i + 1) + ": " + printWeights(winner1, false));
            log.error("Parent #" + (i + 2) + ": " + printWeights(winner2, false));

            ArrayList<Integer> twinPrevention = new ArrayList<Integer>();
            int crossover = 0;
            twinPrevention.add(crossover);

            // Generate four new children
            for (int childIdx = 0; childIdx < 4; childIdx++) {
                double[] child = new double[7];
                StringBuilder sb = new StringBuilder();

                if (useCrossover) {
                    // Prevents parents from having identical offspring (twins)
                    while (twinPrevention.contains(crossover)) {
                        try {
                            if (rnd == null) {
                                rnd = new Random();
                            }
                            crossover = rnd.nextInt(5) + 1;
                        } catch (Exception e) {
                            log.error("Exception occurred when performing crossover. Exception: ", e);
                        }
                    }
                    twinPrevention.add(crossover);

                    sb.append("Child: " + (childIdx + 1) + ". Crossover: " + crossover + ". Weights: ");
                    for (int j = 0; j < 7; j++) {
                        child[j] = j < crossover ? winner1[j] : winner2[j];

                        // Chance of mutation
                        boolean mutate = rnd.nextDouble() < mutation_rate;
                        if (mutate) {
                            // Change this value anywhere from -5 to 5
                            double change = rnd.nextDouble() * 10 - 5;
                            child[j] += change;
                        }
                        sb.append(Double.toString(((double) Math.round(child[j] * 100)) / 100));
                        if (j != (crossover - 1) && j != 6)
                            sb.append(", ");
                        if (j == (crossover - 1))
                            sb.append(" || ");
                    }
                    log.error(sb.toString());
                } else if (useParentsAverage) {
                    sb.append("Child: " + childIdx + ". Weights: ");
                    for (int j = 0; j < 7; j++) {
                        child[j] = (winner1[j] + winner2[j]) / 2;

                        // Chance of mutation
                        boolean mutate = rnd.nextDouble() < mutation_rate;
                        if (mutate) {
                            // Change this value anywhere from -5 to 5
                            double change = rnd.nextDouble() * 10 - 5;
                            child[j] += change;
                        }
                        sb.append(Double.toString(((double) Math.round(child[j] * 100)) / 100));
                        if (j != 6)
                            sb.append(", ");
                    }
                } else {
                    sb.append("Child: " + childIdx + ". Weights: ");
                    // Pick at random a mixed subset of the two winners and make it the new chromosome
                    for (int j = 0; j < 7; j++) {
                        child[j] = rnd.nextInt(2) > 0 ? winner1[j] : winner2[j];

                        // Chance of mutation
                        boolean mutate = rnd.nextDouble() < mutation_rate;
                        if (mutate) {
                            // Change this value anywhere from -5 to 5
                            double change = rnd.nextDouble() * 10 - 5;
                            child[j] += change;
                        }
                        sb.append(Double.toString(((double) Math.round(child[j] * 100)) / 100));
                        if (j != 6)
                            sb.append(", ");
                    }
                }

                new_population.add(child);
                counter++;
            }
        }

        // Shuffle the new population.
        Collections.shuffle(new_population, rnd);

        // Copy them over
        for (int i = 0; i < population; i++) {
            for (int j = 0; j < 7; j++) {
                chromosomes[i][j] = new_population.get(i)[j];
            }
        }

        if (serializeGeneration) {
            saveGeneration(chromosomes);
        }
        log.info("*************** Evaluating Next Generation *******************");
        generation++;
        current = 0;

    }

    void setAIValues(TetrisAI ai) {
        if (!useGeneticAI)
            return;

        ai._TOUCHING_EDGES = chromosomes[current][0];
        ai._TOUCHING_WALLS = chromosomes[current][1];
        ai._TOUCHING_FLOOR = chromosomes[current][2];
        ai._HEIGHT = chromosomes[current][3];
        ai._HOLES = chromosomes[current][4];
        ai._BLOCKADE = chromosomes[current][5];
        ai._CLEAR = chromosomes[current][6];
    }

    void sendScore(int score) {
        if (!useGeneticAI)
            return;

        String weights = printWeights(chromosomes[current], true);
        //System.out.println(s);

        log.debug("Generation: " + generation + "; Candidate: " + (current + 1) + "; Run: " + (currentRun + 1) + "; Score: " + score);
        currentRunSum += score;
        currentRun++;

        if (currentRun == currentRunTotal) {
            scores[current] = (int) Math.round(currentRunSum / currentRunTotal);
            log.info("Generation: " + generation + "; Candidate: " + (current + 1) + "; Avg Score = " + scores[current] + "  |  " + weights);
            current++;
            currentRun = 0;
            currentRunSum = 0;

        }

        if (current == population)
            newGeneration();
    }

    // Output the weights used for this Generation
    private String printWeights(double[] a, boolean useLabels) {
        String[] labels = {"Walls: ", "Floor: ", "Height: ", "Holes: ", "Blockades: ", "Clears: "};
        String s = "";
        if (useLabels) {
            s += "Edges: ";
        }
        for (int i = 0; i < a.length; i++) {
            s += Double.toString(((double) Math.round(a[i] * 100)) / 100);
            if (i != a.length - 1) {
                s += ", ";
                if (useLabels) {
                    s += labels[i];
                }
            }
        }
        return "[" + s + "]";
    }

    private void saveGeneration(double[][] generation) {
        try {
            File file = ResourceUtils.getFile(generationFile);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(generation);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private double[][] loadGeneration() {
        double[][] generation = new double[population][7];

        try {
            File file = ResourceUtils.getFile(generationFile);
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            generation = (double[][]) in.readObject();
			/*
			for(int i=0; i<population; i++)
			{
				for(int j=0; j<7; j++)
				{
					out.writeObject(generation[i][j]);
				}
			}
			*/
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return generation;
    }

    private double[][] loadPreset() {
        double[][] generation = new double[population][7];
        String[] strings = new String[population];
        int i = 0;

        try {
            File file = ResourceUtils.getFile(presetFile);
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                strings[i] = str;
                i++;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (i = 0; i < population; i++) {
            String[] currentString = strings[i].split(",");
            for (int j = 0; j < currentString.length; j++) {
                generation[i][j] = Double.parseDouble(currentString[j]);
            }
        }
        return generation;
    }

}