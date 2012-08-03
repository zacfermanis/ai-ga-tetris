package code;

import static code.ProjectConstants.sleep_;
import static code.ProjectConstants.GameState;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * 
 * AI Algorithm to control Tetris Game Client
 * Uses Greedy Algorithm to determine best fit
 * for current piece.
 * 
 * @author Zac Fermanis
 */
public class TetrisAI
{
	private TetrisPanel panel;
	private TetrisEngine engine;
	AIThread thread;
	volatile boolean flag = false;
	
	// Time AI has to wait per keypress. //
	public static final int waitTime = 2;
	
	// Do we use hard drops? //
	public static final boolean do_drop = false;
	
	// Output Score to Console? //
	public boolean displayScore = false;
	public boolean displayGrid = false;
	
	
	// Weights for AI
	double _TOUCHING_EDGES = 3.97;
	double _TOUCHING_WALLS = 6.52;
	double _TOUCHING_FLOOR = 0.65;
	double _HEIGHT = -6.78;
	double _HOLES = -3.31;
	double _BLOCKADE = -0.59;
	double _CLEAR = 3.6;
	
	// Set up Logger
	private static final Logger log = Logger.getLogger(TetrisAI.class);
	
	public TetrisAI(TetrisPanel inputPanel)
	{
		DOMConfigurator.configure("log4j.xml");	
		
		panel = inputPanel;
		engine = panel.engine;
		thread = new AIThread();
	}
	
	public void sendReady()
	{
		if(!flag)
		{
			thread.start();
			flag = true;
			engine.lastnewblock = System.currentTimeMillis();
		}
	}
	
	class AIThread extends Thread
	{
		public void run()
		{
			
			while(flag)
			{
				
				try
				{
					// If it's merely paused, do nothing; if it's actually game over
					// then break loop entirely.
					if(engine.state.equals(GameState.PLAYING))
					{
						if(engine.activeblock == null) continue;
						
						BlockPosition temp = computeBestFit(engine);
						
						if (displayScore)
						{
							log.info("*********** BEST FIT: (" + temp.blockX + ", " + temp.blockRotation + ")");
						}
						
						if(engine.state.equals(GameState.PLAYING))
						{
							
							int bestFitX = temp.blockX;
							int bestFitRotation = temp.blockRotation;
							
							// Move it!
							movehere(bestFitX, bestFitRotation);
						}
					}
					// Safety - Thread timings
					sleep_(waitTime);
				}
				catch(Exception e)
				{
					log.error("Exception Occurred: " + e.toString());
				}
			}
			
		}
		
		// Keypresses to move block to calculated position.
		private void movehere(int finalX, int finalBlockRotation)
		{
			int st_blocksdropped = engine.blocksdropped;
			
			// Failsafe here: if at any time we Rotate it
			// or move it and it doesn't move then it's stuck and we give up.
			
			int init_state = engine.activeblock.rot;
			int prev_state = init_state;
			while(flag && engine.activeblock.rot != finalBlockRotation)
			{
				// Rotate first so we don't get stuck in the edges.
				engine.keyrotate();
				
				//Now wait.
				sleep_(waitTime);
				
				if(prev_state == engine.activeblock.rot || init_state == engine.activeblock.rot)
				{
					engine.keyslam();
					sleep_(3);
				}
				prev_state = engine.activeblock.rot;
			}
			
			prev_state = engine.activeblock.x;
			while(flag && engine.activeblock.x != finalX)
			{
				// Move the block left/right
				if(engine.activeblock.x < finalX)
				{
					engine.keyright();
				}
				else if(engine.activeblock.x > finalX)
				{
					engine.keyleft();
				}
				
				sleep_(waitTime);
				
				if(prev_state == engine.activeblock.x)
				{
					engine.keyslam();
					sleep_(3);
				}
				prev_state = engine.activeblock.x;
			}
			
			while(flag && engine.blocksdropped == st_blocksdropped)
			{
				// Move it down until it drops a new block.
				engine.keydown();
				sleep_(waitTime);
			}
		}
	}
	
