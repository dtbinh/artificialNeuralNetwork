/**************************** Tests for artificial neural network *******************************
* 
************************************************************************************************/

public class aNN_Tests {
	/**********************************************
	* main function:
	* just contains the test objects
	**********************************************/
	public static void main(String[] args){
		
		// Unit test for class Neuron
		// TODO: Check test result:
		unitTestNeuron uTNeuron = new unitTestNeuron();
		
		uTNeuron.runTest();
		
		// Test for whole network
		//testNetwork testNetwork = new testNetwork();
		
		//testNetwork.runTest();
	}
}

/************************************ Test for whole network ************************************
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
* TODO:	class MultiLayerPerceptron verwenden
************************************************************************************************/
class testNetwork {
private
	Neuron input[];
	Neuron hidden[];
	Neuron output;
	float connWeights[];
	float outputVector;
	// float trainingVector[]; // 100 für 16 usw.
	Boolean keepGoing;
	
public	
	// Constructor
	testNetwork(){
		// 3-layered perceptron with 4 input, 2 hidden and 1 output neuron,
		// 1 connection (neuron output)
		// MultiLayerPerceptron mLPTest = MultiLayerPerceptron(4, 2, 1, 1, 1);
		
		input = new Neuron[4];
		for (int i = 0; i < input.length; i++)
			input[i] = new Neuron(1);
		
		hidden = new Neuron[2];
		for (int i = 0; i < hidden.length; i++)
			hidden[i] = new Neuron(2);
			
		output = new Neuron(2);
		
		connWeights = new float[6];
		
		// Initial values: all weights and thresholds = 1
		for (int i = 0; i < connWeights.length; i++){
			if (i < input.length){
				input[i].setWeight(1, 1);
				input[i].setThreshold(1);
			}
			
			if ((i/2) < hidden.length){
				hidden[i/2].setWeight(1, 1);
				hidden[i/2].setWeight(2, 1);
				hidden[i/2].setThreshold(1);
			}
			
			connWeights[i] = 1;
		}
		
		output.setWeight(1, 1);
		output.setThreshold(1);
		
		outputVector = 0;
		keepGoing  = false;
	}
	
	/***************************************************************************************
	* Test function:
	***************************************************************************************/
	void runTest(){
		// Switch the inputs on and off (all possibilities)
		//for (int inputVector = 0; inputVector < 16; inputVector++){
		//	setUnsetInputs(inputValues);
		
			// Erstmal nur für alle Inputs = 1 und outputVector soll = 100 sein
			setUnsetInputs(16);
			
			//TODO: Training: Backpropagation
			keepGoing = true;
			while(keepGoing){
			
				// Connect each 2 input neurons to 1 hidden neuron
				/*input[0].connection(hidden[0], 1, connWeights[0]);
				input[1].connection(hidden[0], 2, connWeights[1]);
				input[2].connection(hidden[1], 1, connWeights[2]);
				input[3].connection(hidden[1], 2, connWeights[3]);
				*/
				for (int i = 0; i < input.length; i++){
					input[i].connection(hidden[i/2], ((i/2)+1), connWeights[i]);
				}
				
				// Connect hidden neurons to output neuron
				/*hidden[0].connection(output, 1, connWeights[4]);
				hidden[1].connection(output, 2, connWeights[5]);
				*/
				for (int i = 0; i < hidden.length; i++){
					hidden[i].connection(output, ((i/2)+1), connWeights[i+(input.length - 1)]);
				}
				
				// outputVector = output.getOutput();
				// if (outputVector != 100){
				// weights and thresholds
				// }
				// else
				// 	keepGoing = false;
			}
		//}
	}// runTest()
	
	/***************************************************************************************
	* setUnsetInputs:
	* sets inputs corresponding to bit, e.g. bit 1 -> input 1
	***************************************************************************************/
	void setUnsetInputs(int inputValues){
		
		for (int i = 0; i < input.length; i++){
			
			if ((inputValues & (1 << i)) == (1 << i)){
				input[i].setInput(1);
				System.out.print("Input" + i + " : 1");
			}
			else {
				input[i].unsetInput(1);
				System.out.print("Input" + i + " : 0");
			}
		}
		
		System.out.print("\n");
	}
}

