/**************************** Tests for artificial neural network *******************************
* 
************************************************************************************************/

public class aNN_Tests {
	/**********************************************
	* main function:
	* just contains the test objects
	**********************************************/
	public static void main(String[] args){
		
		// TODO:	Ergebnisse in txt-files
		
		// Unit test for class Neuron
		// TODO: Sieht gut aus, genaue Auswertung fehlt
//		neuronTest neuronTest = new neuronTest();
//		
//		neuronTest.runTest();
		
		// Unit test for class Connection
//		connectionTest connectionTest = new connectionTest();
//		
//		connectionTest.runTest();
		
		// Test for whole network, needs debug prints, look at commit when passed, in class
		// otherwise you'll just see the output vector
//		multilayerPerceptronTest multilayerPerceptronTest = new multilayerPerceptronTest();
//		
//		multilayerPerceptronTest.runTest();
		
		// Test for training using backpropagation
//		backpropagationTest backpropagationTest = new backpropagationTest();
		
//		backpropagationTest.runTestTopologies();
		//backpropagationTest.runTestTrainings();
		
		findFastestNet findFastestNet = new findFastestNet();
		findFastestNet.runTest();
	}
}

/*************************************** Unit Test Neuron ***************************************
* Builds neurons with 1,2 and 4 inputs
* Threshold 0, 0.5, 1.5 and -1.5
* Simulation of training at the end: Changing threshold and get result again and changing
* weights and get result again
************************************************************************************************/
class neuronTest {
	// TODO: Header:	Setzen von Threshold-Werten: 0, 1, 1.5. Ausgabe von Wert für print über getThrehold()
	//			1 Input: unter Threshold, 0 (test unsetInput()), über Threshold
	//			2 Inputs: einer unter Threshold, beide 0, beide zusammen unter Threshold, beide zusammen über threshold
	//			4 Inputs: einer unter Threshold, alle 0, 1 über Threshold
	//			-> für alle vorher Ergebnisse (anhand Formel aus Buch) ausrechen und print mit Ergebnis
	//			Test constructor 1, 2, 4 inputs
	//			
private
	Neuron testNeuron;
	int maxNrInputs;
	float[] casesInputs;
	float weights[];
	float thresholds[];
	
public
	// Constructor
	neuronTest(){
		testNeuron = new Neuron(0);
		maxNrInputs = 4;
		thresholds = new float[4];
		thresholds[0] = 0;
		thresholds[1] = 0.5f;
		thresholds[2] = 1.5f;
		thresholds[3] = -1.5f;	
	}

	/**********************************************
	* Test function
	**********************************************/
	void runTest(){

		for (int nrInputs = 1; nrInputs <= maxNrInputs; nrInputs *= 2){
			
			System.out.println("nrInputs: " + nrInputs);
			System.out.println("\tTesting constructor:\tgetOutput: " + testNeuron.getOutput());
			
			for (int thresh = 0; thresh < thresholds.length; thresh++){
				
				testNeuron = new Neuron(nrInputs);
				
				testNeuron.setThreshold(thresholds[thresh]);

				System.out.println("\n\tThreshold: " + testNeuron.getThreshold());
				
				for (int setInputs = 0; setInputs < 4; setInputs++){
					System.out.print("\tInputs: ");
					
					switch (setInputs){
					case 0:
						if (nrInputs >= 1){
							testNeuron.setInput(0, (thresholds[thresh] - 8));
							System.out.print((thresholds[thresh] - 8));
						}
						
						if (nrInputs >= 2){
							testNeuron.setInput(1, 0);
							System.out.print(", 0");
						}
						
						if (nrInputs >= 4){
							testNeuron.setInput(2, 0);
							System.out.print(", 0");
							testNeuron.setInput(3, 0);
							System.out.print(", 0");
						}
						break;
						
					case 1:
						if (nrInputs == 1){
							testNeuron.setInput(0, (thresholds[thresh] + 8));
							System.out.print((thresholds[thresh] + 8));
						}
						else if (nrInputs > 1){
							testNeuron.setInput(0, 0);
							System.out.print("0");
						}
						
						if (nrInputs >= 2){
							testNeuron.setInput(1, 0);
							System.out.print(", 0");
						}
						
						if (nrInputs >= 4){
							testNeuron.setInput(2, 0);
							System.out.print(", 0");
							testNeuron.setInput(3, 0);
							System.out.print(", 0");
						}
						break;
						
					case 2:
						if (nrInputs >= 1){
							testNeuron.setInput(0, ((thresholds[thresh]/2) - 4));
							System.out.print((thresholds[thresh]/2) - 4);
						}
						
						if (nrInputs >= 2){
							testNeuron.setInput(1, ((thresholds[thresh]/2) - 4));
							System.out.print(", " + ((thresholds[thresh]/2) - 4));
						}
						
						if (nrInputs >= 4){
							testNeuron.setInput(2, 0);
							System.out.print(", 0");
							testNeuron.setInput(3, 0);
							System.out.print(", 0");
						}
						break;
						
					default:
						if (nrInputs >= 1){
							testNeuron.setInput(0, ((thresholds[thresh]/2) + 4));
							System.out.print((thresholds[thresh]/2) + 4);
						}
						
						if (nrInputs >= 2){
							testNeuron.setInput(1, ((thresholds[thresh]/2) + 4));
							System.out.print(", " + ((thresholds[thresh]/2) + 4));
						}
						
						if (nrInputs >= 4){
							testNeuron.setInput(2, 0);
							System.out.print(", 0");
							testNeuron.setInput(3, 0);
							System.out.print(", 0");
						}
					}
					
					System.out.println("\tOutput: " + testNeuron.getOutput());
				}
			}
			
			System.out.print("\n");
		}
	}// runTest()
}// class neuronTest

