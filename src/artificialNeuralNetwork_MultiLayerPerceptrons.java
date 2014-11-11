/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments, TechDoc
* 		Save-Funktion um weights und thresholds zu speichern
* 		System.out.prints komplett für multiLayerPerceptron-Test (Tabelle, wie bei inputTopology each)
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
* automatically builds a multi-layer perceptron, using neurons with logistic function, see
* description of constructors for more details
* Max. number of neurons per layer: 32
************************************************************************************************/
class MultiLayerPerceptron
{
private
	Neuron[] inputNeurons;
	Neuron[] hiddenLayer;
	ArrayList<Neuron[]> hiddenNeurons;
	Neuron[] outputNeurons;
	int[] neededNeuronInputs;
	float[] inputConnWeights;
	float[] hiddenLyrConnWeights;
	ArrayList<float[]> hiddenConnWeights;
	float[] outputConnWeights;
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

public
	// Constructor
	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
	*			int nrOutputNeurons, String inputTopology, String hiddenTopology):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* The type of connection between the layers has to be defined.
	* It's not possible to have more output neurons than hidden neurons
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons,
							String inputTopology, String hiddenTopology, String outputTopology){
	
		neededNeuronInputs = new int[nrHiddenNeurons.length + 1];
		
		hiddenNeurons = new ArrayList<Neuron[]>();
		hiddenConnWeights = new ArrayList<float[]>();
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
		
		/*** Input layer ***/
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
		
		switch (inputTopology){
		case "one":
			// Every input neuron will be connected to the nearest hidden neuron
			if (nrInputNeurons >= nrHiddenNeurons[0]){
				inputConnWeights = new float[nrInputNeurons];
				
				neededNeuronInputs[0] = inputConnWeights.length / nrHiddenNeurons[0];
				
				// Maybe we need 1 more input
				if ((inputConnWeights.length % nrHiddenNeurons[0]) != 0)
					neededNeuronInputs[0]++;
			}
				
			// Every hidden neuron will be connected to the nearest input neuron
			else{
				inputConnWeights = new float[nrHiddenNeurons[0]];
				
				neededNeuronInputs[0] = inputConnWeights.length / nrInputNeurons;
				
				// Maybe we need 1 more input
				if ((inputConnWeights.length % nrInputNeurons) != 0)
					neededNeuronInputs[0]++;
			}
			break;
			
		case "twoGroups":
			// The hidden neurons will be split into two groups
			// Every input neuron will be connected to every neuron of the nearest group
			
			// Prevent wrong usage
			// Two groups with one input neuron makes no sense
			if (nrInputNeurons == 1)
				nrInputNeurons++;
			
			// and with just on hidden neuron neither
			if (nrHiddenNeurons[0] == 1)
				nrHiddenNeurons[0]++;
			
			int neededConnections = nrHiddenNeurons[0]/2 * nrInputNeurons;
			
			// The upper group will have one more neuron
			if ((nrHiddenNeurons[0] % 2) != 0)
				neededConnections += (nrInputNeurons/2);
				
			// The upper group
			inputConnWeights = new float[neededConnections];
			
			neededNeuronInputs[0] = nrInputNeurons/2;
			
			if ((nrInputNeurons % 2) != 0)
				neededNeuronInputs[0]++;	
			break;
		
		default: // "each"
			inputConnWeights = new float[nrInputNeurons * nrHiddenNeurons[0]];
			neededNeuronInputs[0] = nrInputNeurons;
		}// switch (inputTopology)
		
		inputNeurons = new Neuron[nrInputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < nrInputNeurons; i++){
			inputNeurons[i] = new Neuron(1);
			
			// Initial values: all thresholds = 0
			inputNeurons[i].setThreshold(0);
		}
		
		// Initialize weights, all weights = 1
		for (int i = 0; i < inputConnWeights.length; i++)
			inputConnWeights[i] = 1;
		
		// We need as many connections as connection weights (obviously)
		inputConnections = new Connection[inputConnWeights.length];
		
		/*** Hidden layer ***/		
		if (nrHiddenNeurons.length == 1){
			hiddenLayer  = new Neuron[nrHiddenNeurons[0]];
			
			hiddenNeurons.add(hiddenLayer);
		}
			
		else {
			for (int lyr = 0; lyr < nrHiddenNeurons.length; lyr++){
				hiddenLayer = new Neuron[nrHiddenNeurons[lyr]];

				hiddenNeurons.add(hiddenLayer);
			}
			
			switch (hiddenTopology){
			case "one":
				// Every hidden neuron will be connected to the nearest hidden neuron in the next layer			
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1])
						hiddenLyrConnWeights = new float[nrHiddenNeurons[lyr]];
					else
						hiddenLyrConnWeights = new float[nrHiddenNeurons[lyr+1]];
					
					for (int conn = 0; conn < hiddenLyrConnWeights.length; conn++)
						hiddenLyrConnWeights[conn] = 1;
					
					hiddenConnWeights.add(hiddenLyrConnWeights);
					
					neededNeuronInputs[lyr + 1] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
					
					// Maybe we need 1 more input
					if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
						neededNeuronInputs[lyr + 1]++;
				}
				break;
			
			case "cross":
				// Every neuron will be connected to two neurons of the following layer,
				// the neuron a position up and the neuron a position down
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1])
						hiddenLyrConnWeights = new float[nrHiddenNeurons[lyr] * 2];
					else
						hiddenLyrConnWeights = new float[nrHiddenNeurons[lyr+1] * 2];
					
					for (int conn = 0; conn < hiddenLyrConnWeights.length; conn++)
						hiddenLyrConnWeights[conn] = 1;
					
					hiddenConnWeights.add(hiddenLyrConnWeights);
					
					neededNeuronInputs[lyr + 1] = (2 * nrHiddenNeurons[lyr]) / nrHiddenNeurons[lyr+1];
					
					// Maybe we need 1 more input
					if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
						neededNeuronInputs[lyr + 1]++;
				}
				break;
			
			case "zigzag":
				// Every neuron will be connected to two neurons of the following layer,
				// the neuron at same position and the neuron a position up
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1])
						hiddenLyrConnWeights = new float[nrHiddenNeurons[lyr] * 2];
					else
						hiddenLyrConnWeights = new float[nrHiddenNeurons[lyr+1] * 2];
					
					for (int conn = 0; conn < hiddenLyrConnWeights.length; conn++)
						hiddenLyrConnWeights[conn] = 1;
					
					hiddenConnWeights.add(hiddenLyrConnWeights);
					
					neededNeuronInputs[lyr + 1] = 2 * (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]);
					
					// Maybe we need 1 more input
					if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
						neededNeuronInputs[lyr + 1]++;
				}
				break;
			
			default: // each
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					hiddenLyrConnWeights = new float[nrHiddenNeurons[lyr] * nrHiddenNeurons[lyr+1]];
					
					for (int conn = 0; conn < hiddenLyrConnWeights.length; conn++)
						hiddenLyrConnWeights[conn] = 1;
					
					hiddenConnWeights.add(hiddenLyrConnWeights);
					
					neededNeuronInputs[lyr + 1] = nrHiddenNeurons[lyr];
				}
			}// switch (hiddenTopology)
		} // else (hiddenLayers > 1)
					
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int i = 0; i < hiddenNeurons.size(); i++){
			
			for (int n = 0; n < hiddenNeurons.get(i).length; n++){
				hiddenNeurons.get(i)[n] = new Neuron(neededNeuronInputs[i]);
					
				// Initial values: all thresholds = 0
				hiddenNeurons.get(i)[n].setThreshold(0);
			}
		}
		 
		/*** Output layer ***/		
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		switch (outputTopology){
		case "one":
			// Every hidden neuron will be connected to the nearest output neuron
			if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons){
				outputConnWeights = new float[nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			
				neededNeuronInputs[nrHiddenNeurons.length] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
				
				// Maybe we need 1 more input
				if ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons) != 0)
					neededNeuronInputs[nrHiddenNeurons.length]++;
			}
			
			// Every output neuron will be connected to the nearest hidden neuron
			else{
				outputConnWeights = new float[nrOutputNeurons];
			
				neededNeuronInputs[nrHiddenNeurons.length] = nrOutputNeurons / nrHiddenNeurons[nrHiddenNeurons.length - 1];
				
				// Maybe we need 1 more input
				if ((nrOutputNeurons % nrHiddenNeurons[nrHiddenNeurons.length - 1]) != 0)
					neededNeuronInputs[nrHiddenNeurons.length]++;
			}
			break;
			
		default: // "each"
			outputConnWeights = new float[nrOutputNeurons * nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			
			neededNeuronInputs[nrHiddenNeurons.length] = nrHiddenNeurons[nrHiddenNeurons.length - 1];
		}// switch (outputTopology)
		
		for (int i = 0; i < outputNeurons.length; i++){
			outputNeurons[i] = new Neuron(neededNeuronInputs[nrHiddenNeurons.length]);
			
			// Initial values: thresholds = 0
			outputNeurons[i].setThreshold(0);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		for (int i = 0; i < (outputConnWeights.length); i++)
			outputConnWeights[i] = 1;
		
		outputConnections = new Connection[outputConnWeights.length];
		
		/*** Connections ***/
		// Connections between input layer and 1st hidden layer
		int positionInLayer = 0;
		int inp = 0;
		int neur = 0;
		
		// Test output: multilayerPerceptronTest
//		System.out.println("\tinputConnection\t| inputNeuron | hiddenNeuron\tinp|pos");
		
		switch (inputTopology){
		case "one":

			if (inputNeurons.length >= hiddenNeurons.get(0).length){
				
				// From each input neuron...
				for (int inNeur = 0; inNeur < inputNeurons.length; inNeur++){
					
					// ... with step size of the layer difference
					// (We need more connections in the middle)
					if ((inputNeurons.length % hiddenNeurons.get(0).length) != 0){

							// (Even number of input neurons)
							if ((inputNeurons.length % 2) == 0){
							
								// (Second extra connection)
								if (inNeur >= ((inputNeurons.length / 2)
										+ (inputNeurons.length / hiddenNeurons.get(0).length))){
									neur = (inNeur - 2) / (inputNeurons.length / hiddenNeurons.get(0).length);
								
									inp = ((inNeur + 1) % ((inputNeurons.length / hiddenNeurons.get(0).length) + 1));
								}
					
								// (First extra connection)
								else if ((inNeur >= (inputNeurons.length / 2) - 1)){
									neur = (inNeur - 1) / (inputNeurons.length / hiddenNeurons.get(0).length);
									
									inp = ((inNeur + 1) % ((inputNeurons.length / hiddenNeurons.get(0).length) + 1));
								}
								
								else {
									neur = inNeur / (inputNeurons.length / hiddenNeurons.get(0).length);
								
									inp = (inNeur % (inputNeurons.length / hiddenNeurons.get(0).length));
								}
							}
						
							// (Odd number of neurons)
							else if (((inputNeurons.length % 2) != 0)
										&& (inNeur >= ((inputNeurons.length / 2)
												+ (inputNeurons.length / hiddenNeurons.get(0).length)))){
								neur = (inNeur - 1) / (inputNeurons.length / hiddenNeurons.get(0).length);
								
								inp = ((inNeur + 1) % ((inputNeurons.length / hiddenNeurons.get(0).length) + 1));
							}
							
							else {
								neur = inNeur / (inputNeurons.length / hiddenNeurons.get(0).length);
								
								inp = (inNeur % (inputNeurons.length / hiddenNeurons.get(0).length));
							}
					}
					
					// (Whole-number ratio)
					else {
						neur = inNeur / (inputNeurons.length / hiddenNeurons.get(0).length);
						
						inp = (inNeur % (inputNeurons.length / hiddenNeurons.get(0).length));
					}
					
					// Test output: multilayerPerceptronTest
					System.out.println("\t\t["+inNeur+"]\t|\t["+inNeur+"]\t [0]["+neur+"]\t\t "+inp+" | "+neur);
					
					// ...to a hidden neuron
					inputConnections[inNeur] = new Connection(inputNeurons[inNeur],
							hiddenNeurons.get(0)[neur], inp, inputConnWeights[inNeur], neur);
				}
			}
			else {
				// From each hidden neuron...
				for (int hidNeur = 0; hidNeur < hiddenNeurons.get(0).length; hidNeur++){
					
					// ... with step size of the layer difference
					// (We need more connections in the middle)
					if ((hiddenNeurons.get(0).length % inputNeurons.length) != 0){

							// (Even number of hidden neurons)
							if ((hiddenNeurons.get(0).length % 2) == 0){
							
								// (Second extra connection)
								if (hidNeur >= ((hiddenNeurons.get(0).length / 2)
										+ (hiddenNeurons.get(0).length / inputNeurons.length)))
									neur = (hidNeur - 2) / (hiddenNeurons.get(0).length / inputNeurons.length);
					
								// (First extra connection)
								else if ((hidNeur >= (hiddenNeurons.get(0).length / 2) - 1))
									neur = (hidNeur - 1) / (hiddenNeurons.get(0).length / inputNeurons.length);
								
								else
									neur = hidNeur / (hiddenNeurons.get(0).length / inputNeurons.length);
							}
						
							// (Odd number of neurons)
							else if (((hiddenNeurons.get(0).length % 2) != 0)
										&& (hidNeur >= ((hiddenNeurons.get(0).length / 2)
												+ (hiddenNeurons.get(0).length / inputNeurons.length))))
								neur = (hidNeur - 1) / (hiddenNeurons.get(0).length / inputNeurons.length);
							
							else
								neur = hidNeur / (hiddenNeurons.get(0).length / inputNeurons.length);
					}
					
					// (Whole-number ratio)
					else
						neur = hidNeur / (hiddenNeurons.get(0).length / inputNeurons.length);
					
					// Test output: multilayerPerceptronTest
					System.out.println("\t\t["+hidNeur+"]\t|\t["+neur+"]\t [0]["+hidNeur+"]\t\t 0 | "+hidNeur);
					
					// ...to a hidden neuron
					inputConnections[hidNeur] = new Connection(inputNeurons[neur],
							hiddenNeurons.get(0)[hidNeur], 0, inputConnWeights[hidNeur], hidNeur);
				}
			}
			break;
			
		case "twoGroups": // From each input neuron...
			// Split the groups here
			int groupLimit;
			
			if ((hiddenNeurons.get(0).length % 2) == 0)
				groupLimit = hiddenNeurons.get(0).length/2;
			else
				groupLimit = (hiddenNeurons.get(0).length/2) + 1;
			
			for (inp = 0; inp < inputNeurons.length; inp++){
				
				// ...to each neuron in the nearest group
				if (inp < (inputNeurons.length/2)){
						
					for (int conn = 0; conn < groupLimit; conn++){
						
						inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
								hiddenNeurons.get(0)[conn], inp, inputConnWeights[positionInLayer], conn);
						
						positionInLayer++;
					}
				}
				else {
					for (int conn = groupLimit; conn < hiddenNeurons.get(0).length; conn++){
						
						inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
								hiddenNeurons.get(0)[conn], inp, inputConnWeights[positionInLayer], conn);
						
						positionInLayer++;
					}
				}
			}
			break;
		
		default: // "each"
			// From each input neuron...
			for (inp = 0; inp < inputNeurons.length; inp++){
			
				// ...to each neuron of the 1st hidden layer
				for (int conn = 0; conn < hiddenNeurons.get(0).length; conn++){
					
					inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
							hiddenNeurons.get(0)[conn], inp, inputConnWeights[positionInLayer], conn);
					
					positionInLayer++;
				}
			}
		}// switch (inputTopology)
		
		// Connections between hidden layers
		if (hiddenNeurons.size() > 1){
			
			for (int lyr = 0; lyr < hiddenNeurons.size() - 1; lyr++){
				hiddenLyrConnections = new Connection[hiddenConnWeights.get(lyr).length];

				hiddenConnections.add(hiddenLyrConnections);
			}
				
			// Test output: multilayerPerceptronTest
//			System.out.println("\thiddenConnection| hiddenNeuron | hiddenNeuron\tinp|pos");
			
			switch (hiddenTopology){
			case "one":
				for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
					inp = 0;
					neur = 0;
			
					if (hiddenNeurons.get(layer).length >= hiddenNeurons.get(layer+1).length){
						
						// From each neuron...
						for (int hidNeur = 0; hidNeur < hiddenNeurons.get(layer).length; hidNeur++){
							
							// ... with step size of the layer difference
							// (We need more connections in the middle)
							if ((hiddenNeurons.get(layer).length % hiddenNeurons.get(layer+1).length) != 0){

									// (Even number of input neurons)
									if ((hiddenNeurons.get(layer).length % 2) == 0){
									
										// (Second extra connection)
										if (hidNeur >= ((hiddenNeurons.get(layer).length / 2)
												+ (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length))){
											neur = (hidNeur - 2) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
										
											inp = ((hidNeur + 1) % ((hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) + 1));
										}
							
										// (First extra connection)
										else if ((hidNeur >= (hiddenNeurons.get(layer).length / 2) - 1)){
											neur = (hidNeur - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
											
											inp = ((hidNeur + 1) % ((hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) + 1));
										}
										
										else {
											neur = hidNeur / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
										
											inp = (hidNeur % (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length));
										}
									}
								
									// (Odd number of neurons)
									else if (((hiddenNeurons.get(layer).length % 2) != 0)
												&& (hidNeur >= ((hiddenNeurons.get(layer).length / 2)
														+ (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length)))){
										neur = (hidNeur - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
										
										inp = ((hidNeur + 1) % ((hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) + 1));
									}
									
									else {
										neur = hidNeur / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
										
										inp = (hidNeur % (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length));
									}
							}
							
							// (Whole-number ratio)
							else {
								neur = hidNeur / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
								
								inp = (hidNeur % (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length));
							}
							
							// Test output: multilayerPerceptronTest
							System.out.println("\t\t["+hidNeur+"]\t|\t["+(layer)+"]["+hidNeur+"]\t ["+(layer+1)+"]["+neur+"]\t\t "+inp+" | "+neur);
							
							// ...to a hidden neuron
							hiddenConnections.get(layer)[hidNeur] = new Connection(hiddenNeurons.get(layer)[hidNeur],
									hiddenNeurons.get(0)[neur], inp, hiddenConnWeights.get(layer)[hidNeur], neur);
						}
					}
					else {
						// From each neuron...
						for (int hidNeur = 0; hidNeur < hiddenNeurons.get(layer+1).length; hidNeur++){
							
							// ... with step size of the layer difference
							// (We need more connections in the middle)
							if ((hiddenNeurons.get(layer+1).length % hiddenNeurons.get(layer).length) != 0){

									// (Even number of hidden neurons)
									if ((hiddenNeurons.get(layer+1).length % 2) == 0){
									
										// (Second extra connection)
										if (hidNeur >= ((hiddenNeurons.get(layer+1).length / 2)
												+ (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length)))
											neur = (hidNeur - 2) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							
										// (First extra connection)
										else if ((hidNeur >= (hiddenNeurons.get(layer+1).length / 2) - 1))
											neur = (hidNeur - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
										
										else
											neur = hidNeur / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
									}
								
									// (Odd number of neurons)
									else if (((hiddenNeurons.get(layer+1).length % 2) != 0)
												&& (hidNeur >= ((hiddenNeurons.get(layer+1).length / 2)
														+ (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length))))
										neur = (hidNeur - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
									
									else
										neur = hidNeur / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							}
							
							// (Whole-number ratio)
							else
								neur = hidNeur / (hiddenNeurons.get(0).length / hiddenNeurons.get(layer).length);
							
							// Test output: multilayerPerceptronTest
							System.out.println("\t\t["+hidNeur+"]\t|\t["+neur+"]\t [0]["+hidNeur+"]\t\t 0 | "+hidNeur);
							
							// ...to a hidden neuron
							hiddenConnections.get(layer)[hidNeur] = new Connection(hiddenNeurons.get(layer)[neur],
									hiddenNeurons.get(layer+1)[hidNeur], 0, hiddenConnWeights.get(layer)[hidNeur], hidNeur);
						}
					}
				}
				break;
			
			case "cross":
				for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
					inp = 0;
					
					if (hiddenNeurons.get(layer).length >= hiddenNeurons.get(layer+1).length){
					
						// From each hidden neuron...
						for (int n = 0; n < hiddenNeurons.get(layer).length; n++){
							
							// ... with step size of the layer difference
							// (We need more connections in the middle)
							if ((hiddenNeurons.get(layer).length % hiddenNeurons.get(layer+1).length) != 0){

									// (Even number of input neurons)
									if ((hiddenNeurons.get(layer).length % 2) == 0){
									
										// (Second extra connection)
										if (n >= ((hiddenNeurons.get(layer).length / 2)
												+ (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length)))
											neur = (n - 2) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							
										// (First extra connection)
										else if ((n >= (hiddenNeurons.get(layer).length / 2) - 1))
											neur = (n - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
										
										else
											neur = n / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
									}
								
									// (Odd number of neurons)
									else if (((hiddenNeurons.get(layer).length % 2) != 0)
												&& (n >= ((hiddenNeurons.get(layer).length / 2)
														+ (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length))))
										neur = (n - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
									
									else
										neur = n / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							}
							
							// (Whole-number ratio)
							else
								neur = n / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							
							inp = n % (2 * hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							
							// ...to the neuron 1 position down of the next hidden layer...
							if (neur > 0){
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+(n*2)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["
													+(neur-1)+"]\t\t "+inp+" | "+(neur-1));							
					
								hiddenConnections.get(layer)[n*2] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[neur-1], inp, hiddenConnWeights.get(layer)[n*2], (neur-1));
							}
							
							else {
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+(n*2)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"][0]\t\t "
													+inp+" | 0");	
								
								hiddenConnections.get(layer)[n*2] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[0], inp, hiddenConnWeights.get(layer)[n*2], 0);
							}
							
							inp = (2 * hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) - inp - 1;
							
							// ...and the neuron 1 position up of the next hidden layer
							if (neur < hiddenNeurons.get(layer+1).length - 1){
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["
													+(neur+1)+"]\t\t "+inp+" | "+(neur+1));							
					
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[neur+1], inp, hiddenConnWeights.get(layer)[(n*2)+1], (neur+1));
							}
							else{
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["
													+neur+"]\t\t "+inp+" | "+neur);	
								
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[neur], inp, hiddenConnWeights.get(layer)[(n*2)+1], neur);
							}
						}
					}
					
					else {
						
						// From each hidden neuron (of the next layer)...
						for (int n = 0; n < hiddenNeurons.get(layer+1).length; n++){
							
							// ... with step size of the layer difference
							// (We need more connections in the middle)
							if ((hiddenNeurons.get(layer+1).length % hiddenNeurons.get(layer).length) != 0){

									// (Even number of input neurons)
									if ((hiddenNeurons.get(layer+1).length % 2) == 0){
									
										// (Second extra connection)
										if (n >= ((hiddenNeurons.get(layer+1).length / 2)
												+ (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length)))
											neur = (n - 2) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							
										// (First extra connection)
										else if ((n >= (hiddenNeurons.get(layer+1).length / 2) - 1))
											neur = (n - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
										
										else
											neur = n / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
									}
								
									// (Odd number of neurons)
									else if (((hiddenNeurons.get(layer+1).length % 2) != 0)
												&& (n >= ((hiddenNeurons.get(layer+1).length / 2)
														+ (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length))))
										neur = (n - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
									
									else
										neur = n / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							}
							
							// (Whole-number ratio)
							else
								neur = n / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							
							// ...to the neuron 1 position down...
							if (neur > 0){
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+(n*2)+"]\t|\t["+(layer)+"]["+(neur-1)+"]\t ["+(layer+1)+"]["
													+(n)+"]\t\t 0 | "+(n));							
					
								hiddenConnections.get(layer)[n*2] = new Connection(hiddenNeurons.get(layer)[neur-1],
									hiddenNeurons.get(layer+1)[n], 0, hiddenConnWeights.get(layer)[n*2], n);
							}
							
							else {
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+(n*2)+"]\t|\t["+(layer)+"][0]\t ["+(layer+1)+"]["+n+"]\t\t 0 | "+n);	
								
								hiddenConnections.get(layer)[n*2] = new Connection(hiddenNeurons.get(layer)[0],
									hiddenNeurons.get(layer+1)[n], 0, hiddenConnWeights.get(layer)[n*2], n);
							}
							
							// ...and the neuron 1 position up
							if (neur < hiddenNeurons.get(layer).length - 1){
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["
													+(neur+1)+"]\t\t "+1+" | "+(neur+1));							
					
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[neur+1],
									hiddenNeurons.get(layer+1)[n], 1, hiddenConnWeights.get(layer)[(n*2)+1], (n));
							}
							else{
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["
													+neur+"]\t\t "+1+" | "+neur);	
								
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[neur],
									hiddenNeurons.get(layer+1)[n], 1, hiddenConnWeights.get(layer)[(n*2)+1], n);
							}
						}
					}
				}
				break;
			
			case "zigzag":
				
				for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
					inp = 0;
					
					if (hiddenNeurons.get(layer).length >= hiddenNeurons.get(layer+1).length){
					
						// From each hidden neuron...
						for (int n = 0; n < hiddenNeurons.get(layer).length; n++){
							
							// ... with step size of the layer difference
							// (We need more connections in the middle)
							if ((hiddenNeurons.get(layer).length % hiddenNeurons.get(layer+1).length) != 0){

									// (Even number of input neurons)
									if ((hiddenNeurons.get(layer).length % 2) == 0){
									
										// (Second extra connection)
										if (n >= ((hiddenNeurons.get(layer).length / 2)
												+ (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length)))
											neur = (n - 2) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							
										// (First extra connection)
										else if ((n >= (hiddenNeurons.get(layer).length / 2) - 1))
											neur = (n - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
										
										else
											neur = n / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
									}
								
									// (Odd number of neurons)
									else if (((hiddenNeurons.get(layer).length % 2) != 0)
												&& (n >= ((hiddenNeurons.get(layer).length / 2)
														+ (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length))))
										neur = (n - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
									
									else
										neur = n / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							}
							
							// (Whole-number ratio)
							else
								neur = n / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							
							inp = n % (2 * hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
			
							// Test output: multilayerPerceptronTest
							System.out.println("\t\t["+(n*2)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["
												+(neur)+"]\t\t "+inp+" | "+neur);	
							
							// ...to the neuron at the same position of the next hidden layer...
							hiddenConnections.get(layer)[n*2] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[neur], inp, hiddenConnWeights.get(layer)[n*2], neur);
							
							inp = (2 * hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) - inp - 1;
							
							// ...and a neuron 1 position up of the next hidden layer
							if (neur < hiddenNeurons.get(layer+1).length - 1){
								
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["
													+(neur+1)+"]\t\t "+inp+" | "+(neur+1));	
								
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[neur+1], inp, hiddenConnWeights.get(layer)[(n*2)+1], (neur+1));
							}
							
							else{
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"][0]\t\t "+inp+" | 0");	
								
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[0], inp, hiddenConnWeights.get(layer)[(n*2)+1], 0);
							}
						}
					}
					
					else {
						
						// From each hidden neuron (of the next layer)...
						for (int n = 0; n < hiddenNeurons.get(layer+1).length; n++){
							
							// ... with step size of the layer difference
							// (We need more connections in the middle)
							if ((hiddenNeurons.get(layer+1).length % hiddenNeurons.get(layer).length) != 0){

									// (Even number of input neurons)
									if ((hiddenNeurons.get(layer+1).length % 2) == 0){
									
										// (Second extra connection)
										if (n >= ((hiddenNeurons.get(layer+1).length / 2)
												+ (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length)))
											neur = (n - 2) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							
										// (First extra connection)
										else if ((n >= (hiddenNeurons.get(layer+1).length / 2) - 1))
											neur = (n - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
										
										else
											neur = n / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
									}
								
									// (Odd number of neurons)
									else if (((hiddenNeurons.get(layer+1).length % 2) != 0)
												&& (n >= ((hiddenNeurons.get(layer+1).length / 2)
														+ (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length))))
										neur = (n - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
									
									else
										neur = n / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							}
							
							// (Whole-number ratio)
							else
								neur = n / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
			
							// Test output: multilayerPerceptronTest
							System.out.println("\t\t["+(n*2)+"]\t|\t["+(layer)+"]["+neur+"]\t ["+(layer+1)+"]["
												+n+"]\t\t 0 | "+n);	
							
							// ...to the neuron at the same position of the next hidden layer...
							hiddenConnections.get(layer)[n*2] = new Connection(hiddenNeurons.get(layer)[neur],
									hiddenNeurons.get(layer+1)[n], 0, hiddenConnWeights.get(layer)[n*2], n);
							
							// ...and a neuron 1 position up of the next hidden layer
							if (n < hiddenNeurons.get(layer).length - 1){
								
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+neur+"]\t ["+(layer+1)+"]["
													+(n+1)+"]\t\t 1 | "+(n+1));	
								
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[neur],
									hiddenNeurons.get(layer+1)[n+1], 1, hiddenConnWeights.get(layer)[(n*2)+1], (n+1));
							}
							
							else {
								// Test output: multilayerPerceptronTest
								System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(layer)+"]["+neur+"]\t ["+(layer+1)+"][0]\t\t 1 | 0");	
								
								hiddenConnections.get(layer)[(n*2)+1] = new Connection(hiddenNeurons.get(layer)[neur],
									hiddenNeurons.get(layer+1)[0], 1, hiddenConnWeights.get(layer)[(n*2)+1], 0);
							}
						}
					}
				}
				break;
				
			default: // "each"
				for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
					positionInLayer = 0;
					
					// From each hidden neuron...
					for (int n = 0; n < hiddenNeurons.get(layer).length; n++){
					
						// ...to each neuron of the next hidden layer
						for (int conn = 0; conn < hiddenNeurons.get(layer+1).length; conn++){
							
							hiddenConnections.get(layer)[positionInLayer] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[conn], n, hiddenConnWeights.get(layer)[positionInLayer], conn);
							
							positionInLayer++;
						}
					}
				}
			}// switch (hiddenTopology)
		}// if (hiddenLayers > 1)
		
		// Connections between last hidden and output layer
		switch (outputTopology){
		case "one":
			inp = 0;
			neur = 0;
			
			if (hiddenNeurons.get(hiddenNeurons.size() - 1).length >= nrOutputNeurons){
				
				// From each hidden neuron...
				for (int hidNeur = 0; hidNeur < hiddenNeurons.get(hiddenNeurons.size() - 1).length; hidNeur++, inp++){
					
					// Go to next output neuron if all inputs are connected
					if ((hidNeur > 0) && (hidNeur % neededNeuronInputs[nrHiddenNeurons.length]  == 0)){
						neur++;
						inp = 0;
					}
							
					// ...to the "nearest" output neuron (if several are available)
					outputConnections[hidNeur] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[hidNeur],
							outputNeurons[neur], inp, outputConnWeights[hidNeur], neur);
				}
			}
			
			else {
				
				// From each output neuron...
				for (int oNeur = 0; oNeur < outputNeurons.length; oNeur++, inp++){
					
					// Go to next hidden neuron if all inputs are connected
					if ((oNeur > 0) && (oNeur % neededNeuronInputs[nrHiddenNeurons.length]  == 0)){
						neur++;
						inp = 0;
					}
							
					// ...to the "nearest" output neuron (if several are available)
					outputConnections[oNeur] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[neur],
							outputNeurons[oNeur], inp, outputConnWeights[oNeur], oNeur);
				}
			}
			break;
		
		default: // "each"
			// From each neuron of the last hidden layer...
			positionInLayer = 0;
			
			for (int hidNeur = 0; hidNeur < hiddenNeurons.get(hiddenNeurons.size() - 1).length; hidNeur++){
			
				// ...to each output neuron
				for (int conn = 0; conn < outputNeurons.length; conn++){
					
					outputConnections[positionInLayer] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[hidNeur],
							outputNeurons[conn], hidNeur, outputConnWeights[positionInLayer], conn);
					
					positionInLayer++;
				}
			}
		}// switch (outputTopology)
		
		keepGoing  = false;
	}// MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
		// int nrOutputNeurons, String inputTopology, String hiddenTopology)

	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons){
		this(nrInputNeurons, nrHiddenNeurons, nrOutputNeurons, "each", "each", "each");
	}

	MultiLayerPerceptron(int nrInputNeurons, int nrHiddenNeuronsPerLayer, int numberHiddenLayers, int nrOutputNeurons){
		this(nrInputNeurons, getHiddenNeurons(nrHiddenNeuronsPerLayer, numberHiddenLayers), nrOutputNeurons, "each", "each", "each");
	}
	
