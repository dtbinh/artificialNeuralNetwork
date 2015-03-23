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
//		multilayerPerceptronTest multilayerPerceptronTest = new multilayerPerceptronTest();
//		multilayerPerceptronTest.runTest();
		
		// Erst weiter, wenn multiLayerPerceptronTest komplett fertig!
		// Test for training using backpropagation
		// TODO:	System.out.print mit trial = 1 für weight delta berechnung um nachzuvollziehen ob nur thresholds berechnet werden
		//			Tests mit beiden training() (mit und ohne coefficient)
		backpropagationTest backpropagationTest = new backpropagationTest();
		backpropagationTest.runTest();
		
//		findFastestNet findFastestNet = new findFastestNet();
//		findFastestNet.runTest();
		
		saveLoadNet saveLoadNet = new saveLoadNet();
		saveLoadNet.runTest();
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
		testConnection = new Connection(neuronFrom, neuronTo, 0, 8, 0);
	}

	void runTest(){
//		neuronFrom.setThreshold(0);
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());
		
		neuronFrom.setInput(0, 16);
//		neuronTo.setThreshold(0);
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());

		testConnection.run();
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());
		
		testConnection.addWeightDelta(8);
		testConnection.run();
		
		System.out.println("neuronTo Output: " + neuronTo.getOutput());
	}
}// class connectionTest

/******************************** Test for multilayer perceptron ********************************
* Test of the constructor
* To test almost every possible combination the following settigns are done:
*	topologies = null
*	inputs, outputs, nrHiddenNeurons und hiddenlayers < 1
*	Input-topologies:	one:	nrInputNeurons > nrHiddenNeurons
*									nrInputNeurons % nrHiddenNeurons[0] != 0
*									(nrInputNeurons % nrHiddenNeurons[0])/2 < 1
*									(nrInputNeurons % nrHiddenNeurons[0]) = even
*									(nrInputNeurons % nrHiddenNeurons[0]) = odd
*									Even number of input neurons
*									Odd number of input neurons
*									Whole-number ratio
*								nrInputNeurons = nrHiddenNeurons
*								nrInputNeurons < nrHiddenNeurons
*									nrHiddenNeurons[0] % nrInputNeurons != 0
*									(nrHiddenNeurons[0] % nrInputNeurons)/2 < 1
*									(nrHiddenNeurons[0] % nrInputNeurons) = odd
*									(nrHiddenNeurons[0] % nrInputNeurons) = even
*									Even number of hidden neurons
*									Odd number of hidden neurons
*									Whole-number ratio
*						twoGroups:
*									nrInputs = 1;
*									nrHiddenNeurons = 1;
*									(nrHiddenNeurons % 2) != 0)
*									Even number of input neurons
*									Odd number of input neurons
*									nrInputNeurons >=< nrHiddenNeurons
*						each:	nrInputNeurons >=< nrHiddenNeurons
* 	hiddenlayers = 1, hiddenlayers > 1
*	Hidden-Topologien:	one:	nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
*								nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
*								nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
*									nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1] != 0
*									(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1])/2 < 1
*									(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) = even
*									(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) = odd
*									Even number of nrHiddenNeurons[lyr]
*									Odd number of nrHiddenNeurons[lyr]
*									Whole-number ratio
*						each:	nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
*								nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
*								nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
*								(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
*	Output-Topologien:	one:	nrHiddenNeurons > nrOutputNeurons
*									nrHiddenNeurons % nrOutputNeurons != 0
*									(nrHiddenNeurons % nrOutputNeurons)/2 < 1
*									(nrHiddenNeurons % nrOutputNeurons) = even
*									(nrHiddenNeurons % nrOutputNeurons) = odd
*									Even number of hidden neurons
*									Odd number of hidden neurons
*									Whole-number ratio
*								nrHiddenNeurons = nrOutputNeurons
*								nrHiddenNeurons < nrOutputNeurons
*									nrOutputNeurons % nrHiddenNeurons != 0
*									(nrOutputNeurons % nrHiddenNeurons)/2 < 1
*									(nrOutputNeurons % nrHiddenNeurons) = odd
*									(nrOutputNeurons % nrHiddenNeurons) = even
*									Even number of output neurons
*									Odd number of output neurons
*									Whole-number ratio
*						TODO: twoGroups
*						each:	nrHiddenNeurons >=< nrOutputNeurons
*	run:	
*			inputVector.length != inputNeurons.length
*	Testen der anderen Konstruktoren
************************************************************************************************/
class multilayerPerceptronTest {
	
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
		