	/**
	 * Greedy Algorithm to Determine Best Fit for Piece
	 **/
	BlockPosition computeBestFit(TetrisEngine ge)
	{

		byte[][][] allblockRotations = TetrisEngine.blockdef[ge.activeblock.type];
		int numBlockRotations = allblockRotations.length;

		// List of all the possible fits.
		List<BlockPosition> possibleFits = new ArrayList<BlockPosition>();

		// Loop through the Rotations.
		// Here we generate all of the unique valid fits, and evaluate
		// them later.
		for(int i=0; i<numBlockRotations; i++)
		{
			byte[][] tempBlockRotation = allblockRotations[i];
			int free = freeSpaces(tempBlockRotation);
			int freeL = free / 10;
			int freeR = free % 10;
			int minX = 0 - freeL;
			int maxX = (ge.width-4) + freeR;
			// Loop through each position for a blockRotation.
			for(int j=minX; j<=maxX; j++)
			{
				BlockPosition put = new BlockPosition();
				put.blockX = j;
				put.blockRotation = i;
				possibleFits.add(put);
			}
		}
		
		// Do everything again for the next block
		byte[][][] allblockRotations2 = TetrisEngine.blockdef[ge.nextblock.type];
		int numBlockRotations2 = allblockRotations2.length;
		List<BlockPosition> possibleFits2 = new ArrayList<BlockPosition>();
		for(int i=0; i<numBlockRotations2; i++)
		{
			byte[][] tempBlockRotation = allblockRotations2[i];
			int free = freeSpaces(tempBlockRotation);
			int freeL = free / 10;
			int freeR = free % 10;
			int minX = 0 - freeL;
			int maxX = (ge.width-4) + freeR;
			for(int j=minX; j<=maxX; j++)
			{
				BlockPosition put = new BlockPosition();
				put.blockX = j;
				put.blockRotation = i;
				possibleFits2.add(put);
			}
		}

		// Evaluate each possible fits:
		// For each element in the list we have, calculate a score, and pick
		// the best.
		ScoreGrid[] scores = new ScoreGrid[possibleFits.size() * possibleFits2.size()];
		

		for(int i=0; i<possibleFits.size(); i++)
		{
			for(int j=0; j<possibleFits2.size(); j++)
			{
				int index = i*possibleFits2.size()+j;
				ScoreGrid currentScoreGrid = evalPosition(ge, possibleFits.get(i), possibleFits2.get(j));
				scores[index] = new ScoreGrid();
				scores[index].score = currentScoreGrid.score;
				scores[index].mockgrid = currentScoreGrid.mockgrid;
			}
		}

		// Retrieve max.
		double max = Double.NEGATIVE_INFINITY;
		BlockPosition max_b = null;
		int scoreIndex = 0;
		for(int i=0; i<scores.length; i++){
			if(scores[i].score >= max){
				max_b = possibleFits.get(i/possibleFits2.size());
				max = scores[i].score;
				scoreIndex = i;
			}
		}
		
		byte[][] chosenGrid = scores[scoreIndex].mockgrid;
		if (displayGrid)
		{
			printMockGrid(chosenGrid);
		}

		// Return final position.
		return max_b;
	}

