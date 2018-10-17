package be.tarsos.dsp.example;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.filters.LowPassFS;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

public class LEDLightsMain extends JFrame implements PitchDetectionHandler {

	/**
	 * 
	 */
	float sampleRate = 44100;
	int refreshRate = 12;
	int bufferSize = Math.round(sampleRate / refreshRate);
	int overlap = 0;
	
    private String host = "192.168.1.217";
    private int port = 65432;
    
    private Client client = new Client(this, host, port);
    
	private GraphPanel graphPanel;
	
	private GraphPanel normalGraph;
    
	private AmplitudeProcessor amplitudeProcessor = new AmplitudeProcessor(client, graphPanel);
    
	private static final long serialVersionUID = 3501426880288136245L;
	
	private LowPassFS lowPassFS = new LowPassFS(60, sampleRate);
    
	private final JTextArea textArea;

	public JTextArea getTextArea() {
		return textArea;
	}

	private AudioDispatcher dispatcher;
	private Mixer currentMixer;
	
	private PitchEstimationAlgorithm algo;	
	private ActionListener algoChangeListener = new ActionListener(){
		@Override
		public void actionPerformed(final ActionEvent e) {
			String name = e.getActionCommand();
			PitchEstimationAlgorithm newAlgo = PitchEstimationAlgorithm.valueOf(name);
			algo = newAlgo;
			try {
				setNewMixer(currentMixer);
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			}
	}};
	
	public void connectToServer(){
		client.start();
	}

	public LEDLightsMain() {
		this.setLayout(new GridLayout(3, 2));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("LED Light Controller");
		
		JPanel inputPanel = new InputPanel();
		add(inputPanel);
		inputPanel.addPropertyChangeListener("mixer",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						try {
							setNewMixer((Mixer) arg0.getNewValue());
						} catch (LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		
		algo = PitchEstimationAlgorithm.YIN;
		
		JPanel pitchDetectionPanel = new PitchDetectionPanel(algoChangeListener);
		
		add(pitchDetectionPanel);
		
		JPanel configPanel = new ConfigPanel(this, client, amplitudeProcessor, lowPassFS);
		//configPanel.setLayout(new GridLayout(3,3));
		
		add(configPanel);
		
		SwingUtilities.invokeLater(new Runnable() {
          public void run() {
             graphPanel = GraphPanel.createAndShowGui();
             amplitudeProcessor.setGraphPanel(graphPanel);
             add(graphPanel);
             
             normalGraph = GraphPanel.createAndShowGui();
             amplitudeProcessor.setNormalGraph(normalGraph);
             add(normalGraph);
          }
		});
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		add(new JScrollPane(textArea));

		connectToServer();
	}


	
	private void setNewMixer(Mixer mixer) throws LineUnavailableException,
			UnsupportedAudioFileException {
		
		if(dispatcher!= null){
			dispatcher.stop();
		}
		currentMixer = mixer;
		
		textArea.append("Started listening with " + Shared.toLocalString(mixer.getMixerInfo().getName()) + "\n");

		final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
				true);
		final DataLine.Info dataLineInfo = new DataLine.Info(
				TargetDataLine.class, format);
		TargetDataLine line;
		line = (TargetDataLine) mixer.getLine(dataLineInfo);
		final int numberOfSamples = bufferSize;
		line.open(format, numberOfSamples);
		line.start();
		final AudioInputStream stream = new AudioInputStream(line);

		JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
		
		// create a new dispatcher
		dispatcher = new AudioDispatcher(audioStream, bufferSize,
				overlap);
		
		// add a processor
		
		dispatcher.addAudioProcessor(new PitchProcessor(algo, sampleRate, bufferSize, this));
		
		dispatcher.addAudioProcessor(lowPassFS);
		
		dispatcher.addAudioProcessor(amplitudeProcessor);
		
		new Thread(dispatcher,"Audio dispatching").start();
	}

	public static void main(String... strings) throws InterruptedException,
			InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					//ignore failure to set default look en feel;
				}
				JFrame frame = new LEDLightsMain();
				frame.setPreferredSize(new Dimension(1280,960));
				frame.pack();
				frame.setVisible(true);
			}
		});
	}


	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult,AudioEvent audioEvent) {
		if(pitchDetectionResult.getPitch() != -1){
			double timeStamp = audioEvent.getTimeStamp();
			float pitch = pitchDetectionResult.getPitch();
			
			amplitudeProcessor.setPitch(pitch);
			
			float probability = pitchDetectionResult.getProbability();
			double rms = audioEvent.getRMS() * 100;
			String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n", timeStamp,pitch,probability,rms);
			textArea.append(message);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}
}