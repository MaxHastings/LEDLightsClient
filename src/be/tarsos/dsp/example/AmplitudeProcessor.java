package be.tarsos.dsp.example;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class AmplitudeProcessor implements AudioProcessor, AutoBand.OnUpdateBands{

	private Client client;
    
    private float pitch = 0;
    
	private float offset = 0;
	private float multiply = 1;
	
	private float brightnessOffset = 0;
	
	private GraphPanel graphPanel;

	private GraphPanel normalGraph;
	
	private boolean automate = true;
	
	private AutoBand autoBand = new AutoBand(this);
	
	private LEDColor currentColor = new LEDColor(1, 1, 1);
	
	private int pitchIndex = 0;
	
	public AutoBand getAutoBand() {
		return autoBand;
	}

	public void setAutoBand(AutoBand autoBand) {
		this.autoBand = autoBand;
	}

	public float getBrightnessOffset() {
		return brightnessOffset;
	}

	public void setBrightnessOffset(float brightnessOffset) {
		this.brightnessOffset = brightnessOffset;
	}

	public boolean isAutomate() {
		return automate;
	}

	public void setAutomate(boolean automate) {
		this.automate = automate;
	}

	public GraphPanel getNormalGraph() {
		return normalGraph;
	}

	public void setNormalGraph(GraphPanel normalGraph) {
		this.normalGraph = normalGraph;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		//randomColor = getRandomColor(randomColor); //Get new color on pitch change.
	}
	
	public AmplitudeProcessor(Client client, GraphPanel graphPanel){
		this.client = client;
		this.graphPanel = graphPanel;
	}

	public GraphPanel getGraphPanel() {
		return graphPanel;
	}

	public void setGraphPanel(GraphPanel graphPanel) {
		this.graphPanel = graphPanel;
	}
	
	public LEDColor getRandomColor(LEDColor notThisOne){
		
		LEDColor randLED = notThisOne;
		while(randLED.equals(notThisOne)){
			int rand = randomWithRange(1, 6);
			switch(rand){
				case 1:
					randLED = new LEDColor(1, 0, 0);
					break;
				case 2:
					randLED = new LEDColor(0, 1, 0);
					break;
				case 3:
					randLED = new LEDColor(0, 0, 1);
					break;
				case 4:
					randLED = new LEDColor(1, 1, 0);
					break;
				case 5:
					randLED = new LEDColor(1, 0, 1);
					break;
				default:
					randLED = new LEDColor(0, 1, 1);
			}
		}
		return randLED;
	}
	
	int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}

	@Override
	public boolean process(AudioEvent audioEvent) {
		// TODO Auto-generated method stub
		
		float[] data = audioEvent.getFloatBuffer();
		float volume = 0; //1 being loudest
		
		for(int i = 0; i < data.length; i++){
			float value = data[i];
			if(value > volume){
				volume = value;
			}
		}
		
		volume = Math.min(1, volume);
		volume = Math.max(0, volume);
		
		if(normalGraph != null){
			normalGraph.addScore(volume * 100);
			autoBand.newVolume(volume);
		}
		
		float exagVolume;
		
		exagVolume = (multiply * volume) - offset;
		
		exagVolume = Math.min(1, exagVolume);
		exagVolume = Math.max(0, exagVolume);
		
		exagVolume = exagVolume / (100 / Math.max(1, brightnessOffset * 100));
		
		if(graphPanel != null){
			graphPanel.addScore(exagVolume * 100);
		}
		
		if(exagVolume > 1 || exagVolume < 0){
			System.out.println(exagVolume);
			System.exit(0);
		}
		
		//System.out.println("Amplitude : " + maxValue);
		int brightness = Math.round(exagVolume * 255);
		brightness = (int) Math.round(Math.pow(brightness, 2) / 255);
		
		int currPitchIndex = getPitchIndex(pitch);
		if(currPitchIndex != pitchIndex){
			pitchIndex = currPitchIndex;
			currentColor = getRandomColor(currentColor);
		}
		
		LEDColor color = new LEDColor(currentColor.getRed() * brightness, currentColor.getGreen() * brightness, currentColor.getBlue() * brightness);
		
		sendColor(color);
		
		return true;
	}
	
	public int getPitchIndex(float pitch){
		if(pitch <= 80){
			return 1;
		}else if(pitch <= 160){
			return 2;
		}else if(pitch <= 320){
			return 3;
		}else if(pitch <= 480){
			return 4;
		}else if(pitch <= 640){
			return 5;
		}else if(pitch <= 800){
			return 6;
		}else if(pitch <= 1600){
			return 7;
		}else if(pitch <= 3200){
			return 8;
		}else{
			return 9;
		}
	}
	
	public void setMinMaxValues(float max, float min){
		
		if(max < min)
			return;
		
		multiply = 1 / (max - min);
		offset = min * multiply;
		if(normalGraph != null){
			normalGraph.setUpperBand(max);
			normalGraph.setLowerBand(min);
		}
	}
	
	public void setMinValue(float min){
		float max = getMaxValue();

		setMinMaxValues(max, min);
	}
	
	public void setMaxValue(float max){
		float min = getMinValue();
			
		setMinMaxValues(max, min);
	}
	
	public float getMinValue(){
		return offset / multiply;
	}
	
	public float getMaxValue(){
		return (1 / multiply) + getMinValue();
	}
	
	public void sendColor(LEDColor color){
		client.sendColor(color);
	}

	@Override
	public void processingFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBands(float max, float min, float lowerSpacing, float upperSpacing) {
		// TODO Auto-generated method stub
		if(automate){
			normalGraph.setUpperBand(max);
			normalGraph.setLowerBand(min);
			normalGraph.setLowerSpacing(lowerSpacing);
			normalGraph.setUpperSpacing(upperSpacing);
			setMinMaxValues(max, min);
		}
	}

}
