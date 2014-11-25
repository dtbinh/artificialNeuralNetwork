/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments, TechDoc
* 		System.out.prints komplett für multiLayerPerceptron-Test (Tabelle, wie bei inputTopology each)
************************************************************************************************/
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
	
	// Test variables
public static Boolean multiLayerPerceptronTest = false;

public
	// Constructors
	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons,
	*			String inputTopology, String hiddenTopology, String outputTopology):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* each layer, and number of output neurons can (has to) be defined.
	* The type of connection between the layers has to be defined.
	* There must be at least 1 hidden layer
	/****************************************************************************************/
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
		
		// Creation of input connections and 
		// calculation of needed inputs in hidden layer 0
		switch (inputTopology){
		
		case "one":
			
			// Every input neuron will be connected to the nearest hidden neuron
			if (nrInputNeurons >= nrHiddenNeurons[0]){
				
				// Needed inputs in 1st hidden layer
				neuronInputsLayer = new int[nrHiddenNeurons[0]];
				
				// For every neuron in hidden layer 0
				for (int m = 0; m < nrHiddenNeurons[0]; m++){
					
					// TODO:	Bug in Berechnung der Inputs
					//			Übernehmen in nachfolgende Berechnungen
					// We need more connections in the middle
					if ((nrInputNeurons % nrHiddenNeurons[0]) != 0){

						// Outside the range of the neurons with one more connection
						if (m <= (nrInputNeurons/2) - ((nrInputNeurons % nrHiddenNeurons[0])/2)
								|| m >= (nrInputNeurons/2) + ((nrInputNeurons % nrHiddenNeurons[0])/2))
							neuronInputsLayer[m] = nrInputNeurons / nrHiddenNeurons[0];
						
						// Inside the range
						else 
							neuronInputsLayer[m] = (nrInputNeurons / nrHiddenNeurons[0]) + 1;
					}
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrInputNeurons / nrHiddenNeurons[0];
					
					// DEBUG
					System.out.println("neuronInputsLayer["+m+"]="+neuronInputsLayer[m]);
				}
					
				neuronInputs.add(neuronInputsLayer);

				inputConnections = new Connection[nrInputNeurons];
			}
				
			// Every hidden neuron will be connected to the nearest input neuron
			else {
				
				// Needed inputs in 1st hidden layer
				neuronInputsLayer = new int[nrHiddenNeurons[0]];
				
				// For every neuron in hidden layer 0
				for (int m = 0; m < nrHiddenNeurons[0]; m++){
					
					// We need more connections in the middle
					if ((nrHiddenNeurons[0] % nrInputNeurons) != 0){

						// Outside the range of the neurons with one more connection
						if (m <= (nrHiddenNeurons[0]/2) - ((nrHiddenNeurons[0] % nrInputNeurons)/2)
								|| m >= (nrHiddenNeurons[0]/2) + ((nrHiddenNeurons[0] % nrInputNeurons)/2))
							neuronInputsLayer[m] = nrHiddenNeurons[0] / nrInputNeurons;
						
						// Inside the range
						else 
							neuronInputsLayer[m] = (nrHiddenNeurons[0] / nrInputNeurons) + 1;
					}
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrHiddenNeurons[0] / nrInputNeurons;
				}
					
				neuronInputs.add(neuronInputsLayer);

				inputConnections = new Connection[nrHiddenNeurons[0]];
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
			}
			
			neuronInputs.add(neuronInputsLayer);
			break;
		
		default:
			// "each"
			
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
			// (to 8 so the output is almost 0)
			inputNeurons[i].setThreshold(8);
		}
		
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
			
			// Creation of hidden connections and 
			// calculation of needed inputs in next layer
			switch (hiddenTopology){
			
			case "one":
				// Every hidden neuron will be connected to the nearest hidden neuron in the next layer
				
				// For every layer
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]){
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr]];
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							// We need more connections in the middle
							if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0){

								// Outside the range of the neurons with one more connection
								if (m <= (nrHiddenNeurons[lyr]/2) - ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1])/2)
										|| m >= (nrHiddenNeurons[lyr]/2) + ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1])/2))
									neuronInputsLayer[m] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
								
								// Inside the range
								else 
									neuronInputsLayer[m] = (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]) + 1;
							}
							
							// Whole-number ratio
							else 
								neuronInputsLayer[m] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
						}
							
						neuronInputs.add(neuronInputsLayer);
					}
						
					else {
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr+1]];
						
						// Needed inputs in 1st hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							// We need more connections in the middle
							if ((nrHiddenNeurons[lyr+1] % nrHiddenNeurons[lyr]) != 0){

								// Outside the range of the neurons with one more connection
								if (m <= (nrHiddenNeurons[lyr+1]/2) - ((nrHiddenNeurons[lyr+1] % nrHiddenNeurons[lyr])/2)
										|| m >= (nrHiddenNeurons[lyr+1]/2) + ((nrHiddenNeurons[lyr+1] % nrHiddenNeurons[lyr])/2))
									neuronInputsLayer[m] = nrHiddenNeurons[lyr+1] / nrHiddenNeurons[lyr];
								
								// Inside the range
								else 
									neuronInputsLayer[m] = (nrHiddenNeurons[lyr+1] / nrHiddenNeurons[lyr]) + 1;
							}
							
							// Whole-number ratio
							else 
								neuronInputsLayer[m] = nrHiddenNeurons[lyr+1] / nrHiddenNeurons[lyr];
						}
					}
						
					hiddenConnections.add(hiddenLyrConnections);
							
					neuronInputs.add(neuronInputsLayer);
				}
				break;
			
			case "cross":
				// Every neuron will be connected to two neurons of the following layer,
				// the neuron a position up and the neuron a position down
			
			case "zigzag":
				// Every neuron will be connected to two neurons of the following layer,
				// the neuron at same position and the neuron a position up
			
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){

					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]){
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr] * 2];
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							// We need more connections in the middle
							if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0){

								// Outside the range of the neurons with one more connection
								if (m <= (nrHiddenNeurons[lyr]/2) - ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1])/2)
										|| m >= (nrHiddenNeurons[lyr]/2) + ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1])/2))
									neuronInputsLayer[m] = 2 * (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]);
								
								// Inside the range
								else 
									neuronInputsLayer[m] = (2 * (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1])) + 1;
							}
							
							// Whole-number ratio
							else 
								neuronInputsLayer[m] = 2 * (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]);
						}
							
						neuronInputs.add(neuronInputsLayer);
					}
						
					else {
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr+1] * 2];
						
						// Needed inputs in 1st hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							// We need more connections in the middle
							if ((nrHiddenNeurons[lyr+1] % nrHiddenNeurons[lyr]) != 0){

								// Outside the range of the neurons with one more connection
								if (m <= (nrHiddenNeurons[lyr+1]/2) - ((nrHiddenNeurons[lyr+1] % nrHiddenNeurons[lyr])/2)
										|| m >= (nrHiddenNeurons[lyr+1]/2) + ((nrHiddenNeurons[lyr+1] % nrHiddenNeurons[lyr])/2))
									neuronInputsLayer[m] = 2 * (nrHiddenNeurons[lyr+1] / nrHiddenNeurons[lyr]);
								
								// Inside the range
								else 
									neuronInputsLayer[m] = (2 * (nrHiddenNeurons[lyr+1] / nrHiddenNeurons[lyr])) + 1;
							}
							
							// Whole-number ratio
							else 
								neuronInputsLayer[m] = 2 * (nrHiddenNeurons[lyr+1] / nrHiddenNeurons[lyr]);
						}
					}
						
					hiddenConnections.add(hiddenLyrConnections);
							
					neuronInputs.add(neuronInputsLayer);
				}
				break;
			
			default: // each
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr] * nrHiddenNeurons[lyr+1]];
					
					hiddenConnections.add(hiddenLyrConnections);
					
					// Needed inputs in 1st hidden layer
					neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
					
					// For every neuron in next hidden layer
					for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
						neuronInputsLayer[m] = nrHiddenNeurons[lyr];
					
					neuronInputs.add(neuronInputsLayer);
				}
			}// switch (hiddenTopology)
		} // else (hiddenLayers > 1)
					
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int l = 0; l < hiddenNeurons.size(); l++){
			
			for (int n = 0; n < hiddenNeurons.get(l).length; n++){
				hiddenNeurons.get(l)[n] = new Neuron(neuronInputs.get(l)[n]);
					
				// Initialise thresholds
				hiddenNeurons.get(l)[n].setThreshold(8);
			}
		}
		 
		/*** Output layer ***/		
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		switch (outputTopology){
		
		case "one":
			// Every hidden neuron will be connected to the nearest output neuron
			
			if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons){
				outputConnections = new Connection[nrHiddenNeurons[nrHiddenNeurons.length - 1]];
				
				// Needed inputs in next hidden layer
				neuronInputsLayer = new int[nrOutputNeurons];
				
				// For every neuron in output layer
				for (int m = 0; m < nrOutputNeurons; m++){
					
					// We need more connections in the middle
					if ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons) != 0){

						// Outside the range of the neurons with one more connection
						if (m <= (nrHiddenNeurons[nrHiddenNeurons.length - 1]/2) - ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons)/2)
								|| m >= (nrHiddenNeurons[nrHiddenNeurons.length - 1]/2) + ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons)/2))
							neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
						
						// Inside the range
						else 
							neuronInputsLayer[m] = (nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons) + 1;
					}
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
				}
					
				neuronInputs.add(neuronInputsLayer);
			}
				
			else {
				outputConnections = new Connection[nrOutputNeurons];
				
				// Needed inputs in 1st hidden layer
				neuronInputsLayer = new int[nrOutputNeurons];
				
				// For every neuron in output layer
				for (int m = 0; m < nrOutputNeurons; m++){
					
					// We need more connections in the middle
					if ((nrOutputNeurons % nrHiddenNeurons[nrHiddenNeurons.length - 1]) != 0){
							// Outside the range of the neurons with one more connection
						if (m <= (nrOutputNeurons/2) - ((nrOutputNeurons % nrHiddenNeurons[nrHiddenNeurons.length - 1])/2)
								|| m >= (nrOutputNeurons/2) + ((nrOutputNeurons % nrHiddenNeurons[nrHiddenNeurons.length - 1])/2))
							neuronInputsLayer[m] = nrOutputNeurons / nrHiddenNeurons[nrHiddenNeurons.length - 1];
						
						// Inside the range
						else 
							neuronInputsLayer[m] = (nrOutputNeurons / nrHiddenNeurons[nrHiddenNeurons.length - 1]) + 1;
					}
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrOutputNeurons / nrHiddenNeurons[nrHiddenNeurons.length - 1];
				}
			}
				
			hiddenConnections.add(hiddenLyrConnections);
					
			neuronInputs.add(neuronInputsLayer);
			break;
			
		default: // "each"
			outputConnections = new Connection[nrOutputNeurons * nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			
			// Needed inputs in 1st hidden layer
			neuronInputsLayer = new int[nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			
			// For every neuron in next hidden layer
			for (int m = 0; m < nrHiddenNeurons[0]; m++)
				neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1];
			
			neuronInputs.add(neuronInputsLayer);			
		}// switch (outputTopology)
		
		for (int i = 0; i < outputNeurons.length; i++){
			outputNeurons[i] = new Neuron(neuronInputs.get(nrHiddenNeurons.length)[i]);
			
			// Initialise thresholds
			outputNeurons[i].setThreshold(8);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connections ***/
		// Connections between input layer and 1st hidden layer
		
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
					
					// TODO:	Bug in inside range of the neurons
					//			Übernhemen in folgende erechnungen
					// ... with step size of the layer difference
					// (Maybe we need more connections in the middle inside )
					if (((inputNeurons.length % hiddenNeurons.get(0).length) != 0)
							&& (n > (inputNeurons.length/2) - ((inputNeurons.length % hiddenNeurons.get(0).length)/2))
							&& (n < (inputNeurons.length/2) + ((inputNeurons.length % hiddenNeurons.get(0).length)/2))){
						m = n / ((inputNeurons.length / hiddenNeurons.get(0).length) + 1);
							
						i = (n % ((inputNeurons.length / hiddenNeurons.get(0).length) + 1));
					}
					
					// (Whole-number ratio or outside the range of the neurons with one more connection)
					else {						
						m = (n * hiddenNeurons.get(0).length) / inputNeurons.length;
						
						i = (n % (inputNeurons.length / hiddenNeurons.get(0).length));
					}
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+n+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+i+" | "+m);
					
					// ...to a hidden neuron (initialise the connection weight with 1)
					inputConnections[n] = new Connection(inputNeurons[n], hiddenNeurons.get(0)[m], i, 1, m);
				}
			}
			
			else {

				// Neuron in current layer
				int n = 0;
				
				// From each hidden neuron...
				for (int m = 0; m < hiddenNeurons.get(0).length; m++){
					
					// ... with step size of the layer difference
					// (Maybe we need more connections in the middle)
					if ((hiddenNeurons.get(0).length % inputNeurons.length) != 0){

						// Outside the range of the neurons with one more connection
						if (m <= (hiddenNeurons.get(0).length/2) - ((hiddenNeurons.get(0).length % inputNeurons.length)/2)
								|| m >= (hiddenNeurons.get(0).length/2) + ((hiddenNeurons.get(0).length % inputNeurons.length)/2))
							n = m / (hiddenNeurons.get(0).length / inputNeurons.length);
						
						// Inside the range
						else
							n = m / ((hiddenNeurons.get(0).length / inputNeurons.length) + 1);
					}
					
					// (Whole-number ratio)
					else
						n = m / (hiddenNeurons.get(0).length / inputNeurons.length);
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+m+"]\t|\t["+n+"]\t [0]["+m+"]\t\t 0 | "+m);
					
					// ...to a hidden neuron (initialise the connection weight with 1)
					inputConnections[m] = new Connection(inputNeurons[n],
							hiddenNeurons.get(0)[m], 0, 1, m);
				}
			}
			break;
			
		case "twoGroups":
			
			// Split the groups here
			int groupLimit;
			
			int positionInLayer = 0;
			
			if ((hiddenNeurons.get(0).length % 2) == 0)
				groupLimit = hiddenNeurons.get(0).length/2;
			else
				groupLimit = (hiddenNeurons.get(0).length/2) + 1;

			// From each input neuron...
			for (int n = 0; n < inputNeurons.length; n++){
				
				// ...to each neuron in the nearest group
				if (n < (inputNeurons.length/2)){
						
					for (int m = 0; m < groupLimit; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+positionInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+n+" | "+m);
						
						inputConnections[positionInLayer] = new Connection(inputNeurons[n],
								hiddenNeurons.get(0)[m], n, 1, m);
						
						positionInLayer++;
					}
				}
				
				else {
					
					for (int m = groupLimit; m < hiddenNeurons.get(0).length; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+positionInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+n+" | "+m);
						
						inputConnections[positionInLayer] = new Connection(inputNeurons[n],
								hiddenNeurons.get(0)[m], n, 1, m);
						
						positionInLayer++;
					}
				}
			}
			break;
		
		default: // "each"
			
			int posInLayer = 0;
			
			// From each input neuron...
			for (int n = 0; n < inputNeurons.length; n++){
			
				// ...to each neuron of the 1st hidden layer
				for (int conn = 0; conn < hiddenNeurons.get(0).length; conn++){
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+posInLayer+"]\t|\t["+n+"]\t [0]["+conn+"]\t\t "+n+" | "+conn);
					
					inputConnections[posInLayer] = new Connection(inputNeurons[n],
							hiddenNeurons.get(0)[conn], n, 1, conn);
					
					posInLayer++;
				}
			}
		}// switch (inputTopology)
		
		// Connections between hidden layers
		if (hiddenNeurons.size() > 1){
				
			// Test output: multilayerPerceptronTest
			if (multiLayerPerceptronTest)
				System.out.println("\thiddenConnection| hiddenNeuron | hiddenNeuron\tinp|pos");
			
			switch (hiddenTopology){
			
			case "one":
				
				// For each layer
				for (int l = 0; l < (hiddenNeurons.size() - 1); l++){
					
					// Input connection is going to
					int i = 0;
			
					if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length){

						// Neuron of the next layer
						int m = 0;
						
						// From each neuron...
						for (int n = 0; n < hiddenNeurons.get(l).length; n++){
							
							// ... with step size of the layer difference
							// (Maybe we need more connections in the middle)
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){

								// Outside the range of the neurons with one more connection
								if (n <= (hiddenNeurons.get(l).length/2) - ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length)/2)
										|| n >= (hiddenNeurons.get(l).length/2) + ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length)/2)){
									m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
									
									i = (n % (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length));
								}
								
								// Inside the range
								else {
									m = n / ((hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length) + 1);
									
									i = (n % ((hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length) + 1));
								}
							}
							
							// (Whole-number ratio)
							else {
								m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
								
								i = (n % (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length));
							}
								
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+n+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["+m+"]\t\t "+i+" | "+m);
							
							// ...to a hidden neuron (initialise the connection weight with 1)
							hiddenConnections.get(l)[n] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], i, 1, m);
						}
					}
						
					else {

						// Neuron in current layer
						int n = 0;
						
						// From each hidden neuron (of the next layer)...
						for (int m = 0; m < hiddenNeurons.get(l+1).length; m++){
							
							// ... with step size of the layer difference
							// (Maybe we need more connections in the middle)
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){

								// Outside the range of the neurons with one more connection
								if (m <= (hiddenNeurons.get(l+1).length/2) - ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)/2)
										|| m >= (hiddenNeurons.get(l+1).length/2) + ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)/2))
									n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
								
								// Inside the range
								else
									n = m / ((hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length) + 1);
							}
							
							// (Whole-number ratio)
							else
								n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
							
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+m+"]\t|\t["+n+"]\t [0]["+m+"]\t\t 0 | "+m);
							
							// ...to a hidden neuron (in the current layer)
							hiddenConnections.get(l)[m] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], 0, 1, m);
						}
					}
				}
				break;
			
			case "cross":
				
				// For each layer
				for (int l = 0; l < (hiddenNeurons.size() - 1); l++){
					
					// Input connection is going to
					int i = 0;
					
					if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length){

						// Neuron of the next layer
						int m = 0;
					
						// From each hidden neuron...
						for (int n = 0; n < hiddenNeurons.get(l).length; n++){
							
							// ... with step size of the layer difference
							// (Maybe we need more connections in the middle)
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){

								// Outside the range of the neurons with one more connection
								if (n <= (hiddenNeurons.get(l).length/2) - ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length)/2)
										|| n >= (hiddenNeurons.get(l).length/2) + ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length)/2))
									m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
								
								// Inside the range
								else
									m = n / ((hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length) + 1);
							}
							
							// (Whole-number ratio)
							else
								m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
							
							i = n % (2 * hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
							
							// ...to the neuron 1 position down of the next hidden layer...
							if (m > 0){
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+(m-1)+"]\t\t "+i+" | "+(m-1));							
					
								hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m-1], i, 1, (m-1));
							}
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"][0]\t\t "
													+i+" | 0");	
								
								hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[0], i, 1, 0);
							}
							
							i = (2 * hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length) - i - 1;
							
							// ...and the neuron 1 position up of the next hidden layer
							if (m < hiddenNeurons.get(l+1).length - 1){
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+(m+1)+"]\t\t "+i+" | "+(m+1));							
					
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m+1], i, 1, (m+1));
							}
							else{
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+m+"]\t\t "+i+" | "+m);	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], i, 1, m);
							}
						}
					}
					
					else {

						// Neuron in current layer
						int n = 0;
						
						// From each hidden neuron (of the next layer)...
						for (int m = 0; m < hiddenNeurons.get(l+1).length; m++){
							
							// ... with step size of the layer difference
							// (Maybe we need more connections in the middle)
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){

								// Outside the range of the neurons with one more connection
								if (m <= (hiddenNeurons.get(l+1).length/2) - ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)/2)
										|| m >= (hiddenNeurons.get(l+1).length/2) + ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)/2))
									n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
								
								// Inside the range
								else
									n = m / ((hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length) + 1);
							}
							
							// (Whole-number ratio)
							else
								n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
							
							// ...to the neuron 1 position down...
							if (m > 0){
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+(m-1)+"]\t ["+(l+1)+"]["
													+(n)+"]\t\t 0 | "+(n));							
					
								hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[m-1],
									hiddenNeurons.get(l+1)[n], 0, 1, n);
							}
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"][0]\t ["+(l+1)+"]["+n+"]\t\t 0 | "+n);	
								
								hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[0],
									hiddenNeurons.get(l+1)[n], 0, 1, n);
							}
							
							// ...and the neuron 1 position up (in the current layer)
							if (m < hiddenNeurons.get(l).length - 1){
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+(m+1)+"]\t\t "+1+" | "+(m+1));							
					
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[m+1],
									hiddenNeurons.get(l+1)[n], 1, 1, (n));
							}
							else{
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+m+"]\t\t "+1+" | "+m);	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[m],
									hiddenNeurons.get(l+1)[n], 1, 1, n);
							}
						}
					}
				}
				break;
			
			case "zigzag":
				
				for (int l = 0; l < (hiddenNeurons.size() - 1); l++){
					
					// Input connection is going to
					int i = 0;
					
					if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length){

						// Neuron of the next layer
						int m = 0;
					
						// From each hidden neuron...
						for (int n = 0; n < hiddenNeurons.get(l).length; n++){
							
							// ... with step size of the layer difference
							// (Maybe we need more connections in the middle)
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){

								// Outside the range of the neurons with one more connection
								if (n <= (hiddenNeurons.get(l).length/2) - ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length)/2)
										|| n >= (hiddenNeurons.get(l).length/2) + ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length)/2))
									m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
								
								// Inside the range
								else
									m = n / ((hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length) + 1);
							}
							
							// (Whole-number ratio)
							else
								m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
							
							i = n % (2 * hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
			
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
												+(m)+"]\t\t "+i+" | "+m);	
							
							// ...to the neuron at the same position of the next hidden layer...
							hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], i, 1, m);
							
							i = (2 * hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length) - i - 1;
							
							// ...and a neuron 1 position up of the next hidden layer
							if (m < hiddenNeurons.get(l+1).length - 1){
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+(m+1)+"]\t\t "+i+" | "+(m+1));	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m+1], i, 1, (m+1));
							}
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"][0]\t\t "+i+" | 0");	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[0], i, 1, 0);
							}
						}
					}
					
					else {

						// Neuron in current layer
						int n = 0;
						
						// From each hidden neuron (of the next layer)...
						for (int m = 0; m < hiddenNeurons.get(l+1).length; m++){
							
							// ... with step size of the layer difference
							// (Maybe we need more connections in the middle)
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){

								// Outside the range of the neurons with one more connection
								if (m <= (hiddenNeurons.get(l+1).length/2) - ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)/2)
										|| m >= (hiddenNeurons.get(l+1).length/2) + ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)/2))
									n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
								
								// Inside the range
								else
									n = m / ((hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length) + 1);
							}
							
							// (Whole-number ratio)
							else
								n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
			
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+m+"]\t ["+(l+1)+"]["
												+n+"]\t\t 0 | "+n);	
							
							// ...to the neuron at the same position of the next hidden layer...
							hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[m],
									hiddenNeurons.get(l+1)[n], 0, 1, n);
							
							// ...and a neuron 1 position up of the next hidden layer
							if (n < hiddenNeurons.get(l).length - 1){
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+m+"]\t ["+(l+1)+"]["
													+(n+1)+"]\t\t 1 | "+(n+1));	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[m],
									hiddenNeurons.get(l+1)[n+1], 1, 1, (n+1));
							}
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+m+"]\t ["+(l+1)+"][0]\t\t 1 | 0");	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[m],
									hiddenNeurons.get(l+1)[0], 1, 1, 0);
							}
						}
					}
				}
				break;
				
			default: // "each"
				
				int positionInLayer;
				
				for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
					positionInLayer = 0;
					
					// From each hidden neuron...
					for (int n = 0; n < hiddenNeurons.get(layer).length; n++){
					
						// ...to each neuron of the next hidden layer
						for (int conn = 0; conn < hiddenNeurons.get(layer+1).length; conn++){
							
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+positionInLayer+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["+conn+"]\t\t "+n+" | "+conn);	
							
							hiddenConnections.get(layer)[positionInLayer] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[conn], n, 1, conn);
							
							positionInLayer++;
						}
					}
				}
			}// switch (hiddenTopology)
		}// if (hiddenLayers > 1)
		
		// Connections between last hidden and output layer
		
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
					
					// ... with step size of the layer difference
					// (Maybe we need more connections in the middle)
					if ((hiddenNeurons.get(hiddenNeurons.size() - 1).length % outputNeurons.length) != 0){

						// Outside the range of the neurons with one more connection
						if (n <= (hiddenNeurons.get(hiddenNeurons.size() - 1).length/2) - ((hiddenNeurons.get(hiddenNeurons.size() - 1).length % outputNeurons.length)/2)
								|| n >= (hiddenNeurons.get(hiddenNeurons.size() - 1).length/2) + ((hiddenNeurons.get(hiddenNeurons.size() - 1).length % outputNeurons.length)/2)){
							m = n / (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length);
							
							i = (n % (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length));
						}
						
						// Inside the range
						else {
							m = n / ((hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length) + 1);
							
							i = (n % ((hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length) + 1));
						}
					}
					
					// (Whole-number ratio)
					else {
						m = n / (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length);
						
						i = (n % (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length));
					}
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+n+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+i+" | "+m);
					
					// ...to an output neuron (initialise the connection weight with 1)
					outputConnections[n] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
							outputNeurons[m], i, 1, m);
				}
			}
			
			else {

				// Neuron in current layer
				int n = 0;
				
				// From each output neuron...
				for (int m = 0; m < outputNeurons.length; m++){
					
					// ... with step size of the layer difference
					// (Maybe we need more connections in the middle)
					if ((outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length) != 0){

						// Outside the range of the neurons with one more connection
						if (m <= (outputNeurons.length/2) - ((outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length)/2)
								|| m >= (outputNeurons.length/2) + ((outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length)/2))
							n = m / (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length);
						
						// Inside the range
						else
							n = m / ((outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length) + 1);
					}
					
					// (Whole-number ratio)
					else
						n = m / (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length);
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+m+"]\t|\t["+n+"]\t [0]["+m+"]\t\t 0 | "+m);
					
					// ...to a hidden neuron (initialise the connection weight with 1)
					outputConnections[m] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
							outputNeurons[m], 0, 1, m);
				}
			}
			break;
		
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
							outputNeurons[m], n, 1, m);
					
					positionInLayer++;
				}
			}
		}// switch (outputTopology)
		
		keepGoing  = false;
	}// MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
		// int nrOutputNeurons, String inputTopology, String hiddenTopology)


	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* each layer, and number of output neurons can (has to) be defined.
	* Each neuron will be connected to each neuron of the following layer.
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons){
		this(nrInputNeurons, nrHiddenNeurons, nrOutputNeurons, "each", "each", "each");
	}

	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
	*			int nrOutputNeurons):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* Each neuron will be connected to each neuron of the following layer.
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int nrHiddenNeuronsPerLayer, int numberHiddenLayers,
			int nrOutputNeurons){
		this(nrInputNeurons, getHiddenNeurons(nrHiddenNeuronsPerLayer, numberHiddenLayers),
				nrOutputNeurons, "each", "each", "each");
	}
	
