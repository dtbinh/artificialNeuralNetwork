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
		unitTestNeuron uTNeuron = new unitTestNeuron();
		
		uTNeuron.runTest();
		
		// Test for whole network, needs debug prints in class to test
		//testsBuildingNetworks testsBuildingNetworks = new testsBuildingNetworks();
		
		//testsBuildingNetworks.runTest();
		
		//TODO: Test mit Training: Backpropagation
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
class testsBuildingNetworks {
private
	MultiLayerPerceptron mLPTest[];
	
public	
	// Constructor
	testsBuildingNetworks(){
		//MultiLayerPerceptron(inputNeurons, hiddenNeuronsPerLayer, hiddenLayers,
		//						connectionsPerNeuron, outputNeurons)
		mLPTest = new MultiLayerPerceptron[3];
		mLPTest[0] = new MultiLayerPerceptron(2, 1, 1, 1, 1);
		mLPTest[1] = new MultiLayerPerceptron(4, 2, 2, 2, 2);
		mLPTest[2] = new MultiLayerPerceptron(8, 12, 6, 4, 4);
		// TODO: Test für Teilerreste
	}
	
	/***************************************************************************************
	* Test function:
	***************************************************************************************/
	void runTest(){
		// Switch the inputs on and off (all possibilities)
		//for (int inputVector = 0; inputVector < 16; inputVector++){
		//	float[] testResult = mLPTest.run(inputValues);
		
		for (int i = 0; i < mLPTest.length; i++){
			// Run with all inputs high
			int[] outputVector = mLPTest[i].run(0b11111111);
			
			for (int nrVec = 0; nrVec < outputVector.length; nrVec++)
				System.out.print("outputVector: " + outputVector[nrVec] + " | ");
			
			System.out.print("\n--------------------------------------------------------\n");
		}
		//}
	}// runTest()
}

/*************************************** Unit Test Neuron ***************************************
* neuron with 1,2 and 4 inputs - every input gets toggled
* weights -1 and 1 - every possible combination
* threshold -2, -1, 1 and 2
************************************************************************************************/
class unitTestNeuron {
private
	NeuronFloat testNeuron;
	int nrInputs;
	int maxNrInputs;
	int setInputs;
	float weights[];
	float thresholds[];
	
public
	// Constructor
	unitTestNeuron(){
	testNeuron = new NeuronFloat(0);
	nrInputs = 1;
	maxNrInputs = 4;
	setInputs = 0b0000;
	weights = new float[4];
	thresholds = new float[4];
	thresholds[0] = -2.1f;
	thresholds[1] =  1.3f;
	thresholds[2] =  2.5f;
	thresholds[3] =  4;	
	}
	/**********************************************
	* Test function
	**********************************************/
	void runTest(){
		// Number of inputs: 1, 2 & 4
		System.out.println("// Number of inputs: 1, 2 & 4");
		
		for (; nrInputs <= maxNrInputs; nrInputs *= 2){
			testNeuron = new NeuronFloat(nrInputs);
				
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
			
				// Set weights
				for(int casesWheights = 0; casesWheights < 5; casesWheights++){
					System.out.println("\n// Set weights");
					
					switch (casesWheights){
						case 0:	weights[0] = weights[1] = weights[2] = weights[3] = -1.9f;
							break;
						case 1:	weights[0] = weights[1] = weights[2] = weights[3] = 2.3f;
							break;
						case 2:	weights[0] = weights[1] = -1;
							weights[2] = weights[3] =  1;
							break;
						case 3:	weights[0] = weights[2] = 1.5f;
							weights[1] = weights[3] = 3.4f;
							break;
						case 4:	weights[0] = 3.4f;
							weights[1] = 2.3f;
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
					
					System.out.print("\n\n");
					
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
				}// Set weights
			}// Set thresholds
		}// Number of inputs
		
		// Simulate training: nrInputs = 4, setInputs = 0b1111
		// threshold = 4, weights = {3, 2, 1, -1}
		System.out.println("// Simulate training: nrInputs = 4, setInputs = 0b1111");
		System.out.println("// threshold = 4, weights = {3.4, 2.3, 1, -1}");
		System.out.print("w0: " + weights[0]);
		System.out.print(", w1: " + weights[1]);
		System.out.print(", w2: " + weights[2]);
		System.out.print(", w3: " + weights[3]);
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
		
		// Change weights and get output
		System.out.println("// Change weights and get output");
		weights[0] = weights[1] = 2.1f;
		weights[2] = weights[3] = 1.6f;
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
			
			if ((inputValues & (1 << i)) == (1 << i))
				testNeuron.setInput(i);
			
			else 
				testNeuron.unsetInput(i);
			
		}
		
		System.out.print("\n");
	}
}