/**************************** Tests for artificial neural network *******************************
* 
************************************************************************************************/

public class aNN_Tests {
	
	/**********************************************
	* main function:
	* just contains the test objects
	**********************************************/
	public static void main(String[] args){
		
		// TODO:	Tests nochmal durchgehen
		//			Ergebnisse in txt-files
		
		// Unit test for class Neuron
		// TODO: Sieht gut aus, genaue Auswertung fehlt
//		neuronTest neuronTest = new neuronTest();
//		neuronTest.runTest();
		
		// Unit test for class Connection
//		connectionTest connectionTest = new connectionTest();
//		connectionTest.runTest();
		
		// Test for whole network, needs debug prints, look at commit when passed, in class
		// otherwise you'll just see the output vector
		multilayerPerceptronTest multilayerPerceptronTest = new multilayerPerceptronTest();
		multilayerPerceptronTest.runTest();
		
		// Erst weiter, wenn multiLayerPerceptronTest komplett fertig!
		// Test for training using backpropagation
		// TODO:	System.out.print mit trial = 1 für weight delta berechnung um nachzuvollziehen ob nur thresholds berechnet werden
		//			Tests mit beiden training() (mit und ohne coefficient)
//		backpropagationTest backpropagationTest = new backpropagationTest();
//		backpropagationTest.runTest();
		
//		findFastestNet findFastestNet = new findFastestNet();
//		findFastestNet.runTest();
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
			
			for (int thresh = 0; thresh < thresholds.length; thresh++){
				
				testNeuron = new Neuron(nrInputs);
				
				if (thresh == 0)
					System.out.println("\tTesting constructor:\tgetOutput: " + testNeuron.getOutput());
				
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
* TODO: Testcases neu erstellen
* 		System.out.print-Tabelle für alle cases
* 		Header aus Kommentar
************************************************************************************************/
class multilayerPerceptronTest {
	// Testcase:	topologien = null
	//				inputs, outputs, hiddenNeuronsPerLayer und hiddenlayers < 1
	//				Input-Topologien:	one:	nrInputNeurons > hiddenNeuronsPerLayer
	//												(inputConnWeights.length % hiddenNeuronsPerLayer) != 0
	//											nrInputNeurons = hiddenNeuronsPerLayer
	//											nrInputNeurons < hiddenNeuronsPerLayer
	//												(inputConnWeights.length % nrInputNeurons) != 0
	//											inputNeurons.length % hiddenNeurons.get(0).length) != 0
	//												Even number of input neurons
	//												Odd number of neurons
	//											Whole-number ratio
	//									twoGroups:
	//												nrInputs = 1;
	//												nrHiddenNeurons = 1;
	//												(hiddenNeuronsPerLayer % 2) != 0)
	//												inputNeurons.length/2 geht auf
	//												inputNeurons.length/2 geht nicht auf
	//												nrInputNeurons >=< hiddenNeuronsPerLayer
	//									each:	nrInputNeurons >=< hiddenNeuronsPerLayer
	//				hiddenlayers = 1, hiddenlayers > 1
	//				Hidden-Topologien:	one:	nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
	//											(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
	//									cross:	nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
	//											(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
	//									zigzag:	nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
	//											(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
	//									each:	nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
	//											nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
	//											(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
	//				Output-Topologien:	one:	hiddenNeuronsPerLayer > nrOutputNeurons
	//											hiddenNeuronsPerLayer = nrOutputNeurons
	//											hiddenNeuronsPerLayer < nrOutputNeurons
	//												(nrOutputNeurons % hiddenNeuronsPerLayer) != 0
	//									each:	hiddenNeuronsPerLayer > nrOutputNeurons
	//											hiddenNeuronsPerLayer = nrOutputNeurons
	//											hiddenNeuronsPerLayer < nrOutputNeurons
	//												(nrOutputNeurons % hiddenNeuronsPerLayer) != 0					
	//				run:	
	//						inputVector.length != inputNeurons.length
	//				Testen der anderen Konstruktoren
	
private
	MultiLayerPerceptron multiLayerPerceptron;
	int numberInputs;
	int numberOutputs;
	int[] numberHiddenNeurons;
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
		
		MultiLayerPerceptron.multiLayerPerceptronTest = true;
		
		for (int testCase = 0; testCase <= 3/*28*/; testCase++){
			switch (testCase){
			// Topologies = null
			case 0:
				numberInputs = numberOutputs = 2;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 2;
				break;
				
			// Inputs, outputs, hiddenNeuronsPerLayer und hiddenlayers < 1
			case 1:				
				numberInputs = numberOutputs = 0;
				numberHiddenNeurons = new int[1];
				inputTopology = hiddenTopology = "each";
				outputTopology = "each";
				break;
				
			// Input topology: one, nrInputNeurons > hiddenNeuronsPerLayer, hiddenlayers > 1,
			// hiddenNeuronsPerLayer = nrOutputNeurons
			// inputNeurons.length % hiddenNeurons.get(0).length = Whole-number ratio
			case 2:
				inputTopology = "one";
				numberInputs = 4;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 2;
				numberOutputs = 2;
				break;
			
			// (inputConnWeights.length % hiddenNeuronsPerLayer) != 0
			// hiddenNeuronsPerLayer > nrOutputNeurons, (hiddenNeuronsPerLayer % nrOutputNeurons) != 0
			// inputNeurons.length % hiddenNeurons.get(0).length != 0: Even number of input neurons
			case 3:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 3;
				break;
				
			// inputNeurons.length % hiddenNeurons.get(0).length != 0: Odd number of input neurons
			case 4:
				numberInputs = 5;
				break;

			// nrInputNeurons = hiddenNeuronsPerLayer, (hiddenNeuronsPerLayer % nrOutputNeurons) == 0
			case 5:
				numberInputs = 4;
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 4;
				break;
				
			// nrInputNeurons < hiddenNeuronsPerLayer
			case 6:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 8;
				break;
				
			// (inputConnWeights.length % nrInputNeurons) != 0
			case 7:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 9;
				break;
				
			// Input topology: twoGroups, nrInputs = 1
			case 8:
				inputTopology = "twoGroups";
				numberInputs = 1;
				break;
				
			// nrHiddenNeurons = 1
			case 9:
				numberInputs = 4;
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 1;
				break;
				
			// inputNeurons.length/2 geht auf, nrInputNeurons < hiddenNeuronsPerLayer
			case 10:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 8;
				break;
				
			// (hiddenNeuronsPerLayer % 2) != 0)
			case 11:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 9;
				break;
				
			// (nrInputNeurons % 2) != 0 und inputNeurons.length/2 geht nicht auf
			case 12:
				numberInputs = 5;
				break;

			// nrInputNeurons = hiddenNeuronsPerLayer
			case 13:
				numberInputs = 8;
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 8;
				break;
				
			// nrInputNeurons > hiddenNeuronsPerLayer
			case 14:
				numberInputs = 9;
				break;
				
			// hiddenLayers = 1
			case 15:
				numberInputs = 4;
				numberHiddenNeurons = new int[1];
				numberHiddenNeurons[0] = 9;
				break;

			// Hidden topology:	one, nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
			case 16:
				hiddenTopology = "one";
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = 8;
				numberHiddenNeurons[1] = 5;
				break;
				
			// nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
			case 17:
				numberHiddenNeurons[0] = 5;
				numberHiddenNeurons[1] = 5;				
				break;
				
			// nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
			case 18:
				numberHiddenNeurons[0] = 5;
				numberHiddenNeurons[1] = 8;				
				break;
				
			// Hidden topology:	cross, nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
			case 19:
				hiddenTopology = "cross";
				numberHiddenNeurons[0] = 10;
				numberHiddenNeurons[1] = 4;
				break;
				
			// nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
			case 20:
				numberHiddenNeurons[0] = 4;
				numberHiddenNeurons[1] = 4;				
				break;
				
			// nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
			case 21:
				numberHiddenNeurons[0] = 4;
				numberHiddenNeurons[1] = 10;				
				break;
				
			// Hidden topology:	zigzag, nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
			case 22:
				hiddenTopology = "zigzag";
				numberHiddenNeurons[0] = 15;
				numberHiddenNeurons[1] = 5;
				break;
				
			// nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
			case 23:
				numberHiddenNeurons[0] = 5;
				numberHiddenNeurons[1] = 5;				
				break;
				
			// nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
			case 24:
				numberHiddenNeurons[0] = 5;
				numberHiddenNeurons[1] = 15;				
				break;
				
			// Output topology:	one, hiddenNeuronsPerLayer > nrOutputNeurons
			case 25:
				outputTopology = "one";
				break;
				
			// hiddenNeuronsPerLayer = nrOutputNeurons
			case 26:
				numberHiddenNeurons[1] = 4;
				numberOutputs = 4;
				break;
				
			// hiddenNeuronsPerLayer < nrOutputNeurons
			case 27:
				numberOutputs = 8;
				break;
				
			// Output topology:	each, hiddenNeuronsPerLayer < nrOutputNeurons
			//						 (nrOutputNeurons % hiddenNeuronsPerLayer) == 0
			default:
				outputTopology = "each";
			}

			// inputVector.length != inputNeurons.length
			inputVector = new float[8];
			
			outputVector = new float[numberOutputs];
			
			for (int i = 0; i < inputVector.length; i++){
				inputVector[i] = 0;
			}
			
			System.out.println("Test case " + testCase + ":");
			System.out.print("\tMultiLayerPerceptron("+numberInputs+",");
			for (int i = 0; i < numberHiddenNeurons.length; i++)
				System.out.print("[" + numberHiddenNeurons[i] +"],");
			
			System.out.println(numberOutputs+", "+inputTopology+", "+hiddenTopology+", "+outputTopology+")");
			
			multiLayerPerceptron = new MultiLayerPerceptron(numberInputs, numberHiddenNeurons, 
													numberOutputs, inputTopology, hiddenTopology, outputTopology);
			
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
* Builts a multilayer perceptron with the highest number of connections (each, each, each)
* and executes the training function
* Each interim result is printed
************************************************************************************************/
class backpropagationTest {
	// TODO:	trainingOutVector.length != outputNeurons.length
	//			print aller Zwischenergebnisse
	
private
	MultiLayerPerceptron  perceptron;
	int[] hiddenNeurons;
	float[] trainingInVector;
	float[] trainingOutVector;
	int trials;

public
	// Constructor
	backpropagationTest(){
		hiddenNeurons = new int[5];
		
		for (int h = 0; h < hiddenNeurons.length; h++)
			hiddenNeurons[h] = 32;
			
		trainingInVector = new float[16];
		trainingOutVector = new float[16];
		
		perceptron = new MultiLayerPerceptron(16, hiddenNeurons, 16, "each", "each", "each");
	}

	void runTest(){
		trainingInVector[0]= 0.5f;
		trainingInVector[1]= 0;
		trainingInVector[2]= 1;
		trainingInVector[3]= 1;
		trainingInVector[4]= 1;
		trainingInVector[5]= 0;
		trainingInVector[6]= 0.5f;
		trainingInVector[7]= 1;
		trainingInVector[8]= 0.5f;
		trainingInVector[9]= 0;
		trainingInVector[10]= 1;
		trainingInVector[11]= 1;
		trainingInVector[12]= 1;
		trainingInVector[13]= 0;
		trainingInVector[14]= 0.5f;
		trainingInVector[15]= 1;
			
		trainingOutVector[0] = 0.1f;
		trainingOutVector[1] = 0.9f;
		trainingOutVector[2] = 0.2f;
		trainingOutVector[3] = 0.3f;
		trainingOutVector[4] = 0.4f;
		trainingOutVector[5] = 0.5f;
		trainingOutVector[6] = 0.6f;
		trainingOutVector[7] = 0.7f;	
		trainingOutVector[8] = 0.8f;
		trainingOutVector[9] = 0.9f;
		trainingOutVector[10] = 0.9f;
		trainingOutVector[11] = 0.8f;
		trainingOutVector[12] = 0.6f;
		trainingOutVector[13] = 0.4f;
		trainingOutVector[14] = 0.2f;
		trainingOutVector[15] = 0.1f;
		
//			System.out.println("MultiLayerPerceptron("+numberInputs[run]+", "+numberHiddenNeurons[run]+
//									", "+numberHiddenLayers[run]+", "+numberOutputs[run]+")");
		
			trials = perceptron.training(trainingInVector, trainingOutVector, 0.001f, 10000);
//			System.out.println("     --------------------------------------------------");
			
			System.out.print("trials: "+ trials);
			
			System.out.println("\n------------------------------------------------------------------------------------------");
	}// runTest()

		// Trains the perceptron 2 times with 2 different input vectors,
		// then prints the output vector by using 1st input vector again
/*		void runTestTrainings(){
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
*/
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
	int[] numberHidNeurons;
	float[] trainingInVector;
	float[] trainingOutVector;
	String[] fastestString;	
	
public
	//Constructor
	findFastestNet(){
		numberInputs = new int[3];
		numberOutputs = new int[3];
		numberHiddenNeurons = new int[3];
		numberHiddenLayers = new int[2];
		trainingInVector = new float[8];
		trainingOutVector = new float[8];
		fastestString = new String[4];
	}

	void runTest(){
		numberInputs[0] = numberOutputs[0] = numberHiddenNeurons[0] = numberHiddenLayers[0] = 1;
		numberInputs[1] = numberOutputs[1] = numberHiddenNeurons[1] = 7;
		numberInputs[2] = numberOutputs[2] = numberHiddenNeurons[2] = 8;
		numberHiddenLayers[1] = 6;
		
		trainingInVector[0] = 0;	trainingOutVector[0] = 0.9f;
		trainingInVector[1] = 1;	trainingOutVector[1] = 0.1f;
		trainingInVector[2] = 0.1f;	trainingOutVector[2] = 0.8f;
		trainingInVector[3] = 0.9f;	trainingOutVector[3] = 0.2f;
		trainingInVector[4] = 0.2f;	trainingOutVector[4] = 0.7f;
		trainingInVector[5] = 0.8f;	trainingOutVector[5] = 0.3f;
		trainingInVector[6] = 0.3f;	trainingOutVector[6] = 0.6f;
		trainingInVector[7] = 0.7f;	trainingOutVector[7] = 0.4f;
		
		for (int nrI = 0; nrI < numberInputs.length; nrI++){
			
			for (int nrO = 0; nrO < numberOutputs.length; nrO++){
				
				for (int nrH = 0; nrH < numberHiddenNeurons.length; nrH++){
					
					for (int nrL = 0; nrL < numberHiddenLayers.length; nrL++){
						
						System.out.println("MultiLayerPerceptron("+numberInputs[nrI]+", ["+numberHiddenLayers[nrL]
																	+"]["+numberHiddenNeurons[nrH]+"], "+numberOutputs[nrO]+"):");
						
						numberHidNeurons = new int[numberHiddenLayers[nrL]];
						
						for (int i = 0; i < numberHidNeurons.length; i++){
							numberHidNeurons[i] = numberHiddenNeurons[nrH];
						}
						
						perceptron = new MultiLayerPerceptron(numberInputs[nrI], numberHidNeurons, numberOutputs[nrO]);
						
						fastestString = perceptron.findFastest(trainingInVector, trainingOutVector, 0.001f, 10000);
						
						System.out.print("\tFastest: ");
						for (int s = 0; s < fastestString.length - 1; s++)
							System.out.print(fastestString[s]+ ", ");
						
						System.out.println("Trials: " + fastestString[3]);
						
					}// for (numberHiddenLayers)
				}// for (numberHiddenNeurons)
			}// for (numberOutputs)
		}// for (numberInputs)
		System.out.println("(done)");
	}
}// class findFastestNet