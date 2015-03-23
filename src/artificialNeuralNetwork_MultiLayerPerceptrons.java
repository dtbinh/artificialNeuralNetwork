/********************************** Artificial neural network ***********************************
* Description:	This file contains the MultiLayerPerceptron class which builds the artificial
* 
* Author:		Giso Pillar
* 
* Date:			03.2015
************************************************************************************************/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/************************************************************************************************
* class MultiLayerPerceptron:
* 	Automatically builds a multi-layer perceptron, using neurons with logistic function, see
* 	description of constructors for more details.
* 	Max. number of neurons per layer: 32
************************************************************************************************/
class MultiLayerPerceptron
{
	
private
	Neuron[] inputNeurons;
	Neuron[] hiddenLayer;
	ArrayList<Neuron[]> hiddenNeurons;
	Neuron[] outputNeurons;
	int[] neuronInputsLayer;
	ArrayList<int[]> neuronInputs;
	float[] outputVector;
	Connection[] inputConnections;
	Connection[] hiddenLyrConnections;
	ArrayList<Connection[]> hiddenConnections;
	Connection[] outputConnections;
	String inputTopology;
	String hiddenTopology;
	String outputTopology;
	// For training loop
	Boolean keepGoing;
	// Initialise connection weight
	static final int INITWEIGHT = 1;
	// Initialise thresholds
	static final int INITTHRESH = 0;

public
	// Test variables
	static Boolean multiLayerPerceptronTest = false;
	static Boolean backpropagationTest = false;
	
