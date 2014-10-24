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
	
		MultiLayerPerceptron = new MultiLayerPerceptron[1];
		//MultiLayerPerceptron[0] = new MultiLayerPerceptron(2, 4, 1, 1, "one");
		//MultiLayerPerceptron[0] = new MultiLayerPerceptron(4, 2, 2, 2, "one");
		MultiLayerPerceptron[0] = new MultiLayerPerceptron(8, 12, 6, 4, "one");
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
	float[] trainingInVector;
	float[] trainingOutVector;
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
		
		trainingInVector = new float[16];
		trainingOutVector = new float[16];
		
		perceptron = new MultiLayerPerceptron[2];
	}

	void runTest(){
		numberInputs[0] = numberOutputs[0] = 2;
		numberInputs[1] = numberOutputs[1] = 4;
		numberInputs[2] = numberOutputs[2] = 8;
		numberInputs[3] = numberOutputs[3] = 16;
			
		numberHiddenNeurons[0] = 4;
		numberHiddenNeurons[1] = 8;
		numberHiddenNeurons[2] = 16;
		numberHiddenNeurons[3] = 32;
		
		numberHiddenLayers[0] = 1;
		numberHiddenLayers[1] = 2;
		numberHiddenLayers[2] = 4;
		numberHiddenLayers[3] = 16;
		
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
		trainingOutVector[1] = 0.1f;
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
		
		int[] trials = new int[perceptron.length * numberInputs.length];
		int trial = 0;
		
		for (int run = 0; run < numberInputs.length-1; run++){
			perceptron[0] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run], numberHiddenLayers[run], numberOutputs[run]);
			perceptron[1] = new MultiLayerPerceptron(numberInputs[run], numberHiddenNeurons[run], numberHiddenLayers[run], numberOutputs[run], "one");
			
			System.out.println("MultiLayerPerceptron("+numberInputs[run]+", "+numberHiddenNeurons[run]+", "+numberHiddenLayers[run]+", "+numberOutputs[run]+")");
		
			for (int exec = 0; exec < perceptron.length; exec++, trial++){
				trials[trial] = perceptron[exec].training(trainingInVector, trainingOutVector, 1);
				System.out.println("     --------------------------------------------------");
			}
			
			for (int print = trial - perceptron.length; print < trial; print++){
				if (print > trial - perceptron.length)
					System.out.print(", ");
					
				System.out.print("trials["+print+"]: "+ trials[print]);
			}
			
			System.out.println("\n------------------------------------------------------------------------------------------");
		}// for(int perc)
			
//		for (trial = 0; trial < trials.length; trial+=perceptron.length){
//			
//			System.out.print("trials["+trial+"]: "+ trials[trial]);
//			System.out.println(", trials["+(trial+1)+"]: "+ trials[trial+1]);
//		}
		
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
	}

}// class backpropagationTest