/************************************* Unit Test Connection *************************************
* TODO: Test zu ende
* 		Header
* 		Test Connection:	Connection(neuronFrom, neuronTo, 0, connWeight, 0);
*							run, neuronTo.getOutput -> input zurückrechnen
*							addWeightDelta
************************************************************************************************/
class connectionTest {
private
	Connection testConnection;
	Neuron neuronFrom;
	Neuron neuronTo;
	
public
	// Constructor
	connectionTest(){
		neuronFrom = new Neuron(1);
		neuronTo = new Neuron(1);
		testConnection = new Connection(neuronFrom, neuronTo, 0, 4, 0);
	}

	void runTest(){
		neuronFrom.setThreshold(0);
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());
		
		neuronFrom.setInput(0, 1);
		neuronTo.setThreshold(0);
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());

		testConnection.run();
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());
		
		testConnection.addWeightDelta(4);
		testConnection.run();
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());
	}
}// class connectionTest

/******************************** Test for multilayer perceptron ********************************
* TODO: Header aus Kommentar, wemm wirklich fertig
************************************************************************************************/
class multilayerPerceptronTest {
	// Testcase:	inputs, outputs, hiddenNeuronsPerLayer und hiddenlayers < 1
	//				Input-Topologien:	one:	nrInputNeurons > hiddenNeuronsPerLayer
	//												(inputConnWeights.length % hiddenNeuronsPerLayer) != 0
	//											nrInputNeurons = hiddenNeuronsPerLayer
	//											nrInputNeurons < hiddenNeuronsPerLayer
	//												(inputConnWeights.length % nrInputNeurons) != 0
	//									twoGroups:
	//												(hiddenNeuronsPerLayer % 2) != 0)
	//												(nrInputNeurons % 2) != 0
	//												inputNeurons.length/2 geht auf
	//												inputNeurons.length/2 geht nicht auf
	//									each
	//				hiddenlayers = 1, hiddenlayers > 1
	//				Hidden-Topologien:	one
	//									cross
	//									zigzag
	//									each
	//				Output-Topologien:	each
	//									one:	hiddenNeuronsPerLayer >= nrOutputNeurons
	//												(hiddenNeuronsPerLayer % nrOutputNeurons) != 0
	//											hiddenNeuronsPerLayer = nrOutputNeurons
	//											hiddenNeuronsPerLayer < nrOutputNeurons
	//												(nrOutputNeurons % hiddenNeuronsPerLayer) != 0
	//				run:	
	//						inputVector.length != inputNeurons.length
	
private
	MultiLayerPerceptron multiLayerPerceptron;
	int numberInputs;
	int numberOutputs;
	int numberHiddenNeurons;
	int numberHiddenLayers;
	float[] inputVector;
	float[] outputVector;
	String inputTopology;
	String hiddenTopology;
	String outputTopology;
	
public	
	// Constructor
	multilayerPerceptronTest(){
	}
	