private static	int[] getHiddenNeurons(int nrHiddenNeuronsPerLayer, int numberHiddenLayers){
		int[] nrHiddenNeurons = new int[numberHiddenLayers];
	
		for (int i = 0; i < nrHiddenNeurons.length; i++)
			nrHiddenNeurons[i] = nrHiddenNeuronsPerLayer;
		
		return nrHiddenNeurons;
	}

	/****************************************************************************************
	* float[] run(float[] inputVector):
	* Executes the built multi-layer perceptron with given input vector
	* Returns the output vector as single integer
	* Bit is set if depending output value is > 0.5
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
		}
		
		//Set inputs
		for (int i = 0; i < inputNeurons.length; i++)
			inputNeurons[i].setInput(0, inputVector[i]);
		
		// Execute input layer
		for (int pos = 0; pos < inputConnections.length; pos++){			
			// TODO: Test output für welchen Test?
//			System.out.println("inputConnections.length: "+inputConnections.length
//								+", inputConnection[" + pos + "]");
			
			inputConnections[pos].run();
		}
			
		// Execute hidden layer(s)
		for(int layer = 0; layer < (hiddenNeurons.size()-1); layer++){
			
			// Test output
//			System.out.println("connection["+layer+"].length: "+ hiddenConnections[layer].length);
			
			for (int pos = 0; pos < hiddenConnections.get(layer).length; pos++){
				
				// Test output
//				System.out.println("hiddenConnections[" + layer + "][" + pos + "]");
				
				hiddenConnections.get(layer)[pos].run();
			}
		}
		
		for (int pos = 0; pos < outputConnections.length; pos++){
			
			// Test output
//			System.out.println("outputConnection[" + pos + "]");
			
			outputConnections[pos].run();
		}
		
		// Now get the result(s)
		for (int i = 0; i < outputNeurons.length; i++)
			outputVector[i] = outputNeurons[i].getOutput();
			
		return outputVector;
	}// run()
	
	/****************************************************************************************
	* int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
	*				int abort):
	* Trains the multi layer perceptron
	* Executes the perceptron with given input vector, compares with the given output vector,
	* and calculates (if necessary) new connection weights and neuron thresholds
	* The training will be aborted (to avoid endless execution if now result can be found)
	* after the given number of trials
	* Returns the number of trials
	****************************************************************************************/
	int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
					int abort){
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
		}
		
		keepGoing = true;
		
		outVector = new float[outputNeurons.length];
		
		// We don't want endless executions
		int abortTraining = 0;
		
		while(keepGoing){
			// Execute perceptron with training input vector
			outVector = run(trainingInVector);
			
			abortTraining++;
			
			// Backpropagation test output: print output vector
			System.out.println("Trial " + abortTraining + ":");
			
			for (int deb = 0; deb < outVector.length; deb++){
				System.out.print("trainingOutVector[" + deb + "]: " + (trainingOutVector[deb] - errorTolerance));
				System.out.print(" - " + (trainingOutVector[deb] + errorTolerance));
				System.out.println(", outVector[" + deb + "]: " + outVector[deb]);
			}
			
			// Wrong result, use backpropagation to find a better one and try again
			for (int i = 0; (i < outVector.length) && (wrong == false); i++){
				if ((outVector[i] < (trainingOutVector[i] - errorTolerance))
						|| (outVector[i] > (trainingOutVector[i] + errorTolerance)))
					wrong = true;					
			}
			
			// At least 1 wrong output
			if (wrong == true){
				backpropagation(outVector, trainingOutVector);
				
				wrong = false;
			}
			
			// All results are like we want them, stop training
			else
				keepGoing = false;
			
			// Cancel training if it takes too long
			// Max number of trials ~2*10^9 (2^31)
			if (abortTraining >= abort)
				keepGoing = false;
		}
		
		// For debug purposes, print output vector
