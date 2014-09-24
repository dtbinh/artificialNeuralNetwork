/**************************** Tests for artificial neural network *******************************
* 
************************************************************************************************/

public class aNN_Tests {
	/**********************************************
	* main function:
	* test loops
	**********************************************/
	public static void main(String[] args){
		
		// Unit test for class Neuron
		// TODO: Check test result:
		/*unitTestNeuron uTNeuron = new unitTestNeuron();
		
		uTNeuron.runTest();
		*/
		
		// Test for whole network
		testNetwork testNetwork = new testNetwork();
		
		testNetwork.runTest();
	}
}

/************************************ Test for whole network ************************************
* TODO:	In a loop:
*	-> inputs are set and unset
*	-> output of input neurons is calculated -> in connection
*	------------------------------------------
*	-> input of hidden neurons is set or unset -> in connection
*	-> weight of hidden neuron is set (either just output value or output value * connection weight)
*		(Is really neuron output = weight of connection) -> in connection
*	-> output of hidden neurons is calculated
*	------------------------------------------
*	-> input of output neuron is set or unset
*	-> weight of output neuron is set 
*	-> output of output neurons is calculated
************************************************************************************************/
class testNetwork {
private
	Neuron inputA;
	Neuron inputB;
	Neuron inputC;
	Neuron inputD;
	Neuron hiddenA;
	Neuron hiddenB;
	Neuron output;
	
	// Constructor
	testNetwork(){
		inputA = new Neuron(1);
		inputB = new Neuron(1);
		inputC = new Neuron(1);
		inputD = new Neuron(1);
		hiddenA = new Neuron(2);
		hiddenB = new Neuron(2);
		output = new Neuron(2);
		
	}
	
public
	/***************************************************************************************
	* Test function:
	***************************************************************************************/
	void runTest(){
		// Simple network with 4 input, 2 hidden and 1 output neuron
		
		// Initial values: wheights = 1, threshold = 1
		
		
		//TODO: Training: Backpropagation
		//while(result != 100){
			
			// Connect input neurons to hidden neurons
			// A -> A1, B -> A2, C -> B1, D -> B2
			inputA.connection(hiddenA, 1);
			inputB.connection(hiddenA, 2);
			inputC.connection(hiddenB, 1);
			inputD.connection(hiddenB, 2);
			
			// Connect hidden neurons to output neuron
			hiddenA.connection(output, 1);
			hiddenB.connection(output, 2);
			
			// if (result != 100){
			// weights and thresholds
			//}
		//}
			
		// Just switch the inputs on and off (all possibilities)
		// to see the final results
		for (int i = 0; i < 16; i++){
			if ((i & 0x1) == 0x1){
				inputA.setInput(1);
				System.out.print("InputA = 1");
			}
			else {
				inputA.unsetInput(1);
				System.out.print("InputA = 0");
			}
				
			if ((i & 0x2) == 0x2){
				inputB.setInput(1);
				System.out.print("InputB = 1");
			}
			else {
				inputB.unsetInput(1);
				System.out.print("InputB = 0");
			}
			
			if ((i & 0x4) == 0x4){
				inputC.setInput(1);
				System.out.print("InputC = 1");
			}
			else {
				inputC.unsetInput(1);
				System.out.print("InputC = 0");
			}
				
			if ((i & 0x8) == 0x8){
				inputD.setInput(1);
				System.out.print("InputD = 1");
			}
			else {
				inputD.unsetInput(1);
				System.out.print("InputD = 0");
			}
			System.out.print("\n");
			
		}
	}// runTest()
}

/*************************************** Unit Test Neuron ***************************************
* neuron with 1,2 and 4 inputs - every input gets toggled
* weights -1 and 1 - every possible combination
* treshold -2, -1, 1 and 2
************************************************************************************************/
class unitTestNeuron {
private
	Neuron testNeuron;
	int maxNrInputs;
	int setInputs;
	int weights[];
	int thresholds[];
	
public
	// Constructor
	unitTestNeuron(){
	testNeuron = new Neuron(0);
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
		for (int nrInputs = 1; nrInputs <= maxNrInputs; nrInputs *= 2){
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
					
				if ((setInputs & 0x1) == 0x1)
					testNeuron.setInput(0);
				else
					testNeuron.unsetInput(0);
				
				if ((nrInputs >= 2) && ((setInputs & 0x2) == 0x2))
					testNeuron.setInput(1);
				else if (nrInputs >= 2)
					testNeuron.unsetInput(1);
				
				if ((nrInputs >= 3) && ((setInputs & 0x4) == 0x4))
					testNeuron.setInput(2);
				else if (nrInputs >= 3)
					testNeuron.unsetInput(2);
				
				if ((nrInputs >= 4) && ((setInputs & 0x8) == 0x8))
					testNeuron.setInput(3);
				else if (nrInputs >= 4)
					testNeuron.unsetInput(3);
				
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
					
						if ((setInputs & 0x1) == 0x1)
							testNeuron.setInput(0);
						else
							testNeuron.unsetInput(0);
						
						if ((nrInputs >= 2) && ((setInputs & 0x2) == 0x2))
							testNeuron.setInput(1);
						else if (nrInputs >= 2)
							testNeuron.unsetInput(1);
						
						if ((nrInputs >= 3) && ((setInputs & 0x4) == 0x4))
							testNeuron.setInput(2);
						else if (nrInputs >= 3)
							testNeuron.unsetInput(2);
						
						if ((nrInputs >= 4) && ((setInputs & 0x8) == 0x8))
							testNeuron.setInput(3);
						else if (nrInputs >= 4)
							testNeuron.unsetInput(3);
						
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
		
		// Change treshold and get output
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
}