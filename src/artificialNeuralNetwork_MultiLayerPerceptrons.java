/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments, TechDoc
************************************************************************************************/

/************************************************************************************************
* class MultiLayerPerceptron:
* automatically builds a multi-layer perceptron, using neurons with logistic function, see
* description of constructors for more details
* Max. number of neurons per layer: 32
************************************************************************************************/
class MultiLayerPerceptron
{
private
	Neuron inputNeurons[];
	Neuron hiddenNeurons[][];
	Neuron outputNeurons[];
	int hiddenLayers;
	float inputConnWeights[];
	float hiddenConnWeights[][];
	float outputConnWeights[];
	float outputVector[];
	Connection inputConnections[];
	Connection hiddenConnections[][];
	Connection outputConnections[];
	Boolean keepGoing;	// For training loop

public
	// Constructor
	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
	*			int nrOutputNeurons, String inputTopology, String hiddenTopology):
	*Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* The type of connection between the layers has to be defined.
	* It's not possible to have more output neurons than hidden neurons
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
				int nrOutputNeurons, String inputTopology, String hiddenTopology, String outputTopology){
		//			Connections für hidden layer:	cross, zigzag
		
		int neededNeuronInputs;
		
		/*** Input layer ***/
		// Just to prevent wrong usage
		if (nrInputNeurons < 1)
			nrInputNeurons = 1;
		
		// Abhängikeiten von inputTopology:	Anzahl der Inputs der hidden[0] Neuronen
		//									Arraygröße inputConnWeights & -> inputConnections
		//									Algorythmus für Connections between input layer and 1st hidden layer
		switch (inputTopology){
		case "one":
			// Every input neuron will be connected to the nearest hidden neuron
			if (nrInputNeurons >= hiddenNeuronsPerLayer){
				inputConnWeights = new float[nrInputNeurons];
				
				neededNeuronInputs = inputConnWeights.length / hiddenNeuronsPerLayer;
				
				// Maybe we need 1 more input
				if ((inputConnWeights.length % hiddenNeuronsPerLayer) != 0)
					neededNeuronInputs++;
			}
				
			// Every hidden neuron will be connected to the nearest input neuron
			else{
				inputConnWeights = new float[hiddenNeuronsPerLayer];
				
				neededNeuronInputs = inputConnWeights.length / nrInputNeurons;
				
				// Maybe we need 1 more input
				if ((inputConnWeights.length % nrInputNeurons) != 0)
					neededNeuronInputs++;
			}
			break;
			
		case "twoGroups":
			// The hidden neurons will be split into two groups
			// Every input neuron will be connected to every neuron of the nearest group
			int neededConnections = hiddenNeuronsPerLayer/2;
			
			if ((hiddenNeuronsPerLayer % 2) != 0)
				neededConnections++;
			
			inputConnWeights = new float[nrInputNeurons * neededConnections];
			
			neededNeuronInputs = nrInputNeurons/2;
			
			if ((nrInputNeurons % 2) != 0)
				neededNeuronInputs++;	
			break;
		
		default: // each
			inputConnWeights = new float[nrInputNeurons * hiddenNeuronsPerLayer];
			neededNeuronInputs = nrInputNeurons;
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
		// Just to prevent wrong usage
		if (hiddenNeuronsPerLayer < 1)
			hiddenNeuronsPerLayer = 1;
		
		// Makes no sense when we have hidden neurons
		if (hiddenLayers < 1)
			hiddenLayers = 1;
		
		// We need this in run() too for building the connections
		this.hiddenLayers = hiddenLayers;
		
		hiddenNeurons = new Neuron[hiddenLayers][hiddenNeuronsPerLayer];
				
		// Abhängikeiten von hiddenTopology:	Anzahl der Inputs der hidden[>0] Neuronen
		//										Arraygröße hiddenConnWeights & -> hiddenConnections
		//										Algorythmus für Connections between hidden layer
		switch (hiddenTopology){
		case "one":
			// Every hidden neuron will be connected to the nearest hidden neuron in the next layer
			if (hiddenLayers > 1){
				hiddenConnWeights = new float[hiddenLayers-1][hiddenNeuronsPerLayer];
		
				for (int i = 0; i < hiddenLayers-1; i++){
					
					for (int j = 0; j < hiddenConnWeights[i].length; j++)
						hiddenConnWeights[i][j] = 1;
				}
			}
			
			neededNeuronInputs = 1;
			break;
				
		default: // each
			hiddenConnWeights = new float[hiddenLayers-1][hiddenNeuronsPerLayer * hiddenNeuronsPerLayer];
	
			for (int i = 0; i < hiddenLayers-1; i++){
				
				for (int j = 0; j < hiddenConnWeights[i].length; j++)
					hiddenConnWeights[i][j] = 1;
			}
			
			neededNeuronInputs = hiddenNeuronsPerLayer;
		}// switch (hiddenTopology)
				
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int i = 0; i < hiddenLayers; i++){
			
			for (int j = 0; j < hiddenNeuronsPerLayer; j++){
				
				// Input layer to 1st hidden in switch
				if (i < 0)
					neededNeuronInputs = hiddenNeuronsPerLayer;
					
				hiddenNeurons[i][j] = new Neuron(neededNeuronInputs);
					
				// Initial values: all thresholds = 0
					
				hiddenNeurons[i][j].setThreshold(0);
			}
		}
		
		/*** Output layer ***/
		// Just to prevent wrong usage
		if (nrOutputNeurons < 1)
			nrOutputNeurons = 1;
			
		else if (nrOutputNeurons > hiddenNeuronsPerLayer)
			nrOutputNeurons = hiddenNeuronsPerLayer;
			
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		switch (outputTopology){
		case "each":
			outputConnWeights = new float[nrOutputNeurons * hiddenNeuronsPerLayer];
			
			neededNeuronInputs = hiddenNeuronsPerLayer;
			break;
			
		default:
			outputConnWeights = new float[hiddenNeuronsPerLayer];
			
			neededNeuronInputs = hiddenNeuronsPerLayer / nrOutputNeurons;
			
			// Maybe we need 1 more input
			if ((hiddenNeuronsPerLayer % nrOutputNeurons) != 0)
				neededNeuronInputs++;
			break;
		}// switch (outputTopology)
		
		for (int i = 0; i < outputNeurons.length; i++){
			outputNeurons[i] = new Neuron(neededNeuronInputs);
			
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
		int outNeur = 0;
		int numberOfInputs;
		
		switch (inputTopology){
		case "one":

			if (nrInputNeurons >= hiddenNeuronsPerLayer){
				numberOfInputs = (inputNeurons.length/hiddenNeurons[0].length);
				
				if ((inputNeurons.length % hiddenNeurons[0].length) != 0)
					numberOfInputs++;
				
				// From each input neuron...
				for (int inNeur = 0; inNeur < inputNeurons.length; inNeur++, inp++){
					
					// Go to next hidden neuron if all inputs are connected
					if ((inNeur > 0) && (inNeur % numberOfInputs == 0)){
						outNeur++;
						inp = 0;
					}
					
					System.out.println("inputConnections["+inNeur+"] = new Connection(inputNeurons["+inNeur
							+"] ,hiddenNeurons[0]["+outNeur+"], "+inp+", inputConnWeights["+inNeur+"]);");
					
					// ...to the "nearest" hidden neuron (if several are available)
					inputConnections[inNeur] = new Connection(inputNeurons[inNeur],
							hiddenNeurons[0][outNeur], inp, inputConnWeights[inNeur], outNeur);
				}
			}
			else{
				numberOfInputs = (hiddenNeurons[0].length/inputNeurons.length);
				
				if ((hiddenNeurons[0].length % inputNeurons.length) != 0)
					numberOfInputs++;
				
				// From each hidden neuron...
				for (int hidNeur = 0; hidNeur < hiddenNeurons[0].length; hidNeur++, inp++){
					
					// Go to next hidden neuron if all inputs are connected
					if ((hidNeur > 0) && (hidNeur % numberOfInputs == 0)){
						outNeur++;
						inp = 0;
					}
					
					// ...to the "nearest" input neuron (if several are available)
					inputConnections[hidNeur] = new Connection(inputNeurons[outNeur],
							hiddenNeurons[0][hidNeur], inp, inputConnWeights[hidNeur], hidNeur);
				}
			}
			break;
			
		case "twoGroups": // From each input neuron...
			for (inp = 0; inp < inputNeurons.length; inp++){
			
				// ...to each neuron in the nearest group
				if (inp < (inputNeurons.length/2)){
					for (int conn = 0; conn < (hiddenNeurons[0].length/2); conn++){
						
						inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
								hiddenNeurons[0][conn], inp, inputConnWeights[positionInLayer], conn);
						
						positionInLayer++;
					}
				}
				else {
					for (int conn = (hiddenNeurons[0].length/2); conn < hiddenNeurons[0].length; conn++){
						
						inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
								hiddenNeurons[0][conn], inp, inputConnWeights[positionInLayer], conn);
						
						positionInLayer++;
					}
				}
			}
			break;
		
		default: // From each input neuron...
			for (inp = 0; inp < inputNeurons.length; inp++){
			
				// ...to each neuron of the 1st hidden layer
				for (int conn = 0; conn < hiddenNeurons[0].length; conn++){
					
					inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
							hiddenNeurons[0][conn], inp, inputConnWeights[positionInLayer], conn);
					
					positionInLayer++;
				}
			}
		}// switch (inputTopology)
		
		// Connections between hidden layers
		if (hiddenLayers > 1){
			hiddenConnections = new Connection[hiddenLayers-1][hiddenConnWeights[0].length];
				
			switch (hiddenTopology){
			case "one":		
				for (int layer = 0; layer < (hiddenLayers-1); layer++){
			
					// From each hidden neuron...
					for (int neur = 0; neur < hiddenNeurons[layer].length; neur++){
			
						// ...to the nearest neuron of the next hidden layer
						hiddenConnections[layer][neur] = new Connection(hiddenNeurons[layer][neur],
							hiddenNeurons[layer+1][neur], 1, hiddenConnWeights[layer][neur], neur);
					}
				}
				break;
				
			default: // each
				for (int layer = 0; layer < (hiddenLayers-1); layer++){
					positionInLayer = 0;
					
					// From each hidden neuron...
					for (int neur = 0; neur < hiddenNeurons[layer].length; neur++){
					
						// ...to each neuron of the next hidden layer
						for (int conn = 0; conn < hiddenNeurons[layer+1].length; conn++){
							
							hiddenConnections[layer][positionInLayer] = new Connection(hiddenNeurons[layer][neur],
									hiddenNeurons[layer+1][conn], neur, hiddenConnWeights[layer][positionInLayer], conn);
							
							positionInLayer++;
						}
					}
				}
			}// switch (hiddenTopology)
		}// if (hiddenLayers > 1)
		
		// Connections between last hidden and output layer
		switch (outputTopology){
		case "each": // From each neuron of the last hidden layer...
			positionInLayer = 0;
			
			for (int hidNeur = 0; hidNeur < hiddenNeurons[hiddenLayers-1].length; hidNeur++){
			
				// ...to each output neuron
				for (int conn = 0; conn < outputNeurons.length; conn++){
					
					outputConnections[positionInLayer] = new Connection(hiddenNeurons[hiddenLayers-1][hidNeur],
							outputNeurons[conn], hidNeur, outputConnWeights[positionInLayer], conn);
					
					positionInLayer++;
				}
			}
			
		default: // one	
			inp = 0;
			outNeur = 0;
			numberOfInputs = (hiddenNeurons[hiddenLayers-1].length/outputNeurons.length);
			
			if ((hiddenNeurons[hiddenLayers-1].length % outputNeurons.length) != 0)
				numberOfInputs++;
			
			// From each hidden neuron...
			for (int hidNeur = 0; hidNeur < hiddenNeurons[hiddenLayers-1].length; hidNeur++, inp++){
				
				// Go to next output neuron if all inputs are connected
				if ((hidNeur > 0) && (hidNeur % numberOfInputs  == 0)){
					outNeur++;
					inp = 0;
				}
						
				// ...to the "nearest" output neuron (if several are available)
				outputConnections[hidNeur] = new Connection(hiddenNeurons[hiddenLayers-1][hidNeur],
						outputNeurons[outNeur], inp, outputConnWeights[hidNeur], outNeur);
			}
			break;
		}// switch (inputTopology)
		
		keepGoing  = false;
	}// MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
		// int nrOutputNeurons, String inputTopology, String hiddenTopology)

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
		
		// Test output