	// Evaluate position not with one, but with two blocks.
	ScoreGrid evalPosition(TetrisEngine ge, BlockPosition firstBlock, BlockPosition secondBlock)
	{

		// First step: Simulate the drop. Do this on a mock grid.
		// This copies the grid into the mock grid.
		byte[][] mockgrid = new byte[ge.width][ge.height];
		for(int i=0; i<ge.width; i++)
		{
			for(int j=0; j<ge.height; j++)
			{
				byte s = (byte) ge.blocks[i][j].getState();
				if(s==2) s=0;
				mockgrid[i][j] = s;
			}
		}
		
		int cleared = 0;
		for(int blockNumber=1; blockNumber<=2; blockNumber++)
		{			
			byte[][] block;
			BlockPosition currentBlock;
			
			if(blockNumber==1) 
			{
				currentBlock=firstBlock;
			}
			else
			{
				currentBlock=secondBlock;
			}
			
			try
			{	
    			if(blockNumber==1)
    			{
    				block = ge.blockdef[ge.activeblock.type][currentBlock.blockRotation];
    			}
    			else 
    			{
    				block = ge.blockdef[ge.nextblock.type][currentBlock.blockRotation];
    			}

			
    			// Find the fitting height by starting from the bottom and
    			// working upwards. The bottom of the grid is height-1, so start there    	
    			int h;
    			for(h = ge.height-1;; h--)
    			{    	
    				// Indicator to determine if fit is valid. 1: fits. 0: doesn't fit. -1: game over.
    				int fit_state = 1;
    	
    				for(int i=0; i<4; i++)
    					for(int j=0; j<4; j++)
    					{
    						// Check for bounds.
    						boolean block_p = block[j][i] >= 1;

    						if(block_p)
    						{
    							// Still have to check for overflow. X-overflow can't
    							// Happen at this stage but Y-overflow can.
    	
    							if(h+j >= ge.height)
    							{
    								fit_state = 0;
    							}
    							else if(h+j < 0)
    							{
    								fit_state = -1;
    							}
    							else
    							{
    								boolean board_p = mockgrid[i+currentBlock.blockX][h+j] >= 1;
    	
    								// Already filled, doesn't fit.
    								if(board_p)
    								{
    									fit_state = 0;
    								}
    	
    								// Still the possibility that another block
    								// might still be over it.
    								if(fit_state==1)
    								{
    									for(int h1=h+j-1; h1>=0; h1--)
    									{
    										if(mockgrid[i+currentBlock.blockX][h1]>=1)
    										{
    											fit_state = 0;
    											break;
    										}
    									}
    								}
    							}
    						}
    					}
    	
    				// Game over occurred, so return a horrible score
    				if(fit_state==-1)
    				{
    					return new ScoreGrid(-99999999,mockgrid);
    				}
    				
    				// 1 = found!
    				if(fit_state==1)
    				{
    					break;
    				}
    	
    			}
    			
    			// copy over block position
    			for(int i=0; i<4; i++)
    				for(int j=0; j<4; j++)
    					if(block[j][i]==1)
    						mockgrid[currentBlock.blockX+i][h+j] = 2;
    			
			}
			catch (Exception e)
			{
				if (blockNumber==1)
				{
					log.error("Exception Found. ge.blockdef["+ ge.activeblock.type + "][" + currentBlock.blockRotation + "]");
				}
				else
				{
					log.error("Exception Found. ge.blockdef["+ ge.nextblock.type + "][" + currentBlock.blockRotation + "]");
				}
				log.error(e.toString());
			}
			
			
			try
			{
    			// Check for clears
    			boolean foundline;
    			do{
    				foundline = false;
    				MultiLine:
    				for(int i = mockgrid[0].length-1;i>=0;i--)
    				{
    					for(int y = 0;y < mockgrid.length;y++)
    					{
    						if(!(mockgrid[y][i] > 0))
    							continue MultiLine;
    					}
    					
    					// Line i is full, clear it and copy
    					cleared++;
    					foundline = true;
    					for(int a = i;a>0;a--)
    					{
    						for(int y = 0;y < mockgrid.length;y++)
    						{
    							mockgrid[y][a] = mockgrid[y][a-1];
    						}
    					}
    					break MultiLine;
    				}
    			}while(foundline);
    			
			}
			catch (Exception e)
			{
				log.error(e.toString());
			}
		}

		// Now we evaluate the resulting position.

		// Part of the evaluation algorithm is to count the number of touching sides.
		// First, Generating all pairs and see how many them are touching.
		// If they add up to 3, it means one of them is from the active block and the
		// other is a normal block (ie. they're touching).

		double score = 0.0;

		// Horizontal pairs
		for(int i=0; i<ge.height; i++)
			for(int j=0; j<ge.width-1; j++)
			{
				if(j==0 && mockgrid[j][i]==2) score += _TOUCHING_WALLS;
				if(j+1==ge.width-1 && mockgrid[j+1][i]==2) score += _TOUCHING_WALLS;
				if(mockgrid[j][i] + mockgrid[j+1][i] >= 3) score += _TOUCHING_EDGES;
			}

		// Vertical pairs
		for(int i=0; i<ge.width; i++)
			for(int j=0; j<ge.height-1; j++)
			{
				if(j+1==ge.height-1 && mockgrid[i][j+1]==2) score += _TOUCHING_FLOOR;
				if(mockgrid[i][j] + mockgrid[i][j+1] >= 3) score += _TOUCHING_EDGES;
			}

		// Penalize height.
		for(int i=0; i<ge.width; i++)
			for(int j=0; j<ge.height; j++)
			{
				int curheight = ge.height - j;
				if(mockgrid[i][j]>0) score += curheight * _HEIGHT;
			}

		// Penalize holes. Also penalize blocks above holes.
		for(int i=0; i<ge.width; i++) 
		{
			
			// Part 1: Count how many holes (space beneath blocks)
			boolean f = false;
			int holes = 0;
			for(int j=0; j<ge.height; j++)
			{
				if(mockgrid[i][j]>0) f = true;
				if(f && mockgrid[i][j]==0) holes++;
			}
			
			// Part 2: Count how many blockades (block above space)
			f = false;
			int blockades = 0;
			for(int j=ge.height-1; j>=0; j--)
			{
				if(mockgrid[i][j]==0) f=true;
				if(f&&mockgrid[i][j]>0) blockades++;
			}
			
			score += _HOLES*holes;
			score += _BLOCKADE*blockades;
		}
		
		score += cleared * _CLEAR;
		
		if (displayScore)
		{
    		printMockGrid(mockgrid);
    		log.info(score);    		
		}

		return new ScoreGrid(score,mockgrid);
	}
	
