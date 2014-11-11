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
//		
//		neuronTest.runTest();
		
		// Unit test for class Connection
//		connectionTest connectionTest = new connectionTest();
//		
//		connectionTest.runTest();
		
		// Test for whole network, needs debug prints, look at commit when passed, in class
		// otherwise you'll just see the output vector
//		multilayerPerceptronTest multilayerPerceptronTest = new multilayerPerceptronTest();
//		multilayerPerceptronTest.runTest();
		
		// Test for training using backpropagation
//		backpropagationTest backpropagationTest = new backpropagationTest();
//		
//		backpropagationTest.runTest();
		
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
	
	// TODO: import java.lang.Math; mit und ohne ergebnis testen			
	
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
* TODO: Testcases neu erstellen
* 		System.out.print-Tabelle für alle cases
* 		Header aus Kommentar
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
		
		for (int testCase = 0; testCase <= 0 /*15*/; testCase++){
			switch (testCase){
			// inputs, outputs, hiddenNeuronsPerLayer und hiddenlayers < 1
			case 0:				
//				numberInputs = numberOutputs = 0;
//				numberHiddenNeurons = new int[1];
//				inputTopology = hiddenTopology = "each";
//				outputTopology = "one";	
				
				// DEBUG
				numberInputs = numberOutputs = 16;
				numberHiddenNeurons = new int[5];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = numberHiddenNeurons[2] = 32;
				numberHiddenNeurons[3] = numberHiddenNeurons[4] = 32;
				inputTopology = hiddenTopology = outputTopology = "each";
				break;
				
			// Input topology: one, nrInputNeurons > hiddenNeuronsPerLayer, hiddenlayers > 1,
			// hiddenNeuronsPerLayer = nrOutputNeurons
			case 1:
				inputTopology = "one";
				numberInputs = 4;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 2;
				numberOutputs = 2;
				break;
			
			// (inputConnWeights.length % hiddenNeuronsPerLayer) != 0
			// hiddenNeuronsPerLayer > nrOutputNeurons, (hiddenNeuronsPerLayer % nrOutputNeurons) != 0
			case 2:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 3;
				break;

			// nrInputNeurons = hiddenNeuronsPerLayer, (hiddenNeuronsPerLayer % nrOutputNeurons) == 0
			case 3:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 4;
				break;
				
			// nrInputNeurons < hiddenNeuronsPerLayer
			case 4:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 8;
				break;
				
			// (inputConnWeights.length % nrInputNeurons) != 0
			case 5:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 9;
				break;
				
			// Input topology: twoGroups, inputNeurons.length/2 geht auf
			case 6:
				inputTopology = "twoGroups";
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 8;
				break;
				
			// (hiddenNeuronsPerLayer % 2) != 0)
			case 7:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 9;
				break;
				
			// (nrInputNeurons % 2) != 0 und inputNeurons.length/2 geht nicht auf
			case 8:
				numberInputs = 5;
				break;
				
			// hiddenLayers = 1
			case 9:
				numberHiddenNeurons = new int[1];
				numberHiddenNeurons[0] = 9;
				break;

			// Hidden topology:	one
			case 10:
				hiddenTopology = "one";
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 9;
				break;
				
			// Hidden topology:	cross
			case 11:
				// DEBUG
				numberInputs = 4;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = 10;
				numberHiddenNeurons[1] = 4;
				numberOutputs = 2;
				inputTopology = "one";
				outputTopology = "each";
				
				hiddenTopology = "cross";
				break;
				
			// Hidden topology:	zigzag
			case 12:
				// DEBUG 1
				// hiddenTopology = "cross";
//				numberHiddenNeurons[0] = 4;
//				numberHiddenNeurons[1] = 10;
				
				hiddenTopology = "zigzag";
				
				// DEBUG 2
				numberInputs = 4;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = 4;
				numberHiddenNeurons[1] = 10;
				numberOutputs = 2;
				inputTopology = "one";
				outputTopology = "each";				
				break;
				
			// Output topology:	each
			case 13:
				outputTopology = "each";
				break;
				
			// Output topology:	one, hiddenNeuronsPerLayer < nrOutputNeurons
			//						 (nrOutputNeurons % hiddenNeuronsPerLayer) == 0
			case 14:
				outputTopology = "one";
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 4;
				numberOutputs = 8;
				break;
			
			// case 15: (nrOutputNeurons % hiddenNeuronsPerLayer) != 0
			default:
				numberOutputs = 7;	
			}

			// inputVector.length != inputNeurons.length
			inputVector = new float[8];
			
			outputVector = new float[numberOutputs];
			
			for (int i = 0; i < inputVector.length; i++){
				inputVector[i] = 0;
			}
			
			System.out.println("Test case " + testCase + ":");
			System.out.println("\tMultiLayerPerceptron("+numberInputs+", ["+numberHiddenNeurons.length+"]["
								+numberHiddenNeurons[0]+"], "+numberOutputs+", "+inputTopology+", "
								+hiddenTopology+", "+outputTopology+")");
			
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
	float[] trainingInVector;
	float[] trainingOutVector;
	int trials;

public
	// Constructor
	backpropagationTest(){
		trainingInVector = new float[16];
		trainingOutVector = new float[16];
		
//		perceptron = new MultiLayerPerceptron(16, 32, 5, 16);
		perceptron = new MultiLayerPerceptron(16, 32, 5, 16);
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
		trainingOutVector[10] = 1;
		trainingOutVector[11] = 0.8f;
		trainingOutVector[12] = 0.6f;
		trainingOutVector[13] = 0.4f;
		trainingOutVector[14] = 0.2f;
		trainingOutVector[15] = 0.1f;
		
//			System.out.println("MultiLayerPerceptron("+numberInputs[run]+", "+numberHiddenNeurons[run]+
//									", "+numberHiddenLayers[run]+", "+numberOutputs[run]+")");
		
			trials = perceptron.training(trainingInVector, trainingOutVector, 0.001f, 1);
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
						
						for (int i = 0; i < numberHidNeurons.length; i+=2){
							numberHidNeurons[i] = numberHiddenNeurons[nrH];
							
							if (numberHidNeurons.length > 1)
								numberHidNeurons[i+1] = numberHiddenNeurons[nrH]+2;
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