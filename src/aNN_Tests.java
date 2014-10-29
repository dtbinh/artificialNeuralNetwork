/**************************** Tests for artificial neural network *******************************
* 
************************************************************************************************/

public class aNN_Tests {
	/**********************************************
	* main function:
	* just contains the test objects
	**********************************************/
	public static void main(String[] args){
		
		// TODO:	artificialNeuralNetwork durchgehen und Test entwerfen, die jede Zeile und Sonderfälle abdecken
		//			Alle Tests rein kommentieren, Ergebnisse in extra txt-files
		
		// Unit test for class Neuron
		//unitTestNeuron uTNeuron = new unitTestNeuron();
		
		//uTNeuron.runTest();
		
		// Test for whole network, needs debug prints, look at commit when passed, in class
		// otherwise you'll just see the output vector
		//multilayerPerceptronTest multilayerPerceptronTest = new multilayerPerceptronTest();
		
		//multilayerPerceptronTest.runTest();
		
		// Test for training using backpropagation
		// TODO: -> Vergleich von verschiedenen Topologien, welche erreicht am schnellsten die lösung
		backpropagationTest backpropagationTest = new backpropagationTest();
		
		backpropagationTest.runTestTopologies();
		//backpropagationTest.runTestTrainings();
	}
}

/*************************************** Unit Test Neuron ***************************************
* neuron with 1,2 and 4 inputs - every input gets toggled
* threshold -10, 1.3f, 6.5f and 10
* Simulation of training at the end: Changing threshold and get result again and changing
* weights and get result again
************************************************************************************************/
class unitTestNeuron {
private
	Neuron testNeuron;
	int nrInputs;
	int maxNrInputs;
	int setInputs;
	float weights[];
	float thresholds[];
	
public
	// Constructor
	unitTestNeuron(){
		testNeuron = new Neuron(0);
		nrInputs = 1;
		maxNrInputs = 4;
		setInputs = 0b0000;
		thresholds = new float[4];
		thresholds[0] = -10;
		thresholds[1] =  1.3f;
		thresholds[2] =  6.5f;
		thresholds[3] =  10;	
	}

	/**********************************************
	* Test function
	**********************************************/
	void runTest(){
		// Number of inputs: 1, 2 & 4
		System.out.println("// Number of inputs: 1, 2 & 4");
		
		for (; nrInputs <= maxNrInputs; nrInputs *= 2){
			testNeuron = new Neuron(nrInputs);
				
			System.out.println("Testing constructor");
			System.out.print("nrInputs: " + nrInputs + "\n\n");
				
			// Set/unset inputs just with constructor
			// cases: 1010, 1111
			System.out.println("// Set/unset inputs just with constructor, cases: 1010, 1111");
			
			for (int casesInputs = 0; casesInputs < 2; casesInputs++){
				switch (casesInputs){
					case 0:	setInputs = 0b1010;
						break;
					case 1:	setInputs = 0b1111;
						break;
				}
					
				setUnsetInputs(setInputs);
				
				System.out.print("Inputs: " + Integer.toBinaryString(setInputs));
				
				System.out.println(", Output: " + testNeuron.getOutput());
				System.out.print("\n");
			}// Set/unset inputs just with constructor
				
			// Set thresholds
			for(int casesThreshold = 0; casesThreshold < 4; casesThreshold++){
				System.out.println("\n// Set threshold");
		
				testNeuron.setThreshold(thresholds[casesThreshold]);
				
				System.out.println("treshold: " + thresholds[casesThreshold]);
			
				// Set/unset inputs
				System.out.print("// Set/unset inputs");
				// cases: 0000, 0011, 0101, 1010, 1100, 1111, 
				for (int casesInputs = 0; casesInputs < 6; casesInputs++){
					switch (casesInputs){
						case 0:	setInputs = 0b0000;
							break;
						case 1:	setInputs = 0b0011;
							break;
						case 2:	setInputs = 0b0101;
							break;
						case 3:	setInputs = 0b1010;
							break;
						case 4:	setInputs = 0b1100;
							break;
						case 5:	setInputs = 0b1111;
							break;
					}
					
					setUnsetInputs(setInputs);
						
					System.out.print("Inputs: " + Integer.toBinaryString(setInputs));
						
					System.out.print(", Output: " + testNeuron.getOutput());
				}// Set/unset inputs
					
				System.out.print("\n");
			}// Set thresholds
		}// Number of inputs
		
		// Simulate training: nrInputs = 4, setInputs = 0b1111
		System.out.println("// Simulate training: nrInputs = 4, setInputs = 0b1111");
		System.out.print("\n\n");
		
		// Change threshold and get output
		System.out.println("// Change threshold and get output");
		testNeuron.setThreshold(thresholds[2]);
		System.out.println("treshold: " + thresholds[2]);
		System.out.print("Inputs: " + Integer.toBinaryString(setInputs));
		System.out.println(", Output: " + testNeuron.getOutput());
		System.out.print("\n");
		
		testNeuron.setThreshold(thresholds[1]);
		System.out.println("treshold: " + thresholds[1]);
		System.out.print("Inputs: " + Integer.toBinaryString(setInputs));
		System.out.println(", Output: " + testNeuron.getOutput());
		System.out.print("\n");
		
		testNeuron.setThreshold(thresholds[3]);
		System.out.println("treshold: " + thresholds[3]);
		
		// Change and get output
		System.out.println("// Change get output");
		System.out.print("Inputs: " + Integer.toBinaryString(setInputs));
		System.out.println(", Output: " + testNeuron.getOutput());
		System.out.print("\n");
		
		System.out.print("Inputs: " + Integer.toBinaryString(setInputs));
		System.out.println(", Output: " + testNeuron.getOutput());
		System.out.print("\n");
	}// runTest()
	
