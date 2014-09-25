/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments
*	Network-Constructor: aNNetwork(int i){
*		// Simple network with i input, i/2 hidden and i/4 output neurons
*		input = new Neuron[i](1);
*		hidden = new Neuron[i/2](2);
*		output = new Neuron[i/4](2);
*		//TODO: Berechnung für [6]: connWeight = new float[6]
*	}
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
*			 --o--o
* 				   \
* 			--o--o--o--
*          		   /
*			 --o--o
************************************************************************************************/

/**********************************************
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
**********************************************/
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
	
	/***************************************************************************************
	* connection:
	* Connects the output of this neuron to an input of another neuron
	* The weight of the following input is the output value times the weight of the connection
	***************************************************************************************/
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

/**********************************************
* class Connection:
* connects neurons -> routing input to outputs,
* setting weights
* 
* TODO:	Reicht Connection als Funktion?
* 		Woher kommt die Information für nrInput?
**********************************************/
/*class Connection
{
//private
//	Neuron neuronFrom;
//	Neuron neuronTo;
	
public
	//Constructors
	// 1 to 1
	Connection(Neuron neuronFrom, Neuron neuronTo, int weight){
		if (neuronFrom.getOutput() != 0){
			neuronTo.setInput(1);
			neuronTo.setWeight(1, weight);
			}
		else
			neuronTo.unsetInput(1);
	}

	// 1 to x
}*/

/**********************************************
* class Attribute:
* for symbolic attributes, each attribute contains
* as many neurons as values
* 
* Bsp:
* Attribute Farbe
* 	values:		Rot | Gelb| Blau
* 	Neuronen:	001 | 010 | 100
* 	-> Namen müssen hinterlegt werden
**********************************************/
//class Attribute
//{
//private
//	Neuron value;
//} 

/**********************************************
* class MultiLayerPerceptron:
* contains the attributes
**********************************************/