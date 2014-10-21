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
		//unitTestNeuron uTNeuron = new unitTestNeuron();
		
		//uTNeuron.runTest();
		
		// Test for whole network, needs debug prints, look at commit when passed, in class
		// otherwise you'll just see the output vector
		//multilayerPerceptronTest multilayerPerceptronTest = new multilayerPerceptronTest();
		
		//multilayerPerceptronTest.runTest();
		
		// Test for training using backpropagation
		// TODO: -> Vergleich von verschiedenen Topologien, welche erreicht am schnellsten die lösung
		backpropagationTest backpropagationTest = new backpropagationTest();
		
		backpropagationTest.runTest();
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
		//MultiLayerPerceptron(inputNeurons, hiddenNeuronsPerLayer, hiddenLayers, outputNeurons)
		// TODO: Needed Tests:	// From each neuron to each neuron
								// Split input neurons in 2 groups
								// Split hidden neurons in 2 groups
								// From each neuron to each neuron (between hidden layers)
								// connectionsPerNeuron < hiddenNeuron.length
								// Test für Teilerreste
	
		MultiLayerPerceptron = new MultiLayerPerceptron[2];
		MultiLayerPerceptron[0] = new MultiLayerPerceptron(2, 1, 1, 1);
		MultiLayerPerceptron[1] = new MultiLayerPerceptron(4, 2, 2, 2);
		//MultiLayerPerceptron[2] = new MultiLayerPerceptron(8, 12, 6, 4);
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
			
			//TODO: run test again with int input vector
			//int outputVector = MultiLayerPerceptron[i].runInt(0b11111111);
		}
		//}
	}// runTest()
}// class multilayerPerceptronTest

/****************************** Test for backpropagation training *******************************
* Builts a multilayer perceptron and executes the training function
************************************************************************************************/
class backpropagationTest {
private
	MultiLayerPerceptron  perceptron;
	float[] trainingInVector;
	float[] trainingOutVector;
	int numberInputs;
	int numberOutputs;
	int numberHiddenNeurons;
	int numberHiddenLayers;

public
	// Constructor
	backpropagationTest(){
		numberInputs = 4;
		numberOutputs = 2;
		numberHiddenNeurons = 2;
		numberHiddenLayers = 1;
		
		trainingInVector = new float[numberInputs];
		trainingOutVector = new float[numberOutputs];
		
		perceptron = new MultiLayerPerceptron(numberInputs, numberHiddenNeurons, numberHiddenLayers, numberOutputs);
	}

	void runTest(){
		if(numberOutputs > numberHiddenNeurons)
			System.out.println("numberOutputs ("+numberOutputs+") > numberHiddenNeurons ("+numberHiddenNeurons+")");
		else {
//			System.out.println(
//					"-------------------------------- 1st run --------------------------------"
//					);
			
			trainingInVector[0]= 1;
			trainingInVector[1]= 1;
			trainingInVector[2]= 0;
			trainingInVector[3]= 0;
//			trainingInVector[4]= 1;
//			trainingInVector[5]= 0.5f;
//			trainingInVector[6]= 0.5f;
//			trainingInVector[7]= 1;
			
			trainingOutVector[0] = 0.8f;
			trainingOutVector[1] = 0.5f;
			
			perceptron.training(trainingInVector, trainingOutVector);
/*			
			System.out.println(
					"-------------------------------- 2nd run --------------------------------"
					);
			
			trainingInVector[0]= 0.5f;
			trainingInVector[1]= 0.5f;
			trainingInVector[2]= 1;
			trainingInVector[3]= 1;
			trainingInVector[4]= 0.5f;
			trainingInVector[5]= 0;
			trainingInVector[6]= 0;
			trainingInVector[7]= 0.5f;
			
			trainingOutVector[0] = 0.2f;
			
			perceptron.training(trainingInVector, trainingOutVector);

			System.out.println(
					"-------------------------- Run with 1st input ---------------------------"
					);
			trainingInVector[0]= 1;
			trainingInVector[1]= 1;
			trainingInVector[2]= 0;
			trainingInVector[3]= 0;
			trainingInVector[4]= 1;
			trainingInVector[5]= 0.5f;
			trainingInVector[6]= 0.5f;
			trainingInVector[7]= 1;
			
			float out[] = perceptron.run(trainingInVector);
			
			System.out.println("out[0]: "+out[0]);
*/		}
	}

}// class backpropagationTest