	void runTest(){
		
		for (int testCase = 0; testCase < 25; testCase++){
			switch (testCase){
			// inputs, outputs, hiddenNeuronsPerLayer und hiddenlayers < 1
			case 0:
				numberInputs = numberHiddenNeurons = numberHiddenLayers = numberOutputs = 0;
				inputTopology = hiddenTopology = "each";
				outputTopology = "one";
				break;
				
			// Input topology: one, nrInputNeurons > hiddenNeuronsPerLayer, hiddenlayers > 1,
			// hiddenNeuronsPerLayer = nrOutputNeurons
			case 1:
				inputTopology = "one";
				numberInputs = 4;
				numberHiddenNeurons = 2;
				numberHiddenLayers = 2;
				numberOutputs = 2;
				break;
			
			// (inputConnWeights.length % hiddenNeuronsPerLayer) != 0
			// hiddenNeuronsPerLayer > nrOutputNeurons, (hiddenNeuronsPerLayer % nrOutputNeurons) != 0
			case 2:
				numberHiddenNeurons = 3;
				break;

			// nrInputNeurons = hiddenNeuronsPerLayer, (hiddenNeuronsPerLayer % nrOutputNeurons) == 0
			case 3:
				numberHiddenNeurons = 4;
				break;
				
			// nrInputNeurons < hiddenNeuronsPerLayer
			case 4:
				numberHiddenNeurons = 8;
				break;
				
			// (inputConnWeights.length % nrInputNeurons) != 0
			case 5:
				numberHiddenNeurons = 9;
				break;
				
			// Input topology: twoGroups, inputNeurons.length/2 geht auf
			case 6:
				inputTopology = "twoGroups";
				numberHiddenNeurons = 8;
				break;
				
			// (hiddenNeuronsPerLayer % 2) != 0)
			case 7:
				numberHiddenNeurons = 9;
				break;
				
			// (nrInputNeurons % 2) != 0 und inputNeurons.length/2 geht nicht auf
			case 8:
				numberInputs = 5;
				break;
				
			// hiddenLayers = 1
			case 9:
				numberHiddenLayers = 1;
				break;

			// Hidden topology:	one
			case 10:
				hiddenTopology = "one";
				numberHiddenLayers = 2;
				break;
				
			// Hidden topology:	cross
			case 11:
				hiddenTopology = "cross";
				break;
				
			// Hidden topology:	zigzag
			case 12:
				hiddenTopology = "zigzag";
				break;
				
			// Output topology:	each
			case 13:
				outputTopology = "each";
				break;
				
			// Output topology:	one, hiddenNeuronsPerLayer < nrOutputNeurons
			//						 (nrOutputNeurons % hiddenNeuronsPerLayer) == 0
			case 14:
				outputTopology = "one";
				numberHiddenNeurons = 4;
				numberOutputs = 8;
				break;
			
			// case 15: (nrOutputNeurons % hiddenNeuronsPerLayer) != 0
			default:
				numberOutputs = 7;
				break;		
			}

			// inputVector.length != inputNeurons.length
			inputVector = new float[8];
			
			outputVector = new float[numberOutputs];
			
			for (int i = 0; i < inputVector.length; i++){
				inputVector[i] = 0;
			}
			
			System.out.println("Test case " + testCase + ":");
			System.out.println("\tMultiLayerPerceptron("+numberInputs+", "+numberHiddenNeurons+", "
								+numberHiddenLayers+", "+numberOutputs+", "+inputTopology+", "
								+hiddenTopology+", "+outputTopology+")");
			
			multiLayerPerceptron = new MultiLayerPerceptron(numberInputs, numberHiddenNeurons, numberHiddenLayers
													, numberOutputs, inputTopology, hiddenTopology, outputTopology);
			
			outputVector = multiLayerPerceptron.run(inputVector);
			
			System.out.print("\t\toutputVector: ");
			for (int i = 0; i < outputVector.length; i++){
				System.out.print("[" + outputVector[i] + "]");
			}
			
			System.out.println("\n--------------------------------------------------------");
		}// for (testCase)
	}// runTest()
}// class multilayerPerceptronTest

/****************************** Test for backpropagation training *******************************
* Builts a multilayer perceptron and executes the training function
************************************************************************************************/
class backpropagationTest {
	// TODO:	trainingOutVector.length != outputNeurons.length
	//			print aller Zwischenergebnisse
	
private
	MultiLayerPerceptron[]  perceptron;
	float[][] trainingInVector;
	float[][] trainingOutVector;
	int[] numberInputs;
	int[] numberOutputs;
	int[] numberHiddenNeurons;
	int[] numberHiddenLayers;

public
	// Constructor
	backpropagationTest(){
		numberInputs = new int[4];
		numberOutputs = new int[4];
		numberHiddenNeurons = new int[4];
		numberHiddenLayers = new int[4];
		
		trainingInVector = new float[2][16];
		trainingOutVector = new float[2][16];
		
		perceptron = new MultiLayerPerceptron[6];
	}

