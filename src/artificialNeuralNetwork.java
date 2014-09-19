/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments
************************************************************************************************/
/************************************************************************************************
* class Neuron:
* up to 32 inputs possible
* calculates the output by the weights and the set inputs
************************************************************************************************/
class Neuron
{
private
	int inputs;
	int nrInputs;
	int output;
	int weights[];
	int threshold;
	
public
	// Constructor
	Neuron(int nrInputs){
		this.nrInputs = nrInputs;
		weights = new int[nrInputs];
		
		// Initialize inputs, output and weights to 0, threshold to max
		inputs = 0;
		output = 0;
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
		// Reset output
		output = 0;
		
		// Activation function f_act
		for (int i = 0; i < nrInputs; i++){
			if((inputs & (1 << i)) == (1 << i))
				output += weights[i];
		}
		
		// Output function f_out
		if (output >= threshold)
			return output;
		else
			return 0;
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
class Connection
{
//private
//	Neuron neuronFrom;
//	Neuron neuronTo;
	
public
	//Constructors
	// 1 to 1
	Connection(Neuron neuronFrom, Neuron neuronTo){
		if (neuronFrom.getOutput() != 0){
			neuronTo.setInput(1);
			neuronTo.setWeight(1, neuronFrom.getOutput());
			}
		else
			neuronTo.unsetInput(1);
	}

	// 1 to x
}

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
class Attribute
{
private
	Neuron value;
} 
