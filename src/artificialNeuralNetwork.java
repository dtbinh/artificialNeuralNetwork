/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments
* 		class Neuron64 mit long für inputs
* 		class NeuronFloat & class Neuron64 implements class Neuron?
* 		connectionFloat
* 		Activation function und Output function von außen setzbar machen: (In extra classes, damit class Neuron möglichst klein bleibt)
*		(siehe ComputaionalIntelligence S.52)
*		Sprungfunktion:		if (netInput >= threshold) return netInput; else return 0;
*		semi-lineare Funktion:	if (netInput > threshold + 1/2) return netInput;
*					else if ((netInput < threshold + 1/2) && (netInput > threshold - 1/2)) output = (netInput - threshold) + 1/2;
*					else return 0;
*		Sinus bis Sättigung:	if (netInput > threshold + pi/2) return netInput;
*					else if ((netInput < threshold + pi/2) && (netInput > threshold - pi/2)) output = (sin(netInput - threshold) + 1)/2;
*					else return 0;
*		logistische Funktion:	output = 1 / (1 + e^(-(netInput - threshold))) // geht nur von 0 - 1
*		radiale Basis Funktionen
*		enum für Funktionen in Klasse über Neuron
*
* Beispiel für Aufbau:
* (aus NeuronaleKodierung.pdf)
* Neuron integriert Eingangsignale zu Ausgang
* Ausgang verzweigt sich am Ende weiter und ist mit anderen Neuronen verbunden
* Spikes können, wie Buchstaben, als Grundelement der Sprache gesehen werden
* Spike-Kombinationen bilden neuronalen Code
* Nicht nur Anzahl der Spikes, sondern auch das zeitliche Muster könnte Informationen in sich bergen
* Ein Neuron der Gehirnrinde empfängt die Aktivitäten einer ganzen Population vorgeschalteter Neuronen
* Sie berechen durch Mittelwertbildung über die von den einzelnen Neuronen bevorzugten Augenpositionen, jeweils gewichtet mit deren Aktivität, einen so genannten Populationsvektor
* ->
* Attribute Farbe:	enthält 3 Neuronen Rot, Gelb, Blau
*			-> 3 Inputs
*			-> Output Farbvektor
************************************************************************************************/

/************************************************************************************************
* class Neuron:
* up to 32 inputs possible
* inputs are just 0 or 1
* output = input.1*weights[0] + ... + input.32*weights[31]
* So far, output = netInput
* activated just returns true if output >= threshold
* Note! So far, activated can just be called if getOutput was called before
************************************************************************************************/
class Neuron
{
private
	int inputs;
	int nrInputs;
	// So far also used as output
	int netInput;
	int weights[];
	int threshold;
	// Used as output
	Boolean activated;
	
public
	// Constructor
	Neuron(int nrInputs){
		this.nrInputs = nrInputs;
		weights = new int[nrInputs];
		
		// Initialize inputs, output and weights to 0, threshold to max
		inputs = 0;
		netInput = 0;
		activated = false;
		threshold = 0x7fffffff;
		
		for (int i = 0; i < nrInputs; i++)
			weights[i] = 0;
	}
	
	Boolean setInput(int nrInput){
		if(nrInput < nrInputs){
			inputs |= 1 << nrInput;
			
			return true;
		}
		
		return false;
	}
	
	Boolean unsetInput(int nrInput){
		if(nrInput < nrInputs){
			inputs &= ~(1 << nrInput);
			
			return true;
		}
		
		return false;
	}
	
	Boolean setWeight(int nrInput, int weight){
		if(nrInput < weights.length){
			weights[nrInput] = weight;
			return true;
		}
		
		return false;
	}
	
	void setThreshold(int threshold){
		this.threshold = threshold;
	}
	
	int getOutput(){
		// Reset net input
		netInput = 0;
		
		// Input function f_net
		for (int i = 0; i < nrInputs; i++){
			if((inputs & (1 << i)) == (1 << i))
				netInput += weights[i];
		}
		
		// Activation function f_act: step function
		if (netInput >= threshold){
			activated = true;
			
			// Output function f_out: Identity
			return netInput;
		}
		else {
			activated = false;
			return 0;
		}
	}
	
	Boolean getActivation(){
		return activated;
	}
}// class Neuron