//		System.out.println("inputNeurons: " + inputNeurons.length +
//				", hiddenNeuronsPerLayer: " + hiddenNeurons[0].length +
//				", hiddenLayers: " + hiddenLayers +
//				", outputNeurons: " + outputNeurons.length);
		
		//Set inputs
		for (int i = 0; i < inputNeurons.length; i++)
			inputNeurons[i].setInput(0, inputVector[i]);
		
		// Execute input layer
		for (int pos = 0; pos < inputConnections.length; pos++){			
			// Test output
//			System.out.println("inputConnection[" + pos + "]");
			
			inputConnections[pos].run();
		}
			
		// Execute hidden layer(s)
		for(int layer = 0; layer < (hiddenLayers-1); layer++){
			
			// Test output
//			System.out.println("connection["+layer+"].length: "+ hiddenConnections[layer].length);
			
			for (int pos = 0; pos < hiddenConnections[layer].length; pos++){
				
				// Test output
//				System.out.println("hiddenConnection[" + layer + "][" + pos + "]");
				
				hiddenConnections[layer][pos].run();
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
			
			// For debug purposes, print output vector
//			System.out.println("Trial " + debugStopTraining + ":");
//			
//			for (int deb = 0; deb < outVector.length; deb++){
//				System.out.print("trainingOutVector[" + deb + "]: " + (trainingOutVector[deb] - absErrorTol[deb]));
//				System.out.print(" - " + (trainingOutVector[deb] + absErrorTol[deb]));
//				System.out.println(", outVector[" + deb + "]: " + outVector[deb]);
//			}
			
			// Wrong result, use backpropagation to find a better one and try again
			// TODO: In gitk schauen, wie for-loop zuerst war
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
		System.out.println("\ttrainingOut\t\t| outVector");
		for (int deb = 0; deb < outVector.length; deb++){
			System.out.print("\t[" + deb + "]: " + (trainingOutVector[deb] - errorTolerance));
			System.out.print(" - " + (trainingOutVector[deb] + errorTolerance));
			System.out.println("\t| [" + deb + "]: " + outVector[deb]);
		}
		
		return abortTraining;
	}// training()
	
	/****************************************************************************************
	* void backpropagation(float[] resultOut, float[] wantedOut):
	* Calculates the differences for weights and thresholds and add them.
	****************************************************************************************/
private	void backpropagation(float[] resultOut, float[] wantedOut){
		float trainingCoefficient = 2;
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
		int connectionLayer = hiddenLayers - 1;
		
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
					succWeights = new float [hiddenConnWeights[0].length];
					succWeights = hiddenConnWeights[0];
					
					// Number of inputs of neuron in 1st hidden layer = number of
					// successive neurons
					succNeurons = hiddenConnWeights[0].length/hiddenNeurons[0].length;
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
					succWeights = new float [hiddenConnWeights[layer + 1].length];
					succWeights = hiddenConnWeights[layer + 1];
				
					// To each neuron of the following layer
					succNeurons = hiddenNeurons[layer + 1].length;
				}
				
				connections = new Connection [hiddenConnections[layer].length];
				connections = hiddenConnections[layer];
				
				numberOfThresholds = hiddenNeurons[layer].length;
			}
			
			// ...and now over all connections
			for (int conn = 0; conn < (connections.length); conn++){
				
				// For output layer: Gradient descent
				if (layer == connectionLayer){
					position = connections[conn].getPositionNeuronTo();
					
					weightDelta = wantedOut[position] - resultOut[position];
					
					// Everything else we need comes after the for-loops
				}
				
				// Da real backpropagation for the rest
				else {
					// Sum over successive neurons
					for (int succ = 0; succ < succNeurons; succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++)
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
										resultOut[out] * (1- resultOut[out]));
				
						weightDelta += sumOutputNeurons;
						
						position = connections[conn].getPositionNeuronTo();
						
						weightDelta *= succWeights[position];
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
}// class MultiLayerPerceptron