	// Constructors
	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons,
	*			String inputTopology, String hiddenTopology, String outputTopology):
	* 	Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* 	each layer, and number of output neurons can (has to) be defined.
	* 	The type of connection between the layers has to be defined.
	* 	There must be at least 1 hidden layer
	****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons,
							String inputTopology, String hiddenTopology, String outputTopology){
	
		neuronInputs = new ArrayList<int[]>();
		
		hiddenNeurons = new ArrayList<Neuron[]>();
		hiddenConnections = new ArrayList<Connection[]>();
		
		if (inputTopology == null)
			inputTopology = new String("each");
		else
			this.inputTopology = inputTopology;
		
		if (hiddenTopology == null)
			hiddenTopology = new String("each");
		else
			this.hiddenTopology = hiddenTopology;
		
		if (outputTopology == null)
			outputTopology = new String("each");
		else
			this.outputTopology = outputTopology;
		
		// Just to prevent wrong usage
		if (nrInputNeurons < 1)
			nrInputNeurons = 1;
			
		if (nrHiddenNeurons.length < 1)
			nrHiddenNeurons = new int[1];
		
		for (int i = 0; i < nrHiddenNeurons.length; i++){
			
			if (nrHiddenNeurons[i] < 1)
				nrHiddenNeurons[i] = 1;
		}

		if (nrOutputNeurons < 1)
			nrOutputNeurons = 1;
		
		// Creation of connections and calculation of needed inputs in hidden layer 0
		/*** Input layer ***/
		switch (inputTopology){
		
		// Every input neuron will be connected to the nearest hidden neuron
		case "one":
			
			if (nrInputNeurons >= nrHiddenNeurons[0]){
				
				// Needed inputs in 1st hidden layer
				neuronInputsLayer = new int[nrHiddenNeurons[0]];
				
				// Range of hidden neurons with one more input
				int range = nrInputNeurons % nrHiddenNeurons[0];
				int rangeStart  = (nrHiddenNeurons[0] - ((nrHiddenNeurons[0] - range) / 2)) - range;
				
				// For every neuron in hidden layer 0
				for (int m = 0; m < nrHiddenNeurons[0]; m++){
					
					// We need more connections in the middle
					if ((nrInputNeurons % nrHiddenNeurons[0]) != 0){
						
						// Inside the range of the neurons with one more connection
						if (m >= rangeStart && m < (rangeStart + range))
							neuronInputsLayer[m] = (nrInputNeurons / nrHiddenNeurons[0]) + 1;
						
						// Outside the range
						else 
							neuronInputsLayer[m] = nrInputNeurons / nrHiddenNeurons[0];
					}// if ((nrInputNeurons % nrHiddenNeurons[0]) != 0)
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrInputNeurons / nrHiddenNeurons[0];
				}// for (int m = 0; m < nrHiddenNeurons[0]; m++)
					
				neuronInputs.add(neuronInputsLayer);

				inputConnections = new Connection[nrInputNeurons];
			}// if (nrInputNeurons >= nrHiddenNeurons[0])
				
			// Every hidden neuron will be connected to the nearest input neuron
			else {
				
				// Needed inputs in 1st hidden layer
				neuronInputsLayer = new int[nrHiddenNeurons[0]];
				
				// For every neuron in hidden layer 0...
				for (int m = 0; m < nrHiddenNeurons[0]; m++){
					
					// ...we just need one input
					neuronInputsLayer[m] = 1;
				}
					
				neuronInputs.add(neuronInputsLayer);

				inputConnections = new Connection[nrHiddenNeurons[0]];
			}// else (if (nrInputNeurons >= nrHiddenNeurons[0]))
			break;// case "one":
			
		// The hidden neurons will be split into two groups
		// Every input neuron will be connected to every neuron of the nearest group
		case "twoGroups":
			
			// Prevent wrong usage
			// Two groups with one input neuron makes no sense
			if (nrInputNeurons == 1)
				nrInputNeurons++;
			
			// and with just on hidden neuron neither
			if (nrHiddenNeurons[0] == 1)
				nrHiddenNeurons[0]++;
			
			int neededConnections = (nrHiddenNeurons[0]/2) * nrInputNeurons;
			
			// The upper group will have one more neuron
			if (nrHiddenNeurons[0] % 2 != 0){
				
				// Input neurons will be split at nrInputNeurons/2
				if (nrInputNeurons % 2 == 0)
					neededConnections += nrInputNeurons/2;
				else
					neededConnections += (nrInputNeurons/2) + 1;
			}// if (nrHiddenNeurons[0] % 2 != 0)
				
			inputConnections = new Connection[neededConnections];
			
			// Needed inputs in 1st hidden layer
			neuronInputsLayer = new int[nrHiddenNeurons[0]];
			
			// For every neuron in hidden layer 0
			for (int m = 0; m < nrHiddenNeurons[0]; m++){
				
				// The upper group has one input more if there
				// is an odd amount of input neurons
				if (((nrInputNeurons % 2) != 0 ) && (m >= (nrHiddenNeurons[0]/ 2)))
					neuronInputsLayer[m] = (nrInputNeurons/2) + 1;
				
				else
					neuronInputsLayer[m] = nrInputNeurons/2;
			}// for (int m = 0; m < nrHiddenNeurons[0]; m++)
			
			neuronInputs.add(neuronInputsLayer);
			break;// case "twoGroups":

		// "each": Every neuron of the current layer will be connected to every neuron of the following layer
		default:
			
			inputConnections = new Connection[nrInputNeurons * nrHiddenNeurons[0]];
			
			// Needed inputs in 1st hidden layer
			neuronInputsLayer = new int[nrHiddenNeurons[0]];
			
			// For every neuron in hidden layer 0
			for (int m = 0; m < nrHiddenNeurons[0]; m++)
				neuronInputsLayer[m] = nrInputNeurons;
			
			neuronInputs.add(neuronInputsLayer);
		}// switch (inputTopology)
		
		inputNeurons = new Neuron[nrInputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < nrInputNeurons; i++){
			inputNeurons[i] = new Neuron(1);
			
			// Initialise thresholds
			inputNeurons[i].setThreshold(INITTHRESH);
		}
		
		/*** Hidden layer(s) ***/
		if (nrHiddenNeurons.length == 1){
			hiddenLayer  = new Neuron[nrHiddenNeurons[0]];
			
			hiddenNeurons.add(hiddenLayer);
		}// if (nrHiddenNeurons.length == 1)
			
		else {
			
			for (int lyr = 0; lyr < nrHiddenNeurons.length; lyr++){
				hiddenLayer = new Neuron[nrHiddenNeurons[lyr]];

				hiddenNeurons.add(hiddenLayer);
			}// for (int lyr = 0; lyr < nrHiddenNeurons.length; lyr++)
			
			// Creation of hidden connections and 
			// calculation of needed inputs in next layer
			switch (hiddenTopology){
			
			// Every neuron will be connected to the nearest neuron in the next layer
			case "one":
				
				// For every layer
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]){
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];

						// Range of neurons in next layer with one more input
						int range = nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1];
						int rangeStart  = (nrHiddenNeurons[lyr+1] - ((nrHiddenNeurons[lyr+1] - range) / 2)) - range;
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							// We need more connections in the middle
							if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0){
								
								// Inside the range of the neurons with one more connection
								if (m >= rangeStart && m < (rangeStart + range))
									neuronInputsLayer[m] = (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]) + 1;
								
								// Outside the range
								else 
									neuronInputsLayer[m] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
							}
							
							// Whole-number ratio
							else 
								neuronInputsLayer[m] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
						}
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr]];
					}
						
					// Every neuron in the next layer will be connected to the nearest neuron in the current layer
					else {
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer...
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
							
							// ...we just need one input
							neuronInputsLayer[m] = 1;
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr+1]];
					}
					
					neuronInputs.add(neuronInputsLayer);
						
					hiddenConnections.add(hiddenLyrConnections);
				}
				break;// case "one"
			
			// "each": Every neuron of the current layer will be connected to every neuron of the following layer
			default: 
				
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr] * nrHiddenNeurons[lyr+1]];
					
					hiddenConnections.add(hiddenLyrConnections);
					
					// Needed inputs in 1st hidden layer
					neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
					
					// For every neuron in next hidden layer
					for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
						neuronInputsLayer[m] = nrHiddenNeurons[lyr];
					
					neuronInputs.add(neuronInputsLayer);
				}// for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++)
			}// switch (hiddenTopology)
		}// else if (nrHiddenNeurons.length == 1)
					
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int l = 0; l < hiddenNeurons.size(); l++){
			
			for (int n = 0; n < hiddenNeurons.get(l).length; n++){
				hiddenNeurons.get(l)[n] = new Neuron(neuronInputs.get(l)[n]);
					
				// Initialise thresholds
				hiddenNeurons.get(l)[n].setThreshold(INITTHRESH);
			}
		}// for (int l = 0; l < hiddenNeurons.size(); l++)
		 
		/*** Output layer ***/		
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		switch (outputTopology){
		
		case "one":
			// Every hidden neuron will be connected to the nearest output neuron
			
			if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons){
				
				// Needed inputs in output layer
				neuronInputsLayer = new int[nrOutputNeurons];
				
				// Range of output neurons with one more input
				int range = nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons;
				int rangeStart  = (nrOutputNeurons - ((nrOutputNeurons - range) / 2)) - range;
				
				// For every neuron in output layer
				for (int m = 0; m < nrOutputNeurons; m++){
					
					// We need more connections in the middle
					if ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons) != 0){
						
						// Inside the range of the neurons with one more connection
						if (m >= rangeStart && m < (rangeStart + range))
							neuronInputsLayer[m] = (nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons) + 1;
						
						// Outside the range
						else 
							neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
					}// if ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons) != 0)
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
				}// for (int m = 0; m < nrOutputNeurons; m++)
					
				neuronInputs.add(neuronInputsLayer);

				outputConnections = new Connection[nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			}// if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons)
				
			// Every hidden neuron will be connected to the nearest input neuron
			else {
				
				// Needed inputs in output layer
				neuronInputsLayer = new int[nrOutputNeurons];
				
				// For every neuron in output...
				for (int m = 0; m < nrOutputNeurons; m++){
					
					// ...we just need one input
					neuronInputsLayer[m] = 1;
				}
					
				neuronInputs.add(neuronInputsLayer);

				outputConnections = new Connection[nrOutputNeurons];
			}// else (if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons))
			break;// case "one"
			
		// The output neurons will be split into two groups
		// Every hidden neuron will be connected to every neuron of the nearest group
		case "twoGroups":
			
			// Prevent wrong usage
			// Two groups with one input neuron makes no sense
			if (nrHiddenNeurons[nrHiddenNeurons.length - 1] == 1)
				nrHiddenNeurons[nrHiddenNeurons.length - 1]++;
			
			// and with just on hidden neuron neither
			if (nrOutputNeurons == 1)
				nrOutputNeurons++;
			
			int neededConnections = (nrOutputNeurons/2) * nrHiddenNeurons[nrHiddenNeurons.length - 1];
			
			// The upper group will have one more neuron
			if (nrOutputNeurons % 2 != 0){
				
				// Hidden neurons will be split at nrHiddenNeurons/2
				if (nrHiddenNeurons[nrHiddenNeurons.length - 1] % 2 == 0)
					neededConnections += nrHiddenNeurons[nrHiddenNeurons.length - 1]/2;
				else
					neededConnections += (nrHiddenNeurons[nrHiddenNeurons.length - 1]/2) + 1;
			}// if (nrHiddenNeurons[0] % 2 != 0)
				
			outputConnections = new Connection[neededConnections];
			
			// Needed inputs in output layer
			neuronInputsLayer = new int[nrOutputNeurons];
			
			// For every neuron in output layer
			for (int m = 0; m < nrOutputNeurons; m++){
				
				// The upper group has one input more if there
				// is an odd amount of hidden neurons
				if (((nrHiddenNeurons[nrHiddenNeurons.length - 1] % 2) != 0 ) && (m >= (nrOutputNeurons/ 2)))
					neuronInputsLayer[m] = (nrHiddenNeurons[nrHiddenNeurons.length - 1]/2) + 1;
				
				else
					neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1]/2;
			}// for (int m = 0; m < nrOutputNeurons; m++)
			
			neuronInputs.add(neuronInputsLayer);
			break;// case "twoGroups":
		
		// "each": Every neuron of the last hidden layer will be connected to every output neuron
		default:
			
			outputConnections = new Connection[nrOutputNeurons * nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			
			// Needed inputs in output layer
			neuronInputsLayer = new int[nrOutputNeurons];
			
			// For every neuron in output layer
			for (int m = 0; m < nrOutputNeurons; m++)
				neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1];
			
			neuronInputs.add(neuronInputsLayer);			
		}// switch (outputTopology)
		
		for (int i = 0; i < outputNeurons.length; i++){
			outputNeurons[i] = new Neuron(neuronInputs.get(nrHiddenNeurons.length)[i]);
			
			// Initialise thresholds
			outputNeurons[i].setThreshold(INITTHRESH);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connections between input layer and 1st hidden layer ***/
		
		// Test output: multilayerPerceptronTest
		if (multiLayerPerceptronTest)
			System.out.println("\tinputConnection\t| inputNeuron | hiddenNeuron\tinp|pos");
		
		switch (inputTopology){
		
		case "one":

			if (inputNeurons.length >= hiddenNeurons.get(0).length){

				// Neuron of the next layer
				int m = 0;
				
				// Input connection is going to
				int i = 0;
				
				// From each input neuron...
				for (int n = 0; n < inputNeurons.length; n++){
					
					// Go to next neuron in next layer if all inputs are connected
					if (i >= hiddenNeurons.get(0)[m].getNumberOfInputs()){
						i = 0;
						m++;
					}
					 
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+n+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+i+" | "+m);
					
					// ...to a hidden neuron
					inputConnections[n] = new Connection(inputNeurons[n], hiddenNeurons.get(0)[m], i, INITWEIGHT, m);
					
					// Go to next input
					i++;
				}// for (int n = 0; n < inputNeurons.length; n++)
			}// if (inputNeurons.length >= hiddenNeurons.get(0).length)
			
			else {

				// Neuron of the current layer
				int n = 0;
				
				// Range of input neurons with one more connection
				int range = (hiddenNeurons.get(0).length % inputNeurons.length)
								* ((hiddenNeurons.get(0).length / inputNeurons.length) + 1);
				int rangeStart  = (hiddenNeurons.get(0).length - ((hiddenNeurons.get(0).length - range) / 2)) - range;
				
				// Number of connections to next layer
				int c;
				
				// Current connection to next layer
				int c_i = 0;
				
				// From each hidden neuron...
				for (int m = 0; m < hiddenNeurons.get(0).length; m++){
					
					// We need more connections in the middle
					if ((hiddenNeurons.get(0).length % inputNeurons.length) != 0){
						
						// Inside the range of the neurons with one more connection
						if (m > rangeStart && m <= (rangeStart + range))
							c = (hiddenNeurons.get(0).length / inputNeurons.length) + 1;
						
						// Outside the range
						else 
							c = hiddenNeurons.get(0).length / inputNeurons.length;
					}// if ((hiddenNeurons.get(0).length % inputNeurons.length) != 0)
					
					// Whole-number ratio
					else 
						c = hiddenNeurons.get(0).length / inputNeurons.length;
					
					// Go to next neuron of the current layer if all connections are done
					if (c_i >= c){
						n++;
						
						c_i = 0;
					}
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+m+"]\t|\t["+n+"]\t [0]["+m+"]\t\t 0 | "+m);
					
					// ...to a hidden neuron
					inputConnections[m] = new Connection(inputNeurons[n], hiddenNeurons.get(0)[m], 0, INITWEIGHT, m);
					
					// Next connection
					c_i++;
				}// for (int m = 0; m < hiddenNeurons.get(0).length; m++)
			}// else (if (inputNeurons.length >= hiddenNeurons.get(0).length))
			break;// case "one"
			
		case "twoGroups":
			
			// Split the groups here
			int groupLimit;
			
			int positionInLayer = 0;
			
			// We don't need to differentiate between odd and even number of neurons here
			// the neuron more is in the upper group
			groupLimit = hiddenNeurons.get(0).length/2;

			// From each input neuron...
			for (int n = 0; n < inputNeurons.length; n++){
				
				// ...to each neuron in the nearest group
				// Lower group
				if (n < (inputNeurons.length/2)){
						
					for (int m = 0; m < groupLimit; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+positionInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+n+" | "+m);
						
						// Note: The input is selected by the number of the input neuron
						inputConnections[positionInLayer] = new Connection(inputNeurons[n],
								hiddenNeurons.get(0)[m], n, INITWEIGHT, m);
						
						positionInLayer++;
					}// for (int m = 0; m < groupLimit; m++)
				}// if (n < (inputNeurons.length/2))
				
				// Upper group
				else {
					
					for (int m = groupLimit; m < hiddenNeurons.get(0).length; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+positionInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+(n-(inputNeurons.length/2))+" | "+m);
						
						// Note: The input is select by the number of the input neuron - the half of the input neurons
						inputConnections[positionInLayer] = new Connection(inputNeurons[n],
								hiddenNeurons.get(0)[m], (n-(inputNeurons.length/2)), INITWEIGHT, m);
						
						positionInLayer++;
					}// for (int m = groupLimit; m < hiddenNeurons.get(0).length; m++)
				}// else (if (n < (inputNeurons.length/2)))
			}// for (int n = 0; n < inputNeurons.length; n++)
			break;// case "twoGroups"
		
		// "each"
		default:
			
			int posInLayer = 0;
			
			// From each input neuron...
			for (int n = 0; n < inputNeurons.length; n++){
			
				// ...to each neuron of the 1st hidden layer
				for (int conn = 0; conn < hiddenNeurons.get(0).length; conn++){
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+posInLayer+"]\t|\t["+n+"]\t [0]["+conn+"]\t\t "+n+" | "+conn);
					
					inputConnections[posInLayer] = new Connection(inputNeurons[n],
							hiddenNeurons.get(0)[conn], n, INITWEIGHT, conn);
					
					posInLayer++;
				}// for (int conn = 0; conn < hiddenNeurons.get(0).length; conn++)
			}// for (int n = 0; n < inputNeurons.length; n++)
		}// switch (inputTopology)
		
		/*** Connections between hidden layers ***/
		if (hiddenNeurons.size() > 1){
				
			// Test output: multilayerPerceptronTest
			if (multiLayerPerceptronTest)
				System.out.println("\thiddenConnection| hiddenNeuron | hiddenNeuron\tinp|pos");
			
			switch (hiddenTopology){
			
			case "one":
				
				// For each layer
				for (int l = 0; l < (hiddenNeurons.size() - 1); l++){
					
					if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length){

						// Neuron of the next layer
						int m = 0;
						
						// Input connection is going to
						int i = 0;
						
						// From each neuron in the current layer...
						for (int n = 0; n < hiddenNeurons.get(l).length; n++){
							
							// Go to next neuron in next layer if all inputs are connected
							if (i >= hiddenNeurons.get(l+1)[m].getNumberOfInputs()){
								i = 0;
								m++;
							}
							 
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+n+"]\t|\t["+l+"]["+n+"]\t ["+(l+1)+"]["+m+"]\t\t "+i+" | "+m);
							
							// ...to a hidden neuron (initialise the connection weight with 1)
							hiddenConnections.get(l)[n] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], i, INITWEIGHT, m);
							
							// Go to next input
							i++;
						}// for (int n = 0; n < hiddenNeurons.get(l).length; n++)
					}// if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length)
					
					else {

						// Neuron of the current layer
						int n = 0;
						
						// Range of input neurons with one more connection
						int range = (hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)
										* ((hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length) + 1);
						int rangeStart  = (hiddenNeurons.get(l+1).length - ((hiddenNeurons.get(l+1).length - range) / 2)) - range;
						
						// Number of connections to next layer
						int c;
						
						// Current connection to next layer
						int c_i = 0;
						
						// From each hidden neuron...
						for (int m = 0; m < hiddenNeurons.get(l+1).length; m++){
							
							// We need more connections in the middle
							if ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length) != 0){
								
								// Inside the range of the neurons with one more connection
								if (m > rangeStart && m <= (rangeStart + range))
									c = (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length) + 1;
								
								// Outside the range
								else 
									c = hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length;
							}// if ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length) != 0)
							
							// Whole-number ratio
							else 
								c = hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length;
							
							// Go to next neuron of the current layer if all connections are done
							if (c_i >= c){
								n++;
								
								c_i = 0;
							}
							
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+m+"]\t|\t["+l+"]["+n+"]\t ["+(l+1)+"]["+m+"]\t\t 0 | "+m);
							
							// ...to a hidden neuron (initialise the connection weight with 1)
							hiddenConnections.get(l)[m] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], 0, INITWEIGHT, m);
							
							// Next connection
							c_i++;
						}// for (int m = 0; m < hiddenNeurons.get(l+1).length; m++)
					}// else (if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length))
				}// for (int l = 0; l < (hiddenNeurons.size() - 1); l++)
				break;// case "one"
				
			default: // "each"
				
				int positionInLayer;
				
				// For each hidden layer...
				for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
					positionInLayer = 0;
					
					// ...from each hidden neuron...
					for (int n = 0; n < hiddenNeurons.get(layer).length; n++){
					
						// ...to each neuron of the next hidden layer
						for (int conn = 0; conn < hiddenNeurons.get(layer+1).length; conn++){
							
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+positionInLayer+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["+conn+"]\t\t "+n+" | "+conn);	
							
							hiddenConnections.get(layer)[positionInLayer] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[conn], n, INITWEIGHT, conn);
							
							positionInLayer++;
						}// for (int conn = 0; conn < hiddenNeurons.get(layer+1).length; conn++)
					}// for (int n = 0; n < hiddenNeurons.get(layer).length; n++)
				}// for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++)
			}// switch (hiddenTopology)
		}// if (hiddenNeurons.size() > 1)
		
		/*** Connections between last hidden and output layer ***/
		
		// Test output: multilayerPerceptronTest
		if (multiLayerPerceptronTest)
			System.out.println("\toutputConnection| hiddenNeuron | outputNeuron\tinp|pos");
						
		switch (outputTopology){
		
		case "one":
			if (hiddenNeurons.get(hiddenNeurons.size() - 1).length >= outputNeurons.length){

				// Neuron of the next layer
				int m = 0;
				
				// Input connection is going to
				int i = 0;
				
				// From each hidden neuron...
				for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++){
					
					// Go to next neuron in next layer if all inputs are connected
					if (i >= outputNeurons[m].getNumberOfInputs()){
						i = 0;
						m++;
					}
					 
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+n+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+i+" | "+m);
					
					// ...to an output neuron
					outputConnections[n] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
							outputNeurons[m], i, INITWEIGHT, m);
					
					// Go to next input
					i++;
				}// for (int n = 0; n < inputNeurons.length; n++)
			}// if (inputNeurons.length >= hiddenNeurons.get(0).length)
			
			else {

				// Neuron of the current layer
				int n = 0;
				
				// Range of input neurons with one more connection
				int range = (outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length)
								* ((outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length) + 1);
				int rangeStart  = (outputNeurons.length - ((outputNeurons.length - range) / 2)) - range;
				
				// Number of connections to next layer
				int c;
				
				// Current connection to next layer
				int c_i = 0;
				
				// From each output neuron...
				for (int m = 0; m < outputNeurons.length; m++){
					
					// We need more connections in the middle
					if ((outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length) != 0){
						
						// Inside the range of the neurons with one more connection
						if (m > rangeStart && m <= (rangeStart + range))
							c = (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length) + 1;
						
						// Outside the range
						else 
							c = outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length;
					}// if ((outputNeurons.length % inputNeurons.length) != 0)
					
					// Whole-number ratio
					else 
						c = outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length;
					
					// Go to next neuron of the current layer if all connections are done
					if (c_i >= c){
						n++;
						
						c_i = 0;
					}
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+m+"]\t|\t["+n+"]\t [0]["+m+"]\t\t 0 | "+m);
					
					// ...to an output neuron
					outputConnections[m] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
															outputNeurons[m], 0, INITWEIGHT, m);
					
					// Next connection
					c_i++;
				}// for (int m = 0; m < outputNeurons.length; m++)
			}// else (if (hiddenNeurons.get(hiddenNeurons.size() - 1).length >= outputNeurons.length))
			break;// case "one"
			
		case "twoGroups":
			
			// Split the groups here
			int groupLimit;
			
			int posInLayer = 0;
			
			// We don't need to differentiate between odd and even number of neurons here
			// the neuron more is in the upper group
			groupLimit = outputNeurons.length/2;

			// From each hidden neuron...
			for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++){
				
				// ...to each neuron in the nearest group
				// Lower group
				if (n < (hiddenNeurons.get(hiddenNeurons.size() - 1).length/2)){
						
					for (int m = 0; m < groupLimit; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+posInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+n+" | "+m);
						
						// Note: The input is selected by the number of the input neuron
						outputConnections[posInLayer] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
								outputNeurons[m], n, INITWEIGHT, m);
						
						posInLayer++;
					}// for (int m = 0; m < groupLimit; m++)
				}// if (n < (hiddenNeurons.get(hiddenNeurons.size() - 1).length/2))
				
				// Upper group
				else {
					
					for (int m = groupLimit; m < outputNeurons.length; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+posInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+(n-(hiddenNeurons.get(hiddenNeurons.size() - 1).length/2))+" | "+m);
						
						// Note: The input is selected by the number of the hidden neuron - the half of the hidden neurons
						outputConnections[posInLayer] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
								outputNeurons[m], (n-(hiddenNeurons.get(hiddenNeurons.size() - 1).length/2)), INITWEIGHT, m);
						
						posInLayer++;
					}// for (int m = groupLimit; m < outputNeurons.length; m++)
				}// else (if (n < (hiddenNeurons.get(hiddenNeurons.size() - 1).length/2)))
			}// for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++)
			break;// case "twoGroups"
		
		default: // "each"

			int positionInLayer = 0;
			
			// From each neuron of the last hidden layer...			
			for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++){
			
				// ...to each output neuron
				for (int m = 0; m < outputNeurons.length; m++){
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+positionInLayer+"]\t|\t["+(hiddenNeurons.size()-1)+"]["+n+"]\t ["+m+"]\t\t "+n+" | "+m);	
					
					outputConnections[positionInLayer] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
							outputNeurons[m], n, INITWEIGHT, m);
					
					positionInLayer++;
				}// for (int m = 0; m < outputNeurons.length; m++)
			}// for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++)
		}// switch (outputTopology)
		
		keepGoing  = false;
	}// MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
		// int nrOutputNeurons, String inputTopology, String hiddenTopology)


	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons):
	* 	Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* 	each layer, and number of output neurons can (has to) be defined.
	* 	Each neuron will be connected to each neuron of the following layer.
	* 	There must be at least 1 hidden layer
	****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons){
		this(nrInputNeurons, nrHiddenNeurons, nrOutputNeurons, "each", "each", "each");
	}

	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
	*			int nrOutputNeurons):
	* 	Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* 	layer, number of layers and number of output neurons can (has to) be defined.
	* 	Each neuron will be connected to each neuron of the following layer.
	* 	There must be at least 1 hidden layer
	****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int nrHiddenNeuronsPerLayer, int numberHiddenLayers,
			int nrOutputNeurons){
		this(nrInputNeurons, getHiddenNeurons(nrHiddenNeuronsPerLayer, numberHiddenLayers),
				nrOutputNeurons, "each", "each", "each");
	}

	/****************************************************************************************
	* static int[] getHiddenNeurons(int nrHiddenNeuronsPerLayer, int numberHiddenLayers):
	* 	Helper function to create integer array with hidden neurons.
	****************************************************************************************/