/************************************************************************************************
 * class NeuronFloat
 * Same as class Neuron, just using floats for weights, threshold and output
************************************************************************************************/
class NeuronFloat
{
private
	int inputs;
	int nrInputs;
	// So far also used as output
	float netInput;
	float weights[];
	float threshold;
	// Used as output
	Boolean activated;
	
public
	// Constructor
	NeuronFloat(int nrInputs){
		this.nrInputs = nrInputs;
		weights = new float[nrInputs];
		
		// Initialize inputs, output and weights to 0, threshold to max
		inputs = 0;
		netInput = 0;
		activated = false;
		threshold = 0x7fffffff;
		
		for (int i = 0; i < nrInputs; i++)
			weights[i] = 0;
	}
	
	Boolean setInput(int nrInput){
		if(nrInput < nrInputs){
			inputs |= 1 << nrInput;
			
			return true;
		}
		
		return false;
	}
	
	Boolean unsetInput(int nrInput){
		if(nrInput < nrInputs){
			inputs &= ~(1 << nrInput);
			
			return true;
		}
		
		return false;
	}
	
	Boolean setWeight(int nrInput, float weight){
		if(nrInput < weights.length){
			weights[nrInput] = weight;
			return true;
		}
		
		return false;
	}
	
	void setThreshold(float threshold){
		this.threshold = threshold;
	}
	
	float getOutput(){
		// Reset net input
		netInput = 0;
		
		// Input function f_net
		for (int i = 0; i < nrInputs; i++){
			if((inputs & (1 << i)) == (1 << i))
				netInput += weights[i];
		}
		
		// Activation function f_act: step function
		if (netInput >= threshold){
			activated = true;
			
			// Output function f_out: Identity
			return netInput;
		}
		else {
			activated = false;
			return 0;
		}
	}
	
	Boolean getActivation(){
		return activated;
	}
}// class NeuronFloat

/************************************************************************************************
* class Connection:
* Connects the output of a neuron to an input of another neuron
* The weight of the following input is the output value times the weight of the connection
************************************************************************************************/
class Connection
{
private
	Neuron neuronFrom;
	Neuron neuronTo;
	int input;
	int connWeight;
	int weightToSet;
	
public
	//Constructor
	Connection(Neuron neuronFrom, Neuron neuronTo, int input, int connWeight){
		this.neuronFrom = neuronFrom;
		this.neuronTo = neuronTo;
		this.input = input;
		this.connWeight = connWeight;
	}// Connection()

	void run(){
		weightToSet = neuronFrom.getOutput();
		weightToSet *= connWeight;
		
		neuronTo.setWeight(input, weightToSet);
	
		if (neuronFrom.getActivation())
			neuronTo.setInput(input);
		else
			neuronTo.unsetInput(input);
	}// run()
}// class Connection

/************************************************************************************************
* class MultiLayerPerceptron:
* automatically builds a multi-layer perceptron, see description of constructors for more details
* Max. number of neurons per layer: 32
* TODO:	Erstellen der Topologie in Constructor mit class Connection, in run() nur noch for-loop
* 		der die connections runter rattert? 
************************************************************************************************/
class MultiLayerPerceptron
{
private
	Neuron inputNeuron[];
	Neuron hiddenNeuron[][];
	Neuron outputNeuron[];
	int hiddenLayers;
	//int connectionsPerNeuron;
	int inputConnWeights[];
	int hiddenConnWeights[][];
	int outputConnWeights[];
	int outputVector[];
	Connection inputConnection[];
	Connection hiddenConnection[][];
	Connection outputConnection[];
	// float trainingVector[]; // 100 für 16 usw.
	// Boolean keepGoing;	// For training loop

public
	// Constructors
	/****************************************************************************************
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* Each neuron will be connected to each neuron of the following layer (Except output layer).
	* TODO: Next Constructos: With connectionsPerNeuron the number of connections to the neurons of the following
	* layer are defined (-> number of outputs of neuron (excl output neurons))
	* In case of more than 1 output neuron the outputs of the last layer are splitted to the
	* output neurons; the lower hidden neurons to the lower output neurons etc
	* It's not possible to have more output neurons than hidden neurons
	* There must be at least 1 hidden layer
	****************************************************************************************/
	MultiLayerPerceptron(int inputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
							int outputNeurons){
	
		int neededNeuronInputs;
		
		// Just to prevent wrong usage
		if (inputNeurons < 1)
			inputNeurons = 1;
		
		inputNeuron = new Neuron[inputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < inputNeurons; i++){
			inputNeuron[i] = new Neuron(1);
			
			// Initial values: all weights and thresholds = 1
			inputNeuron[i].setWeight(0, 1);
			inputNeuron[i].setThreshold(1);
		}
		
		// Just to prevent wrong usage
		if (hiddenNeuronsPerLayer < 1)
			hiddenNeuronsPerLayer = 1;
		
		// Makes no sense when we have hidden neurons
		if (hiddenLayers < 1)
			hiddenLayers = 1;
		
		// We need this in run() too for building the connections
		this.hiddenLayers = hiddenLayers;
		
		hiddenNeuron = new Neuron[hiddenLayers][hiddenNeuronsPerLayer];
			
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int i = 0; i < hiddenLayers; i++){
			
			for (int j = 0; j < hiddenNeuronsPerLayer; j++){
				
				// Input layer to 1st hidden
				if (i == 0)
					neededNeuronInputs = inputNeurons;
				// All others
				else
					neededNeuronInputs = hiddenNeuronsPerLayer;
					
				hiddenNeuron[i][j] = new Neuron(neededNeuronInputs);
					
				// Initial values: all weights and thresholds = 1
				for (int k = 0; k < neededNeuronInputs; k++)
					hiddenNeuron[i][j].setWeight(k, 1);
					
				hiddenNeuron[i][j].setThreshold(1);
			}
		}
		
