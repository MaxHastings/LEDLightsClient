package be.tarsos.dsp.example;

import be.tarsos.dsp.filters.LowPassFS;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigPanel extends JPanel implements ChangeListener{
	
	private Client client;
	
	private int frequency;
	private int segment;
	
	private JSlider freqSlider;
	private JSlider maxSlider;
	private JSlider minSlider;
	private JSlider segSlider;
	private JSlider brightnessSlider;
	private JSlider upperSpacingSlider;
	private JSlider lowerSpacingSlider;
	
	private JSlider lowPassSlider;
	
	private JCheckBox automate;
	
	private JCheckBox lowPassBox;
	
	private JTextField frequencyInput;
	
	private AmplitudeProcessor amplitudeProcessor;
	
	private LowPassFS lowPassFS;
	
	private LEDLightsMain panel;
	
	private float lowPassFreq = 60;
	
	public ConfigPanel(LEDLightsMain panel, Client client, AmplitudeProcessor amplitudeProcessor, LowPassFS lowPassFS){
		this.client = client;
		this.panel = panel;
		this.amplitudeProcessor = amplitudeProcessor;
		this.lowPassFS = lowPassFS;
		
		createFreqSlider();
		createMaxOffSlider();
		createMinOffSlider();
		createSegmentSlider();
		createBrightnessSlider();
		createLowerSpacingSlider();
		
		automate = new JCheckBox("Automate");
		automate.setSelected(amplitudeProcessor.isAutomate());
		automate.addChangeListener(this);
		add(automate);
		
		lowPassBox = new JCheckBox("Low Pass");
		lowPassBox.addChangeListener(this);
		add(lowPassBox);
		
		createUpperSpacingSlider();
		createLowPassFilter();
	}
	
	public void createLowPassFilter(){
		JLabel label = new JLabel("Low Pass Freq");
		add(label);
		lowPassSlider = new JSlider(JSlider.HORIZONTAL,
                60, 600, 60);
		lowPassSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		lowPassSlider.setMajorTickSpacing(100);
		lowPassSlider.setMinorTickSpacing(10);
		lowPassSlider.setPaintTicks(true);
		lowPassSlider.setPaintLabels(true);
		
		lowPassSlider.setValue(60);

		lowPassFS.setFrequency(20_000);
		
		add(lowPassSlider);
	}
	
	public void createLowerSpacingSlider(){
		JLabel label = new JLabel("Lower Spacing");
		add(label);
		lowerSpacingSlider = new JSlider(JSlider.HORIZONTAL,
                -30, 30, 1);
		lowerSpacingSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		lowerSpacingSlider.setMajorTickSpacing(5);
		lowerSpacingSlider.setMinorTickSpacing(1);
		lowerSpacingSlider.setPaintTicks(true);
		lowerSpacingSlider.setPaintLabels(true);
		
		lowerSpacingSlider.setValue(amplitudeProcessor.getAutoBand().getLowerSpacing());
		
		add(lowerSpacingSlider);
	}
	
	public void createUpperSpacingSlider(){
		JLabel label = new JLabel("Upper Spacing");
		add(label);
		upperSpacingSlider = new JSlider(JSlider.HORIZONTAL,
                -30, 30, 1);
		upperSpacingSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		upperSpacingSlider.setMajorTickSpacing(5);
		upperSpacingSlider.setMinorTickSpacing(1);
		upperSpacingSlider.setPaintTicks(true);
		upperSpacingSlider.setPaintLabels(true);
		
		upperSpacingSlider.setValue(amplitudeProcessor.getAutoBand().getUpperSpacing());
		
		add(upperSpacingSlider);
	}
	
	public void createBrightnessSlider(){
		JLabel label = new JLabel("Brightness");
		add(label);
		brightnessSlider = new JSlider(JSlider.HORIZONTAL,
                0, 100, 1);
		brightnessSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		brightnessSlider.setMajorTickSpacing(10);
		brightnessSlider.setMinorTickSpacing(1);
		brightnessSlider.setPaintTicks(true);
		brightnessSlider.setPaintLabels(true);
		
		brightnessSlider.setValue(100);
		
		add(brightnessSlider);
	}
	
	public void createSegmentSlider(){
		JLabel label = new JLabel("Segment Size");
		add(label);
		segSlider = new JSlider(JSlider.HORIZONTAL,
                1, 10, 1);
		segSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		segSlider.setMajorTickSpacing(2);
		segSlider.setMinorTickSpacing(1);
		segSlider.setPaintTicks(true);
		segSlider.setPaintLabels(true);
		
		segSlider.setValue(3);
		
		add(segSlider);
	}
	
	public void createFreqSlider(){
		JLabel label = new JLabel("Frequency Speed");
		add(label);
		freqSlider = new JSlider(JSlider.HORIZONTAL,
                1, 100, 1);
		freqSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		freqSlider.setMajorTickSpacing(10);
		freqSlider.setMinorTickSpacing(1);
		freqSlider.setPaintTicks(true);
		freqSlider.setPaintLabels(true);
		
		freqSlider.setValue(50);
		
		add(freqSlider);
	}
	
	public void createMaxOffSlider(){
		JLabel label = new JLabel("Upper Band");
		add(label);
		maxSlider = new JSlider(JSlider.HORIZONTAL,
                0, 100, 1);
		maxSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		maxSlider.setMajorTickSpacing(10);
		maxSlider.setMinorTickSpacing(2);
		maxSlider.setPaintTicks(true);
		maxSlider.setPaintLabels(true);
		
		maxSlider.setValue(Math.round(amplitudeProcessor.getMaxValue() * 100));
		
		add(maxSlider);
	}
	
	public void createMinOffSlider(){
		JLabel label = new JLabel("Lower Band");
		add(label);
		minSlider = new JSlider(JSlider.HORIZONTAL,
                0, 100, 1);
		minSlider.addChangeListener( this);
		//Turn on labels at major tick marks.
		minSlider.setMajorTickSpacing(10);
		minSlider.setMinorTickSpacing(2);
		minSlider.setPaintTicks(true);
		minSlider.setPaintLabels(true);
		
		minSlider.setValue(Math.round(amplitudeProcessor.getMinValue() * 100));
		
		add(minSlider);
	}
	
	public void updateMinValue(float min){
		  amplitudeProcessor.setMinValue(min);
	}
	
	public void updateMaxValue(float max){
		  amplitudeProcessor.setMaxValue(max);
	}
	
	public void updateFrequency(int freq){
		this.frequency = freq;
		client.sendFrequency(frequency);
	}
	
	public void updateLength(int segment){
		this.segment = segment;
		client.sendLength(segment);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if(source == freqSlider){
			int freq = freqSlider.getValue();
			updateFrequency(freq);
		}else if(source == minSlider){
			int min = minSlider.getValue();
			updateMinValue((float)min / 100);
		}else if(source == maxSlider){
			int max = maxSlider.getValue();
			updateMaxValue((float)max / 100);
		}else if(source == segSlider){
			int segmentSize = segSlider.getValue();
			updateLength(segmentSize);
		}else if(source == automate){
			amplitudeProcessor.setAutomate(automate.isSelected());
		}else if(source == brightnessSlider){
			int brightness = brightnessSlider.getValue();
			amplitudeProcessor.setBrightnessOffset((float)brightness / 100);
		}else if(source == lowerSpacingSlider){
			int spacing = lowerSpacingSlider.getValue();
			amplitudeProcessor.getAutoBand().setLowerSpacing(spacing);
		}else if(source == upperSpacingSlider){
			int spacing = upperSpacingSlider.getValue();
			amplitudeProcessor.getAutoBand().setUpperSpacing(spacing);
		}else if(source == lowPassSlider){
			lowPassFreq = lowPassSlider.getValue();
			if(lowPassBox.isSelected()){
				lowPassFS.setFrequency(lowPassFreq);
			}
		}else if(source == lowPassBox){
			if(lowPassBox.isSelected()){
				lowPassFS.setFrequency(lowPassFreq);
			}else{
				lowPassFS.setFrequency(20_000);
			}
		}
	}

}