private static int[] getHiddenNeurons(int nrHiddenNeuronsPerLayer, int numberHiddenLayers){
		int[] nrHiddenNeurons = new int[numberHiddenLayers];
	
		for (int i = 0; i < nrHiddenNeurons.length; i++)
			nrHiddenNeurons[i] = nrHiddenNeuronsPerLayer;
		
		return nrHiddenNeurons;
	}

public
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
			
			inputConnections[pos].run();
		}
			
		// Execute hidden layer(s)
		for(int layer = 0; layer < (hiddenNeurons.size()-1); layer++){
			
			for (int pos = 0; pos < hiddenConnections.get(layer).length; pos++)
				hiddenConnections.get(layer)[pos].run();
		}
		
		for (int pos = 0; pos < outputConnections.length; pos++){
			
			outputConnections[pos].run();
		}
		
		// Now get the result(s)
		for (int i = 0; i < outputNeurons.length; i++)
			outputVector[i] = outputNeurons[i].getOutput();
			
		return outputVector;
	}// run()
	
	/****************************************************************************************
	* int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
	*				int abort, float trainingCoefficient):
	* Trains the multi layer perceptron
	* Executes the perceptron with given input vector, compares with the given output vector,
	* and calculates (if necessary) new connection weights and neuron thresholds
	* The training will be aborted (to avoid endless execution if now result can be found)
	* after the given number of trials
	* The training coefficient defines the learning step size in backpropagation
	* Returns the number of trials
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
		}
		
		keepGoing = true;
		
		outVector = new float[outputNeurons.length];
		
		// We don't want endless executions
		int abortTraining = 0;
		
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
		}
		
		return abortTraining;
	}// training()
	
	/****************************************************************************************
	* int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
	*				int abort):
	* Trains the multi layer perceptron
	* Executes the perceptron with given input vector, compares with the given output vector,
	* and calculates (if necessary) new connection weights and neuron thresholds
	* The training will be aborted (to avoid endless execution if now result can be found)
	* after the given number of trials
	* The training coefficient is defined as 0.2.
	* Returns the number of trials
	****************************************************************************************/
	int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
			int abort){
		int trials = training(trainingInVector, trainingOutVector, errorTolerance, abort, 0.2f);
		
		return trials;
	}
	
	/****************************************************************************************
	* void backpropagation(float[] resultOut, float[] wantedOut):
	* Calculates the differences for weights and thresholds and add them.
	* TODO: Test output von App-Version
	****************************************************************************************/