		// Just to prevent wrong usage
		if (outputNeurons < 1)
			outputNeurons = 1;
			
		else if (outputNeurons > hiddenNeuronsPerLayer)
			outputNeurons = hiddenNeuronsPerLayer;
			
		outputNeuron = new Neuron[outputNeurons];
		
		outputVector = new int[outputNeurons];
		
		for (int i = 0; i < outputNeuron.length; i++){
			
			// Set number of inputs; resp. number of connections
			// to the previous layer
			neededNeuronInputs = hiddenNeuronsPerLayer / outputNeurons;
			
			// Maybe we need 1 more input
			if ((hiddenNeuronsPerLayer % outputNeurons) != 0)
				neededNeuronInputs++;
			
			outputNeuron[i] = new Neuron(neededNeuronInputs);
			
			// Initial values: all weights and thresholds = 1
			for (int j = 0; j < neededNeuronInputs; j++)
				outputNeuron[i].setWeight(j, 1);
			
			outputNeuron[i].setThreshold(1);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		// Initialize weights, all weights = 1
		inputConnWeights = new int[inputNeurons * hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (inputNeurons * hiddenNeuronsPerLayer); i++)
			inputConnWeights[i] = 1;
		
		hiddenConnWeights = new int[hiddenLayers][hiddenNeuronsPerLayer * hiddenNeuronsPerLayer];

		for (int i = 0; i < hiddenLayers; i++){
			
			for (int j = 0; j < hiddenConnWeights[i].length; j++)
				hiddenConnWeights[i][j] = 1;
		}
		
		outputConnWeights = new int[hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (hiddenNeuronsPerLayer); i++)
			outputConnWeights[i] = 1;
		
		/*** Connections ***/
		// We need as many connections as connection weights (obviously)
		inputConnection = new Connection[inputConnWeights.length];
		hiddenConnection = new Connection[hiddenLayers][hiddenConnWeights[0].length];
		outputConnection = new Connection[outputConnWeights.length];
		
		// Connections between input layer and 1st hidden layer
		int positionInLayer = 0;
		
		// From each input neuron...
		for (int inp = 0; inp < inputNeuron.length; inp++){
		
			// ...to each neuron of the 1st hidden layer
			for (int conn = 0; conn < hiddenNeuron[0].length; conn++){
				
				inputConnection[positionInLayer] = new Connection(inputNeuron[inp],
						hiddenNeuron[0][conn], inp, inputConnWeights[positionInLayer]);
				
				positionInLayer++;
			}
		}
		
		// Connections between hidden layers
		for (int layer = 0; layer < (hiddenLayers-1); layer++){
			positionInLayer = 0;
			
			// From each hidden neuron...
			for (int neur = 0; neur < hiddenNeuron[layer].length; neur++){
			
				// ...to each neuron of the next hidden layer
				for (int conn = 0; conn < hiddenNeuron[layer+1].length; conn++){
					
					hiddenConnection[layer][positionInLayer] = new Connection(hiddenNeuron[layer][neur],
							hiddenNeuron[layer+1][conn], neur, hiddenConnWeights[layer][positionInLayer]);
					
					positionInLayer++;
				}
			}
		}
		
		// Connections between last hidden and output layer
		int inp = 0;
		int outNeur = -1;
		
		// From each hidden neuron...
		for (int hidNeur = 0; hidNeur < hiddenNeuron[hiddenLayers-1].length; hidNeur++, inp++){
			
			// Go to next output neuron if all inputs are connected
			if ((hidNeur % (hiddenNeuron[hiddenLayers-1].length/outputNeuron.length)) == 0){
				outNeur++;
				inp = 0;
			}
			
			// ...to the "nearest" output neuron (if several are available)
			outputConnection[hidNeur] = new Connection(hiddenNeuron[hiddenLayers-1][hidNeur],
					outputNeuron[outNeur], inp, outputConnWeights[hidNeur]);
		}
		
		//keepGoing  = false;
	}// MultiLayerPerceptron()