private static int[] getHiddenNeurons(int nrHiddenNeuronsPerLayer, int numberHiddenLayers){
		int[] nrHiddenNeurons = new int[numberHiddenLayers];
	
		for (int i = 0; i < nrHiddenNeurons.length; i++)
			nrHiddenNeurons[i] = nrHiddenNeuronsPerLayer;
		
		return nrHiddenNeurons;
	}

public
	/****************************************************************************************
	* float[] run(float[] inputVector):
	* 	Executes the built multi-layer perceptron with given input vector and returns the
	* 	output vector.
	****************************************************************************************/
	float[] run(float[] inputVector){
		
		// Prevent wrong usage
		if (inputVector.length != inputNeurons.length){
			float[] vectorCopy = new float[inputNeurons.length];
			
			for (int i = 0; i < vectorCopy.length; i++){
				if (i >= inputVector.length)
					vectorCopy[i] = 0;
				else
					vectorCopy[i] = inputVector[i];
			}
			
			inputVector = new float[inputNeurons.length];
			inputVector = vectorCopy;
		}// if (inputVector.length != inputNeurons.length)
		
		//Set inputs
		for (int i = 0; i < inputNeurons.length; i++)
			inputNeurons[i].setInput(0, inputVector[i]);
		
		// Execute input layer
		for (int pos = 0; pos < inputConnections.length; pos++)					
			inputConnections[pos].run();
			
		// Execute hidden layer(s)
		for(int layer = 0; layer < (hiddenNeurons.size()-1); layer++){
			
			for (int pos = 0; pos < hiddenConnections.get(layer).length; pos++)
				hiddenConnections.get(layer)[pos].run();
		}
		
		// Execute output layer
		for (int pos = 0; pos < outputConnections.length; pos++)
			outputConnections[pos].run();
		
		// Now get the result(s)
		for (int i = 0; i < outputNeurons.length; i++)
			outputVector[i] = outputNeurons[i].getOutput();
			
		return outputVector;
	}// run()
	
	/****************************************************************************************
	* int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
	*				int abort, float trainingCoefficient):
	* 	Trains the multi layer perceptron.
	* 	Executes the perceptron with given input vector, compares with the given output vector,
	* 	and calculates (if necessary) new connection weights and neuron thresholds.
	* 	The training will be aborted (to avoid endless execution if no result can be found)
	* 	after the given number of trials.
	* 	The training coefficient defines the learning step size in backpropagation.
	* 	Returns the number of needed trials to get the correct result.
	****************************************************************************************/
	int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
					int abort, float trainingCoefficient){
		float[] outVector;
		Boolean wrong = false;
		
		// Prevent wrong usage (InVector will be corrected in run())
		if (trainingOutVector.length != outputNeurons.length){
			float[] vectorCopy = new float[trainingOutVector.length];
			
			for (int i = 0; i < vectorCopy.length; i++){
				if (i >= trainingOutVector.length)
					vectorCopy[i] = 0;
				else
					vectorCopy[i] = trainingOutVector[i];
			}
			
			trainingOutVector = new float[trainingOutVector.length];
			trainingOutVector = vectorCopy;
		}// if (trainingOutVector.length != outputNeurons.length)
		
		keepGoing = true;
		
		outVector = new float[outputNeurons.length];
		
		// We don't want endless executions
		int abortTraining = 0;
		
		// Test output: backpropagationTest
		if (backpropagationTest){
			
			// Print input connection weights
			System.out.print("Input layer:\t");
			
			for (int n = 0; n < inputConnections.length; n++)
				System.out.print("\t["+inputConnections[n].getConnectionWeight()+"]");
			
			// Print hidden connection weights
			for (int l = 0; l < hiddenConnections.size(); l++){
				
				System.out.print("\nHidden layer "+l+":\t");
				
				for (int n = 0; n < hiddenConnections.get(l).length; n++)
					System.out.print("\t["+hiddenConnections.get(l)[n].getConnectionWeight()+"]");
			}
			
			// Print output connection weights
			System.out.print("\nOutput layer:\t");
			
			for (int n = 0; n < outputConnections.length; n++)
				System.out.print("\t["+outputConnections[n].getConnectionWeight()+"]");

			// Print input thresholds
			System.out.print("\nInput thresholds:");
			
			for (int n = 0; n < inputNeurons.length; n++)
				System.out.print("\t["+inputNeurons[n].getThreshold()+"]");

			// Print hidden thresholds
			for (int l = 0; l < hiddenNeurons.size(); l++){
				
				System.out.print("\nHidden thresholds:");
			
				for (int n = 0; n < hiddenNeurons.get(l).length; n++)
					System.out.print("\t["+hiddenNeurons.get(l)[n].getThreshold()+"]");
			}

			// Print output thresholds
			System.out.print("\nOutput thresholds:");
			
			for (int n = 0; n < outputNeurons.length; n++)
				System.out.print("\t["+outputNeurons[n].getThreshold()+"]");
		}
		
		while(keepGoing){
			// Execute perceptron with training input vector
			outVector = run(trainingInVector);
			
			abortTraining++;
			
			// Wrong result, use backpropagation to find a better one and try again
			for (int i = 0; (i < outVector.length) && (wrong == false); i++){
				if ((outVector[i] < (trainingOutVector[i] - errorTolerance))
						|| (outVector[i] > (trainingOutVector[i] + errorTolerance)))
					wrong = true;					
			}
			
			// At least 1 wrong output
			if (wrong == true){
				backpropagation(outVector, trainingOutVector, trainingCoefficient);
				
				wrong = false;
			}
			
			// All results are like we want them, stop training
			else
				keepGoing = false;
			
			// Cancel training if it takes too long
			// Max number of trials ~2*10^9 (2^31)
			if (abortTraining >= abort)
				keepGoing = false;
			
			// Test output: backpropagationTest
//			if (backpropagationTest){
//				System.out.print(abortTraining+":\tResult Out: ");
//				for (int i = 0; i < outVector.length; i++)
//					System.out.print("\t["+ outVector[i] +"]");
//				System.out.print("\n");
//			}
		}// while(keepGoing)
		
		// Test output: backpropagationTest
		if (backpropagationTest){
			
			// Print input connection weights
			System.out.print("\nInput layer:\t");
			
			for (int n = 0; n < inputConnections.length; n++)
				System.out.print("\t["+inputConnections[n].getConnectionWeight()+"]");
			
			// Print hidden connection weights
			for (int l = 0; l < hiddenConnections.size(); l++){
				
				System.out.print("\nHidden layer "+l+":\t");
				
				for (int n = 0; n < hiddenConnections.get(l).length; n++)
					System.out.print("\t["+hiddenConnections.get(l)[n].getConnectionWeight()+"]");
			}
			
			// Print output connection weights
			System.out.print("\nOutput layer:\t");
			
			for (int n = 0; n < outputConnections.length; n++)
				System.out.print("\t["+outputConnections[n].getConnectionWeight()+"]");

			// Print input thresholds
			System.out.print("\nInput thresholds:");
			
			for (int n = 0; n < inputNeurons.length; n++)
				System.out.print("\t["+inputNeurons[n].getThreshold()+"]");

			// Print hidden thresholds
			for (int l = 0; l < hiddenNeurons.size(); l++){
				
				System.out.print("\nHidden thresholds:");
			
				for (int n = 0; n < hiddenNeurons.get(l).length; n++)
					System.out.print("\t["+hiddenNeurons.get(l)[n].getThreshold()+"]");
			}

			// Print output thresholds
			System.out.print("\nOutput thresholds:");
			
			for (int n = 0; n < outputNeurons.length; n++)
				System.out.print("\t["+outputNeurons[n].getThreshold()+"]");
			
			// Print final result output vector
			System.out.print("\n\nResult Out: ");
			
			for (int i = 0; i < outVector.length; i++)
				System.out.print("\t["+ outVector[i] +"]");
			
			System.out.print("\n");
		}
		
		return abortTraining;
	}// training()
	
	/****************************************************************************************
	* int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
	*				int abort):
	* 	Trains the multi layer perceptron.
	* 	Executes the perceptron with given input vector, compares with the given output vector,
	* 	and calculates (if necessary) new connection weights and neuron thresholds.
	* 	The training will be aborted (to avoid endless execution if no result can be found)
	* 	after the given number of trials.
	* 	The training coefficient is defined as 0.2.
	* 	Returns the number of needed trials to get the correct result.
	****************************************************************************************/
	int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
			int abort){
		int trials = training(trainingInVector, trainingOutVector, errorTolerance, abort, 0.2f);
		
		return trials;
	}
	
	/****************************************************************************************
	* void backpropagation(float[] resultOut, float[] wantedOut):
	* 	Calculates the differences for weights and thresholds and adds them.
	****************************************************************************************/