private	void backpropagation(float[] resultOut, float[] wantedOut, float trainingCoefficient){
		float weightDelta = 0;
		Connection[] connections = new Connection[0];
		Connection[] succConnections = new Connection[0];
		int numberOfThresholds;
		float outputOfNeuron;
		float inputOfNeuron;
		int position;
		
		// For calculating the weight differences for every connection loop over every
		// (connection) layer...
		for (int layer = 0; layer <= (hiddenNeurons.size() - 1); layer++){
			
			// Outputconnection layer
			if (layer == (hiddenNeurons.size() - 1)){
				connections = new Connection [outputConnections.length];
				connections = outputConnections;
				
				numberOfThresholds = outputNeurons.length;
			}
			
			// InputConnection layer
			else if (layer == 0){
				
				if ((hiddenNeurons.size() - 1) > 1)
					succConnections = hiddenConnections.get(0);

				else
					succConnections = outputConnections;

				connections = new Connection [inputConnections.length];
				connections = inputConnections;
				
				numberOfThresholds = inputNeurons.length;
			}
			
			// Hidden Layer
			else {

				if (layer == hiddenNeurons.size() - 2)
					succConnections = outputConnections;

				else
					succConnections = hiddenConnections.get(layer + 1);
				
				connections = new Connection [hiddenConnections.get(layer).length];
				connections = hiddenConnections.get(layer);
				
				numberOfThresholds = hiddenNeurons.get(layer).length;
			}
			
			// ...and now over all connections
			for (int conn = 0; conn < connections.length; conn++){
				
				// For output layer: Gradient descent
				if (layer == (hiddenNeurons.size() - 1)){
					position = connections[conn].getPositionNeuronTo();
					
					weightDelta = wantedOut[position] - resultOut[position];
					
					// Everything else we need comes after the for-loops
				}
				
				// Da real backpropagation for the rest
				else {
					
					position = connections[conn].getPositionNeuronTo();
					
					// Sum over successive neurons
					for (int succ = 0; succ < succConnections[position].getNeuronTo().getNumberOfInputs(); succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++){
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
										resultOut[out] * (1- resultOut[out]));
						}
						
						weightDelta += sumOutputNeurons;
						
						weightDelta *= succConnections[position + succ].getConnectionWeight();
					}// Sum over successive neurons
				}
				
				outputOfNeuron = connections[conn].getNeuronTo().getOutput();
				inputOfNeuron = connections[conn].getNeuronFrom().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron) * inputOfNeuron);
					
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to connection weight
				connections[conn].addWeightDelta(weightDelta);
			}// for() over all connections
			
			// Calculation of thresholds
			for (int thresh = 0; thresh < numberOfThresholds; thresh++){
				int positionInLayer = thresh * (connections.length / numberOfThresholds);
				// TODO: Welche Version stimmt?
				float oldThresh = /*-1 * */connections[positionInLayer].getNeuronTo().getThreshold();
				
				// For output layer: Gradient descent
				if (layer == hiddenNeurons.size() - 1)
					weightDelta = wantedOut[thresh] - resultOut[thresh];
				
				// Backpropagation for the rest
				else {
					// Sum over successive neurons
					for (int succ = 0; succ < succConnections[positionInLayer].getNeuronTo().getNumberOfInputs(); succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++)
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
									      resultOut[out] * (1- resultOut[out]));
				
						weightDelta += sumOutputNeurons;
						
						weightDelta *= succConnections[positionInLayer + succ].getConnectionWeight();
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
}// class MultiLayerPerceptron