	public void printMockGrid(byte[][] mockgrid)
	{
		for (int yMock = 0; yMock < mockgrid[0].length ; yMock++) {
			for (int xMock = 0; xMock < mockgrid.length; xMock++) {
				log.debug(mockgrid[xMock][yMock] + " ");
			}
		}
	}


	// Takes a int array and calculates how many blocks of free spaces are there
	// on the left and right. The return value is a 2 digit integer.
 	static int freeSpaces(byte[][] in)
 	{

		// It's free if all of them are zero, and their sum is zero.
		boolean c1free = in[0][0] + in[1][0] + in[2][0] + in[3][0] == 0;
		boolean c2free = in[0][1] + in[1][1] + in[2][1] + in[3][1] == 0;
		boolean c3free = in[0][2] + in[1][2] + in[2][2] + in[3][2] == 0;
		boolean c4free = in[0][3] + in[1][3] + in[2][3] + in[3][3] == 0;

		int lfree = 0;

		if(c1free){
			lfree++;
			if(c2free){
				lfree++;
				if(c3free){
					lfree++;
					if(c4free){
						lfree++;
		} } } }

		int rfree = 0;
		if(c4free){
			rfree++;
			if(c3free){
				rfree++;
				if(c2free){
					rfree++;
					if(c1free){
						rfree++;
		} } } }

		return lfree*10 + rfree;
	}
	
}

// No tuple support in java.
class BlockPosition{
	int blockX, blockRotation;
}

class ScoreGrid{
	double score;
	byte[][] mockgrid;
	
	public ScoreGrid(double scr, byte[][] grid)
	{
		this.score = scr;
		this.mockgrid = grid;
	}
	
	public ScoreGrid()
	{
		this.score = 0.0;
		this.mockgrid = new byte[0][0];
	}
}