//		System.out.println("\ttrainingOut\t\t| outVector");
//		for (int deb = 0; deb < outVector.length; deb++){
//			System.out.print("\t[" + deb + "]: " + (trainingOutVector[deb] - errorTolerance));
//			System.out.print(" - " + (trainingOutVector[deb] + errorTolerance));
//			System.out.println("\t| [" + deb + "]: " + outVector[deb]);
//		}
		
		return abortTraining;
	}// training()
	
	//TODO int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance, int abort, float trainingCoefficient)
	
	/****************************************************************************************
	* void backpropagation(float[] resultOut, float[] wantedOut):
	* Calculates the differences for weights and thresholds and add them.
	****************************************************************************************/
private	void backpropagation(float[] resultOut, float[] wantedOut){
		float trainingCoefficient = 0.2f;
		float weightDelta = 0;
		float succWeights[] = new float[1];
		Connection connections[] = new Connection[1];
		int succNeurons = 0;
		int numberOfThresholds;
		float outputOfNeuron;
		float inputOfNeuron;
		int position;
		
		// For calculating the weight differences for every connection loop over every
		// (connection) layer...
		int connectionLayer = hiddenNeurons.size() - 1;
		
		for (int layer = connectionLayer; layer >= 0; layer--){
			
			// Outputconnection layer
			if (layer == connectionLayer){
				connections = new Connection [outputConnections.length];
				connections = outputConnections;
				
				numberOfThresholds = outputNeurons.length;
			}
			
			// InputConnection layer
			else if (layer == 0){
				if (connectionLayer > 1){
					succWeights = new float [hiddenConnWeights.get(0).length];
					succWeights = hiddenConnWeights.get(0);
					
					// Number of inputs of neuron in 1st hidden layer = number of
					// successive neurons
					succNeurons = hiddenConnWeights.get(0).length/hiddenNeurons.get(0).length;
				}
				else {
					succWeights = new float [outputConnWeights.length];
					succWeights = outputConnWeights;
					
					succNeurons = outputConnWeights.length/outputNeurons.length;
				}

				connections = new Connection [inputConnections.length];
				connections = inputConnections;
				
				numberOfThresholds = inputNeurons.length;
			}
			
			// Hidden Layer
			else {
				if (layer == connectionLayer - 1){
					succWeights = new float [outputConnWeights.length];
					succWeights = outputConnWeights;
					
					// Just one connection per neuron to output layer
					succNeurons = 1;
				}

				else {
					succWeights = new float [hiddenConnWeights.get(layer + 1).length];
					succWeights = hiddenConnWeights.get(layer + 1);
				
					// To each neuron of the following layer
					succNeurons = hiddenNeurons.get(layer + 1).length;
				}
				
				connections = new Connection [hiddenConnections.get(layer).length];
				connections = hiddenConnections.get(layer);
				
				numberOfThresholds = hiddenNeurons.get(layer).length;
			}
			
			// ...and now over all connections
			for (int conn = 0; conn < (connections.length); conn++){
				
				// For output layer: Gradient descent
				if (layer == connectionLayer){
					position = connections[conn].getPositionNeuronTo();
					
					weightDelta = wantedOut[position] - resultOut[position];
					
//					System.out.println("weightDelta = wantedOut[position] - resultOut[position]");
//					System.out.println(weightDelta+" = "+wantedOut[position]+" - "+resultOut[position]);
					
					// Everything else we need comes after the for-loops
				}
				
				// Da real backpropagation for the rest
				else {
					// Sum over successive neurons
					for (int succ = 0; succ < succNeurons; succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++){
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
										resultOut[out] * (1- resultOut[out]));
				
//							System.out.println("sumOutputNeurons += ((wantedOut[out] - resultOut[out]) * resultOut[out] * (1- resultOut[out]))");
//							System.out.println(sumOutputNeurons+" += (("+wantedOut[out]+" - "+resultOut[out]+") * "+resultOut[out]+" * ("+(1- resultOut[out])+"))");
						}
						
						weightDelta += sumOutputNeurons;
						
						position = connections[conn].getPositionNeuronTo();
						
						weightDelta *= succWeights[position];
					}// Sum over successive neurons
				}
				
				outputOfNeuron = connections[conn].getNeuronTo().getOutput();
				inputOfNeuron = connections[conn].getNeuronFrom().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron) * inputOfNeuron);
					
				weightDelta *= trainingCoefficient;
				