/*************************************** Unit Test Neuron ***************************************
* neuron with 1,2 and 4 inputs - every input gets toggled
* weights -1 and 1 - every possible combination
* threshold -2, -1, 1 and 2
************************************************************************************************/
class unitTestNeuron {
private
	Neuron testNeuron;
	int nrInputs;
	int maxNrInputs;
	int setInputs;
	int weights[];
	int thresholds[];
	
public
	// Constructor
	unitTestNeuron(){
	testNeuron = new Neuron(0);
	nrInputs = 1;
	maxNrInputs = 4;
	setInputs = 0b0000;
	weights = new int[4];
	thresholds[0] = -2;
	thresholds[1] =  1;
	thresholds[2] =  2;
	thresholds[3] =  4;	
	}
	/**********************************************
	* Test function
	**********************************************/
	void runTest(){
		// Number of inputs: 1, 2 & 4
		for (; nrInputs <= maxNrInputs; nrInputs *= 2){
			testNeuron = new Neuron(nrInputs);
				
			System.out.println("Testing constructor");
			System.out.print("nrInputs: " + nrInputs + "\n\n");
				
			// Set/unset inputs just with constructor
			// cases: 1010, 1111, 
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
		
				testNeuron.setThreshold(thresholds[casesThreshold]);
				
				System.out.println("treshold: " + thresholds[casesThreshold]);
			
				// Set weights
				// cases: all -2, all 2, half -1 half 1, half 1 half 3, 3 2 1 -1
				for(int casesWheights = 0; casesWheights < 5; casesWheights++){
					switch (casesWheights){
						case 0:	weights[0] = weights[1] = weights[2] = weights[3] = -2;
							break;
						case 1:	weights[0] = weights[1] = weights[2] = weights[3] = 2;
							break;
						case 2:	weights[0] = weights[1] = -1;
							weights[2] = weights[3] =  1;
							break;
						case 3:	weights[0] = weights[2] = 1;
							weights[1] = weights[3] = 3;
							break;
						case 4:	weights[0] = 3;
							weights[1] = 2;
							weights[2] = 1;
							weights[3] = -1;
							break;
					}
				
					testNeuron.setWeight(0, weights[0]);
					System.out.print("w0: " + weights[0]);
					
					if (nrInputs >= 2){
						testNeuron.setWeight(1, weights[1]);
						System.out.print(", w1: " + weights[1]);
					}
					
					if (nrInputs >= 3){
						testNeuron.setWeight(2, weights[2]);
						System.out.print(", w2: " + weights[2]);
					}
					
					if (nrInputs >= 4){
						testNeuron.setWeight(3, weights[3]);
						System.out.print(", w3: " + weights[3]);
					}
					
					System.out.print("\n");
					
					// Set/unset inputs
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
						
						System.out.println(", Output: " + testNeuron.getOutput());
					}// Set/unset inputs
					
					System.out.print("\n");
				}// Set weights
			}// Set thresholds
		}// Number of inputs
		
		// threshold = 4, weights = {3, 2, 1, -1}
		// Simulate training: nrInputs = 4, setInputs = 0b1111
		System.out.println("\nSimulation of training:");
		System.out.print("w0: " + weights[0]);
		System.out.print(", w1: " + weights[1]);
		System.out.print(", w2: " + weights[2]);
		System.out.print(", w3: " + weights[3]);
		System.out.print("\n\n");
		
		// Change threshold and get output
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
		
		// Change weights and get output
		weights[0] = weights[1] = 2;
		weights[2] = weights[3] = 1;
		testNeuron.setWeight(0, weights[0]);
		System.out.print("w0: " + weights[0]);
		testNeuron.setWeight(1, weights[1]);
		System.out.print(", w1: " + weights[1]);
		testNeuron.setWeight(2, weights[2]);
		System.out.print(", w2: " + weights[2]);
		testNeuron.setWeight(3, weights[3]);
		System.out.print(", w3: " + weights[3]);
		System.out.print("\n");
		System.out.print("Inputs: " + Integer.toBinaryString(setInputs));
		System.out.println(", Output: " + testNeuron.getOutput());
		System.out.print("\n");
		
		weights[0] = -1;
		weights[1] = weights[2] = 2;
		weights[3] = 3;
		testNeuron.setWeight(0, weights[0]);
		System.out.print("w0: " + weights[0]);
		testNeuron.setWeight(1, weights[1]);
		System.out.print(", w1: " + weights[1]);
		testNeuron.setWeight(2, weights[2]);
		System.out.print(", w2: " + weights[2]);
		testNeuron.setWeight(3, weights[3]);
		System.out.print(", w3: " + weights[3]);
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
			
			if ((inputValues & (1 << i)) == (1 << i)){
				testNeuron.setInput(i);
				System.out.print("Input" + i + " : 1");
			}
			else {
				testNeuron.unsetInput(i);
				System.out.print("Input" + i + " : 0");
			}
		}
		
		System.out.print("\n");
	}
}