	void runTestTopologies(){
		numberInputs[0] = numberOutputs[0] = 2;
		numberInputs[1] = numberOutputs[1] = 4;
		numberInputs[2] = numberOutputs[2] = 8;
		numberInputs[3] = numberOutputs[3] = 16;
			
		numberHiddenNeurons[0] = 4;		numberHiddenLayers[0] = 1;
		numberHiddenNeurons[1] = 8;		numberHiddenLayers[1] = 2;
		numberHiddenNeurons[2] = 16;	numberHiddenLayers[2] = 4;
		numberHiddenNeurons[3] = 32;	numberHiddenLayers[3] = 5;
		
		trainingInVector[0][0]= 0.5f;
		trainingInVector[0][1]= 0;
		trainingInVector[0][2]= 1;
		trainingInVector[0][3]= 1;
		trainingInVector[0][4]= 1;
		trainingInVector[0][5]= 0;
		trainingInVector[0][6]= 0.5f;
		trainingInVector[0][7]= 1;
		trainingInVector[0][8]= 0.5f;
		trainingInVector[0][9]= 0;
		trainingInVector[0][10]= 1;
		trainingInVector[0][11]= 1;
		trainingInVector[0][12]= 1;
		trainingInVector[0][13]= 0;
		trainingInVector[0][14]= 0.5f;
		trainingInVector[0][15]= 1;
			
		trainingOutVector[0][0] = 0.1f;
		trainingOutVector[0][1] = 0.9f;
		trainingOutVector[0][2] = 0.2f;
		trainingOutVector[0][3] = 0.3f;
		trainingOutVector[0][4] = 0.4f;
		trainingOutVector[0][5] = 0.5f;
		trainingOutVector[0][6] = 0.6f;
		trainingOutVector[0][7] = 0.7f;	
		trainingOutVector[0][8] = 0.8f;
		trainingOutVector[0][9] = 0.9f;
		trainingOutVector[0][10] = 1;
		trainingOutVector[0][11] = 0.8f;
		trainingOutVector[0][12] = 0.6f;
		trainingOutVector[0][13] = 0.4f;
		trainingOutVector[0][14] = 0.2f;
		trainingOutVector[0][15] = 0.1f;
		
		int[] trials = new int[perceptron.length * numberInputs.length];
		int trial = 0;
		
		for (int run = 0; run < numberInputs.length-1; run++){
			perceptron[0] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run],
														numberHiddenLayers[run], numberOutputs[run], "each", "each", "one");
			perceptron[1] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run],
														numberHiddenLayers[run], numberOutputs[run], "one", "each", "one");
			perceptron[2] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run],
														numberHiddenLayers[run], numberOutputs[run], "twoGroups", "each", "one");
			perceptron[3] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run],
														numberHiddenLayers[run], numberOutputs[run], "one", "one", "one");
			perceptron[4] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run],
														numberHiddenLayers[run], numberOutputs[run], "each", "cross", "each");
			perceptron[5] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run],
														numberHiddenLayers[run], numberOutputs[run], "each", "zigzag", "each");
			
			System.out.println("MultiLayerPerceptron("+numberInputs[run]+", "+numberHiddenNeurons[run]+
									", "+numberHiddenLayers[run]+", "+numberOutputs[run]+")");
		
			for (int exec = 0; exec < perceptron.length; exec++, trial++){
				trials[trial] = perceptron[exec].training(trainingInVector[0], trainingOutVector[0], 0.001f, 10000);
				System.out.println("     --------------------------------------------------");
			}
			
			for (int print = trial - perceptron.length; print < trial; print++){
				if (print > trial - perceptron.length)
					System.out.print(", ");
					
				System.out.print("trials["+print+"]: "+ trials[print]);
			}
			
			System.out.println("\n------------------------------------------------------------------------------------------");
		}// for(int perc)
		
