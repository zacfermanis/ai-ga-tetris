package code;

import java.util.*;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


/*
 * A genetic algorithm to find combinations for AI values. This is an interface to the rest
 * of JTetris: they start by calling setAIValues() to let us set some values for the AI, then
 * they call sendScore() to give us what they got.
 */
public class GeneticAIAlgorithm 
{
	// *************************************************** //
	// ******** Genetic Algorithm Configuration ********** //
	// *************************************************** //
	
	// How many candidates are there in a generation?
	// Must be a multiple of 4.
	final int population = 16;
	
	// How often do chromosomes mutate?
	double mutation_rate = 0.05;
	
	// ****** Fitness Function ******* //
	// For Picking Winners - use Top scoring half of population?
	// If False - use random head-to-head pairings to determine winners.
	boolean useTopHalf = true;
	
	/* *** Reproduction options:*****
	 * if useCrossover is true, crossover is used to generate children
	 * if useParentsAverage is true, an average of each parents' weights is used
	 * if neither is true, a "coin-flip" is used per gene to pick which parent the gene comes from
	 */	
	boolean useCrossover = true;
	boolean useParentsAverage = false;
	
	
	
	// ********** Application Configuration *** //
	
	// If false, use the input values
	boolean useGeneticAI = true;
	
	// Which generation are we in?
	int generation = 1;
	
	// A chromosome is just an array of 7 doubles.
	double[][] chromosomes = new double[population][7];
	int[] scores = new int[population];
	
	// Set up Logger
	private static final Logger log = Logger.getLogger(GeneticAIAlgorithm.class);
	
	// Application hooks
	Random rnd;
	TetrisEngine tetris;
	int current = 0;
	
	/**
	 * Constructor
	 * @param tetris
	 * @param useGenetic
	 */
	public GeneticAIAlgorithm(TetrisEngine tetris, boolean useGenetic)
	{
		DOMConfigurator.configure("log4j.xml");		
		
		this.tetris = tetris;
		
		useGeneticAI = useGenetic;
		
		// Randomize starting chromosomes with values between -5 and 5.
		rnd = new Random();
		for(int i=0; i<population; i++){
			for(int j=0; j<7; j++){
				chromosomes[i][j] = rnd.nextDouble()*10 - 5;
			}
		}
	
	}
	