	/***************************************************************************************
	* setUnsetInputs:
	* sets inputs corresponding to bit, e.g. bit 1 -> input 1
	***************************************************************************************/
	void setUnsetInputs(int inputValues){
		
		for (int i = 0; i < nrInputs; i++){
			
			if ((inputValues & (1 << i)) == (1 << i))
				testNeuron.setInput(i, 1);
			
			else 
				testNeuron.unsetInput(i);
			
		}
		
		System.out.print("\n");
	}
}// class unitTestNeuron

/******************************** Test for multilayer perceptron ********************************
* In a loop:
*	-> inputs are set and unset
*	-> output of input neurons is calculated -> in connection
*	------------------------------------------
*	-> input of hidden neurons is set or unset -> in connection
*	-> weight of hidden neuron is set (output value * connection weight) -> in connection
*	-> output of hidden neurons is calculated
*	------------------------------------------
*	-> input of output neuron is set or unset
*	-> weight of output neuron is set 
*	-> output of output neurons is calculated
*
*	-> Online-learning: Calculate weights and threshold for first result, than second etc
*	-> Batch-learning: Calculate all results, then weights and thresholds
************************************************************************************************/
class multilayerPerceptronTest {
private
	MultiLayerPerceptron MultiLayerPerceptron[];
	
public	
	// Constructor
	multilayerPerceptronTest(){
		//MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
		//						int nrOutputNeurons, String inputTopology, String hiddenTopology, String outputTopology){
	
		MultiLayerPerceptron = new MultiLayerPerceptron[1];
		//MultiLayerPerceptron[0] = new MultiLayerPerceptron(2, 4, 1, 1, "one");
		//MultiLayerPerceptron[0] = new MultiLayerPerceptron(4, 2, 2, 2, "one");
		//MultiLayerPerceptron[0] = new MultiLayerPerceptron(8, 12, 6, 4, "one");
	}
	
	/***************************************************************************************
	* Test function:
	***************************************************************************************/
	void runTest(){
		// Switch the inputs on and off (all possibilities)
		//for (int inputVector = 0; inputVector < 16; inputVector++){
		//	float[] testResult = mLPTest.run(inputValues);
		
		float[] inputVector;
		inputVector = new float[8];
		
		for (int i = 0; i < inputVector.length; i++){
			inputVector[i] = 1;
		}
		
		for (int i = 0; i < MultiLayerPerceptron.length; i++){
			
			// Run with all inputs high
			float[] outputVector = MultiLayerPerceptron[i].run(inputVector);
			
			for (int nrVec = 0; nrVec < outputVector.length; nrVec++){
				if (nrVec > 0)
					System.out.print(", ");
				
				System.out.print("outputVector[" + nrVec + "]: " + outputVector[nrVec]);
			}
			
			System.out.print("\n--------------------------------------------------------\n");
		}
		//}
	}// runTest()
}// class multilayerPerceptronTest

/****************************** Test for backpropagation training *******************************
* Builts a multilayer perceptron and executes the training function
************************************************************************************************/
class backpropagationTest {
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
														numberHiddenLayers[run], numberOutputs[run], "twoGroups", "one", "one");
			perceptron[5] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run],
														numberHiddenLayers[run], numberOutputs[run], "each", "each", "each");
			
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