		for (int testCase = 0; testCase <= 24; testCase++){
			switch (testCase){
			// Topologies = null
			case 0:
				numberInputs = numberOutputs = 2;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 2;
				break;
				
			// Inputs, outputs, nrHiddenNeurons and hiddenlayers < 1
			case 1:				
				numberInputs = numberOutputs = 0;
				numberHiddenNeurons = new int[1];
				inputTopology = hiddenTopology = "each";
				outputTopology = "each";
				break;
				
			// Input topology: one
			// nrInputNeurons > nrHiddenNeurons
			// nrInputNeurons % nrHiddenNeurons[0] = 0  (Whole-number ratio)
			// Even number of input neurons
			// hiddenlayers > 1
			// nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
			// nrHiddenNeurons = nrOutputNeurons
			case 2:
				inputTopology = "one";
				numberInputs = 4;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 2;
				numberOutputs = 2;
				break;
			
			// nrInputNeurons % nrHiddenNeurons[0] != 0,
			// (nrInputNeurons % nrHiddenNeurons[0])/2 < 1
			// nrHiddenNeurons > nrOutputNeurons
			// (nrHiddenNeurons % nrOutputNeurons) != 0
			// hiddenLayers = 1
			case 3:
				numberHiddenNeurons = new int[1];
				numberHiddenNeurons[0] = 3;
				break;

			// (nrInputNeurons % nrHiddenNeurons[0]) = even
			// Odd number of input neurons
			// nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
			// nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
			// Output topology: one
			// nrHiddenNeurons > nrOutputNeurons
			// nrHiddenNeurons % nrOutputNeurons = 0  (Whole-number ratio)
			// Even number of hidden neurons
			// TODO: Check results of output tests
			case 4:
				numberInputs = 5;
				numberHiddenNeurons = new int[4];
				numberHiddenNeurons[0] = numberHiddenNeurons[2] = 3;
				numberHiddenNeurons[1] = numberHiddenNeurons[3] = 4;
				outputTopology = "one";
				break;
				
			// (nrInputNeurons % nrHiddenNeurons[0]) = even
			// Even number of input neurons
			// Hidden topology: one
			// nrHiddenNeurons[lyr] >=< nrHiddenNeurons[lyr+1]
			// nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1] != 0
			// (nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1])/2 < 1
			// Even number of nrHiddenNeurons[lyr]
			// Odd number of nrHiddenNeurons[lyr]
			// nrHiddenNeurons % nrOutputNeurons != 0,
			// (nrHiddenNeurons % nrOutputNeurons)/2 < 1
			case 5:
				numberInputs = 6;
				hiddenTopology = "one";
				numberHiddenNeurons = new int[4];
				numberHiddenNeurons[0] = numberHiddenNeurons[3] = 4;
				numberHiddenNeurons[1] = numberHiddenNeurons[2] = 3;
				numberOutputs = 3;
				break;
				
			// (nrInputNeurons % nrHiddenNeurons[0]) = odd
			// Odd number of input neurons
			// (nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) = even
			// (nrHiddenNeurons % nrOutputNeurons) = even
			// Even number of hidden neurons
			case 6:
				numberInputs = 7;
				numberHiddenNeurons[0] = numberHiddenNeurons[2] = numberHiddenNeurons[3] = 4;
				numberHiddenNeurons[1] = 6;
				numberOutputs = 4;
				break;
				
			// (nrInputNeurons % nrHiddenNeurons[0]) = odd
			// Even number of input neurons
			// (nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) = odd
			// (nrHiddenNeurons % nrOutputNeurons) = even
			// Odd number of hidden neurons
			case 7:
				numberInputs = 8;
				numberHiddenNeurons[0] = numberHiddenNeurons[2] = numberHiddenNeurons[3] = 5;
				numberHiddenNeurons[1] = 8;
				numberOutputs = 3;
				break;
				
			// nrInputNeurons = nrHiddenNeurons
			// nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1] = 0  (Whole-number ratio)
			// (nrHiddenNeurons % nrOutputNeurons) = odd
			// Even number of hidden neurons
			case 8:
				numberHiddenNeurons[0] = numberHiddenNeurons[3] = 8;
				numberHiddenNeurons[1] = numberHiddenNeurons[2] = 4;
				numberOutputs = 5;
				break;
				
			// nrInputNeurons < nrHiddenNeurons
			// nrHiddenNeurons[0] % nrInputNeurons = 0  (Whole-number ratio)
			// Even number of hidden neurons
			// Hidden topology: cross
			// nrHiddenNeurons[lyr] >=< nrHiddenNeurons[lyr+1]
			// nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1] != 0
			// (nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0
			// Even number of nrHiddenNeurons[lyr]
			// Odd number of nrHiddenNeurons[lyr]
			// TODO: Check if results are correct
			// (nrHiddenNeurons % nrOutputNeurons) = odd
			// Odd number of hidden neurons
			case 9:
				numberInputs = 4;
//				hiddenTopology = "cross";
				numberHiddenNeurons = new int[5];
				numberHiddenNeurons[0] = numberHiddenNeurons[3] = 8;
				numberHiddenNeurons[1] = numberHiddenNeurons[2] = numberHiddenNeurons[4] = 7;
				numberOutputs = 4;
				break;
				
			// nrHiddenNeurons[0] % nrInputNeurons != 0,
			// (nrHiddenNeurons[0] % nrInputNeurons)/2 < 1
			// Hidden topology: zigzag
			// nrHiddenNeurons[lyr] >=< nrHiddenNeurons[lyr+1]
			// nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1] != 0
			// (nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0
			// Even number of nrHiddenNeurons[lyr]
			// Odd number of nrHiddenNeurons[lyr]
			// nrHiddenNeurons = nrOutputNeurons
			case 10:
				numberInputs = 7;
//				hiddenTopology = "zigzag";
				numberHiddenNeurons = new int[4];
				numberHiddenNeurons[0] = numberHiddenNeurons[3] = 8;
				numberHiddenNeurons[1] = numberHiddenNeurons[2] = 6;
				numberOutputs = 8;
				break;
				
			// (nrHiddenNeurons[0] % nrInputNeurons) = even
			// Even number of hidden neurons
			// nrHiddenNeurons < nrOutputNeurons
			// nrOutputNeurons % nrHiddenNeurons = 0  (Whole-number ratio)
			// Even number of output neurons
			case 11:
				numberInputs = 6;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = 8;
				numberHiddenNeurons[1] = 4;
				break;

			// (nrHiddenNeurons[0] % nrInputNeurons) = even
			// Odd number of hidden neurons
			// nrOutputNeurons % nrHiddenNeurons != 0,
			// (nrOutputNeurons % nrHiddenNeurons)/2 < 1
			case 12:
				numberInputs = 5;
				numberHiddenNeurons[0] = 9;
				numberHiddenNeurons[1] = 5;
				numberOutputs = 6;
				break;
					
			// (nrHiddenNeurons[0] % nrInputNeurons) = odd
			// Odd number of hidden neurons
			// (nrOutputNeurons % nrHiddenNeurons) = even
			// Even number of output neurons
			case 13:
				numberInputs = 6;
				numberHiddenNeurons[1] = 4;
				break;
					
			// (nrHiddenNeurons[0] % nrInputNeurons) = odd
			// Even number of hidden neurons
			// (nrOutputNeurons % nrHiddenNeurons) = even
			// Odd number of output neurons
			case 14:
				numberInputs = 5;
				numberHiddenNeurons[0] = 8;
				numberHiddenNeurons[1] = 5;
				numberOutputs = 9;
				break;
				
			// Input topology: twoGroups
			// nrInputs = 1
			// (nrOutputNeurons % nrHiddenNeurons) = odd
			// Odd number of output neurons
			case 15:
				inputTopology = "twoGroups";
				numberInputs = 1;
				numberHiddenNeurons[1] = 6;
				break;
				
			// nrHiddenNeurons = 1
			// (nrOutputNeurons % nrHiddenNeurons) = odd
			// Even number of output neurons
			case 16:
				numberInputs = 4;
				numberHiddenNeurons[0] = 1;
				numberHiddenNeurons[1] = 5;
				numberOutputs = 8;
				break;
				
			// Even number of input neurons
			// nrInputNeurons < nrHiddenNeurons
			// Output topology: each
			// nrHiddenNeurons = nrOutputNeurons
			case 17:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 8;
				outputTopology = "each";
				break;
				
			// Odd number of hidden neurons
			// nrHiddenNeurons > nrOutputNeurons
			case 18:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 9;
				break;
				
			// Odd number of input neurons
			// nrHiddenNeurons < nrOutputNeurons
			case 19:
				numberInputs = 5;
				break;

			// nrInputNeurons = nrHiddenNeurons
			case 20:
				numberInputs = 4;
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 4;
				break;
				
			// nrInputNeurons > nrHiddenNeurons
			case 21:
				numberInputs = 6;
				break;
				
			// Input topology: each
			// nrInputNeurons = nrHiddenNeurons
			case 22:
				inputTopology = "each";
				numberInputs = 3;
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 3;
				break;
				
			// nrInputNeurons > nrHiddenNeurons
			case 23:
				numberInputs = 4;
				break;
				
			// nrInputNeurons < nrHiddenNeurons
			default:
				numberInputs = 2;
				break;
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
				System.out.print("[" + numberHiddenNeurons[i] +"]");
			
			System.out.println(", "+numberOutputs+", "+inputTopology+", "+hiddenTopology+", "+outputTopology+")");
			
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
	// TODO:	if-Abfragen durchgehen und testen
	//			trainingOutVector.length != outputNeurons.length
	//			print aller Zwischenergebnisse
	
private
	MultiLayerPerceptron  perceptron;
	int inputNeurons;
	int[] hiddenNeurons;
	int outputNeurons;
	String inputTopology;
	String hiddenTopology;
	String outputTopology;
	float[] trainingInVector;
	float[] trainingOutVector;
	int trials;

public
	// Constructor
	backpropagationTest(){
	
		MultiLayerPerceptron.backpropagationTest = true;
		
		inputNeurons = 16;
		hiddenNeurons = new int[4];
		for (int h = 0; h < hiddenNeurons.length; h++)
			hiddenNeurons[h] = 32;
		outputNeurons = 16;
		
		inputTopology = "one";
		hiddenTopology = "each";
		outputTopology = "one";
			
		trainingInVector = new float[16];
		trainingOutVector = new float[16];
		
		perceptron = new MultiLayerPerceptron(inputNeurons, hiddenNeurons, outputNeurons,
				inputTopology, hiddenTopology, outputTopology);
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
		
		// Print perceptron settings
		System.out.print("MultiLayerPerceptron("+inputNeurons+", ");
			
		for (int h = 0; h < hiddenNeurons.length; h++)
			System.out.print("["+hiddenNeurons[h]+"]");
			
		System.out.println(", "+outputNeurons+", "+inputTopology+", "+hiddenTopology+", "+outputTopology+")");
		
		// Run training
//		trials = perceptron.training(trainingInVector, trainingOutVector, 0.001f, 2);
		trials = perceptron.training(trainingInVector, trainingOutVector, 0.001f, 10000);
		
		// Print result
		System.out.println("------------------------------------------------------------------------------------------");
			
		System.out.print("Trials: "+ trials);
		
		// Print used outvector
		System.out.print("\tTraining Out: ");
		
		for (int o = 0; o < outputNeurons; o++)
			System.out.print("\t["+trainingOutVector[o]+"]");
		
		System.out.print("\n");
		
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

/**************************** Test for saving and loading networks *********+********************
* TODO:	Header: Multilayerperceptron trainieren
*		Gewichte speichern (erstmal Sichtprüfung)
*		neues Perceptron
*		Gewichte laden
*		mit TrainigsInVector ausführen und Ergebnis mit Trainingsoutvector vergleichen
************************************************************************************************/
class saveLoadNet {
private
	MultiLayerPerceptron[]  perceptron;
	int[] hiddenNeurons;
	float[] trainingInVector;
	float[] trainingOutVector;
	float[] outVector;
	int trials;
	String fileName;

public
	// Constructor
	saveLoadNet(){
		hiddenNeurons = new int[3];
		hiddenNeurons[0] = 6;
		hiddenNeurons[1] = 8;
		hiddenNeurons[2] = 6;

		trainingInVector = new float[4];
		trainingInVector[0] = 0;
		trainingInVector[1] = 1;
		trainingInVector[2] = 0.1f;
		trainingInVector[3] = 0.9f;
		
		trainingOutVector = new float[4];
		trainingOutVector[0] = 0.9f;
		trainingOutVector[1] = 0.8f;
		trainingOutVector[2] = 0.2f;
		trainingOutVector[3] = 0.1f;

		outVector = new float[4];
		
		perceptron = new MultiLayerPerceptron[2];
		perceptron[0] = new MultiLayerPerceptron(4, hiddenNeurons, 4, "one", "cross", "each");
		perceptron[1] = new MultiLayerPerceptron(4, hiddenNeurons, 4, "one", "cross", "each");
		
		fileName = new String();
	}

	void runTest(){
		trials = perceptron[0].training(trainingInVector, trainingOutVector, 0.001f, 1000);
		
		System.out.print("trainingOutVector:\n\t");
		for (int i = 0; i < trainingOutVector.length; i++)
			System.out.print("["+trainingOutVector[i]+"]");
		System.out.print("\n");
		
		System.out.println("Needed trials for training: " + trials);
		
		outVector = perceptron[0].run(trainingInVector);
		
		System.out.print("outVector before saving:\n\t");
		for (int i = 0; i < outVector.length; i++)
			System.out.print("["+outVector[i]+"]");
		System.out.print("\n");
		
		fileName = perceptron[0].savePerceptron(null);
		
		System.out.println("file name: " + fileName);
		
		perceptron[1].loadPerceptron(fileName);
		
		outVector = perceptron[1].run(trainingInVector);
		
		System.out.print("outVector after saving:\n\t");
		for (int i = 0; i < outVector.length; i++)
			System.out.print("["+outVector[i]+"]");
		System.out.print("\n");
		
		perceptron[1].savePerceptron("perceptron2");
	}
}// class saveLoadNet