	void newGeneration()
	{
		log.error("************* NEW GENERATION *****************");
		// Calculate average fitness
		int[] sortedScores = new int[population];
		ArrayList<Integer> scoreList = new ArrayList<Integer>();
		int scoreTotal = 0;
		for(int i=0; i<scores.length; i++) 
		{
			sortedScores[i] = scores[i];
			scoreTotal+=scores[i];
		}
		Arrays.sort(sortedScores);
		log.fatal("Generation " + generation + 
				"; min = " + sortedScores[0] +
				"; med = " + sortedScores[population/2] +
				"; max = " + sortedScores[population-1] +
				"; avg = " + scoreTotal/population);
		List<double[]> winners = new ArrayList<double[]>();

		if(useTopHalf)
		{
			for(int i=(population-1); i>(population/2)-1; i--)
			{
				int nextTopScore = sortedScores[i];
				for(int j=0;j<sortedScores.length;j++)
				{
					if(scores[j]==nextTopScore)
					{
						// Multiple chromosomes with same score - ignore ones already added
						if (!scoreList.contains(j))
						{	
							winners.add(chromosomes[j]);
							scoreList.add(j);
							log.error("Winner " + j  + ": Score: " + nextTopScore + " " + printWeights(chromosomes[j],false));
							break;
						}
					}
				}
			}

		}
		else
		{
			// Pair 1 with 2, 3 with 4, etc.
			for(int i=0; i<population; i=i+2)
			{
				
				// Pick the more fit of the two pairs
				int c1score = scores[i];
				int c2score = scores[i+1];
				int winner = c1score > c2score? i : i+1;
				
				// Keep the winner, discard the loser.
				winners.add(chromosomes[winner]);
				log.error("Winner: " + printWeights(chromosomes[winner],true));
			}
		}
		
		
		int counter = 0;
		List<double[]> new_population = new ArrayList<double[]>();
		
		log.error("*************** Making Babies!! ******************");
		
		// Pair up two winners at a time
		for(int i=0; i<winners.size(); i=i+2){
			double[] winner1 = winners.get(i);
			double[] winner2 = winners.get(i+1);
			
			log.error("Parent #" + (i+1) + ": " + printWeights(winner1, false));
			log.error("Parent #" + (i+2) + ": " + printWeights(winner2, false));
			
			// Generate four new children
			for(int childIdx=0; childIdx<4; childIdx++)
			{
				double[] child = new double[7];
				StringBuilder sb = new StringBuilder();
				
				if (useCrossover)
				{
					int crossover = rnd.nextInt(7);

					sb.append("Child: " + (childIdx+1) + ". Crossover: " + crossover + ". Weights: ");
					for(int j=0; j<7;j++)
					{
						child[j] = j<crossover ? winner1[j] : winner2[j];
						
						// Chance of mutation
						boolean mutate = rnd.nextDouble() < mutation_rate;
						if(mutate){
							// Change this value anywhere from -5 to 5
							double change = rnd.nextDouble()*10 - 5;
							child[j] += change;
						}
						sb.append(Double.toString(((double)Math.round(child[j]*100))/100));
						if (j!=(crossover-1) && j!=6)
							sb.append(", ");
						if (j==(crossover-1))
							sb.append(" ||");
					}
					log.error(sb);
				}
				else if (useParentsAverage)
				{
					sb.append("Child: " + childIdx + ". Weights: ");
					for(int j=0; j<7;j++)
					{
						child[j] = (winner1[j] + winner2[j])/2;
						
						// Chance of mutation
						boolean mutate = rnd.nextDouble() < mutation_rate;
						if(mutate){
							// Change this value anywhere from -5 to 5
							double change = rnd.nextDouble()*10 - 5;
							child[j] += change;
						}
						sb.append(Double.toString(((double)Math.round(child[j]*100))/100));
						if (j!=6)
							sb.append(", ");
					}
				}
				else
				{
					sb.append("Child: " + childIdx + ". Weights: ");
					// Pick at random a mixed subset of the two winners and make it the new chromosome
					for(int j=0; j<7; j++)
					{
						child[j] = rnd.nextInt(2)>0 ? winner1[j] : winner2[j];
						
						// Chance of mutation
						boolean mutate = rnd.nextDouble() < mutation_rate;
						if(mutate){
							// Change this value anywhere from -5 to 5
							double change = rnd.nextDouble()*10 - 5;
							child[j] += change;
						}
						sb.append(Double.toString(((double)Math.round(child[j]*100))/100));
						if (j!=6)
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
		for(int i=0; i<population; i++){
			for(int j=0; j<7; j++)
				chromosomes[i][j] = new_population.get(i)[j];
		}
		
		generation++;
		current = 0;
		
	}
	
	void setAIValues(TetrisAI ai)
	{
		if(!useGeneticAI) 
			return;
		
		ai._TOUCHING_EDGES = chromosomes[current][0];
		ai._TOUCHING_WALLS = chromosomes[current][1];
		ai._TOUCHING_FLOOR = chromosomes[current][2];
		ai._HEIGHT = chromosomes[current][3];
		ai._HOLES = chromosomes[current][4];
		ai._BLOCKADE = chromosomes[current][5];
		ai._CLEAR = chromosomes[current][6];
	}
	
	void sendScore(int score)
	{
		if(!useGeneticAI) 
			return;
		
		String s = printWeights(chromosomes[current], true);
		s = "Generation " + generation + "; Candidate " + (current+1) + ": " + s + " Score = " + score;
		log.info(s);
		//System.out.println(s);
		scores[current] = score;
		current++;
		
		if(current == population)
			newGeneration();
	}
	
	// Output the weights used for this Generation
	private String printWeights(double[] a, boolean useLabels)
	{
		String[] labels = {"Walls: ", "Floor: ", "Height: ", "Holes: ", "Blockades: ", "Clears: "};
		String s = "";
		if (useLabels)
			s += "Edges: ";
		for(int i=0; i<a.length; i++)
		{
			s += Double.toString(((double)Math.round(a[i]*100))/100);
			if(i != a.length-1)
			{
				s += ", ";
				if(useLabels)
					s += labels[i];
			}
		}
		return "[" + s + "]";
	}
}