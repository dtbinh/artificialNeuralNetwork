/********************************** Artificial neural network ***********************************
************************************************************************************************/
class Neuron
{
private
	Boolean inputs[];
	Boolean output;
	int weights[];
	int threshold;
	
public
	// Constructor
	Neuron(int nrInputs){
		inputs = new Boolean[nrInputs];
		weights = new int[nrInputs];
		
		for (int i = 0; i < nrInputs; i++){
			inputs[i] = false;
			weights[i] = -1;
		}
	}
	
	Boolean setInput(int nrInput){
		if(nrInput < inputs.length){
			inputs[nrInput] = true;
			
			return true;
		}
		
		return false;
	}
	
	Boolean unsetInput(int nrInput){
		if(nrInput < inputs.length){
			inputs[nrInput] = false;
			
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
	
	void setThreshold(int iThreshold){
		threshold = iThreshold;
	}
	
	// Output function f_out
	Boolean getResult(){
		int out = 0;
				
		for (int i = 0; i < inputs.length; i++){
			if(inputs[i])
				out += weights[i];
		}
		
		if (out >= threshold)
			output = true;
		else
		output = false;
	
		return output;
	}
};