//				System.out.println("weightDelta: " + weightDelta);
					
				// Add calculated weight difference to connection weight
				connections[conn].addWeightDelta(weightDelta);
			}// for() over all connections
			
			// Calculation of thresholds
			for (int thresh = 0; thresh < numberOfThresholds; thresh++){
				int positionInLayer = thresh * (connections.length / numberOfThresholds);
				float oldThresh = -1 * connections[positionInLayer].getNeuronTo().getThreshold();
				
				// For output layer: Gradient descent
				if (layer == connectionLayer)
					weightDelta = wantedOut[thresh] - resultOut[thresh];
				
				// Backpropagation for the rest
				else {
					// Sum over successive neurons
					for (int succ = 0; succ < succNeurons; succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++)
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
									      resultOut[out] * (1- resultOut[out]));
				
						weightDelta += sumOutputNeurons;
						
						weightDelta *= succWeights[succ];
					}// Sum over successive neurons
				}
				
				outputOfNeuron = connections[positionInLayer].getNeuronTo().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron));
					
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to old threshold and set new threshold
				connections[positionInLayer].getNeuronTo().setThreshold(oldThresh + weightDelta);
			}// for() over thresholds
		}// for() over every (connection) layer
	}// backpropagation()

	/****************************************************************************************
	* String[] findFastest()
	*
	****************************************************************************************/
	String[] findFastest(float[] inputVector, float[] outputVector, float errorTolerance, int abort){
		MultiLayerPerceptron  perceptron;
		int[] nrHiddenNeurons;
		String[] inputTopologies;
		String[] hiddenTopologies;
		String[] outputTopologies;
		String[][] topologies;
		String[] topoSort;
		String[] fastestString;
		int[] trials;
		int trialPos;
		int trialsSort;
		
		nrHiddenNeurons = new int[hiddenNeurons.size()];
		
		for (int i = 0; i < nrHiddenNeurons.length; i++)
			nrHiddenNeurons[i] = hiddenNeurons.get(i).length;
		
		inputTopologies = new String[3];
		hiddenTopologies = new String[4];
		outputTopologies = new String[2];
		
		fastestString = new String[4];
		
		trials = new int[inputTopologies.length * hiddenTopologies.length * outputTopologies.length];
		trialPos = 0;
		topologies = new String[trials.length][3];
		topoSort = new String[3];
		
		inputTopologies[0] = hiddenTopologies[0] = outputTopologies[0] = "one";
		inputTopologies[1] = hiddenTopologies[3] = outputTopologies[1] = "twoGroups";
		inputTopologies[2] = "each";
		hiddenTopologies[1] = "cross";
		hiddenTopologies[2] = "zigzag";
		
		for (int inTop = 0; inTop < inputTopologies.length; inTop++){
			
			for (int hidTop = 0; hidTop < hiddenTopologies.length; hidTop++){
				
				for (int outTop = 0; outTop < outputTopologies.length; outTop++){
					
					perceptron = new MultiLayerPerceptron(inputNeurons.length, nrHiddenNeurons, outputNeurons.length
															, inputTopologies[inTop], hiddenTopologies[hidTop]
															, outputTopologies[outTop]);
					
					topologies[trialPos][0] = inputTopologies[inTop];
					topologies[trialPos][1] = hiddenTopologies[hidTop];
					topologies[trialPos][2] = outputTopologies[outTop];
					
					trials[trialPos] = perceptron.training(inputVector, outputVector, errorTolerance, abort);
					
					trialPos++;
				}// for (hiddenTopologies)
			}// for (hiddenTopologies)
		}// for (inputTopologies)
		
		for (int i = 0; i < trials.length; i++){
			
			for (int j = (trials.length-1); j > 0; j--){
				
				if (trials[j-1] > trials[j]){
					
					trialsSort = trials[j];
					trials[j] = trials[j-1];
					trials[j-1] = trialsSort;
					
					for (int k = 0; k < 3; k++){
						topoSort[k] = topologies[j][k];
						topologies[j][k] = topologies[j-1][k];
						topologies[j-1][k] = topoSort[k];
					}
				}
			}	
		}
		
		for (int i = 0; i < 3; i++)
			fastestString[i] = topologies[0][i];
		
		if (trials[0] == abort)
			fastestString[3] = "ABORTED";
		else
			fastestString[3] = Integer.toString(trials[0]);
		
		// DEBUG
//		System.out.print("\t\tFastest:\t");
		
		for (int j = 0; j < trials.length; j++){
			
			if (trials[j] == trials[0]){
				
				System.out.print("\t\t");
				
				for (int i = 0; i < 3; i++)
					System.out.print(topologies[j][i] + ", ");
				
				System.out.println("\n\t\tTrials: " + trials[j]);
				
			}
		}
		
		trialPos = 0;
		
		return fastestString;
	}// findFastest()
	
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