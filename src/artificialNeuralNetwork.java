/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments
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
* up to 32 inputs possible: 1 - 32
* inputs are just 0 or 1
* output = input.1*weights[0] + ... + input.32*weights[31]
* So far, output = netInput
* activated just returns true if output >= threshold
* Note! So far, activated can just be called if getOutput was called before
*
* TODO:	Inputs nur 0 oder 1 oder float?
*	Activation function und Output function von außen setzbar machen: (In extra classes, damit class Neuron möglichst klein bleibt)
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
************************************************************************************************/
class Neuron
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
	Neuron(int nrInputs){
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
	
	/*****************************************************************************************
	* connection:
	* Connects the output of this neuron to an input of another neuron
	* The weight of the following input is the output value times the weight of the connection
	*****************************************************************************************/
	void connection(Neuron neuronTo, int input, float connWeight){
		float weightToSet = getOutput();
		weightToSet *= connWeight;
		neuronTo.setWeight(input, weightToSet);
		
		if (getActivation())
			neuronTo.setInput(input);
		else
			neuronTo.unsetInput(input);
	}
}

/************************************************************************************************
* class MultiLayerPerceptron:
* automatically builds a multi-layer perceptron
************************************************************************************************/
class MultiLayerPerceptron
{
private
	Neuron inputNeuron[];
	Neuron hiddenNeuron[][];
	Neuron outputNeuron[];
	int hiddenLayers;
	int connectionsPerNeuron;
	float[][] connWeights;
	float outputVector[];
	// float trainingVector[]; // 100 für 16 usw.
	// Boolean keepGoing;	// For training loop

public
	// Constructors
	/****************************************************************************************
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined
	* With connectionsPerNeuron the number of connections to the neurons of the following
	* layer are defined (-> number of outputs of neuron (excl output neurons))
	* In case of more than 1 output neuron the outputs of the last layer are splitted to the
	* output neurons; the lower hidden neurons to the lower output neurons etc
	* It's not possible to have more output neurons than hidden neurons
	* (or input neurons if there aren't hidden layers)
	****************************************************************************************/
	MultiLayerPerceptron(int inputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers, int connectionsPerNeuron, int outputNeurons){
		// Just to prevent wrong usage
		if (inputNeurons < 1)
			inputNeurons = 1;
			
		inputNeuron = new Neuron[inputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < inputNeurons; i++){
			inputNeuron[i] = new Neuron(1);
			
			// Initial values: all weights and thresholds = 1
				inputNeuron[i].setWeight(1, 1);
				inputNeuron[i].setThreshold(1);			
		}
		
		// Just to prevent wrong usage
		if (hiddenNeuronsPerLayer < 0)
			hiddenNeuronsPerLayer = 0;
		
		// There can't be more connections per neuron to the next layer
		// than neurons in this layer
		if (connectionsPerNeuron > hiddenNeuronsPerLayer)
			connectionsPerNeuron = hiddenNeuronsPerLayer;
			
		// We need this in run() too for building the connections
		this.connectionsPerNeuron = connectionsPerNeuron;
		
		// So we have hidden neurons
		if (hiddenNeuronsPerLayer > 0){
			int neededInputs;
			
			// Makes no sense when we have hidden neurons
			if (hiddenLayers < 1)
				hiddenLayers = 1;
			
			// We need this in run() too for building the connections
			this.hiddenLayers = hiddenLayers;
		
			hiddenNeuron = new Neuron[hiddenLayers][hiddenNeuronsPerLayer];
			// Im Notfall: hiddenNeuron = new Neuron[hiddenLayers * hiddenNeuronsPerLayer];
			
			// Set number of inputs; resp. number of connections
			// to the previous layer
			for (int i = 0; i < hiddenLayers; i++){
				for (int j = 0; j < hiddenNeuronsPerLayer; j++){
					// input layer to 1st hidden
					if (i == 0)
						neededInputs = (inputNeurons * connectionsPerNeuron) / hiddenNeuronsPerLayer;
					else
						neededInputs = connectionsPerNeuron;
						
					hiddenNeuron[i][j] = new Neuron(neededInputs);
						
					// Initial values: all weights and thresholds = 1
					for (int k = 1; k <= neededInputs; k++)
						hiddenNeuron[i][j].setWeight(k, 1);
					
					hiddenNeuron[i][j].setThreshold(1);
				}
			}
		}
		
		// TODO: connectionsPerNeuron bei 0 hiddenNeurons???
		
		// Just to prevent wrong usage
		if (outputNeurons < 1)
			outputNeurons = 1;
			
		else if ((outputNeurons > inputNeurons) && (hiddenLayers < 1))
			outputNeurons = inputNeurons;
			
		else if (outputNeurons > hiddenNeuronsPerLayer)
			outputNeurons = hiddenNeuronsPerLayer;
			
		outputNeuron = new Neuron[outputNeurons];
		
		outputVector = new float[outputNeurons];
		
		// TODO: if (hiddenLayers < 1) {hiddenLayers = 0; // als Sicherheit}
		for (int i = 0; i < outputNeuron.length; i++){
			// Set number of inputs; resp. number of connections
			// to the previous layer
			// TODO: Check, wenn Rest bleibt
			outputNeuron[i] = new Neuron(hiddenNeuronsPerLayer/outputNeurons);
			
			// Initial values: all weights and thresholds = 1
			for (int j = 1; j <= (hiddenNeuronsPerLayer/outputNeurons); j++)
				outputNeuron[i].setWeight(j, 1);
			
			outputNeuron[i].setThreshold(1);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		// Calculation of how many connection weights are needed
		// 1st array dimension: hidden layers + 1
		// 2nd array dimension: Largest possible number of connections in one layer
		int maxNumberOfConnections;
		
		// outputNeurons > hiddenNeurons or inputNeurons is not allowed
		if (hiddenNeuronsPerLayer > inputNeurons)
			maxNumberOfConnections = hiddenNeuronsPerLayer * connectionsPerNeuron;
		else
			maxNumberOfConnections = inputNeurons * connectionsPerNeuron;
		
		connWeights = new float[hiddenLayers + 2][maxNumberOfConnections];
		
		// Initial values: all weights = 1
		for (int i = 0; i < hiddenLayers + 1; i++){
			for (int j = 0; j < maxNumberOfConnections; j++)
				connWeights[i][j] = 1;
		}
		
		//keepGoing  = false;
	} // MultiLayerPerceptron()
	
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
	* Execute the built multi-layer perceptron with given input vector
	* Returns the output vector
	****************************************************************************************/
	float[] run(int inputVector){
		
		//TODO: Set/Unset inputs
		//TODO: if (hiddenLayers > 0){ wenn fall für outputNeurons in Constructor erledigt
		
		// Connections between input layer and 1st hidden layer
		// From each input neuron...
		for (int inp = 0; inp < inputNeuron.length; inp++){
		
			// ...'connectionsPerNeuron'-connections to the 1st hidden layer
			for (int conn = 0; conn < connectionsPerNeuron; conn++)
				inputNeuron[inp].connection(hiddenNeuron[0][conn], (conn+1), connWeights[0][conn]);
		}
		
		// Connections between hidden layers
		for (int layer = 0; layer < hiddenLayers-1; layer++){
			
			// From each hidden neuron...
			for (int neur = 0; neur < hiddenNeuron[layer].length; neur++){
			
				// ...'connectionsPerNeuron'-connections to the next hidden layer
				for (int conn = 0; conn < connectionsPerNeuron; conn++)
					hiddenNeuron[layer][neur].connection(hiddenNeuron[layer+1][conn],
														(conn+1), connWeights[layer+1][conn]);
			}
		}
		
		// Connections between last hidden layer and output
		int inp = 0;
		int outNeur = 0;
		
		// From each hidden neuron...
		for (int hidNeur = 0; hidNeur < hiddenNeuron[hiddenLayers-1].length; hidNeur++){
			
			// Go to next output neuron if all inputs are connected
			if (hidNeur >= ((hiddenNeuron[hiddenLayers-1].length/outputNeuron.length)-1)){
				outNeur++;
				inp = 0;
			}
			
			// ...to the "nearest" output neuron (if several are available)
			hiddenNeuron[hiddenLayers-1][hidNeur].connection(outputNeuron[outNeur], inp,
													connWeights[hiddenLayers][hidNeur]);
			
			inp++;
		}
		
		// Now get the result(s)
		for (int i = 0; i < outputNeuron.length; i++)
			outputVector[i] = outputNeuron[i].getOutput();
				
		return outputVector;
	}
	
	//TODO: void training(float[] trainingVector) -> run() in while(keepGoing){if (outputVector != trainingVector)}
}