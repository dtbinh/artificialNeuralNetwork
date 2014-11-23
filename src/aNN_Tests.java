/**************************** Tests for artificial neural network *******************************
* 
************************************************************************************************/

public class aNN_Tests {
	
	/**********************************************
	* main function:
	* just contains the test objects
	**********************************************/
	public static void main(String[] args){
		
		// Test for whole network, needs debug prints, look at commit when passed, in class
		// otherwise you'll just see the output vector
//		multilayerPerceptronTest multilayerPerceptronTest = new multilayerPerceptronTest();
//		multilayerPerceptronTest.runTest();
		
		// Erst weiter, wenn multiLayerPerceptronTest komplett fertig!
		// Test for training using backpropagation
		backpropagationTest backpropagationTest = new backpropagationTest();
		backpropagationTest.runTest();
		
		// TODO Test für alle möglichen Konfigurationen ob Trials = 1000 reichen
	}
}

/******************************** Test for multilayer perceptron ********************************
*
************************************************************************************************/
class multilayerPerceptronTest {
	// Testcase:	topologien = null
	//				inputs, outputs, hiddenNeuronsPerLayer und hiddenlayers < 1
	//				nrInputNeurons > hiddenNeuronsPerLayer
	//					(inputConnWeights.length % hiddenNeuronsPerLayer) != 0
	//				nrInputNeurons = hiddenNeuronsPerLayer
	//				nrInputNeurons < hiddenNeuronsPerLayer
	//					(inputConnWeights.length % nrInputNeurons) != 0
	//					inputNeurons.length % hiddenNeurons.get(0).length) != 0
	//				Whole-number ratio
	//				hiddenlayers = 1, hiddenlayers > 1
	//				nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
	//				nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
	//				nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
	//				(nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
	//				hiddenNeuronsPerLayer > nrOutputNeurons
	//				hiddenNeuronsPerLayer = nrOutputNeurons
	//				hiddenNeuronsPerLayer < nrOutputNeurons
	//					(nrOutputNeurons % hiddenNeuronsPerLayer) != 0
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
	
public	
	// Constructor
	multilayerPerceptronTest(){
	}
	
	void runTest(){
		
		MultiLayerPerceptron.multiLayerPerceptronTest = true;
		
		for (int testCase = 0; testCase <= 9; testCase++){
			switch (testCase){
			// Inputs, outputs, hiddenNeuronsPerLayer und hiddenlayers < 1
			case 0:				
				numberInputs = numberOutputs = 0;
				numberHiddenNeurons = new int[1];
				break;
				
			// nrInputNeurons > hiddenNeuronsPerLayer, hiddenlayers > 1,
			// hiddenNeuronsPerLayer = nrOutputNeurons
			// inputNeurons.length % hiddenNeurons.get(0).length = Whole-number ratio
			case 1:
				numberInputs = 4;
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 2;
				numberOutputs = 2;
				break;
			
			// nrInputNeurons = hiddenNeuronsPerLayer, 
			// hiddenNeuronsPerLayer > nrOutputNeurons, (hiddenNeuronsPerLayer % nrOutputNeurons) != 0
			case 2:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 4;
				break;

			// nrInputNeurons < hiddenNeuronsPerLayer
			case 3:
				numberHiddenNeurons[0] = numberHiddenNeurons[1] = 8;
				break;
				
			// hiddenLayers = 1
			case 4:
				numberHiddenNeurons = new int[1];
				numberHiddenNeurons[0] = 8;
				break;

			// nrHiddenNeurons[lyr] > nrHiddenNeurons[lyr+1]
			case 5:
				numberHiddenNeurons = new int[2];
				numberHiddenNeurons[0] = 8;
				numberHiddenNeurons[1] = 4;
				break;
				
			// nrHiddenNeurons[lyr] = nrHiddenNeurons[lyr+1]
			case 6:
				numberHiddenNeurons[0] = 4;			
				break;
				
			// nrHiddenNeurons[lyr] < nrHiddenNeurons[lyr+1]
			case 7:
				numberHiddenNeurons[0] = 2;				
				break;
				
			// hiddenNeuronsPerLayer < nrOutputNeurons
			case 8:
				numberOutputs = 4;
				break;
				
			// again with 8 outputs
			default:
				numberOutputs = 8;
			}

			// inputVector.length != inputNeurons.length
			inputVector = new float[8];
			
			outputVector = new float[numberOutputs];
			
			for (int i = 0; i < inputVector.length; i++){
				inputVector[i] = 0;
			}
			
			System.out.println("Test case " + testCase + ":");
			System.out.println("\tMultiLayerPerceptron("+numberInputs+", ["+numberHiddenNeurons.length+"]["
								+numberHiddenNeurons[0]+"], "+numberOutputs+")");
			
			multiLayerPerceptron = new MultiLayerPerceptron(numberInputs, numberHiddenNeurons, numberOutputs);
			
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
	
private
	MultiLayerPerceptron  perceptron;
	int[] hiddenNeurons;
	float[] trainingInVector;
	float[] trainingOutVector;
	int trials;

public
	// Constructor
	backpropagationTest(){
	
		MultiLayerPerceptron.backpropagationTest = true;
	
		hiddenNeurons = new int[3];
		
		for (int h = 0; h < hiddenNeurons.length; h++)
			hiddenNeurons[h] = 8;
			
		trainingInVector = new float[8];
		trainingOutVector = new float[8];
		
		perceptron = new MultiLayerPerceptron(8, hiddenNeurons, 8);
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
			
		trainingOutVector[0] = 0.1f;
		trainingOutVector[1] = 0.9f;
		trainingOutVector[2] = 0.2f;
		trainingOutVector[3] = 0.3f;
		trainingOutVector[4] = 0.4f;
		trainingOutVector[5] = 0.5f;
		trainingOutVector[6] = 0.6f;
		trainingOutVector[7] = 0.7f;
		
			System.out.println("MultiLayerPerceptron(8, [8][8][8], 8)");
		
			trials = perceptron.training(trainingInVector, trainingOutVector, 0.001f, 10000, 2);
			
			
			
			System.out.print("\ttrainingOutVector:\n\t\t");
			for (int i = 0; i < trainingOutVector.length; i++)
				System.out.print("["+ trainingOutVector[i] +"]");
			System.out.print("\n");
			
			System.out.println("\tTrials: "+ trials);
			
			System.out.println("------------------------------------------------------------------------------------------");
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