/*		// Shiften einer 1 durch das gesamte Array
		for (int i = 0; i < numberInputs; i++){
			System.out.print("trainingInVector: ");
			
			for (int j = 0; j < numberInputs; j++){
				if (j == i)
					trainingInVector[j]= 1;
				else
					trainingInVector[j]= 0;
				
				System.out.print("["+trainingInVector[j]+"]");
			}
		
			System.out.print("\n");
			
			float out[] = perceptron.run(trainingInVector);
			
			for (int k = 0; k < numberOutputs; k++)
				System.out.println("out["+k+"]: " + out[k]);
		}
*/
//		}
	}// runTestTopologies()

	void runTestTrainings(){
		int[] trials = new int[3];
		
		numberInputs[0] = numberOutputs[0] = 4;
		numberHiddenNeurons[0] = 8;		numberHiddenLayers[0] = 3;
		
		trainingInVector[0][0]= 0;		trainingInVector[1][0]= 1;
		trainingInVector[0][1]= 0;		trainingInVector[1][1]= 0;
		trainingInVector[0][2]= 0;		trainingInVector[1][2]= 0;
		trainingInVector[0][3]= 1;		trainingInVector[1][3]= 0;
		
		trainingOutVector[0][0] = 0.1f;	trainingOutVector[1][0] = 0.9f;
		trainingOutVector[0][1] = 0.2f;	trainingOutVector[1][1] = 0.8f;
		trainingOutVector[0][2] = 0.3f;	trainingOutVector[1][2] = 0.7f;
		trainingOutVector[0][3] = 0.4f;	trainingOutVector[1][3] = 0.6f;
		
		perceptron[0] = new MultiLayerPerceptron(numberInputs[0], numberHiddenNeurons[0],
				numberHiddenLayers[0], numberOutputs[0], "each", "each", "one");
		
		System.out.println("MultiLayerPerceptron("+numberInputs[0]+", "+numberHiddenNeurons[0]+
								", "+numberHiddenLayers[0]+", "+numberOutputs[0]+")");
		
		for (int exec = 0; exec < 2; exec++){
			trials[exec] = perceptron[0].training(trainingInVector[exec], trainingOutVector[exec], 0.001f, 10000);
		
			System.out.println("     --------------------------------------------------");
			System.out.print("\ttrials["+exec+"]: "+ trials[exec]);
		}
		
		System.out.println("\n------------------------------------------------------------------------------------------");
		
		float outVector[] = perceptron[0].run(trainingInVector[0]);
		
		// For debug purposes, print output vector
		System.out.println("\ttrainingOut\t\t| outVector");
		for (int deb = 0; deb < outVector.length; deb++){
			System.out.print("\t[" + deb + "]: " + (trainingOutVector[0][deb]));
			System.out.println("\t| [" + deb + "]: " + outVector[deb]);
		}
	}// runTestTrainings()
}// class backpropagationTest

/**************************** Test for finding the fastest network *****************************
* Builds all possible multilayer perceptron topologies for
* 	1 input, odd and even number of inputs,
* 	1 hidden neuron, odd and even number of hidden neurons
* 	1 and more hidden layers
* 	1 output, odd and even number of outputs
* Each perceptron is executed and the number of trials are compared
************************************************************************************************/
class findFastestNet {
private
	MultiLayerPerceptron  perceptron;
	int[] numberInputs;
	int[] numberOutputs;
	int[] numberHiddenNeurons;
	int[] numberHiddenLayers;
	String[] inputTopologies;
	String[] hiddenTopologies;
	String[] outputTopologies;
	String[][] topologies;
	String[] topoSort;
	int run;
	int totalRuns;
	int[] trials;
	int trialPos;
	int trialsSort;
	float[] trainingInVector;
	float[] trainingOutVector;	
	
public
	//Constructor
	findFastestNet(){
		numberInputs = new int[3];
		numberOutputs = new int[3];
		numberHiddenNeurons = new int[3];
		numberHiddenLayers = new int[2];
		inputTopologies = new String[3];
		hiddenTopologies = new String[4];
		outputTopologies = new String[2];
		trials = new int[inputTopologies.length * hiddenTopologies.length * outputTopologies.length];
		topologies = new String[trials.length][3];
		topoSort = new String[3];
		trainingInVector = new float[8];
		trainingOutVector = new float[8];
	}