	/****************************************************************************************
	* Multi-layer perceptron where:
	*	output[i] = new Neuron((hiddenNeuronsPerLayer*connectionsPerNeuron)/outputNeurons);
	*	 /* Number of total weights:
	*	 *	inputNeurons *  connections	<- input - hidden layer 1
	*	 *	+ hiddenLayers * connections	<- hiddenLayer 1 - n
	*	 *	+ hiddenNeuronsPerLayer		<- hiddenLayer n - output <- Anders bei diesem Perzeptron
	*	 */
	/****************************************************************************************/
	/****************************************************************************************
	* Multi-layer perceptron with crossed connections
	****************************************************************************************/
	/****************************************************************************************
	* Multi-layer perceptron with different numbers of hidden neurons per layer
	****************************************************************************************/
	/****************************************************************************************
	* Multi-layer perceptron with different numbers of connections between input layer and
	* hidden layer and hidden layers
	****************************************************************************************/
	
	/****************************************************************************************
	* Execute the built multi-layer perceptron with given input vector
	* Returns the output vector
	****************************************************************************************/
	int[] run(int inputVector){
		// DEBUG
		System.out.println("inputNeurons: " + inputNeuron.length +
				", hiddenNeuronsPerLayer: " + hiddenNeuron[0].length +
				", hiddenLayers: " + hiddenLayers +
				", outputNeurons: " + outputNeuron.length);
		
		//Set input if corresponding bit is high, unset if low
		for (int i = 0; i < inputNeuron.length; i++){
			
			if ((inputVector & (1 << i)) == (1 << i))
				inputNeuron[i].setInput(0);
			else
				inputNeuron[i].unsetInput(0);
		}
		
		// Execute input layer
		for (int pos = 0; pos < inputConnection.length; pos++){
			
			// DEBUG
			System.out.println("inputConnection[" + pos + "]");
			
			inputConnection[pos].run();
		}
		
		// Execute hidden layer(s)
		for(int layer = 0; layer < (hiddenLayers-1); layer++){
			
			//DEBUG
			System.out.println("connection["+layer+"].length: "+ hiddenConnection[layer].length);
			
			for (int pos = 0; pos < hiddenConnection[layer].length; pos++){
				
				// DEBUG
				System.out.println("hiddenConnection[" + layer + "][" + pos + "]");
				
				hiddenConnection[layer][pos].run();
			}
		}
		
		for (int pos = 0; pos < outputConnection.length; pos++){
			
			// DEBUG
			System.out.println("outputConnection[" + pos + "]");
			
			outputConnection[pos].run();
		}
		
		// Now get the result(s)
		for (int i = 0; i < outputNeuron.length; i++)
			outputVector[i] = outputNeuron[i].getOutput();
				
		return outputVector;
	}// run()
	
	//TODO: void training(int[] trainingVector) -> run() in while(keepGoing){if (outputVector != trainingVector)}
}// class MultiLayerPerceptron

/************************************************************************************************
* TODO: class MultiLayerPerceptronFloat:
* copy of class MultiLayerPerceptron but with NeuronFloat
************************************************************************************************/