private	void backpropagation(float[] resultOut, float[] wantedOut, float trainingCoefficient){
		float weightDelta = 0;
		Connection[] connections = new Connection[0];
		Connection[] succConnections = new Connection[0];
		int numberOfNeurons;
		float outputOfNeuron;
		float inputOfNeuron;
		int position;
		
		// For calculating the weight differences for every connection loop over every
		// (connection) layer...
		for (int layer = 0; layer <= (hiddenConnections.size() + 1); layer++){
			
			// Outputconnection layer
			if (layer == (hiddenConnections.size() + 1)){
				connections = new Connection [outputConnections.length];
				connections = outputConnections;
				
				numberOfNeurons = outputNeurons.length;
			}// if (layer == (hiddenConnections.size() + 1))
			
			// InputConnection layer
			else if (layer == 0){
				
				// More than one hidden layer
				if (hiddenConnections.size() > 0)
					succConnections = hiddenConnections.get(0);

				else
					succConnections = outputConnections;

				connections = new Connection [inputConnections.length];
				connections = inputConnections;
				
				numberOfNeurons = inputNeurons.length;
			}// else if (layer == 0)
			
			// Hidden Layer
			else {

				if (layer == hiddenConnections.size())
					succConnections = outputConnections;

				else
					succConnections = hiddenConnections.get(layer);
				
				connections = new Connection [hiddenConnections.get(layer-1).length];
				connections = hiddenConnections.get(layer-1);
				
				numberOfNeurons = hiddenNeurons.get(layer-1).length;
			}// else (if (layer == (hiddenConnections.size() + 1)))
			
			// ...and now over all connections
			for (int conn = 0; conn < connections.length; conn++){
				
				// For output layer: Gradient descent
				if (layer == (hiddenConnections.size() + 1)){
					position = connections[conn].getPositionNeuronTo();
					
					weightDelta = wantedOut[position] - resultOut[position];
					
					// Everything else we need comes after the for-loops
				}// if (layer == (hiddenNeurons.size() - 1))
				
				// Da real backpropagation for the rest
				else {
					
					// Sum over successive neurons
					for (int succ = 0; succ < succConnections.length; succ++){
						
						// Not every connection in succConnections is a succulent connection
						if (connections[conn].getNeuronTo() == succConnections[succ].getNeuronFrom()){
							float sumOutputNeurons = 0;
							
							// Sum over output neurons
							for (int out = 0; out < outputNeurons.length; out++)
								sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
											resultOut[out] * (1- resultOut[out]));
							
							weightDelta += sumOutputNeurons;
							
							weightDelta *= succConnections[succ].getConnectionWeight();
						}// if (connections[conn].getNeuronTo() == succConnections[succ].getNeuronFrom())
					}// for (int succ = 0; succ < succConnections.length; succ++)
				}// else (if (layer == (hiddenNeurons.size() - 1)))
				
				outputOfNeuron = connections[conn].getNeuronTo().getOutput();
				inputOfNeuron = connections[conn].getNeuronFrom().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron) * inputOfNeuron);
					
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to connection weight
				connections[conn].addWeightDelta(weightDelta);
			}// for (int conn = 0; conn < connections.length; conn++)
			
			// Calculation of thresholds
			for (int thresh = 0; thresh < numberOfNeurons; thresh++){
				
				// Find the neuron in connection layer
				for (int neuron = 0; neuron < connections.length; neuron++){
					
					if (connections[neuron].getPositionNeuronTo() == thresh){
						float oldThresh = (-1) * connections[neuron].getNeuronTo().getThreshold();
						
						// For output layer: Gradient descent
						if (layer == (hiddenConnections.size() + 1))
							weightDelta = wantedOut[thresh] - resultOut[thresh];
						
						// Backpropagation for the rest
						else {
							
							// Sum over successive neurons
							for (int succ = 0; succ < succConnections.length; succ++){
								
								// Not every connection in succConnections is a succulent connection
								if (connections[neuron].getNeuronTo() == succConnections[succ].getNeuronFrom()){
									float sumOutputNeurons = 0;
									
									// Sum over output neurons
									for (int out = 0; out < outputNeurons.length; out++)
										sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
												      resultOut[out] * (1- resultOut[out]));
							
									weightDelta += sumOutputNeurons;
									
									weightDelta *= succConnections[succ].getConnectionWeight();
								}// if (connections[thresh].getNeuronTo() == succConnections[succ].getNeuronFrom())
							}// for (int succ = 0; succ < succConnections.length; succ++)
						}// else (if (layer == hiddenNeurons.size() - 1))
						
						outputOfNeuron = connections[neuron].getNeuronTo().getOutput();
							
						weightDelta *= (outputOfNeuron * (1 - outputOfNeuron));
							
						weightDelta *= trainingCoefficient;
							
						// Add calculated weight difference to old threshold and set new threshold
						connections[neuron].getNeuronTo().setThreshold(oldThresh + weightDelta);
						
						// End loop
						neuron = connections.length;
					}// if (connections[neuron].getPositionNeuronTo() == thresh)
				}// for (int neuron = 0; neuron < connections.length; neuron++)
			}// for (int thresh = 0; thresh < numberOfNeurons; thresh++)
		}// for (int layer = 0; layer <= (hiddenConnections.size() + 1); layer++)
	}// backpropagation()
	
	/****************************************************************************************
	* String savePerceptron(String fileName)
	* Saves weights and thresholds to file
	* Returns filename
	****************************************************************************************/
	String savePerceptron(String fileName){
		BufferedWriter writer = null;
		
		if (fileName == null){
			fileName = new String("aNN_MultiLayerPerceptron_" + Integer.toString(inputNeurons.length));
			
			for (int i = 0; i < hiddenNeurons.size(); i++)
				fileName += "_"+ Integer.toString(hiddenNeurons.get(i).length);
									
			fileName += "_"+ inputTopology + "_"+ hiddenTopology + "_"+ outputTopology;
		}
		
		File file = new File(fileName);
		
		if (file.exists()){
			int add = 0;
			String oldFileName = new String(fileName);
			
			while (file.exists()){
				add++;
				
				fileName = oldFileName;
				
				fileName += "_" + Integer.toString(add);
				
				file = new File(fileName);
			}
		}
		
		try
		{
		    writer = new BufferedWriter( new FileWriter(file));
		    
		    // Save weights
		    writer.write("inputConnWeights");	writer.newLine();
		    writer.write(Integer.toString(inputConnWeights.length));	writer.newLine();
		    
		    for (int w = 0; w < inputConnWeights.length; w++){
		    	writer.write(Float.toString(inputConnWeights[w]));	writer.newLine();
		    }

		    writer.write("hiddenConnWeights");	writer.newLine();
		    writer.write(Integer.toString(hiddenConnWeights.size()));	writer.newLine();
		    
		    for (int lyr = 0; lyr < hiddenConnWeights.size(); lyr++){
		    	writer.write(Integer.toString(hiddenConnWeights.get(lyr).length));	writer.newLine();
		    	
		    	for (int w = 0; w < hiddenConnWeights.get(lyr).length; w++){
			    	writer.write(Float.toString(hiddenConnWeights.get(lyr)[w]));	writer.newLine();
			    }
		    }
		    	
		    writer.write("outputConnWeights");	writer.newLine();
		    writer.write(Integer.toString(outputConnWeights.length));	writer.newLine();
		    
		    for (int w = 0; w < outputConnWeights.length; w++){
		    	writer.write(Float.toString(outputConnWeights[w]));	writer.newLine();
		    }
		    
		    // Add thresholds
		    writer.write("inputThresholds");	writer.newLine();
		    writer.write(Integer.toString(inputNeurons.length));	writer.newLine();
		    
		    for (int n = 0; n < inputNeurons.length; n++){
				writer.write(Float.toString(inputNeurons[n].getThreshold()));	writer.newLine();
		    }
			
		    writer.write("hiddenThresholds");	writer.newLine();
		    writer.write(Integer.toString(hiddenNeurons.size()));	writer.newLine();
		    
		    for (int lyr = 0; lyr < hiddenNeurons.size(); lyr++){
		    	writer.write(Integer.toString(hiddenNeurons.get(lyr).length));	writer.newLine();
		    	
				for (int n = 0; n < hiddenNeurons.get(lyr).length; n++){
					writer.write(Float.toString(hiddenNeurons.get(lyr)[n].getThreshold()));	writer.newLine();
			    }
			}
			
		    writer.write("outputThresholds");	writer.newLine();
		    writer.write(Integer.toString(outputNeurons.length));	writer.newLine();
		    
		    for (int n = 0; n < outputNeurons.length; n++){
				writer.write(Float.toString(outputNeurons[n].getThreshold())); writer.newLine();
		    }
		}
		
		catch ( IOException e){}
		
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		        
		        return fileName;
		    }
		    
		    catch ( IOException e){}
		}
		
		return "ERROR";
		// string to float: Float.parseFloat("25");
	}// savePerceptron()
	
	/****************************************************************************************
	* void loadPerceptron(String fileName)
	* Loads weights and thresholds from file and sets it to perceptron
	****************************************************************************************/
	void loadPerceptron(String fileName){
		if (fileName != null){
			
			BufferedReader reader;
			
			try {
				reader = new BufferedReader(new FileReader (fileName));
			} catch (FileNotFoundException e) {
				reader = null;
				e.printStackTrace();
			}
			
			String line = null;
			String mode = new String("default");
			int amount = 0;
			int nrLayers = 0;
			int layer = 0;
			int read = 0;
			
			try {
				while((line = reader.readLine()) != null){
					
					switch (mode) {
					case "inputConnWeights":
						if (read == 0)
							amount = Integer.parseInt(line);
						
						else if (read <= amount)
							inputConnections[read-1].setWeight(Float.parseFloat(line));

						read++;
						
						if (read > amount){
							amount = 0;
							read = 0;
							mode = "default";
						}
						break;
						
					case "hiddenConnWeights":
						if (read == 0)
							nrLayers = Integer.parseInt(line);
						
						else if (read == 1)
							amount = Integer.parseInt(line);
						
						else if (read <= (amount+1))
							hiddenConnections.get(layer)[read-2].setWeight(Float.parseFloat(line));
						
						read++;
							
						if ((read > (amount+1)) && (layer < (nrLayers-1))){
							read = 1;
							layer++;
						}
						
						else if (read > (amount+1)){
							amount = 0;
							layer = 0;
							read = 0;
							mode = "default";
						}
						break;

					case "outputConnWeights":
						if (read == 0)
							amount = Integer.parseInt(line);
						
						else if (read <= amount)
							outputConnections[read-1].setWeight(Float.parseFloat(line));

						read++;
						
						if (read > amount){
							amount = 0;
							read = 0;
							mode = "default";
						}
						break;

					case "inputThresholds":
						if (read == 0)
							amount = Integer.parseInt(line);
						
						else if (read <= amount)
							inputNeurons[read-1].setThreshold(Float.parseFloat(line));

						read++;
						
						if (read > amount){
							amount = 0;
							read = 0;
							mode = "default";
						}
						break;
						
					case "hiddenThresholds":
						if (read == 0)
							nrLayers = Integer.parseInt(line);
						
						else if (read == 1)
							amount = Integer.parseInt(line);
						
						else if (read <= (amount+1))
							hiddenNeurons.get(layer)[read-2].setThreshold(Float.parseFloat(line));
						
						read++;
							
						if ((read > (amount+1)) && (layer < (nrLayers-1))){
							read = 1;
							layer++;
						}
						
						else if (read > (amount+1)){
							amount = 0;
							layer = 0;
							read = 0;
							mode = "default";
						}
						break;

					case "outputThresholds":
						if (read == 0)
							amount = Integer.parseInt(line);
						
						else if (read <= amount)
							outputNeurons[read-1].setThreshold(Float.parseFloat(line));

						read++;
						
						if (read > amount){
							amount = 0;
							read = 0;
							mode = "default";
						}
						break;
						
					default:
						if (line.equals("inputConnWeights")){
							mode = "inputConnWeights";
						}
						
						else if (line.equals("hiddenConnWeights")){
							mode = "hiddenConnWeights";
						}
						
						else if (line.equals("outputConnWeights")){
							mode = "outputConnWeights";
						}
						
						else if (line.equals("inputThresholds")){
							mode = "inputThresholds";
						}
						
						else if (line.equals("hiddenThresholds")){
							mode = "hiddenThresholds";
						}
						
						else if (line.equals("outputThresholds")){
							mode = "outputThresholds";
						}
					}
				}
				
				reader.close();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}// loadPerceptron()
}// class MultiLayerPerceptron