	void runTest(){
		numberInputs[0] = numberOutputs[0] = numberHiddenNeurons[0] = numberHiddenLayers[0] = 1;
		numberInputs[1] = numberOutputs[1] = numberHiddenNeurons[1] = 7;
		numberInputs[2] = numberOutputs[2] = numberHiddenNeurons[2] = 8;
		numberHiddenLayers[1] = 5;
		
		inputTopologies[0] = hiddenTopologies[0] = outputTopologies[0] = "one";
		inputTopologies[1] = "twoGroups";
		inputTopologies[2] = hiddenTopologies[1] = outputTopologies[1] = "each";
		hiddenTopologies[2] = "cross";
		hiddenTopologies[3] = "zigzag";
		
		trainingInVector[0] = 0;	trainingOutVector[0] = 0.99f;
		trainingInVector[1] = 1;	trainingOutVector[1] = 0.1f;
		trainingInVector[2] = 0.1f;	trainingOutVector[2] = 0.8f;
		trainingInVector[3] = 0.9f;	trainingOutVector[3] = 0.2f;
		trainingInVector[4] = 0.2f;	trainingOutVector[4] = 0.7f;
		trainingInVector[5] = 0.8f;	trainingOutVector[5] = 0.3f;
		trainingInVector[6] = 0.3f;	trainingOutVector[6] = 0.6f;
		trainingInVector[7] = 0.7f;	trainingOutVector[7] = 0.4f;
		
		run = 0;
		totalRuns = numberInputs.length * numberOutputs.length * numberHiddenNeurons.length 
						* numberHiddenLayers.length * inputTopologies.length * hiddenTopologies.length 
						* outputTopologies.length;
		
		for (int nrI = 0; nrI < numberInputs.length; nrI++){
			
			for (int nrO = 0; nrO < numberOutputs.length; nrO++){
				
				for (int nrH = 0; nrH < numberHiddenNeurons.length; nrH++){
					
					for (int nrL = 0; nrL < numberHiddenLayers.length; nrL++){
						
						System.out.println("MultiLayerPerceptron("+numberInputs[nrI]+", "+numberHiddenNeurons[nrH]
																	+", "+numberHiddenLayers[nrL]+", "+numberOutputs[nrO]+"):");
						
						for (int inTop = 0; inTop < inputTopologies.length; inTop++){
							
							for (int hidTop = 0; hidTop < hiddenTopologies.length; hidTop++){
								
								for (int outTop = 0; outTop < outputTopologies.length; outTop++){
									
//									run++;
//									System.out.print("\tRun: " + run + "/" + totalRuns);
									
									perceptron = new MultiLayerPerceptron(numberInputs[nrI], numberHiddenNeurons[nrH]
																			, numberHiddenLayers[nrL], numberOutputs[nrO]
																			, inputTopologies[inTop], hiddenTopologies[hidTop]
																			, outputTopologies[outTop]);
									
									topologies[trialPos][0] = inputTopologies[inTop];
									topologies[trialPos][1] = hiddenTopologies[hidTop];
									topologies[trialPos][2] = outputTopologies[outTop];
									
//									System.out.println("\tTopology: "+inputTopologies[inTop]+", "
//														+hiddenTopologies[hidTop]+", "+outputTopologies[outTop]);
									
									trials[trialPos] = perceptron.training(trainingInVector, trainingOutVector, 0.001f, 10000);
									
//									System.out.println("\t\tTrials: " + trials[trialPos]);
									
									trialPos++;
								}// for (hiddenTopologies)
							}// for (hiddenTopologies)
						}// for (inputTopologies)
						
						System.out.println("\t------------------------------------------------------------------------");
						
						for (int i = 0; i < trials.length; i++){
							
							for (int j = (trials.length-1); j > 0; j--){
								
								if (trials[j-1] > trials[j]){
									
									trialsSort = trials[j];
									trials[j] = trials[j-1];
									trials[j-1] = trialsSort;
									
									for (int k = 0; k < 3; k++){
										topoSort[k] = topologies[j][k];
										topologies[j][k] = topologies[j-1][k];
										topologies[j-1][k] = topoSort[k];
									}
								}
							}	
						}
						
						System.out.print("\tFastest:\t");
						
						for (int i = 0; i < 3; i++)
							System.out.print(topologies[0][i] + ", ");
						
						System.out.println("\tTrials: " + trials[0]);
						
						for (int j = 1; j < trials.length; j++){
							
							if (trials[j] == trials[0]){
								
								System.out.print("\t\t\t");
								
								for (int i = 0; i < 3; i++)
									System.out.print(topologies[j][i] + ", ");
								
								System.out.println("\tTrials: " + trials[j]);
								
							}
						}
						
						System.out.println("--------------------------------------------------------------------------------");
						
						trialPos = 0;
					}// for (numberHiddenLayers)
				}// for (numberHiddenNeurons)
			}// for (numberOutputs)
		}// for (numberInputs)
		System.out.println("(done)");
	}
}// class findFastestNet